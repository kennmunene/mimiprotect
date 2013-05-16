package com.variance.mimiprotect.util;

/**
 * Possibly transfer data between inner classes
 * 
 * @author marembo
 * 
 */
public class ValueWrapper<T> {
	private T value;

	public ValueWrapper() {
		super();
	}

	public ValueWrapper(T value) {
		super();
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ValueWrapper [value=" + value + "]";
	}

}
