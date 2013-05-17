package com.variance.vjax.android.converter.impl;

import java.util.Calendar;

import com.variance.vjax.android.converter.Converter;

/**
*
* @author marembo
*/
public class CalendarConverter implements Converter<Calendar, Long> {

 @Override
 public Long convertFrom(Calendar value) {
   return value.getTimeInMillis();
 }

 @Override
 public Calendar convertTo(Long value) {
   Calendar now = Calendar.getInstance();
   now.setTimeInMillis(value);
   return now;
 }
}
