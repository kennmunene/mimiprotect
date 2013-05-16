package com.variance.vjax.android.converter;

public interface Converter<T, K> {
	/**
	 * Forward conversion. from the main object to a another representation
	 * 
	 * @param value
	 * @return
	 */
	K convertFrom(T value);

	/**
	 * From the converted value to the main object
	 * 
	 * @param value
	 * @return
	 */
	T convertTo(K value);
}
