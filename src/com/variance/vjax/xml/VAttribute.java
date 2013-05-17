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
public class VAttribute {

	private String name;
	private String value;
	private VNamespace attributeNamespace;

	public VAttribute() {
	}

	public VAttribute(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public VAttribute(String name, String value, VNamespace attributeNamespace) {
		this.name = name;
		this.value = value;
		this.attributeNamespace = attributeNamespace;
	}

	public VNamespace getAttributeNamespace() {
		return attributeNamespace;
	}

	public void setAttributeNamespace(VNamespace attributeNamespace) {
		this.attributeNamespace = attributeNamespace;
	}

	public boolean isDefined() {
		return this.name != null && !Utils.isNullOrEmpty(this.name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isBoolean() {
		return this.value.equalsIgnoreCase("true")
				|| this.value.equalsIgnoreCase("false");
	}

	public boolean getBoolenValue() {
		return Boolean.valueOf(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final VAttribute other = (VAttribute) obj;
		if ((this.name == null) ? (other.name != null) : !this.name
				.equals(other.name)) {
			return false;
		}
		if ((this.value == null) ? (other.value != null) : !this.value
				.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 29 * hash + (this.value != null ? this.value.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		String attrName = this.getName();
		if (attributeNamespace != null) {
			if (attributeNamespace.isAttributeFormDefault()) {
				String attrPrf = attributeNamespace.getPrefix();
				if (attrPrf != null && !Utils.isNullOrEmpty(attrPrf)) {
					attrName = attrPrf + ":" + attrName;
				}
			}
		}
		return attrName + "=\"" + this.getValue() + "\"";
	}
}