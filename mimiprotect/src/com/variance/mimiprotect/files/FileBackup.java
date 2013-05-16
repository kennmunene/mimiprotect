package com.variance.mimiprotect.files;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.variance.mimiprotect.request.HttpRequestManager;
import com.variance.mimiprotect.util.DataParser;
import com.variance.mimiprotect.util.Settings;
import com.variance.mimiprotect.util.UploadedContent;

/**
 * 
 * @author kenn
 */
public class FileBackup {

	private String backupFilesURL = Settings.getServletUploadURL();
	private String boundary = "----------V2ymHFg03ehbqgZCaKO6jy";
	private String endBoundary = "\r\n--" + boundary + "--\r\n";
	private String sessionID = "";
	private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	private String downloadFileURL = Settings.getFileDownloadURL();

	public FileBackup() {
		sessionID = Settings.getSessionID();
	}

	public void restoreFile(String fileID, String destinationPath) {
		HttpURLConnection connection;
		try {
			nameValuePairs.clear();
			nameValuePairs.add(new BasicNameValuePair("action", "download"));
			nameValuePairs.add(new BasicNameValuePair("sessionID", Settings
					.getSessionID()));
			String downloadUrl = downloadFileURL
					.concat("?action=download&sessionID="
							+ Settings.getSessionID() + "&fileID=" + fileID);
			URL url = new URL(downloadUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream is = connection.getInputStream();
			File file = new File(destinationPath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			byte[] data = new byte[1024];
			int size = 0;
			while ((size = is.read(data)) > -1) {
				fos.write(data, 0, size);
			}
			is.close();
			fos.close();
			connection.disconnect();
		} catch (Exception e) {
			Log.e("File Backup", e.getMessage());
			e.printStackTrace();
		}
	}

	public String upload(final String path) {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		try {
			File file = new File(path);
			FileInputStream fileInputStream = new FileInputStream(file);
			URL url = new URL(backupFilesURL);
			connection = (HttpURLConnection) url.openConnection();
			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			// Enable POST method
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);
			connection.setRequestProperty("Connection", "Keep-Alive");
			outputStream = new DataOutputStream(connection.getOutputStream());
			String fileName = path.substring(path.lastIndexOf("/") + 1);
			Hashtable<String, String> params = new Hashtable<String, String>(3);
			String fileField = Settings.getSessionID();
			String fileType = getFileType(fileName);
			String filePath = file.getPath();
			int fileLength = (int) file.length();
			params.put("name", fileName);
			params.put("sessionID", sessionID);
			params.put("ext", "png");
			params.put("path", filePath);
			String boundaryMessage = getBoundaryMessage(boundary, params,
					fileField, fileName, fileType, fileLength);
			outputStream.write(boundaryMessage.getBytes());
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			outputStream.write(endBoundary.getBytes());
			// Responses from the server (code and message)
			BufferedReader bin = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line = "";
			String msg = "";
			while ((line = bin.readLine()) != null) {
				msg += line;
			}
			showAlert(msg);
			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
			return "Sucessfully uploaded file";
		} catch (Exception ex) {
			// Exception handling
			ex.printStackTrace();
			Log.e("Error File Backup", ex.getMessage());
			return "Error Backing up file";
		}
	}

	private void showAlert(String err) {
		Log.e("File Backup", err);
	}

	private String getBoundaryMessage(String boundary,
			Hashtable<String, String> params, String fileField,
			String fileName, String fileType, int fileLength) {
		StringBuffer res = new StringBuffer("--").append(boundary).append(
				"\r\n");
		res.append("Content-Disposition: form-data; name=\"").append(fileField)
				.append("\"; filename=\"").append(fileName).append("\"\r\n")
				.append("Content-Type: ").append(fileType)
				.append("Content-Length: ").append(fileLength)
				.append("\r\n\r\n");
		return res.toString();
	}

	private String getFileType(String fileName) {
		String fileType = "application/octet-stream";
		if (fileName.endsWith(".jpg")) {
			fileType = "image/jpeg";
		} else if (fileName.endsWith(".mp3")) {
			fileType = "audio/mp3";
		} else if (fileName.endsWith(".txt")) {
			fileType = "text/plain";
		} else if (fileName.endsWith(".pdf")) {
			fileType = "application/pdf";
		}
		return fileType;
	}

	public List<UploadedContent> getBackedUpFilesList() {
		List<UploadedContent> filesList = new ArrayList<UploadedContent>();
		try {
			String xml = HttpRequestManager.doRequest(
					Settings.getFileDownloadURL(),
					Settings.makeDownloadFileListParameters());
			Log.e("Response", xml);
			return DataParser.getUploadedContentsFrom(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filesList;
	}

}
