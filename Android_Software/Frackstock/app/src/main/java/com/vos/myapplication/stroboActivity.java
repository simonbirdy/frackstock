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

public class stroboActivity extends AppCompatActivity {

    Button bSetColor,bTriggerSingle,bTriggerSync,bTriggerAsync,bTriggerRand;

    SeekBar sbLength,sbPause;

    int color1;

    int globalBrightness;

    stroboActivity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strobo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        thisActivity = this;

        bSetColor = (Button) findViewById(R.id.bStroboSetColor);
        bTriggerSingle = (Button) findViewById(R.id.bStroboSingle);
        bTriggerSync = (Button) findViewById(R.id.bStroboSync);
        bTriggerAsync = (Button) findViewById(R.id.bStroboAsync);
        bTriggerRand = (Button) findViewById(R.id.bStroboRand);

        sbLength = (SeekBar) findViewById(R.id.sbStroboLength);
        sbPause = (SeekBar) findViewById(R.id.sbStroboPause);

        color1 = getIntent().getIntExtra("Color1",Color.RED);
        globalBrightness = getIntent().getIntExtra("GlobalBrightness",255);
        bSetColor.setBackgroundColor(color1);

        sbLength.setProgress(getIntent().getIntExtra("Length",1));
        sbPause.setProgress(getIntent().getIntExtra("Pause",2));

        bSetColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = thisActivity.getFragmentManager();

                //Generate Effect Dialog
                ColorDialog dialogFragment = new ColorDialog();
                dialogFragment.setColorDialogResultListener(new ColorDialog.ColorDialogResultListener() {
                    @Override
                    public void colorSelected(float[] pColor1, float[] pColor2, int pDirection) {
                        color1 = Color.HSVToColor(pColor1);
                        bSetColor.setBackgroundColor(Color.HSVToColor(pColor1));
                    }
                });
                //Set attributes
                //Show dialog
                dialogFragment.show(fm, "Sample Fragment");
            }
        });

        bTriggerSingle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                byte[] bytes;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {


                        byte length = (byte) sbLength.getProgress();
                        byte pause  = (byte) sbPause.getProgress();
                        byte[] col = RGBUtils.rgbRound(color1,8);

                        byte r = col[0];
                        byte g = col[1];
                        byte b = col[2];
                        bytes = new byte[]{
                                (byte) 0x3F,
                                (byte) 0x03,
                                (byte) (0x00 | (pause << 5) | (length << 2)),
                                (byte) (r&0xF8 | (g&0xE0)>>5),
                                (byte) ((g&0x18)<<3 | (b&0xF8)>>2)
                        };
                        new SendPacket().execute(bytes);

                    } catch (Exception e) {
                    }
                }
                return true;
            }
        });

        bTriggerSingle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    trigger(MODE_SINGLE,color1,sbLength.getProgress(),sbPause.getProgress(),globalBrightness);
                }
                return true;
            }
        });

        bTriggerSync.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    trigger(MODE_SYNC,color1,sbLength.getProgress(),sbPause.getProgress(),globalBrightness);
                }
                return true;
            }
        });


        bTriggerAsync.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    trigger(MODE_ASYNC,color1,sbLength.getProgress(),sbPause.getProgress(),globalBrightness);
                }
                return true;
            }
        });

        bTriggerRand.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    trigger(MODE_RAND,color1,sbLength.getProgress(),sbPause.getProgress(),globalBrightness);
                }
                return true;
            }
        });
    }

    public static int MODE_SINGLE = 0;
    public static int MODE_SYNC = 1;
    public static int MODE_ASYNC = 2;
    public static int MODE_RAND = 3;

    public static void trigger(int pMode, int pColor,int pLength, int pPause,int globalBrightness){
        byte[] bytes;
        try {
            byte length = (byte) pLength;
            byte pause  = (byte) pPause;

            byte[] col = RGBUtils.rgbRound(RGBUtils.rgbBrightness(pColor,globalBrightness),8);

            byte r = col[0];
            byte g = col[1];
            byte b = col[2];
            bytes = new byte[]{
                    (byte) 0x3F,
                    (byte) 0x02,
                    (byte) (((byte)pMode) | (pause << 5) | (length << 2)),
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
        intent.putExtra("Length",sbLength.getProgress());
        intent.putExtra("Pause",sbPause.getProgress());
        setResult(RESULT_OK, intent);
        finish();
    }


}
