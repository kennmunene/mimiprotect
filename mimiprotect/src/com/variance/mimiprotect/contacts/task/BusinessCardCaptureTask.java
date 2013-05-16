package com.variance.mimiprotect.contacts.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import com.variance.mimiprotect.R;

public class BusinessCardCaptureTask extends AsyncTask<String, Void, Byte[]> {
	private Activity context;

	public BusinessCardCaptureTask(Activity context) {
		super();
		this.context = context;
	}

	@Override
	protected Byte[] doInBackground(String... arg0) {
		captureImage();
		return null;
	}

	public void captureImage() {
		Camera camera = Camera.open();
		Camera.Parameters params = camera.getParameters();
		camera.setParameters(params);
		Camera.PictureCallback jpgCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				try {
					Dialog d = new Dialog(context);
					d.setContentView(R.layout.mimi_connect_cameraview);
					BitmapFactory.Options opts = new BitmapFactory.Options();
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length, opts);
					TextView tv = (TextView) d.findViewById(R.id.cameraTxtView);
					ImageView i = (ImageView) d.findViewById(R.id.cameraView);
					i.setImageBitmap(bitmap);
					tv.setText("Hai" + data.length);
					d.show();
				} catch (Exception e) {
					AlertDialog.Builder alert = new AlertDialog.Builder(context);
					alert.setMessage("Exception1" + e.getMessage());
					alert.create();
					alert.show();
				}
			}

		};
		camera.takePicture(null, null, jpgCallback);
	}

}
