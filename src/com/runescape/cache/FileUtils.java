package com.runescape.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.runescape.Configuration;

public final class FileUtils {

	public static String findcachedir() {
		final File cacheDirectory = new File(Configuration.CACHE_DIRECTORY);
		if (!cacheDirectory.exists()) {
			cacheDirectory.mkdir();
		}
		return Configuration.CACHE_DIRECTORY;
	}
	
	public static byte[] decompressGzip(byte[] data) {
		try {
			if (data == null)
				return null;
			GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(data));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = gzipInputStream.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}
			gzipInputStream.close();
			bos.close();
			return bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void decompressZip(String zipFile, String outputFolder, boolean deleteAfter) {
		byte[] buffer = new byte[1024];

		try {
			/**
			 * Create output directory is not exists
			 */
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			/**
			 * Get the zip file content
			 */
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));

			/**
			 * Get the zipped file list entry
			 */
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				/**
				 * Create all non exists folders
				 * Else you will hit FileNotFoundException for compressed folder
				 */
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			if (deleteAfter) {
				new File(zipFile).delete();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
