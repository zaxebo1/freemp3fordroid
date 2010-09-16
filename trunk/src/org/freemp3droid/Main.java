package org.freemp3droid;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Main extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent convIntent = new Intent(Main.this, Converter.class);
		startService(convIntent);
		bindService(convIntent, mConvertConnection, Context.BIND_AUTO_CREATE);
		initUI();
	}
	public void initUI() {
		setContentView(R.layout.main);
		layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fileChooserButton = (ImageButton) findViewById(R.id.file_chooser);
		fileChooserButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent f = new Intent(Main.this,org.freemp3droid.filechooser.FileChooser.class);
				startActivityForResult(f,0);
			}
		});
		fileList = (ListView) findViewById(R.id.file_list);
		refreshFileList(); 
	}

	private ServiceConnection mConvertConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBoundConvService = ((Converter.LocalBinder)service).getService();
			if(getIntent() !=null && getIntent().getAction()!= null &&
					getIntent().getAction().equals("org.freemp3droid.CONVERT")) {
				String convertFile = getIntent().getExtras().getString("convertFile");		
				int sampleRate =  getIntent().getExtras().getInt("sampleRate");
				int bitRate =  getIntent().getExtras().getInt("bitRate");
				progressDialog = initConvertingDialog();
				progressDialog.show();
				mBoundConvService.startConverting(Main.this,convertFile, sampleRate, bitRate);
			}
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {	}	
	};

	@Override
	protected void onPause() {
		super.onPause();
		//unbindService(mConvertConnection);
	} 
	public ProgressDialog initConvertingDialog() {
		String message = getString(R.string.prog_msg);
		ProgressDialog pDialog = new ProgressDialog(Main.this);
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pDialog.setProgress(0);
		pDialog.setTitle(getString(R.string.prog_title));
		pDialog.setMessage(message);
		pDialog.setCancelable(false);
		pDialog.setMax(100);
		pDialog.setButton(getString(R.string.convert_later), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				killConversion();
			}
		});
		return pDialog;
	}
	public void killConversion() {
		if(mBoundConvService != null) { 	
			mBoundConvService.killedByUser=true;
			mBoundConvService.killConvert();
		}
	}
	public void refreshFileList() {
		final ArrayList<File> files = Util.listFiles(this);
		fileList.setAdapter(new ArrayAdapter<File>(this,android.R.layout.simple_list_item_1,files) {
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = layoutInflater.inflate(android.R.layout.simple_list_item_1,null);
				TextView i = (TextView)v.findViewById(android.R.id.text1);
				i.setText(files.get(position).getName());
				i.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, 
						LayoutParams.WRAP_CONTENT));
				return i;
			}
		});
		fileList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long rid) {
				initFileActionMenu(files.get(pos));
			}	
		});
	}
	public void initFileActionMenu(final File inFile) {
		ListView ret = new ListView(Main.this);
		final AlertDialog d = new AlertDialog.Builder(Main.this).create();
		d.setTitle(inFile.getName());
		final Integer[] listImages = new Integer[] { 
			android.R.drawable.ic_media_play, 
			android.R.drawable.ic_menu_send, 
			android.R.drawable.ic_menu_delete
		};
		ImageAdapter ia = new ImageAdapter(this,0,listImages);
		ret.setAdapter(ia);
		ret.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,long arg3) {
				if(listImages[pos] == android.R.drawable.ic_media_play) {
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW); 
			        intent.setDataAndType(Uri.parse("file://" +inFile.getAbsolutePath()),"audio/mp3"); 
			        startActivity(intent); 
				}else if (listImages[pos] == android.R.drawable.ic_menu_send) {
					Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("audio/mp3");
					intent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file://" +inFile.getAbsolutePath()));
					startActivity(intent);
				} else if (listImages[pos] == android.R.drawable.ic_menu_delete) {
					inFile.delete();
				}
				refreshFileList();
				d.dismiss();
			}
		});
		d.setView(ret);
		d.show();
	}
	public void onConversionComplete() {
		if(progressDialog != null) {
			progressDialog.dismiss();
		}
		refreshFileList();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data !=null && data.hasExtra("chosenFile")) {
			Intent convDialog = new Intent(this,ConvertDialog.class);
			convDialog.putExtra("chosenFile", data.getExtras().getString("chosenFile"));
			startActivity(convDialog);
			finish();
		}
	}

	ImageButton fileChooserButton;
	Converter mBoundConvService;
	ProgressDialog progressDialog;
	ListView fileList;
	LayoutInflater layoutInflater;
}