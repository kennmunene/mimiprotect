package com.variance.mimiprotect.util;

public enum Month {
	JAN("January"), FEB("February"), MAR("March"), APR("April"), MAY("May"), JUN(
			"June"), JUL("July"), AUG("August"), SEP("September"), OCT(
			"October"), NOV("November"), DEC("December");
	private String name;

	private Month(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static Month getInstance(int ordinal){
		for(Month m : values()){
			if(m.ordinal()==ordinal){
				return m;
			}
		}
		return null;
	}
}
