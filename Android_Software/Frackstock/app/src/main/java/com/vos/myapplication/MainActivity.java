package com.vos.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;


public class MainActivity extends AppCompatActivity {

    Button bTriggers, bStripe, bSparks, bConstColor, bRainbow,bBlink,bRain,bStrobo;
    MainActivity thisActivity;
    SeekBar sbGlobalBrightness;

    static int R_SPARKS = 1;
    static int R_STRIPE = 2;
    static int R_CONST = 3;
    static int R_RAINBOW = 4;
    static int R_BLINK = 5;
    static int R_RAIN = 6;
    static int R_STROBO = 7;

    int Sparkscolor1;
    int SparkModulo;
    int SparkSpeed;


    int StripeColor1,StripeColor2;
    int StripeBrightness,StripeLength,StripeSpeed;

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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        bBlink = (Button) findViewById(R.id.bBlink);
        bStripe = (Button)  findViewById(R.id.bStripeLaunch);
        bSparks = (Button) findViewById(R.id.bSparksLaunch);
        bConstColor = (Button)    findViewById(R.id.bConstColor);
        bRainbow = (Button)    findViewById(R.id.bRainbow);
        bRain = (Button) findViewById(R.id.bRain);
        bStrobo = (Button) findViewById(R.id.bStrobo);
        bTriggers = (Button) findViewById(R.id.bTrigger);
        sbGlobalBrightness = (SeekBar) findViewById(R.id.sbGlobalBrightness);


        sbGlobalBrightness.setProgress(128);

        thisActivity = this;


        /* Stripe */
        StripeColor1 = Color.RED;
        StripeColor2 = Color.GREEN;
        StripeBrightness = 8;
        StripeLength = 15;
        StripeSpeed = 1;

