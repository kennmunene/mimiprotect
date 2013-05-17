package com.variance.mimiprotect.livelink;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.livelink.util.LiveLinkRequest;
import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.ui.LiveLinkRequestsActivity;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.Utils;

public class LivelinkListAdapter extends ArrayAdapter<String> {

	private List<LiveLinkRequest> requests;
	private Context context;

	public LivelinkListAdapter(Context context, int textViewResourceId,
			List<LiveLinkRequest> requests) {
		super(context, textViewResourceId, toString(requests));
		this.requests = requests;
		this.context = context;
	}

	private static String[] toString(List<LiveLinkRequest> requests) {
		String[] str = new String[requests.size()];
		int i = 0;
		for (LiveLinkRequest l : requests) {
			str[i++] = l.getUserName();
		}
		return str;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(
				R.layout.mimi_connect_singlelinkrequest, parent, false);
		final LiveLinkRequest lr = requests.get(position);
		TextView messageView = (TextView) rowView
				.findViewById(R.id.requestMessage);
		TextView org = (TextView) rowView.findViewById(R.id.organization);
		if (lr.getFromUser() != null
				&& !Utils.isNullStringOrEmpty(lr.getFromUser()
						.getOrganization())) {
			org.setText(lr.getFromUser().getOrganization());
		}
		TextView title = (TextView) rowView.findViewById(R.id.title);
		if (lr.getFromUser() != null
				&& !Utils.isNullStringOrEmpty(lr.getFromUser().getTitle())) {
			title.setText(lr.getFromUser().getTitle());
		}
		TextView name = (TextView) rowView.findViewById(R.id.name);
		if (lr.getFromUser() != null
				&& !Utils.isNullStringOrEmpty(lr.getFromUser().getName())) {
			name.setText(lr.getFromUser().getName());
		}
		messageView.setText(lr.getMessage());
		Button ignore = (Button) rowView.findViewById(R.id.btnIgnoreRequest);
		ignore.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String result = HttpRequestManager.doRequest(Settings
						.getLivelinkConfirmationUrl(), Settings
						.getLivelinkIgnoredParameter(lr.getLiveLinkID()));
				Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
				if (context instanceof LiveLinkRequestsActivity) {
					((LiveLinkRequestsActivity) context).refresh();
				}
			}
		});
		Button accept = (Button) rowView.findViewById(R.id.btnAcceptRequest);
		accept.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String result = HttpRequestManager.doRequest(Settings
						.getLivelinkConfirmationUrl(), Settings
						.getLivelinkAcceptedParameter(lr.getLiveLinkID()));
				Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
				if (context instanceof LiveLinkRequestsActivity) {
					((LiveLinkRequestsActivity) context).refresh();
				}
			}
		});
		return rowView;
	}

}
