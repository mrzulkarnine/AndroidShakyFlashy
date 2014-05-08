package com.example.shakyflashy;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity implements SensorEventListener{

	Sensor accelerometer, proximity;
	SensorManager sm;
	TextView acceleration;
	boolean hasFlash = true;
	boolean flashIsOn = false;

	long lastUpdate = 0;
	float last_x = 0, last_y = 0, last_z = 0;
	int SHAKE_THRESHOLD = 30;
	TextView displayNo;
	SeekBar seekbarShake;
	Camera camera;
	Parameters parameters;
	
	boolean flashIsturnOn = false;
	boolean flashStatus;
	
	float x;
	float y;
	float z;
	float p, pComp;
	long currtime = 0;
	long nexttime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//set to potrait
		acceleration = (TextView) findViewById(R.id.textView1);
		displayNo = (TextView) findViewById(R.id.textView4);
		seekbarShake = (SeekBar) findViewById(R.id.seekBar1);
		seekbarShake.setProgress(30);
	    hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	    if(hasFlash == false)
	    {  	
			 acceleration.setText("FlashLight Not Detected" );	
			 
	    }
	    if(hasFlash == true)
	    {
	    	
	    	acceleration.setText("Shake Me To Turn On Light" );	
	    	sm=(SensorManager)getSystemService(SENSOR_SERVICE);
			accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sm.registerListener(this,  accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			proximity = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			sm.registerListener(this,  proximity, SensorManager.SENSOR_DELAY_NORMAL);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    	
	    }	
	    seekbarShake.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressChanged = 0;
        
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
                displayNo.setText("Sensitivity Level : "+progressChanged);
                SHAKE_THRESHOLD = progressChanged;
                
            }
 
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
 
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
		
		
		
		
	
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		synchronized (this) {
	        switch (event.sensor.getType()){
	            case Sensor.TYPE_ACCELEROMETER:
	            			
	            	x =  event.values[0];
	        		y = event.values[1];
	        		z = event.values[2];
	        		
	        		flashStatus = checkShake(x, y, z);	
	        		if (flashStatus == true )
	        			{
	        				
	        				flashOn();
	        				acceleration.setText("FlashLight ON!\nTouch My Head To Turn OFF");
	        				flashIsturnOn = true;
	        				flashStatus = false;
	        				sm.unregisterListener(this, accelerometer);

	        			}


	            break;
	        case Sensor.TYPE_PROXIMITY:
	                p = event.values[0];
	                pComp = proximity.getMaximumRange() - (proximity.getMaximumRange() *1/4) ;
	        		if(p >= pComp  && flashIsturnOn == true)
	        		{
	        			flashOff();
	        			sm.unregisterListener(this, proximity);
	        			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	        			Intent intent = getIntent(); //restart the activity back
	        	        finish();
	        	        startActivity(intent); 
		        	    overridePendingTransition(0, 0);
		        	    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

	        	        
	        		}
	        break;
	 
	        }
	    }
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}


	private void flashOn()
	{

	     camera = Camera.open(); 
		 parameters = camera.getParameters();
		 parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		 camera.setParameters(parameters);
		 //
		
	}
	private void flashOff()
	{
		
		  parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		  camera.setParameters(parameters);
		  camera.release();
		  camera = null;
	}
	
	private boolean checkShake(float newX, float newY, float newZ)
	{
		float speed = Math.abs(newX) + Math.abs(newY) + Math.abs(newZ);
		if (speed > SHAKE_THRESHOLD) {
				return true;}
				else {
					return false;
		
		}
		
	}
	
	
	 @Override
	 protected void onStart() {
	        super.onStart();
	        // The activity is about to become visible.
	 }
	 
	 @Override
	  protected void onResume() {
	        super.onResume();
	        // The activity has become visible (it is now "resumed").
	 }
	 
	 @Override
	 protected void onPause() {
	        super.onPause();
	        // Another activity is taking focus (this activity is about to be "paused").
	 }
	 
	 @Override
	 protected void onStop() {
	        super.onStop();
	        // The activity is no longer visible (it is now "stopped")
	 }
	 
	 @Override
	 protected void onDestroy() {
	        super.onDestroy();
	        // The activity is about to be destroyed.
	 }



}