        bStripe.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(thisActivity, stripeActivity.class);
               intent.putExtra("Color1",StripeColor1);
               intent.putExtra("Color2",StripeColor2);
               intent.putExtra("Brightness",StripeBrightness);
               intent.putExtra("Length",StripeLength);
               intent.putExtra("Speed",StripeSpeed);
               intent.putExtra("GlobalBrightness",sbGlobalBrightness.getProgress());
               startActivityForResult(intent,R_STRIPE);
           }
       });


        /*   Sparks    */
        Sparkscolor1 = Color.RED;
        SparkModulo = 5;
        SparkSpeed = 1;
        bSparks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, sparkActivity.class);
                intent.putExtra("Color1", Sparkscolor1);
                intent.putExtra("Modulo",SparkModulo);
                intent.putExtra("Speed",SparkSpeed);
                intent.putExtra("GlobalBrightness",sbGlobalBrightness.getProgress());

                startActivityForResult(intent,R_SPARKS);

            }
        });

        /* Const Color */
        ConstColor1 = Color.RED;
        ConstColor2 = Color.GREEN;
        ConstBrightness = 10;
        bConstColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, ConstColorActivity.class);
                intent.putExtra("Color1",ConstColor1);
                intent.putExtra("Color2",ConstColor2);
                intent.putExtra("Brightness",ConstBrightness);
                intent.putExtra("GlobalBrightness",sbGlobalBrightness.getProgress());

                startActivityForResult(intent,R_CONST);
            }
        });


        /* Rainbow */

        RainbowSpeed = 20;

        bRainbow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, RainbowActivity.class);
                intent.putExtra("Speed",RainbowSpeed);
                intent.putExtra("GlobalBrightness",sbGlobalBrightness.getProgress());

                startActivityForResult(intent,R_RAINBOW);
            }
        });


        /* Blink */
        BlinkColor1 = Color.RED;
        BlinkColor2 = Color.GREEN;
        BlinkColor3 = Color.BLUE;
        BlinkColor4 = Color.WHITE;

        bBlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, BlinkActivity.class);
                intent.putExtra("Color1",BlinkColor1);
                intent.putExtra("Color2",BlinkColor2);
                intent.putExtra("Color3",BlinkColor3);
                intent.putExtra("Color4",BlinkColor4);
                intent.putExtra("GlobalBrightness",sbGlobalBrightness.getProgress());

                startActivityForResult(intent,R_BLINK);
            }
        });


        /* Rain */
        RainColor1 = Color.RED;
        RainColor2 = Color.GREEN;
        RainColor3 = Color.BLUE;
        RainColor4 = Color.WHITE;

        RainSpeed = 2;
        RainLength = 7;

        bRain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, RainActivity.class);
                intent.putExtra("Color1",RainColor1);
                intent.putExtra("Color2",RainColor2);
                intent.putExtra("Color3",RainColor3);
                intent.putExtra("Color4",RainColor4);
                intent.putExtra("Speed",RainSpeed);
                intent.putExtra("Length",RainLength);
                intent.putExtra("GlobalBrightness",sbGlobalBrightness.getProgress());

                startActivityForResult(intent,R_RAIN);
            }
        });

        StroboColor1 = Color.RED;
        StroboLength = 1;
        StroboPause = 2;

        bStrobo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, stroboActivity.class);
                intent.putExtra("Color1",StroboColor1);
                intent.putExtra("Length",StroboLength);
                intent.putExtra("Pause",StroboPause);
                intent.putExtra("GlobalBrightness",sbGlobalBrightness.getProgress());

                startActivityForResult(intent,R_STROBO);
            }
        });


        /* Triggers */

        bTriggers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, TriggerActivity.class);
                intent.putExtra("StripeColor1"      ,StripeColor1);
                intent.putExtra("StripeColor2"      ,StripeColor2);
                intent.putExtra("StripeBrightness"  ,StripeBrightness);
                intent.putExtra("StripeLength"      ,StripeLength);
                intent.putExtra("StripeSpeed"       ,StripeSpeed);
                intent.putExtra("ConstColor1"       ,ConstColor1);
                intent.putExtra("ConstColor2"       ,ConstColor2);
                intent.putExtra("ConstBrightness"       ,ConstBrightness);

                intent.putExtra("RainbowSpeed"      ,RainbowSpeed);
                intent.putExtra("BlinkColor1"       ,BlinkColor1);
                intent.putExtra("BlinkColor2"       ,BlinkColor2);
                intent.putExtra("BlinkColor3"       ,BlinkColor3);
                intent.putExtra("BlinkColor4"       ,BlinkColor4);
                intent.putExtra("RainColor1"        ,RainColor1);
                intent.putExtra("RainColor2"        ,RainColor2);
                intent.putExtra("RainColor3"        ,RainColor3);
                intent.putExtra("RainColor4"        ,RainColor4);

                intent.putExtra("RainSpeed"         ,RainSpeed);
                intent.putExtra("RainLength"        ,RainLength);

                intent.putExtra("StroboColor1"      ,StroboColor1);
                intent.putExtra("StroboLength"      ,StroboLength);
                intent.putExtra("StroboPause"       ,StroboPause);

                intent.putExtra("SparkColor1"      ,Sparkscolor1);
                intent.putExtra("SparkModulo"      ,SparkModulo);
                intent.putExtra("SparkSpeed"       ,SparkSpeed);
                intent.putExtra("GlobalBrightness",sbGlobalBrightness.getProgress());


                startActivityForResult(intent,0);
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == R_SPARKS) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                Sparkscolor1 = data.getIntExtra("Color1",Color.RED);
                SparkModulo = data.getIntExtra("Modulo",5);
                SparkSpeed = data.getIntExtra("Speed",1);
                // Do something with the contact here (bigger example below)
            }
        }

        if(requestCode == R_STRIPE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                StripeColor1 = data.getIntExtra("Color1",Color.RED);
                StripeColor2 = data.getIntExtra("Color2",Color.GREEN);
                StripeBrightness = data.getIntExtra("Brightness",7);
                StripeLength = data.getIntExtra("Length",15);
                StripeSpeed = data.getIntExtra("Speed",1);
                // Do something with the contact here (bigger example below)
            }
        }

        if(requestCode == R_CONST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                ConstColor1 = data.getIntExtra("Color1",Color.RED);
                ConstColor2 = data.getIntExtra("Color2",Color.GREEN);
                ConstBrightness = data.getIntExtra("Brightness",10);
                // Do something with the contact here (bigger example below)
            }
        }

        if(requestCode == R_RAINBOW) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                RainbowSpeed = data.getIntExtra("Speed",20);

                // Do something with the contact here (bigger example below)
            }
        }

        if(requestCode == R_BLINK) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                BlinkColor1 = data.getIntExtra("Color1",Color.RED);
                BlinkColor2 = data.getIntExtra("Color2",Color.GREEN);
                BlinkColor3 = data.getIntExtra("Color3",Color.BLUE);
                BlinkColor4 = data.getIntExtra("Color4",Color.WHITE);

                // Do something with the contact here (bigger example below)
            }
        }

        if(requestCode == R_RAIN) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                RainColor1 = data.getIntExtra("Color1",Color.RED);
                RainColor2 = data.getIntExtra("Color2",Color.GREEN);
                RainColor3 = data.getIntExtra("Color3",Color.BLUE);
                RainColor4 = data.getIntExtra("Color4",Color.WHITE);
                RainSpeed = data.getIntExtra("Speed",7);
                RainLength = data.getIntExtra("Length",7);

                // Do something with the contact here (bigger example below)
            }
        }

        if(requestCode == R_STROBO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                StroboColor1 = data.getIntExtra("Color1",Color.RED);
                StroboLength = data.getIntExtra("Length",1);
                StroboPause = data.getIntExtra("Pause",2);


                // Do something with the contact here (bigger example below)
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

