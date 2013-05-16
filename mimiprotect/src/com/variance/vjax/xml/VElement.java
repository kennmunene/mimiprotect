/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.variance.vjax.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.annotation.SuppressLint;

import com.variance.mimiprotect.util.Utils;

/**
 * Some methods depend on their parents. To ensure that appropriate parentage
 * hierarchy is maintained on behaviour, a parent should add a child only if it
 * belongs to an existing hierarchy. the exception is when the parent is the
 * root element. However, the hierarchy may not be disrupted but some consistent
 * namespace behaviours may not be guranteed
 * 
 * @author Marembo
 */
@SuppressLint("UseSparseArrays")
public class VElement implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8611012856157193912L;
	/**
	 * When characters that are reserved in the xml specification appears on an
	 * element tag, then the specified characters will be replaced by an
	 * underscore and then an attribute will be
	 */
	public static final String ESCAPE_SEQUENCE = "escapeSequence";
	private static int elementIds = 0;
	private String markup;
	private String content;
	private List<VAttribute> attributes;
	private Map<Integer, VElement> children;
	private boolean empty;
	private VElement parent;
	private String comment;
	private transient int indexing = 0;
	private VNamespace associatedNamespace;
	private int elementId;
	/**
	 * We use this property to determine whether to render this element or not
	 */
	private transient boolean renderable = true;

	public VElement(String markup, VElement parent) {
		this(markup);
		this.SetParent(parent);
	}

	public VElement(String markup) {
		elementId = elementIds++;
		this.attributes = new ArrayList<VAttribute>();
		this.SetTag(markup);
		this.content = null;
		this.children = new HashMap<Integer, VElement>();
		this.parent = null;
		this.empty = true;
	}

	private VElement(VElement elem) {
		this(elem.markup);
		this.content = elem.content;
		this.empty = elem.empty;
		this.comment = elem.comment;
		this.parent = elem.parent;
		this.indexing = elem.indexing;
		if (elem.getAssociatedNamespace() != null) {
			this.associatedNamespace = new VNamespace(elem
					.getAssociatedNamespace().getPrefix(), elem
					.getAssociatedNamespace().getValue());
		}
		for (VAttribute vv : elem.attributes) {
			addAttribute(new VAttribute(vv.getName(), vv.getValue(),
					vv.getAttributeNamespace()));
		}
		for (VElement el : elem.getChildren()) {
			try {
				VElement e = el.clone();
				e.setParent(el.parent);
				addChild(e);
			} catch (CloneNotSupportedException ex) {
				Logger.getLogger(VElement.class.getName()).log(Level.SEVERE,
						null, ex);
			}
		}
	}

	/**
	 * A unique integer that represents the id of the element in the current
	 * application context
	 * 
	 * @return
	 */
	public int getElementId() {
		return elementId;
	}

	public VElement getParent() {
		return parent;
	}

	public boolean isConstantValue() {
		VAttribute va = this
				.getAttribute(VMarshallerConstants.CONSTANT_ATTRIBUTE);
		return va != null && va.getBoolenValue();
	}

	public String getToString() {
		return toString();
	}

	public boolean isRenderable() {
		return renderable;
	}

	/**
	 * Finds an element of the specified name and whose child element specified,
	 * has the content value specified.
	 * 
	 * @param name
	 * @param childName
	 * @param childContent
	 * @return
	 */
	public VElement findChild(String name, String childName, String childContent) {
		for (VElement elem : this.children.values()) {
			if (elem.getMarkup().equalsIgnoreCase(name)) {
				VElement child = elem.findChild(childName);
				if (child != null && child.getContent().equals(childContent)) {
					return elem;
				}
			} else if (elem.hasChildren()) {
				VElement e = elem.findChild(name, childName, childContent);
				if (e != null) {
					return e;
				}
			}
		}
		return null;
	}

	public void setRenderable(boolean renderable) {
		this.renderable = renderable;
	}

	/**
	 * Adds a namespace definition to the namespaces defined by this element
	 * 
	 * @param namespace
	 */
	public void addNamespace(VNamespace namespace) {
		// must be added at the begining of the list
		// be sure that the namespace has right synatx
		VXMLVerifier.verifyNamespace(namespace);
		// ensure that we do not have namespaces already declared
		if (!attributes.contains(namespace)) {
			attributes.add(0, namespace);
		}
	}

	/**
	 * Adds a namespace definition to the namespaces defined by this element
	 * 
	 * @param namespace
	 */
	private void doAddNamespace(VNamespace namespace) {
		if (this == this.parent) {
			if (this.parent.parent != null) {
				this.parent.parent.doAddNamespace(namespace);
			} else {
				this.parent.addNamespace(namespace);
			}
		}
		if (this.parent != null) {
			this.parent.doAddNamespace(namespace);
		} else {
			addNamespace(namespace);
		}
	}

	/**
	 * Returns the current namespace associated with this element If none is
	 * specified but the element defines namespaces, the returned value will be
	 * the first encountered namespace in the definition list, If no namespace
	 * definition is found the associated namespace is not defined a defualt
	 * namespace is returned if this is enabled in the environment property
	 * com.flemax.vjax.defualtnamespace, otherwise returns null
	 * 
	 * <pre>
	 *      The returned value is searched as follows:
	 *      1. If the element defines an associated namespace, that particular namespace is returned
	 *      2. If the element defines namespaces, the first namespace encountered is returned
	 *      3. If the parent defines an associated namespace, the parents associated namespace is returned
	 *      4. If the system property VNamespace.DEFUALT_NAMESPACE_PROPERTY_BINDING is defined to true, the default vjax namespace is returned
	 * 5. Otherwise null is returned
	 * 
	 * <pre>
	 * 
	 * @return the current associated namespace
	 */
	public VNamespace getAssociatedNamespace() {
		if (associatedNamespace != null) {
			return associatedNamespace;
		}
		List<VNamespace> nms = this.getDefinedNamespaces();
		if (!nms.isEmpty()) {
			return nms.get(0);
		}
		// check if the parent defines an associated namespace
		VNamespace pAssoc = (parent == null) ? null : parent
				.getAssociatedNamespace();
		if (pAssoc != null) {
			this.setAssociatedNamespace(pAssoc);
			return pAssoc;
		}
		// String prop =
		// System.getProperty(VNamespace.DEFUALT_NAMESPACE_PROPERTY_BINDING,
		// "false");
		// if (prop.toLowerCase().trim().equals("true")) {
		// //get the default namespace set is as associated to
		// VNamespace defNamespace = VNamespace.VJAX_NAMESPACE;
		// this.addNamespace(defNamespace);
		// this.setAssociatedNamespace(defNamespace);
		// return defNamespace;
		// }
		return null;
	}

	public void setAssociatedNamespace(VNamespace associatedNamespace) {
		// if it has parent that does define this namespace, ignore, otherwise
		// it must be the one who defines the names
		if (!isNamespaceDefined(associatedNamespace)) {
			this.addNamespace(associatedNamespace);
		}
		this.associatedNamespace = associatedNamespace;
		if (this.associatedNamespace.isAttributeFormDefault()) {
			for (VAttribute atr : attributes) {
				if (!(atr instanceof VNamespace)) {
					atr.setAttributeNamespace(associatedNamespace);
				}
			}
		}
	}

	private boolean isNamespaceDefined(VNamespace namespace) {
		return this.getNamespaces().contains(namespace);
	}

	/**
	 * Returns the namespace associated with the current elements. If the
	 * current elements does not define namespace, its parent element is
	 * searched for any namespace definition, otherwise, the current element
	 * overrides namespace definition of its parents
	 * 
	 * <pre>
	 * <b>The returned list of namespaces cannot be used to remove namespaces from this element
	 * It will be needless calling {@literal List.remove(java.lang.Object)} on the returned list</b>
	 * </pre>
	 * 
	 * @return
	 */
	public List<VNamespace> getNamespaces() {
		List<VNamespace> namespaces = getDefinedNamespaces();
		if (namespaces.isEmpty() && this.parent != null) {
			return parent.getNamespaces();
		}
		return namespaces;
	}

	/**
	 * Return namespaces defined only by this element
	 * 
	 * @return
	 */
	private List<VNamespace> getDefinedNamespaces() {
		List<VNamespace> namespaces = new ArrayList<VNamespace>();
		for (VAttribute att : attributes) {
			if (att instanceof VNamespace) {
				namespaces.add(((VNamespace) att));
			}
		}
		return namespaces;
	}

	/**
	 * Returns true only if the namespace is defined by this element
	 * 
	 * @param namespace
	 * @return
	 */
	public boolean definesNamespace(VNamespace namespace) {
		return getDefinedNamespaces().contains(namespace);
	}

	public void setParent(VElement parent) {
		if (this.parent != parent) {
			this.parent = parent;
		}
	}

	public List<VAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<VAttribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * This method returns the references to the children of this element It is
	 * noted that removing any child from the returned list does not affect the
	 * children of this element
	 * 
	 * @return
	 */
	public List<VElement> getChildren() {
		return new ArrayList<VElement>(children.values());
	}

	public void setChildren(List<VElement> children) {
		for (; this.indexing < children.size(); indexing++) {
			this.children.put(indexing, this);
		}
	}

	public boolean isParent(String childName) {
		try {
			return this.findChild(childName) != null;
		} catch (Exception e) {
			return false;
		}
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		if (this.content != null) {
			this.removeAttribute(this
					.getAttribute(VMarshallerConstants.NULL_PROPERTY));
		}
	}

	public String getMarkup() {
		return markup;
	}

	/**
	 * By defualt this method escapes characters that are not recommended for
	 * the xml element/attribute tags
	 * 
	 * @param markup
	 */
	public void setMarkup(String markup) {
		SetTag(markup);
	}

	private void SetTag(String elementTag) {
		this.markup = elementTag;
		// we must escape it with the correct syntax
		String escapeSequence = "";
		char c = this.markup.charAt(0);
		char[] ccs = this.markup.toCharArray();
		for (int i = 0; i < ccs.length; i++) {
			c = this.markup.charAt(i);
			if (!Character.isLetterOrDigit(c) && c != '_' && c != '-') {
				escapeSequence += (i + ":" + c + ";");
				ccs[i] = '%';
			}
		}
		if (!Utils.isNullOrEmpty(escapeSequence)) {
			// remove the last ;
			escapeSequence = escapeSequence.substring(0,
					escapeSequence.length() - 1);
			this.addAttribute(new VAttribute(ESCAPE_SEQUENCE, escapeSequence));
			String tag = new String(ccs);
			this.markup = tag.replaceAll("%", "_");
		}
	}

	public boolean isElementParent() {
		return this.hasChildren();
	}

	/**
	 * True if the element has children
	 * 
	 * @return
	 */
	public boolean isParent() {
		return this.hasChildren();
	}

	public boolean isEmpty() {
		return (this.content != null ? (Utils.isNullOrEmpty(this.content) && this.children
				.isEmpty()) : this.children.isEmpty());
	}

	public void addAttribute(VAttribute attr) {
		for (VAttribute va : attributes) {
			if (va.getName().equalsIgnoreCase(attr.getName())) {
				va.setValue(attr.getValue());
				return;
			}
		}
		if (!this.attributes.contains(attr)) {
			if (this.associatedNamespace != null) {
				attr.setAttributeNamespace(associatedNamespace);
			}
			this.attributes.add(attr);
		}
	}

	private void SetParent(VElement parent) {
		parent.doAddChild(this);
		this.setParent(parent);
	}

	/**
	 * Returns the index of the child If the child does not exist, returns -1
	 * This will in most cases return the first child encountered, if this
	 * element allows duplicate
	 * 
	 * @param child
	 * @return
	 */
	protected int getChildIndex(VElement child) {
		if (children.containsValue(child)) {
			for (int i : children.keySet()) {
				if (children.get(i).equals(child)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Adds the child element within the specified namespace defined by the
	 * parent If the namespace does not exist, it will be added to the parent.
	 * The definition of the namespace will be at the top most parent. If the
	 * child equals the parent (this==child) the child is simply associated with
	 * the namespace and the method returns.
	 * 
	 * @param child
	 *            the child element to add
	 * @param childNamespace
	 *            the namespace to associate with the child element
	 */
	public void addChild(VElement child, VNamespace childNamespace) {
		if (this == child) {
			child.setAssociatedNamespace(childNamespace);
		} else {
			this.addChild(child);
			this.doAddNamespace(childNamespace);
			child.setAssociatedNamespace(childNamespace);
		}
	}

	public void addChild(VElement child) {
		doAddChild(child);
		child.setParent(this);
	}

	public final void doAddChild(VElement child) {
		while (this.children.containsKey(indexing)) {
			// then copy the value
			indexing++;
		}
		if (!this.children.containsValue(child)) {
			this.children.put(indexing, child);
			indexing++;
		}
	}

	public final void addChild(VElement child, int index) {
		// does it exists
		VElement val = children.get(index);
		children.put(index, child);
		if (val != null) {
			addChild(val, index + 1);
		}
		child.setParent(this);
	}

	public void addChildAsFirst(VElement child) {
		this.addChild(child, 0);
	}

	/**
	 * Returns an immediate child of this element
	 * 
	 * @param name
	 * @return
	 */
	public VElement findImmediateChild(String name) {
		for (VElement elem : this.children.values()) {
			if (elem.getMarkup().equalsIgnoreCase(name)) {
				return elem;
			}
		}
		return null;
	}

	/**
	 * Returns an immediate child of this element
	 * 
	 * @param name
	 * @return
	 */
	public VElement findImmediateChild(String name, VAttribute at) {
		for (VElement elem : this.children.values()) {
			if (elem.getMarkup().equalsIgnoreCase(name)) {
				List<VAttribute> atts = elem.getAttributes();
				if (atts.contains(at)) {
					return elem;
				}
			}
		}
		return null;
	}

	public boolean hasAttributes() {
		return !this.attributes.isEmpty();
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	/**
	 * Returns any child, whether grandchild or not of this element
	 * 
	 * @param name
	 *            the name tag of the element
	 * @return null if no child is found with the specified name
	 */
	public VElement findChild(String name) {
		for (VElement elem : this.children.values()) {
			if (elem.getMarkup().equalsIgnoreCase(name)) {
				return elem;
			} else if (elem.hasChildren()) {
				VElement e = elem.findChild(name);
				if (e != null) {
					return e;
				}
			}
		}
		return null;
	}

	/**
	 * Returns any child, whether grandchild or not of this element
	 * 
	 * @param name
	 * @return
	 */
	public VElement findChild(String name, VAttribute at) {
		for (VElement elem : this.children.values()) {
			if (elem.getMarkup().equalsIgnoreCase(name)) {
				List<VAttribute> atts = elem.getAttributes();
				if (atts.contains(at)) {
					return elem;
				}
			} else if (elem.hasChildren()) {
				VElement e = elem.findChild(name, at);
				if (e != null) {
					return e;
				}
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final VElement other = (VElement) obj;
		if ((this.markup == null) ? (other.markup != null) : !this.markup
				.equals(other.markup)) {
			return false;
		}
		if ((this.content == null) ? (other.content != null) : !this.content
				.equals(other.content)) {
			return false;
		}
		if (this.attributes != other.attributes
				&& (this.attributes == null || !this.attributes
						.equals(other.attributes))) {
			return false;
		}
		if (this.children != other.children
				&& (this.children == null || !this.children
						.equals(other.children))) {
			return false;
		}
		if (this.empty != other.empty) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 83 * hash + (this.markup != null ? this.markup.hashCode() : 0);
		hash = 83 * hash + (this.content != null ? this.content.hashCode() : 0);
		hash = 83 * hash
				+ (this.attributes != null ? this.attributes.hashCode() : 0);
		hash = 83 * hash
				+ (this.children != null ? this.children.hashCode() : 0);
		hash = 83 * hash + (this.empty ? 1 : 0);
		return hash;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	private static String toString(VElement element, int tab) {
		String str = "";
		// write comments if we have
		if (element.comment != null) {
			String vTab = VElement.gTab(tab);
			String[] comments = element.getComment().split("\n");
			if (comments.length > 1) {
				for (String s : comments) {
					str += "\n";
					str += vTab;
					str += "<!--";
					str += s;
					str += "-->";
				}
			} else {
				str += "\n";
				str += vTab;
				str += "<!--";
				str += element.comment;
				str += "-->";
			}
		}
		// write markup
		if (element.markup == null) {
			throw new IllegalArgumentException("Markup is null");
		}
		str += "\n";
		str += VElement.gTab(tab);
		str += "<";
		// do we have a namespace
		VNamespace nm = element.getAssociatedNamespace();
		if (nm != null && nm.isElementFormDefault()) {
			str += nm.getPrefix() + ":" + element.markup;
		} else {
			str += element.markup;
		}
		if (element.definesNamespace(nm)
				&& nm.isIncludeFormDefaultAttributeOnFalse()) {
			// then add the attributes of namespace
			VAttribute elemForm = nm.getElementFormDefaultAttribute();
			VAttribute attrForm = nm.getAttributeFormDefaultAttribute();
			str += " ";
			str += elemForm;
			str += " ";
			str += attrForm;
		}
		if (element.hasAttributes()) {
			for (VAttribute a : element.attributes) {
				str += " ";
				str += a;
			}
		}
		if (element.isEmpty()) {
			str += "/>";
		} else {
			// close mark
			str += ">";
			// then write contents and children
			if (element.content != null) {
				String[] conts = element.content.split("\n");
				// tabify it appropriately
				if (conts.length > 1) {
					String vTab = VElement.gTab(tab);
					str += "\n  " + vTab;
					for (String s : conts) {
						str += s;
						str += "\n";
						str += (vTab + "  ");
					}
					// remove the last two spaces
					str = str.substring(0, str.length() - 2);
				} else {
					str += element.content;
				}
			}
			if (element.hasChildren()) {
				List<Integer> keys = new ArrayList<Integer>(
						element.children.keySet());
				Collections.sort(keys);
				for (int indx : keys) {
					VElement elem = element.children.get(indx);
					str += toString(elem, tab + 1);
				}
				str += "\n";
				str += VElement.gTab(tab);
			}
			str += "</";
			if (nm != null && nm.isElementFormDefault()) {
				str += nm.getPrefix() + ":" + element.markup;
			} else {
				str += element.markup;
			}
			str += ">";
		}
		return str;
	}

	protected static String gTab(int tabs) {
		String s = "";
		for (int i = 0; i < tabs; i++) {
			s += "   ";
		}
		return s;
	}

	@Override
	public String toString() {
		String mk = this.getMarkup();
		String str = (mk.charAt(0) + "").toUpperCase();
		boolean isDigit = false;
		boolean cap = false;
		for (int i = 1; i < mk.length(); i++) {
			if (Character.isDigit(mk.charAt(i))) {
				if (!isDigit) {
					str += " ";
					isDigit = true;
				}
			} else if ((mk.charAt(i) + "").toUpperCase().equals(
					mk.charAt(i) + "")) {
				if (!cap && i != 1) {
					str += " ";
					cap = true;
				}
			} else {
				isDigit = false;
				cap = false;
			}

			str += mk.charAt(i);
		}
		return str;
	}

	String elemString() {
		return VElement.toString(this, 0);
	}

	public String toXmlString() {
		return elemString();
	}

	public VAttribute getAttribute(String string) {
		for (VAttribute a : this.attributes) {
			if (a.getName().equalsIgnoreCase(string)) {
				return a;
			}
		}
		return null;
	}

	public void removeChild(VElement elem) {
		if (elem != null) {
			if (this.hasChildren()) {
				int ss = children.size();
				for (int i = 0; i < ss; i++) {
					VElement c = children.get(i);
					if (c == elem) {
						children.remove(i);
						elem.parent = null; // set parent to null
						return;
					} else {
						c.removeChild(elem);
					}
				}
			}
		}
	}

	public void removeChild(String name) {
		removeChild(getChild(name));
	}

	public void removeAttribute(VAttribute a) {
		this.attributes.remove(a);
	}

	/**
	 * Returns the child element with parent hierarchy specified by the markups
	 * if no markup is specified, returns this element. The first markup in the
	 * array represents a child of this element, and subsequent markups
	 * represents a child of its parent.
	 * 
	 * @param markups
	 * @return
	 */
	public VElement getChild(String... markups) {
		if (markups == null || markups.length == 0) {
			return this;
		}
		String str = markups[0];
		VElement elem = new VElement(str);
		for (VElement ve : this.children.values()) {
			if (ve.getMarkup().equals(elem.getMarkup())) {
				if (markups.length == 1) {
					return ve;
				}
				if (ve.hasChildren()) {
					String[] nextMarkups = new String[markups.length - 1];
					System.arraycopy(markups, 1, nextMarkups, 0,
							nextMarkups.length);
					return ve.getChild(nextMarkups);
				}
			}
		}
		return null;
	}

	/**
	 * Returns the child element with parent hierarchy specified by the markups
	 * if no markup is specified, returns this element. The first markup in the
	 * array represents a child of this element, and subsequent markups
	 * represents a child of its parent.
	 * 
	 * @param markups
	 * @return
	 */
	public VElement getChild(boolean mustHaveNoChildren, String... markups) {
		if (markups == null || markups.length == 0) {
			return this;
		}
		String str = markups[0];
		VElement elem = new VElement(str);
		for (VElement ve : this.children.values()) {
			if (ve.getMarkup().equals(elem.getMarkup())) {
				if (markups.length == 1 && mustHaveNoChildren
						&& !ve.hasChildren()) {
					return ve;
				} else if (ve.hasChildren()) {
					String[] nextMarkups = new String[markups.length - 1];
					System.arraycopy(markups, 1, nextMarkups, 0,
							nextMarkups.length);
					return ve.getChild(nextMarkups);
				}
			}
		}
		return null;
	}

	/**
	 * Returns the first child that has an attribute equal to the specified
	 * attribute
	 * 
	 * @param at
	 * @return
	 */
	public VElement findChild(VAttribute at) {
		for (VElement ee : this.children.values()) {
			for (VAttribute a : ee.attributes) {
				if (a.equals(at)) {
					return ee;

				}
			}
		}
		throw new NoSuchElementException("No such child: Attribute=" + at);
	}

	@Override
	public VElement clone() throws CloneNotSupportedException {
		VElement elem = (VElement) super.clone();
		return new VElement(elem);
	}

	public boolean hasChild(String markup) {
		for (VElement e : getChildren()) {
			if (e.getMarkup().equals(markup)) {
				return true;
			}
		}
		return false;
	}

	public List<VElement> getChildren(String markup) {
		List<VElement> el = getChildren();
		for (ListIterator<VElement> it = el.listIterator(); it.hasNext();) {
			VElement e = it.next();
			if (!e.getMarkup().equals(markup)) {
				it.remove();
			}
		}
		return el;
	}

	public List<VElement> getChildren(String markup, String childMarkup) {
		List<VElement> el = getChildren();
		for (ListIterator<VElement> it = el.listIterator(); it.hasNext();) {
			VElement e = it.next();
			if (!e.getMarkup().equals(markup)) {
				it.remove();
			} else {
				try {
					VElement c = e.getChild(childMarkup);
					if (c == null) {
						it.remove();
					}
				} catch (Exception ex) {
					it.remove();
				}
			}
		}
		return el;
	}

	public List<VElement> getChildren(String markup, VAttribute a) {
		List<VElement> el = getChildren();
		for (ListIterator<VElement> it = el.listIterator(); it.hasNext();) {
			VElement e = it.next();
			if (!e.getMarkup().equals(markup)) {
				it.remove();
			} else {
				try {
					if (!e.hasAttributes()) {
						it.remove();
					} else {
						if (!e.getAttributes().contains(a)) {
							it.remove();
						}
					}
				} catch (Exception ex) {
					it.remove();
				}
			}
		}
		return el;
	}

	public VElement getChild(String name, int index) {
		if (index >= this.children.size()) {
			throw new IndexOutOfBoundsException("No such element at index: "
					+ index);
		}
		VElement e = new VElement(name);
		int i = 0;
		for (VElement ee : this.children.values()) {
			if (e.getMarkup().equals(ee.getMarkup()) && i == index) {
				return ee;
			}
			i++;
		}
		throw new NoSuchElementException("No such child: " + name
				+ " at index: " + index);
	}
}