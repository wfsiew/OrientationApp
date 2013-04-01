package com.example.orientationapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;

public class OrientationService extends Service {
	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private AudioManager audioManager;
	private boolean accelerometerPresent;
	private TextToSpeech tts = null;
	private int status = AudioManager.RINGER_MODE_NORMAL;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		initSensor();
		initTTS();
		Toast.makeText(getApplicationContext(), "Orientation Service created ...", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (accelerometerPresent)
			sensorManager.unregisterListener(accelerometerListener);
		
		if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		
		status = AudioManager.RINGER_MODE_NORMAL;
		Toast.makeText(getApplicationContext(), "Orientation Service destroyed ...", Toast.LENGTH_LONG).show();
	}
	
	private void initSensor() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		
		if (sensorList.size() > 0) {
			accelerometerPresent = true;
			accelerometerSensor = sensorList.get(0);
			sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		else {
			accelerometerPresent = false;
			Toast.makeText(getApplicationContext(), "No accelerometer present!", Toast.LENGTH_LONG).show();
		}
		
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
	}
	
	private void initTTS()
	{
		tts = new TextToSpeech(this, new OnInitListener() {
			
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS)
					tts.setLanguage(Locale.US);
				
				else if (status == TextToSpeech.ERROR)
					Toast.makeText(getApplicationContext(), "Sorry! Text To Speech failed ...", Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void speakWords(String a) {
		tts.speak(a, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	@SuppressLint("SimpleDateFormat")
	private String formatDate() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
		return simpleDateFormat.format(calendar.getTime());
	}
	
	private SensorEventListener accelerometerListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			float z_value = event.values[2];
			if (z_value > 9 && z_value < 10) {
				if (status == AudioManager.RINGER_MODE_SILENT) {
					audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					status = AudioManager.RINGER_MODE_NORMAL;
					speakWords("Normal mode activated at " + formatDate());
				}
			}
			 
			else if (z_value > -10 && z_value < -9) {
				if (status == AudioManager.RINGER_MODE_NORMAL) {
					audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					status = AudioManager.RINGER_MODE_SILENT;
					speakWords("Silent mode activated at " + formatDate());
				}
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}
	};
}
