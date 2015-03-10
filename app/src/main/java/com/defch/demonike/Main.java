package com.defch.demonike;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.defch.demonike.ui.IndicatorView;
import com.pubnub.api.Callback;

import org.json.JSONException;
import org.json.JSONObject;


public class Main extends ActionBarActivity implements SensorEventListener {

    private static final String TAG = Main.class.getSimpleName();
    private static final String CHANNEL_NAME = "dataChannel";

    private long lastUpdate = 0;
    private float posX, posY, posZ;

    private MainApplication application;
    private JSONObject json;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private IndicatorView indicatorY, indicatorX, indicatorZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        application = MainApplication.getInstance();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);

        indicatorX = (IndicatorView)findViewById(R.id.indicatorX);
        indicatorY = (IndicatorView)findViewById(R.id.indicatorY);
        indicatorZ = (IndicatorView)findViewById(R.id.indicatorZ);
        indicatorX.setLabel("X");
        indicatorY.setLabel("Y");
        indicatorZ.setLabel("Z");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - posX - posY - posZ)/ diffTime * 10000;
                Log.i(TAG, "speed: " + speed);
                json = new JSONObject();
                posX = x;
                indicatorX.moveChangedListener(posX);
                posY = y;
                indicatorY.moveChangedListener(posY);
                posZ = z;
                indicatorZ.moveChangedListener(posZ);
                Log.i(TAG, "indicators X:" + posX +" - Y:" + posY + " - Z:" +posZ);
                try {
                    json.put("data", "x:" + posX + " y:" + posY + " z:" + posZ);
                    application.getPubnub().publish(CHANNEL_NAME, json, callback);
                }catch(JSONException e) {
                    Log.e(TAG, "ERROR put data in json");
                }
            }
        }
    }

    private Callback callback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            super.successCallback(channel, message);
            if(channel.equalsIgnoreCase(CHANNEL_NAME)) {
                Log.i(TAG, "success data send");
            }
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            createDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.dialog_title));
        dialog.setMessage(getResources().getString(R.string.dialog_msg));
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
