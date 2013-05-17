package com.variance.mimiprotect.widget;

import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.contacts.Contact;
import com.variance.mimiprotect.ui.SplashScreenActivity;
import com.variance.mimiprotect.ui.contact.task.FindContactTask;
import com.variance.mimiprotect.util.Settings;

public class MimiProtectAppWidgetProvider extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int N = appWidgetIds.length;

		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			// Create an Intent to launch ExampleActivity
			Intent mimiConnectIntent = new Intent(context,
					SplashScreenActivity.class);
			PendingIntent mimiConnectPendingIntent = PendingIntent.getActivity(
					context, 0, mimiConnectIntent, 0);
			Intent phoneBookIntent = new Intent(context,
					MimiProtectWidgetPersonalPhonebookActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					phoneBookIntent, 0);
			Intent livelinkIntent = new Intent(context,
					MimiProtectWidgetLivelinkActivity.class);
			PendingIntent livelinkPendingIntent = PendingIntent.getActivity(
					context, 0, livelinkIntent, 0);
			Intent searchPhonebookIntent = new Intent(context,
					MimiProtectWidgetSearchActivity.class);
			PendingIntent searchPhonebookPendingIntent = PendingIntent
					.getActivity(context, 0, searchPhonebookIntent, 0);
			Intent newContactIntent = new Intent(context,
					MimiProtectWidgetNewContactActivity.class);
			PendingIntent newContactPendingIntent = PendingIntent.getActivity(
					context, 0, newContactIntent, 0);
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.mimi_connect_widget);
			views.setOnClickPendingIntent(R.id.widgetMimiProtect,
					mimiConnectPendingIntent);
			views.setOnClickPendingIntent(R.id.widgetPhonebook, pendingIntent);
			views.setOnClickPendingIntent(R.id.widgetTxtSearchBox,
					searchPhonebookPendingIntent);
			views.setOnClickPendingIntent(R.id.widgetLivelink,
					livelinkPendingIntent);
			views.setOnClickPendingIntent(R.id.widgetNewContact,
					newContactPendingIntent);
			FastDialContacts fastDials = Settings.getFastDialContacts(context);
			if (fastDials == null || fastDials.getFastDials().isEmpty()) {
				// set the fast dial to gone
				views.setViewVisibility(R.id.widgetFirstDialPanel, View.GONE);
			} else {
				Log.e("fastDials: widget-size", fastDials.getFastDials().size()
						+ " contacts");
				// then we add only three contacts.
				List<Contact> contacts = fastDials.getFastDials();
				// obviously we have at least one fast contact.
				Contact c0 = contacts.get(0);
				Contact tmpC0 = FindContactTask.findContact(context,
						c0.getId(), false);
				// we add set it if we have connection
				if (tmpC0 != null) {
					c0 = tmpC0;
				}
				addCallIntent(context, c0, views, R.id.widgetCall0);
				addTextIntent(context, c0, views, R.id.widgetText0);
				// get the text view
				views.setCharSequence(R.id.widgetTxtAddView0, "setText",
						c0.getName());
				if (contacts.size() > 1) {
					Contact c1 = contacts.get(1);
					Contact tmpC1 = FindContactTask.findContact(context,
							c1.getId(), false);// we add set it if we have
												// connection
					if (tmpC1 != null) {
						c1 = tmpC1;
					}
					addCallIntent(context, c1, views, R.id.widgetCall1);
					addTextIntent(context, c1, views, R.id.widgetText1);
					views.setCharSequence(R.id.widgetTxtAddView1, "setText",
							c1.getName());
				} else {
					// set the second contact and third to gone
					views.setViewVisibility(R.id.widgetFirstDial1, View.GONE);
					views.setViewVisibility(R.id.widgetFirstDial2, View.GONE);
				}
				if (contacts.size() > 2) {
					Contact c2 = contacts.get(2);
					Log.e("fastDials: widget-3 contact-adding..", c2.getName()
							+ " contacts");
					Contact tmpC2 = FindContactTask.findContact(context,
							c2.getId(), false);// we add set it if we have
												// connection
					if (tmpC2 != null) {
						c2 = tmpC2;
					}
					addCallIntent(context, c2, views, R.id.widgetCall2);
					addTextIntent(context, c2, views, R.id.widgetText2);
					views.setCharSequence(R.id.widgetTxtAddView2, "setText",
							c2.getName());
				} else {
					views.setViewVisibility(R.id.widgetFirstDial2, View.GONE);
				}
			}
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	private void addCallIntent(Context context, Contact contact,
			RemoteViews views, int viewId) {
		if (contact != null && contact.getPhones() != null
				&& contact.getPhones().length > 0) {
			Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ contact.getPhones()[0]));
			PendingIntent newPendingIntent = PendingIntent.getActivity(context,
					0, callIntent, 0);
			views.setOnClickPendingIntent(viewId, newPendingIntent);
		}
	}

	private void addTextIntent(Context context, Contact contact,
			RemoteViews views, int viewId) {
		if (contact != null && contact.getPhones() != null
				&& contact.getPhones().length > 0) {
			Intent textIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
					+ contact.getPhones()[0]));
			PendingIntent newPendingIntent = PendingIntent.getActivity(context,
					0, textIntent, 0);
			views.setOnClickPendingIntent(viewId, newPendingIntent);
		}
	}
}
