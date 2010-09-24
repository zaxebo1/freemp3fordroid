package org.freemp3droid;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.os.Environment;


public class Util {
	public static final String DATE_STRING="yyyy-MM-dd-HH_mm_ss";
	public static String createPath(String fileType,String suffix,Context ctx)
	{
		long dateTaken = System.currentTimeMillis();   
		File filesDir = getStorageDir(ctx);
		String filesDirPath = filesDir.getAbsolutePath();
		filesDir.mkdirs();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_STRING);
		Date date = new Date(dateTaken);
		String filepart = dateFormat.format(date);
		String filename = filesDirPath + "/" + filepart + fileType + suffix;
		return filename;
	}
	
	//takes /mnt/sdcard/1/2/3/whatever.wav and returns /mnt/sdcard/convdir/whatever.mp3
	public static String createMP3File(String orig,Context ctx)
	{
		File filesDir = getStorageDir(ctx);
		String filesDirPath = filesDir.getAbsolutePath();
		String origFile = orig.substring(orig.lastIndexOf("/"));
		origFile = origFile.substring(0, origFile.lastIndexOf("."));
		String filename = filesDirPath + "/" + origFile + ".mp3";
		return filename;
	}
	public static File getStorageDir(Context ctx) {
		String filesDirPath = Environment.getExternalStorageDirectory().toString() +
		"/" + ctx.getResources().getString(R.string.convdir);
		
		File ret = new File(filesDirPath);
		if(!ret.exists()) {
			ret.mkdirs();
		}
		return ret;
	}
	
	public static ArrayList<File> listFiles(Context ctx) {
		ArrayList<File> ret = new ArrayList<File>();
		File filesDir = getStorageDir(ctx);
		File[] list = filesDir.listFiles();
		for(File f: list) {
			if(f.getAbsolutePath().endsWith(".mp3")) {
				ret.add(f);
			}
		}
		return ret;
	}
}