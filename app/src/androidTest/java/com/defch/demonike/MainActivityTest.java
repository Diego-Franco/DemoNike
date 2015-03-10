package com.defch.demonike;

import android.content.Intent;
import android.test.ActivityUnitTestCase;

import com.defch.demonike.ui.IndicatorView;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DiegoFranco on 3/9/15.
 */
public class MainActivityTest  extends ActivityUnitTestCase<Main> {

    private static final String CHANNEL = "datachannel";
    private Main main;
    private IndicatorView indicatorY, indicatorX, indicatorZ;

    public MainActivityTest(Class<Main> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent(getInstrumentation().getTargetContext(), Main.class);
        startActivity(intent, null, null);
        main = getActivity();
    }

    public void testLayout() {
        indicatorY = (IndicatorView)main.findViewById(R.id.indicatorY);
        assertNotNull("IndicatorY not allowed be null", indicatorY);
        indicatorX = (IndicatorView)main.findViewById(R.id.indicatorX);
        assertNotNull("IndicatorX not allowed be null", indicatorX);
        indicatorZ = (IndicatorView)main.findViewById(R.id.indicatorZ);
        assertNotNull("IndicatorZ not allowed be null", indicatorZ);

        indicatorY.setLabel("Y");
        assertNotNull("label indicatorY can be null, but we need for indentify the view", indicatorY.getLabel());
        indicatorX.setLabel("X");
        assertNotNull("label indicatorX can be null, but we need for indentify the view", indicatorX.getLabel());
        indicatorZ.setLabel("Z");
        assertNotNull("label indicatorX can be null, but we need for indentify the view", indicatorZ.getLabel());

        indicatorY.setCurrentSpeed(5f);
        assertEquals(5f, indicatorY.getCurrentSpeed());
        indicatorX.setCurrentSpeed(5f);
        assertEquals(5f, indicatorX.getCurrentSpeed());
        indicatorZ.setCurrentSpeed(5f);
        assertEquals(5f, indicatorZ.getCurrentSpeed());
    }

    public void testSendData() throws JSONException {
        MainApplication application = MainApplication.getInstance();
        Pubnub pubnub = application.getPubnub();
        assertNotNull("pubnub not allowed be null", pubnub);

        JSONObject json = new JSONObject();
        json.put("data",  "x:5f y:5f z:5f");
        assertNotNull("JSONObject not allowed be null", json);

        pubnub.publish(CHANNEL, json, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
                if(channel.equalsIgnoreCase(CHANNEL)) {
                    assertEquals(channel, CHANNEL);
                }
            }
        });
    }

}
