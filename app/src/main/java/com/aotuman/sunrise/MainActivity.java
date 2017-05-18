package com.aotuman.sunrise;

import android.app.Activity;
import android.os.Bundle;

import com.aotuman.sunrise.view.SunriseView;

import java.util.Calendar;

public class MainActivity extends Activity {
    private SunriseView mSunriseView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSunriseView = (SunriseView) findViewById(R.id.sun);
        startSunAnim(6,18);
    }

    public void startSunAnim(int sunrise, int sunset) {
        Calendar calendarNow = Calendar.getInstance();
        int hour = calendarNow.get(Calendar.HOUR_OF_DAY);
        if(hour < sunrise){
            mSunriseView.sunAnim(0);
        }else if(hour > sunset){
            mSunriseView.sunAnim(1);
        }else {
            mSunriseView.sunAnim(((float) hour - (float) sunrise) / ((float) sunset - (float) sunrise));
        }
    }
}
