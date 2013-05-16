package com.variance.mimiprotect.files;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.variance.mimiprotect.R;
import com.variance.mimiprotect.util.UploadedContent;

public class FileDownloadListAdapter extends ArrayAdapter<String> {
	private Context context;
	private List<UploadedContent> uploadedContents;
	private Map<UploadedContent, Boolean> selectedContents;

	public FileDownloadListAdapter(Context context, int textViewResourceId,
			List<UploadedContent> uploadedContents) {
		super(context, textViewResourceId, getDisplayContent(uploadedContents));
		this.context = context;
		this.uploadedContents = uploadedContents;
		this.selectedContents = new HashMap<UploadedContent, Boolean>();
	}

	private static String[] getDisplayContent(
			List<UploadedContent> uploadedContents) {
		String[] cs = new String[uploadedContents.size()];
		int i = 0;
		for (UploadedContent uc : uploadedContents) {
			cs[i++] = uc.getFileName();
		}
		return cs;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final UploadedContent uc = uploadedContents.get(position);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.mimi_connect_singleselectfile,
				parent, false);
		CheckBox cb = (CheckBox) rowView.findViewById(R.id.cbSelectFile);
		cb.setText(uc.getFileName());
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					selectedContents.put(uc, isChecked);
				} else {
					selectedContents.remove(uc);
				}
			}
		});
		return rowView;
	}

	public Set<UploadedContent> getSelectedFiles() {
		return selectedContents.keySet();
	}
}
