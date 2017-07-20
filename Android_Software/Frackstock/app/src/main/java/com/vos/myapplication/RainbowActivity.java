package com.vos.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class RainbowActivity extends AppCompatActivity {

    SeekBar sbSpeed;
    Button bTriggerRainbow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rainbow);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        sbSpeed = (SeekBar) findViewById(R.id.sbRainbowSpeed);
        bTriggerRainbow = (Button) findViewById(R.id.bStartRainbow);


        sbSpeed.setProgress(getIntent().getIntExtra("Speed",20));
        bTriggerRainbow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   trigger(sbSpeed.getProgress());
                }
                return true;
            }
        });
    }

    public static void trigger(int pSpeed){
        byte[] bytes;
        try {

            byte speed = (byte) pSpeed;

            bytes = new byte[]{
                    (byte) 0x3F,
                    (byte) 128,
                    (byte) speed,
                    (byte) 0,
                    (byte) 0
            };
            new SendPacket().execute(bytes);

        } catch (Exception e) {
            Log.d("t",e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        Intent intent = new Intent();
        intent.putExtra("Speed",sbSpeed.getProgress());
        setResult(RESULT_OK, intent);
        finish();
    }

}
