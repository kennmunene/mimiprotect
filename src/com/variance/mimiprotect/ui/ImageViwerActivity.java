package com.variance.mimiprotect.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.util.Settings;

public class ImageViwerActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mimi_connect_imageviewer);
		ImageView qrViewer = (ImageView) findViewById(R.id.imageQRViewer);
		try {
			File file = new File(Settings.getQRFilepath());
			InputStream istream = new FileInputStream(file);
			Bitmap bm = BitmapFactory.decodeStream(istream);
			qrViewer.setImageBitmap(bm);
			file.deleteOnExit();
		} catch (Exception e) {
			Log.e("image viewer", e.toString());
		}
	}
}
