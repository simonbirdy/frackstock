package com.vos.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class TriggerActivity extends AppCompatActivity {


    Button bStripe,bSparks,bConst,bRainbow,bStroboSingle,bStroboSync,bStroboAsync,bStroboRand;
    Button bBlink1,bBlink2,bBlink3,bBlink4,bRain1,bRain2,bRain3,bRain4;

    int StripeColor1,StripeColor2;
    int StripeBrightness,StripeLength,StripeSpeed;

    int Sparkscolor1;
    int SparkModulo;
    int SparkSpeed;
    int globalBrightness;


    int ConstColor1,ConstColor2,ConstBrightness;

    int RainbowSpeed;

    int BlinkColor1,BlinkColor2,BlinkColor3,BlinkColor4;

    int RainColor1,RainColor2,RainColor3,RainColor4;
    int RainSpeed,RainLength;

    int StroboColor1;
    int StroboLength,StroboPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setContentView(R.layout.activity_trigger);

        bStripe = (Button) findViewById(R.id.bTrigStripe1);
        bSparks = (Button) findViewById(R.id.bTrigSparks);
        bConst = (Button) findViewById(R.id.bTrigConst);
        bRainbow = (Button) findViewById(R.id.bTrigRainBow);
        bStroboSingle = (Button) findViewById(R.id.bTrigStroboSingle);
        bStroboSync = (Button) findViewById(R.id.bTrigStroboSync);
        bStroboAsync = (Button) findViewById(R.id.bTrigStroboAsync);
        bStroboRand = (Button) findViewById(R.id.bTrigStroboRand);


        bBlink1 = (Button) findViewById(R.id.bTrigBlink1);
        bBlink2 = (Button) findViewById(R.id.bTrigBlink2);
        bBlink3  = (Button) findViewById(R.id.bTrigBlink3);
        bBlink4 = (Button) findViewById(R.id.bTrigBlink4);

        bRain1 = (Button) findViewById(R.id.bTrigRain1);
        bRain2 = (Button) findViewById(R.id.bTrigRain2);
        bRain3  = (Button)findViewById(R.id.bTrigRain3);
        bRain4 = (Button) findViewById(R.id.bTrigRain4);

        StripeColor1 = getIntent().getIntExtra("StripeColor1"      ,StripeColor1);
        StripeColor2 = getIntent().getIntExtra("StripeColor2"      ,StripeColor2);
        StripeBrightness = getIntent().getIntExtra("StripeBrightness"  ,StripeBrightness);
        StripeLength = getIntent().getIntExtra("StripeLength"      ,StripeLength);
        StripeSpeed = getIntent().getIntExtra("StripeSpeed"       ,StripeSpeed);
        ConstColor1 = getIntent().getIntExtra("ConstColor1"       ,ConstColor1);
        ConstColor2 = getIntent().getIntExtra("ConstColor2"       ,ConstColor2);
        ConstBrightness = getIntent().getIntExtra("ConstBrightness",ConstBrightness);
        RainbowSpeed = getIntent().getIntExtra("RainbowSpeed"      ,RainbowSpeed);
        BlinkColor1 = getIntent().getIntExtra("BlinkColor1"       ,BlinkColor1);
        BlinkColor2 = getIntent().getIntExtra("BlinkColor2"       ,BlinkColor2);
        BlinkColor3 = getIntent().getIntExtra("BlinkColor3"       ,BlinkColor3);
        BlinkColor4 = getIntent().getIntExtra("BlinkColor4"       ,BlinkColor4);
        RainColor1 = getIntent().getIntExtra("RainColor1"        ,RainColor1);
        RainColor2 = getIntent().getIntExtra("RainColor2"        ,RainColor2);
        RainColor3 = getIntent().getIntExtra("RainColor3"        ,RainColor3);
        RainColor4 = getIntent().getIntExtra("RainColor4"        ,RainColor4);
        RainSpeed = getIntent().getIntExtra("RainSpeed"         ,RainSpeed);
        RainLength = getIntent().getIntExtra("RainLength"        ,RainLength);
        StroboColor1 = getIntent().getIntExtra("StroboColor1"      ,StroboColor1);
        StroboLength = getIntent().getIntExtra("StroboLength"      ,StroboLength);
        StroboPause = getIntent().getIntExtra("StroboPause"       ,StroboPause);

        Sparkscolor1 = getIntent().getIntExtra("SparkColor1"      ,Sparkscolor1);
        SparkModulo = getIntent().getIntExtra("SparkModulo"      ,SparkModulo);
        SparkSpeed = getIntent().getIntExtra("SparkSpeed"       ,SparkSpeed);


        globalBrightness = getIntent().getIntExtra("GlobalBrightness",255);




        /* Stripes */

        bStripe.setBackgroundColor(StripeColor1);
        bStripe.setTextColor(StripeColor2);
        bStripe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    stripeActivity.trigger1(StripeColor1,StripeColor2,StripeLength,StripeSpeed,StripeBrightness,globalBrightness);
                }
                return true;
            }
        });

        bSparks.setBackgroundColor(Sparkscolor1);
        bSparks.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    sparkActivity.trigger(Sparkscolor1,SparkModulo,SparkSpeed,globalBrightness);
                }
                return true;
            }
        });

        bConst.setBackgroundColor(ConstColor1);
        bConst.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ConstColorActivity.triggerOneColor(ConstColor1,ConstBrightness,globalBrightness);
                }
                return true;
            }
        });

        bRainbow.setBackgroundColor(Color.WHITE);
        bRainbow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    RainbowActivity.trigger(RainbowSpeed);
                }
                return true;
            }
        });

        bStroboSingle.setBackgroundColor(StroboColor1);
        bStroboSingle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    stroboActivity.trigger(stroboActivity.MODE_SINGLE,StroboColor1,StroboLength,StroboPause,globalBrightness);
                }
                return true;
            }
        });

        bStroboSync.setBackgroundColor(StroboColor1);
        bStroboSync.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    stroboActivity.trigger(stroboActivity.MODE_SYNC,StroboColor1,StroboLength,StroboPause,globalBrightness);
                }
                return true;
            }
        });

        bStroboAsync.setBackgroundColor(StroboColor1);
        bStroboAsync.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    stroboActivity.trigger(stroboActivity.MODE_ASYNC,StroboColor1,StroboLength,StroboPause,globalBrightness);
                }
                return true;
            }
        });

        bStroboRand.setBackgroundColor(StroboColor1);
        bStroboRand.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    stroboActivity.trigger(stroboActivity.MODE_RAND,StroboColor1,StroboLength,StroboPause,globalBrightness);
                }
                return true;
            }
        });

        bBlink1.setBackgroundColor(BlinkColor1);
        bBlink1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        float length = 4/((event.getX() / bBlink1.getWidth()));
                        if(length > 255){
                            length = 255;
                        }
                        BlinkActivity.trigger(BlinkColor1,(int) length,globalBrightness);
                    } catch (Exception e) {
                    }
                }
                return true;
            }
        });

        bBlink2.setBackgroundColor(BlinkColor2);
        bBlink2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        float length = 4/((event.getX() / bBlink2.getWidth()));
                        if(length > 255){
                            length = 255;
                        }
                        BlinkActivity.trigger(BlinkColor2,(int) length,globalBrightness);
                    } catch (Exception e) {
                    }
                }
                return true;
            }
        });

        bBlink3.setBackgroundColor(BlinkColor3);
        bBlink3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        float length = 4/((event.getX() / bBlink3.getWidth()));
                        if(length > 255){
                            length = 255;
                        }
                        BlinkActivity.trigger(BlinkColor3,(int) length,globalBrightness);
                    } catch (Exception e) {
                    }
                }
                return true;
            }
        });

        bBlink4.setBackgroundColor(BlinkColor4);
        bBlink4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        float length = 4/((event.getX() / bBlink4.getWidth()));
                        if(length > 255){
                            length = 255;
                        }
                        BlinkActivity.trigger(BlinkColor4,(int) length,globalBrightness);
                    } catch (Exception e) {
                    }
                }
                return true;
            }
        });

        bRain1.setBackgroundColor(RainColor1);
        bRain1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    RainActivity.trigger(RainColor1,RainLength,RainSpeed,globalBrightness);
                }
                return true;
            }
        });

        bRain1.setBackgroundColor(RainColor1);
        bRain1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    RainActivity.trigger(RainColor1,RainLength,RainSpeed,globalBrightness);
                }
                return true;
            }
        });

        bRain2.setBackgroundColor(RainColor2);
        bRain2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    RainActivity.trigger(RainColor2,RainLength,RainSpeed,globalBrightness);
                }
                return true;
            }
        });

        bRain3.setBackgroundColor(RainColor3);
        bRain3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    RainActivity.trigger(RainColor3,RainLength,RainSpeed,globalBrightness);
                }
                return true;
            }
        });

        bRain4.setBackgroundColor(RainColor4);
        bRain4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    RainActivity.trigger(RainColor4,RainLength,RainSpeed,globalBrightness);
                }
                return true;
            }
        });



    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

}
