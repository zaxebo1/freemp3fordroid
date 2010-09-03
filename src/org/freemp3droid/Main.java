package org.freemp3droid;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class Main extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getIntent() !=null && getIntent().getAction().equals("org.freemp3droid.CONVERT")){
			Intent convIntent = new Intent(Main.this, MP3Service.class);
			startService(convIntent);
			bindService(convIntent, mConvertConnection, Context.BIND_AUTO_CREATE);
		} else {
			finish();
		}
	}

	private ServiceConnection mConvertConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MP3Service mp3Service = ((MP3Service.LocalBinder)service).getService();
			String convertFile = getIntent().getExtras().getString("convertFile");
			String mp3File = getIntent().getExtras().getString("mp3File");
			String progressFile = getIntent().getExtras().getString("progressFile");
			String stopFile = getIntent().getExtras().getString("stopFile");
			//need to touch the file. 1 means stop, anything else means go.
			try {
				FileOutputStream ofo = new FileOutputStream("stop.txt");
				ofo.write("0".getBytes());
				ofo.flush();
				ofo.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			int sampleRate =  getIntent().getExtras().getInt("sampleRate");
			int bitRate =  getIntent().getExtras().getInt("bitRate");
			mp3Service.convertMP3File(convertFile, mp3File, progressFile, stopFile, sampleRate, bitRate);
			Main.this.finish();
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {	}	
	};

	@Override
	protected void onPause() {
		super.onPause();
		unbindService(mConvertConnection);
	}
}