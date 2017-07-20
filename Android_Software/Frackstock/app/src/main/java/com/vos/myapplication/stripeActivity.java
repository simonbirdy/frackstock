package com.vos.myapplication;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class stripeActivity extends AppCompatActivity {

    Button bColor1,bColor2,bTriggerStripe, bTriggerStripe2;
    SeekBar sbLength, sbSpeed, sbBrightness;
    int color1, color2;
    stripeActivity thisActivity;
    int globalBrightness;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        thisActivity = this;

        bColor1 = (Button) findViewById(R.id.bSetColor1);
        bColor2 = (Button) findViewById(R.id.bSetColor2);
        bTriggerStripe = (Button) findViewById(R.id.bStartStripe);
        bTriggerStripe2 = (Button)findViewById(R.id.bStartStripe2);
        sbLength = (SeekBar) findViewById(R.id.seekBarLength);
        sbSpeed = (SeekBar) findViewById(R.id.seekBarSpeed);
        sbBrightness = (SeekBar) findViewById(R.id.seekBarBrightness);

        color1 = getIntent().getIntExtra("Color1",Color.RED);
        bColor1.setBackgroundColor(color1);
        color2 = getIntent().getIntExtra("Color2",Color.GREEN);
        bColor2.setBackgroundColor(color2);

        sbLength.setProgress(getIntent().getIntExtra("Length",15));
        sbSpeed.setProgress(getIntent().getIntExtra("Speed",1));
        sbBrightness.setProgress(getIntent().getIntExtra("Brightness",7));

        globalBrightness = getIntent().getIntExtra("GlobalBrightness",255);


        bColor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = thisActivity.getFragmentManager();

                //Generate Effect Dialog
                ColorDialog dialogFragment = new ColorDialog();
                dialogFragment.setColorDialogResultListener(new ColorDialog.ColorDialogResultListener() {
                    @Override
                    public void colorSelected(float[] pColor1, float[] pColor2, int pDirection) {
                        bColor1.setBackgroundColor(Color.HSVToColor(pColor1));
                        color1 = Color.HSVToColor(pColor1);
                    }
                });
                //Set attributes
                //Show dialog
                dialogFragment.show(fm, "Sample Fragment");
            }
        });

        bColor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = thisActivity.getFragmentManager();

                //Generate Effect Dialog
                ColorDialog dialogFragment = new ColorDialog();
                dialogFragment.setColorDialogResultListener(new ColorDialog.ColorDialogResultListener() {
                    @Override
                    public void colorSelected(float[] pColor1, float[] pColor2, int pDirection) {
                        bColor2.setBackgroundColor(Color.HSVToColor(pColor1));
                        color2 = Color.HSVToColor(pColor1);
                    }
                });

                //Set attributes
                //Show dialog
                dialogFragment.show(fm, "Sample Fragment");
            }
        });

        bTriggerStripe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    trigger1(color1,color2,sbLength.getProgress(),sbSpeed.getProgress(),sbLength.getProgress(),globalBrightness);
                }
                return true;
            }
        });

        bTriggerStripe2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                byte[] bytes;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    trigger2(color1,color2,sbLength.getProgress(),sbSpeed.getProgress(),sbLength.getProgress(),globalBrightness);
                }
                return true;
            }
        });
    }

    public static void trigger1(int pColor1,int pColor2,int pLength, int pSpeed, int pBrightness, int globalBrightness){
        byte[] bytes;
        try {
            byte[] byColor1 = RGBUtils.rgbRound(pColor1,2);
            byte[] byColor2 = RGBUtils.rgbRound(pColor2,2);
            byte length = (byte)pLength;
            byte speed = (byte)pSpeed;
            byte brightness = (byte) ((pBrightness*globalBrightness)/256);

            bytes = new byte[]{
                    (byte) 0x3F,
                    (byte) 128+1,
                    (byte) (length | speed<<5),
                    (byte) ((byColor1[0]&0xC0) | (byColor1[1]&0xC0)>>2 | (byColor1[2]&0xC0)>>4 | (byColor2[0]&0xC0)>>6),
                    (byte) ((byColor2[1]&0xC0) | (byColor2[2]&0xC0)>>2 | brightness&0x0F)
            };
            new SendPacket().execute(bytes);

        } catch (Exception e) {
            Log.d("t",e.toString());
        }
    }

    public static void trigger2(int pColor1,int pColor2,int pLength, int pSpeed, int pBrightness, int globalBrightness){
        byte[] bytes;
        try {
            byte[] byColor1 = RGBUtils.rgbRound(pColor1,2);
            byte[] byColor2 = RGBUtils.rgbRound(pColor2,2);
            byte length = (byte)pLength;
            byte speed = (byte)pSpeed;
            byte brightness = (byte) ((pBrightness*globalBrightness)/256);

            bytes = new byte[]{
                    (byte) 0x3F,
                    (byte) 128+3,
                    (byte) (length | speed<<5),
                    (byte) ((byColor1[0]&0xC0) | (byColor1[1]&0xC0)>>2 | (byColor1[2]&0xC0)>>4 | (byColor2[0]&0xC0)>>6),
                    (byte) ((byColor2[1]&0xC0) | (byColor2[2]&0xC0)>>2 | brightness&0x0F)
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
        intent.putExtra("Color1", color1);
        intent.putExtra("Color2", color2);
        intent.putExtra("Brightness", sbBrightness.getProgress());
        intent.putExtra("Speed", sbSpeed.getProgress());
        intent.putExtra("Length", sbLength.getProgress());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Close activity
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
