package org.freemp3droid;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

public class MP3Service extends Service {
	static {
		System.loadLibrary("Lame");
	}
	public static native void convertMP3(String inFile,String outFile,String progFile, String stopFile,
			int sampleRate,int bitRate);

	private final IBinder mBinder = new LocalBinder();
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	public class LocalBinder extends Binder {
		MP3Service getService() {
			return MP3Service.this;
		}
	}
	public void convertMP3File(String convertFile, String mp3File, String progressFile,
			String stopFile, int sampleRate, int bitRate) {
		this.convertFile = convertFile;
		this.mp3File=mp3File;
		this.progressFile=progressFile;
		this.stopFile=stopFile;
		this.sampleRate =sampleRate;
		this.bitRate = bitRate;
		new BackgroundTask().execute();
	}
	public class BackgroundTask extends AsyncTask<String,String,String> {
		@Override
		protected String doInBackground(String... params) {
			convertMP3(convertFile, mp3File, progressFile,stopFile, sampleRate,bitRate);
			return "";
		}
	}
	String convertFile, mp3File,progressFile,stopFile;
	int sampleRate,bitRate;
}
