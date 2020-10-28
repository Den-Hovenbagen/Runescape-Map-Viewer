package com.runescape.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JOptionPane;

import com.runescape.MapViewer;

public class CacheDownloader {

	private static final String CACHE_URL = "https://dl.dropboxusercontent.com/s/chwdyr4dp9co575/cache.zip";
	private static final String CACHE_NAME = "cache.zip";

	public static void initialize(MapViewer instance) {
		try {
			File file = new File(FileUtils.findcachedir() + "version.txt");

			if(!file.exists()) {
				/**
				 * Download latest cache
				 */
				download(instance);

				/**
				 * Unzip the downloaded cache file
				 */
				FileUtils.decompressZip(FileUtils.findcachedir() + File.separator + CACHE_NAME, FileUtils.findcachedir(), true);
			}
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Cache could not be downloaded.\nPlease try again later.");
		}
	}

	private static void download(MapViewer instance) throws Exception {		
		URL url = new URL(CACHE_URL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.addRequestProperty("User-Agent", "Mozilla/4.76");
		int responseCode = httpConn.getResponseCode();

		/**
		 * Always check HTTP response code first
		 */
		if (responseCode == HttpURLConnection.HTTP_OK) {

			/**
			 * Opens input stream from the HTTP connection
			 */
			InputStream inputStream = httpConn.getInputStream();
			String saveFilePath = FileUtils.findcachedir() + File.separator + CACHE_NAME;

			/**
			 * Opens an output stream to save into file
			 */
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[4096];
			long startTime = System.currentTimeMillis();
			int downloaded = 0;
			long numWritten = 0;
			int length = httpConn.getContentLength();
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
				numWritten += bytesRead;
				downloaded += bytesRead;
				int percentage = (int)(((double)numWritten / (double)length) * 100D);
				int downloadSpeed = (int) ((downloaded / 1024) / (1 + ((System.currentTimeMillis() - startTime) / 1000)));

				instance.drawLoadingText(percentage, "Downloading cache "+percentage+"% @ "+downloadSpeed+"Kb/s");
			}

			outputStream.close();
			inputStream.close();

		} else {
			System.out.println("Cache host replied HTTP code: " + responseCode);
		}
		httpConn.disconnect();
	}
}
