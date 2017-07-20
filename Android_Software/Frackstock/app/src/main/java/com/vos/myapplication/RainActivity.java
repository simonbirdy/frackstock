package com.vos.myapplication;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class RainActivity extends AppCompatActivity {

    Button bSet1,bSet2,bSet3,bSet4;
    Button bTrigger1,bTrigger2,bTrigger3,bTrigger4;

    int color1,color2,color3,color4;
    int globalBrightness;

    SeekBar sbSpeed,sbLength;

    RainActivity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rain);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        thisActivity = this;

        bSet1 = (Button) findViewById(R.id.bRainSetColor1);
        bSet2 = (Button) findViewById(R.id.bRainSetColor2);
        bSet3 = (Button) findViewById(R.id.bRainSetColor3);
        bSet4 = (Button) findViewById(R.id.bRainSetColor4);

        bTrigger1 = (Button) findViewById(R.id.bRainTriggerColor1);
        bTrigger2 = (Button) findViewById(R.id.bRainTriggerColor2);
        bTrigger3 = (Button) findViewById(R.id.bRainTriggerColor3);
        bTrigger4 = (Button) findViewById(R.id.bRainTriggerColor4);

        sbLength = (SeekBar) findViewById(R.id.sbRainLength);
        sbSpeed = (SeekBar) findViewById(R.id.sbRainSpeed);

        color1 = getIntent().getIntExtra("Color1",Color.RED);
        color2 = getIntent().getIntExtra("Color2",Color.GREEN);
        color3 = getIntent().getIntExtra("Color3",Color.BLUE);
        color4 = getIntent().getIntExtra("Color4",Color.WHITE);

        globalBrightness = getIntent().getIntExtra("GlobalBrightness",255);


        float[] tmpcolor = new float[3];
        Color.colorToHSV(color1,tmpcolor);
        tmpcolor[2] = 1.0f;
        bTrigger1.setBackgroundColor(Color.HSVToColor(tmpcolor));
        Color.colorToHSV(color2,tmpcolor);
        tmpcolor[2] = 1.0f;
        bTrigger2.setBackgroundColor(Color.HSVToColor(tmpcolor));
        Color.colorToHSV(color3,tmpcolor);
        tmpcolor[2] = 1.0f;
        bTrigger3.setBackgroundColor(Color.HSVToColor(tmpcolor));
        Color.colorToHSV(color4,tmpcolor);
        tmpcolor[2] = 1.0f;
        bTrigger4.setBackgroundColor(Color.HSVToColor(tmpcolor));

        sbLength.setProgress(getIntent().getIntExtra("Length",7));
        sbSpeed.setProgress(getIntent().getIntExtra("Speed",7));


        bSet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = thisActivity.getFragmentManager();

                //Generate Effect Dialog
                ColorDialog dialogFragment = new ColorDialog();
                dialogFragment.setColorDialogResultListener(new ColorDialog.ColorDialogResultListener() {
                    @Override
                    public void colorSelected(float[] pColor1, float[] pColor2, int pDirection) {
                        color1 = Color.HSVToColor(pColor1);
                        float[] tmpcolor = pColor1;
                        tmpcolor[2] = 1.0f;
                        bTrigger1.setBackgroundColor(Color.HSVToColor(tmpcolor));
                    }
                });
                //Set attributes
                //Show dialog
                dialogFragment.show(fm, "Sample Fragment");
            }
        });

        bSet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = thisActivity.getFragmentManager();

                //Generate Effect Dialog
                ColorDialog dialogFragment = new ColorDialog();
                dialogFragment.setColorDialogResultListener(new ColorDialog.ColorDialogResultListener() {
                    @Override
                    public void colorSelected(float[] pColor1, float[] pColor2, int pDirection) {
                        color2 = Color.HSVToColor(pColor1);
                        float[] tmpcolor = pColor1;
                        tmpcolor[2] = 1.0f;
                        bTrigger2.setBackgroundColor(Color.HSVToColor(tmpcolor));
                    }
                });
                //Set attributes
                //Show dialog
                dialogFragment.show(fm, "Sample Fragment");
            }
        });

        bSet3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = thisActivity.getFragmentManager();

                //Generate Effect Dialog
                ColorDialog dialogFragment = new ColorDialog();
                dialogFragment.setColorDialogResultListener(new ColorDialog.ColorDialogResultListener() {
                    @Override
                    public void colorSelected(float[] pColor1, float[] pColor2, int pDirection) {
                        color3 = Color.HSVToColor(pColor1);
                        float[] tmpcolor = pColor1;
                        tmpcolor[2] = 1.0f;
                        bTrigger3.setBackgroundColor(Color.HSVToColor(tmpcolor));
                    }
                });
                //Set attributes
                //Show dialog
                dialogFragment.show(fm, "Sample Fragment");
            }
        });

        bSet4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = thisActivity.getFragmentManager();

                //Generate Effect Dialog
                ColorDialog dialogFragment = new ColorDialog();
                dialogFragment.setColorDialogResultListener(new ColorDialog.ColorDialogResultListener() {
                    @Override
                    public void colorSelected(float[] pColor1, float[] pColor2, int pDirection) {
                        color4 = Color.HSVToColor(pColor1);
                        float[] tmpcolor = pColor1;
                        tmpcolor[2] = 1.0f;
                        bTrigger4.setBackgroundColor(Color.HSVToColor(tmpcolor));
                    }
                });
                //Set attributes
                //Show dialog
                dialogFragment.show(fm, "Sample Fragment");
            }
        });

        bTrigger1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    trigger(color1,sbLength.getProgress(),sbSpeed.getProgress(),globalBrightness);
                }
                return true;
            }
        });

        bTrigger2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    trigger(color2,sbLength.getProgress(),sbSpeed.getProgress(),globalBrightness);
                }
                return true;
            }
        });

        bTrigger3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    trigger(color3,sbLength.getProgress(),sbSpeed.getProgress(),globalBrightness);
                }
                return true;
            }
        });


        bTrigger4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                byte[] bytes;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    trigger(color4,sbLength.getProgress(),sbSpeed.getProgress(),globalBrightness);
                }
                return true;
            }
        });
    }

    public static void trigger(int pColor,int pLength, int pSpeed,int globalBrightness){
        byte[] bytes;
        try {
            byte[] col = RGBUtils.rgbRound(RGBUtils.rgbBrightness(pColor,globalBrightness),8);
            byte length = (byte) pLength;
            byte speed = (byte) pSpeed;

            byte r = col[0];
            byte g = col[1];
            byte b = col[2];
            bytes = new byte[]{
                    (byte) 0x3F,
                    (byte) 0x03,
                    (byte) ((length&0x0F) | (speed&0x0F)<<4),
                    (byte) (r&0xF8 | (g&0xE0)>>5),
                    (byte) ((g&0x18)<<3 | (b&0xF8)>>2)
            };
            new SendPacket().execute(bytes);

        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        Intent intent = new Intent();
        intent.putExtra("Color1",color1);
        intent.putExtra("Color2",color2);
        intent.putExtra("Color3",color3);
        intent.putExtra("Color4",color4);
        intent.putExtra("Speed",sbSpeed.getProgress());
        intent.putExtra("Length",sbLength.getProgress());
        setResult(RESULT_OK, intent);
        finish();
    }

}
