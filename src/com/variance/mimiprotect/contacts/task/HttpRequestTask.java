package com.variance.mimiprotect.contacts.task;

import android.content.Context;
import android.os.AsyncTask;

import com.variance.mimiprotect.ui.PersonalPhonebookActivity;

public class HttpRequestTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {
	private HttpRequestTaskListener<Params, Result> httpRequestTaskHorse;
	private String taskMessage;
	private Context context;
	private boolean executeInBackground;

	public HttpRequestTask(
			HttpRequestTaskListener<Params, Result> httpRequestTaskHorse,
			String taskMessage, Context context) {
		super();
		this.httpRequestTaskHorse = httpRequestTaskHorse;
		this.taskMessage = taskMessage;
		this.context = context;
	}

	public HttpRequestTask(
			HttpRequestTaskListener<Params, Result> httpRequestTaskHorse,
			String taskMessage) {
		super();
		this.httpRequestTaskHorse = httpRequestTaskHorse;
		this.taskMessage = taskMessage;
	}

	public HttpRequestTask() {
		super();
	}

	public void executeInBackground(Params... params) {
		executeInBackground = true;
		execute(params);
	}

	public HttpRequestTaskListener<Params, Result> getHttpRequestTaskHorse() {
		return httpRequestTaskHorse;
	}

	public void setHttpRequestTaskHorse(
			HttpRequestTaskListener<Params, Result> httpRequestTaskHorse) {
		this.httpRequestTaskHorse = httpRequestTaskHorse;
	}

	public String getTaskMessage() {
		return taskMessage;
	}

	public void setTaskMessage(String taskMessage) {
		this.taskMessage = taskMessage;
	}

	@Override
	protected void onPostExecute(Result result) {
		if (!executeInBackground) {
			PersonalPhonebookActivity.endProgress();
		}
		httpRequestTaskHorse.onTaskCompleted(result);
	}

	@Override
	protected void onPreExecute() {
		if (!executeInBackground) {
			PersonalPhonebookActivity.showProgress(taskMessage, context, this);
		}
		httpRequestTaskHorse.onTaskStarted();
	}

	@Override
	protected Result doInBackground(Params... params) {
		return httpRequestTaskHorse.doTask(params);
	}
}
