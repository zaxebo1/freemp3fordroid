package org.freemp3droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ConvertDialog extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
	}
	public void initUI() {
		setContentView(R.layout.convert_dialog);
		final String chosenFile = getIntent().getExtras().getString("chosenFile");
		TextView chosenFileView = (TextView)findViewById(R.id.convert_file_display);
		chosenFileView.setText(chosenFile);
		
		recSpin = (Spinner) findViewById(R.id.rec_spin);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.rec_quality, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		recSpin.setAdapter(adapter);
		
		compressSpin = (Spinner) findViewById(R.id.compress_spin);
		ArrayAdapter<CharSequence> compAdapter = ArrayAdapter.createFromResource(this, R.array.compress_quality, android.R.layout.simple_spinner_item);
		compAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		compressSpin.setAdapter(compAdapter);
		//choose 192k
		compressSpin.setSelection(6);
		
		Button convertButton = (Button)findViewById(R.id.convert_go_button);
		convertButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent convIntent = new Intent(ConvertDialog.this,Main.class);
				convIntent.setAction("org.freemp3droid.CONVERT");
				convIntent.putExtra("convertFile", chosenFile);
				convIntent.putExtra("bitRate",Integer.parseInt((String)compressSpin.getSelectedItem()));
				convIntent.putExtra("sampleRate",Integer.parseInt((String)recSpin.getSelectedItem()));
				startActivity(convIntent);
				finish();
			}
		});
	}
	
	Spinner compressSpin,recSpin;
}
