/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.variance.vjax.xml;

import com.variance.mimiprotect.util.Utils;

/**
 * 
 * @author Marembo
 */
public class VNamespace extends VAttribute {

	public static final String DEFUALT_NAMESPACE_PROPERTY_BINDING = "com.flemax.vjax.defualtnamespace";
	public static final VNamespace VJAX_NAMESPACE = new VNamespace("vjax",
			"http://www.flemax.com/2011/vjax");
	public static final VNamespace SCHEMA_NAMESPACE = new VNamespace("xsd",
			"http://www.w3.org/2001/XMLSchema");
	public static final VNamespace XINCLUDE_NAMESPACE = new VNamespace("xi",
			"http://www.w3.org/2001/XInclude");
	public static final VNamespace XINSTANCE_NAMESPACE = new VNamespace("xsi",
			"http://www.w3.org/2001/XMLSchema-instance");
	private String prefix;
	private boolean elementFormDefault;
	private boolean attributeFormDefault;
	private String version;
	private String xmlLanguage;
	private boolean includeFormDefaultAttributeOnFalse;
	private boolean hasSchema;
	private String schemaLocation;

	public VNamespace(String prefix, String uri, boolean elementFormDefault,
			boolean attributeFormDefault,
			boolean includeFormDefaultAttributeOnFalse) {
		super("xmlns", uri);
		this.prefix = prefix;
		this.elementFormDefault = elementFormDefault;
		this.attributeFormDefault = attributeFormDefault;
		this.includeFormDefaultAttributeOnFalse = includeFormDefaultAttributeOnFalse;
	}

	public VNamespace(String prefix, String uri, String schemaLocation,
			boolean hasSchema, boolean elementFormDefault,
			boolean attributeFormDefault,
			boolean includeFormDefaultAttributeOnFalse) {
		super("xmlns", uri);
		this.prefix = prefix;
		this.elementFormDefault = elementFormDefault;
		this.attributeFormDefault = attributeFormDefault;
		this.includeFormDefaultAttributeOnFalse = includeFormDefaultAttributeOnFalse;
		this.schemaLocation = schemaLocation;
		this.hasSchema = hasSchema;
	}

	public VNamespace(String prefix, String uri) {
		super("xmlns", uri);
		this.prefix = prefix;
		this.elementFormDefault = true;
		this.attributeFormDefault = false;
	}

	public VNamespace() {
		this.elementFormDefault = true;
		this.attributeFormDefault = false;
	}

	public boolean isHasSchema() {
		return hasSchema;
	}

	public void setHasSchema(boolean hasSchema) {
		this.hasSchema = hasSchema;
	}

	public String getSchemaLocation() {
		return schemaLocation;
	}

	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	public boolean isIncludeFormDefaultAttributeOnFalse() {
		return includeFormDefaultAttributeOnFalse;
	}

	public void setIncludeFormDefaultAttributeOnFalse(
			boolean includeFormDefaultAttributeOnFalse) {
		this.includeFormDefaultAttributeOnFalse = includeFormDefaultAttributeOnFalse;
	}

	public boolean isAttributeFormDefault() {
		return attributeFormDefault && prefix != null
				&& !Utils.isNullOrEmpty(prefix);
	}

	public void setAttributeFormDefault(boolean attributeFormQualified) {
		this.attributeFormDefault = attributeFormQualified;
	}

	public boolean isElementFormDefault() {
		return elementFormDefault && prefix != null
				&& !Utils.isNullOrEmpty(prefix);
	}

	public void setElementFormDefault(boolean elementFormQualified) {
		this.elementFormDefault = elementFormQualified;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public VAttribute getElementFormDefaultAttribute() {
		VAttribute el = new VAttribute("elementFormDefault",
				(this.elementFormDefault ? "qualified" : "unqualified"));
		el.setAttributeNamespace(this);
		return el;
	}

	public VAttribute getAttributeFormDefaultAttribute() {
		VAttribute atr = new VAttribute("attributeFormDefault",
				(this.attributeFormDefault ? "qualified" : "unqualified"));
		atr.setAttributeNamespace(this);
		return atr;
	}

	@Override
	public String getName() {
		String name = super.getName();
		if (!name.contains(":")) {
			if (prefix != null && !Utils.isNullOrEmpty(prefix)) {
				return name + ":" + this.prefix;
			}
		} else {
			String prf = name.substring(name.indexOf(":") + 1);
			if (prefix != null && !Utils.isNullOrEmpty(prefix)
					&& !prefix.equals(prf)) {
				return name.substring(0, name.indexOf(":")) + prefix;
			}
		}
		return name;
	}

	@Override
	public String toString() {
		return this.getName() + "=\"" + this.getValue() + "\"";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final VNamespace other = (VNamespace) obj;
		if (!super.equals(other)) {
			return false;
		}
		if ((this.prefix == null) ? (other.prefix != null) : !this.prefix
				.equals(other.prefix)) {
			return false;
		}
		if (this.elementFormDefault != other.elementFormDefault) {
			return false;
		}
		if (this.attributeFormDefault != other.attributeFormDefault) {
			return false;
		}
		if ((this.version == null) ? (other.version != null) : !this.version
				.equals(other.version)) {
			return false;
		}
		if ((this.xmlLanguage == null) ? (other.xmlLanguage != null)
				: !this.xmlLanguage.equals(other.xmlLanguage)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 17 * hash + (this.prefix != null ? this.prefix.hashCode() : 0);
		hash = 17 * hash + ((this.elementFormDefault) ? 1 : 0);
		hash = 17 * hash + ((this.attributeFormDefault) ? 1 : 0);
		hash = 17 * hash + (this.version != null ? this.version.hashCode() : 0);
		hash = 17 * hash
				+ (this.xmlLanguage != null ? this.xmlLanguage.hashCode() : 0);
		return hash;
	}
}
