package com.variance.mimiprotect.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class WidgetTextView extends TextView {

	public WidgetTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WidgetTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WidgetTextView(Context context) {
		super(context);
	}

	public void setDrawableLeft(Bitmap image) {
		BitmapDrawable drawable = new BitmapDrawable(image);
		setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
	}

	public void setDrawableTop(Bitmap image) {
		BitmapDrawable drawable = new BitmapDrawable(image);
		setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
	}

	public void setDrawableRight(Bitmap image) {
		BitmapDrawable drawable = new BitmapDrawable(image);
		setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
	}

	public void setDrawableBottom(Bitmap image) {
		BitmapDrawable drawable = new BitmapDrawable(image);
		setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
	}
}
