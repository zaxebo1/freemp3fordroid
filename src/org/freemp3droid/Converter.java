package org.freemp3droid;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

public class Converter extends Service {
	@Override
	public void onCreate() {
		super.onCreate();
	}

	static {
		System.loadLibrary("Lame");
	}
	private static native void convertMP3(String inFile,String outFile,String progFile,int sampleRate,int bitRate);
	private static native void kill();

	public void killConvert() {
		killedByUser=true; 
		kill();	
	}
	private final IBinder mBinder = new LocalBinder();
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	public class LocalBinder extends Binder {
		Converter getService() {
			return Converter.this;
		}
	}

	protected void startConverting(Main ctx,String convertFile,int sampleRate,int quality) {
		this.ctx=ctx;
		this.convertFile=convertFile;
		this.sampleRate = sampleRate;
		this.quality = quality;
		mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		this.killedByUser=false;
		this.progf=Util.createPath("prog", ".txt", ctx);
		this.mp3File=Util.createMP3File(convertFile, ctx);
		converter = new BackgroundConverter();
		converter.execute();
	}

	public class BackgroundConverter extends AsyncTask<String,String,String> {
		@Override
		protected String doInBackground(String... params) {
			tempSize = new File(convertFile).length();
			aic = new AudioInnerConverter();
			aic.execute();
			while(aic.getStatus() != AsyncTask.Status.FINISHED) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				publishProgress(progf);
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(!killedByUser) {
				cleanProgTxt();
				notifyComplete();
				new File(progf).delete();
				Converter.this.converter=null;
				ctx.onConversionComplete();
			}
		}
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			File pf = new File(values[0]);
			if(pf.exists()) {
				byte[] pfb = new byte[(int) pf.length()];
				try {
					new FileInputStream(values[0]).read(pfb);
					if(pfb.length > 0) {
						int progress = (int) (100 * (Double.parseDouble(new String(pfb)) / (double)tempSize));
						if(ctx.progressDialog !=null) {
							ctx.progressDialog.setProgress(progress);
						}
					}
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public class AudioInnerConverter extends AsyncTask<String,String,String> {
		@Override
		protected String doInBackground(String... params) {
			convertMP3(convertFile, mp3File, progf, Converter.this.sampleRate, Converter.this.quality); 
			return null;
		}
	}
	//sometimes the process is not killed cleanly, so clean up all prog.txt
	public void cleanProgTxt() {
		String progFile = Util.createPath("prog", ".txt", ctx);
		String progDir = progFile.substring(0,progFile.lastIndexOf("/"));
		for(File f: new File(progDir).listFiles()) {
			if(f.getName().contains("prog")) {
				f.delete();
			}
		}
	}

	public void notifyComplete() {
		int icon = android.R.drawable.ic_menu_rotate;
		CharSequence tickerText = getResources().getString(R.string.conversion_ready);
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		notification.flags=Notification.FLAG_AUTO_CANCEL;
		CharSequence contentTitle = getResources().getString(R.string.conversion_ready);
		CharSequence contentText = contentTitle;
		Intent notificationIntent = new Intent(ctx, Main.class);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
		notification.setLatestEventInfo(ctx, contentTitle, contentText, contentIntent);
		mNotificationManager.notify(NOTI_CONV_FINISHED, notification);
	}
	
	Main ctx;
	String convertFile,progf,mp3File,recFormat;
	long tempSize;
	int sampleRate,quality;
	public AudioInnerConverter aic;
	boolean killedByUser;
	BackgroundConverter converter;
	NotificationManager mNotificationManager;

	int NOTI_CONVERTING=1;
	int NOTI_CONV_FINISHED=2;
}