/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.variance.vjax.android.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If true, then implies that the array has been parented, and its array
 * component are children of the element with field markup.
 * 
 * @author marembo
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ArrayParented {
	String componentMarkup() default "";
}
