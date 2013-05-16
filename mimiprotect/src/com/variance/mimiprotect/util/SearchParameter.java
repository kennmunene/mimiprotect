package com.variance.mimiprotect.util;

import android.util.FloatMath;
import android.util.Log;

public class SearchParameter {
	private String searchTerm;
	private int currentPage;
	private int maxResult;
	private int maxRecords;

	public SearchParameter(String searchTerm, int currentPage, int maxResult,
			int maxRecords) {
		super();
		this.searchTerm = searchTerm;
		this.currentPage = currentPage;
		this.maxResult = maxResult;
		this.maxRecords = maxRecords;
	}

	public SearchParameter(String searchTerm, int currentPage, int maxResult) {
		super();
		this.searchTerm = searchTerm;
		this.currentPage = currentPage;
		this.maxResult = maxResult;
		this.maxRecords = -1;
	}

	public SearchParameter(String searchTerm, int currentPage) {
		super();
		this.searchTerm = searchTerm;
		this.currentPage = currentPage;
		this.maxRecords = -1;
	}

	public SearchParameter() {
		super();
	}

	public int getMaxRecords() {
		return maxRecords;
	}

	public void setMaxRecords(int maxRecords) {
		this.maxRecords = maxRecords;
	}

	public int getMaxPage() {
		int maxPages = (int) FloatMath.ceil(((float) maxRecords) / maxResult);
		Log.i("Max Page:", maxPages + "");
		return maxPages - 1;
	}

	public int getMaxResult() {
		if (maxResult > -1 && maxResult < 3) {
			maxResult = 5;
		}
		return maxResult;
	}

	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}

	public void incrementPage() {
		int maxPages = getMaxPage();
		if (maxPages < 0 || currentPage < maxPages) {
			currentPage++;
		}
	}

	public boolean isMaxPage() {
		return (currentPage >= getMaxPage());
	}

	public void decrementPage() {
		if (this.currentPage > 0) {
			this.currentPage--;
		}
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String toString() {
		return searchTerm + ":{" + currentPage + "}:{" + maxResult + "}:{"
				+ maxRecords + "}";
	}
}
