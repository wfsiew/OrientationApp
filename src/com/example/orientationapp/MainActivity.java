package com.example.orientationapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	public void btnStart_click(View v) {
		startService(new Intent(MainActivity.this, OrientationService.class));
	}
	
	public void btnStop_click(View v) {
		stopService(new Intent(MainActivity.this, OrientationService.class));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
}
