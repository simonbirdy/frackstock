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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class ConstColorActivity extends AppCompatActivity {

    Button bColor1,bColor2,bTriggerOneColor, bTriggerTwoFace;
    SeekBar sbBrightness;
    int color1, color2;
    int globalBrightness;
    ConstColorActivity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_const_color);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        thisActivity = this;

        bColor1 = (Button) findViewById(R.id.bConstSetColor1);
        bColor2 = (Button) findViewById(R.id.bConstSetColor2);

        bTriggerOneColor = (Button) findViewById(R.id.bStartConst);
        bTriggerTwoFace = (Button) findViewById(R.id.bStartTwoFace);

        sbBrightness = (SeekBar) findViewById(R.id.sbConstBrightness);
        globalBrightness = getIntent().getIntExtra("GlobalBrightness",255);

        color1 = getIntent().getIntExtra("Color1",Color.RED);
        bColor1.setBackgroundColor(color1);
        color2 = getIntent().getIntExtra("Color2",Color.GREEN);
        bColor2.setBackgroundColor(color2);

        sbBrightness.setProgress(getIntent().getIntExtra("Brightness",7));


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


        bTriggerOneColor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    triggerOneColor(color1,sbBrightness.getProgress(),globalBrightness);
                }
                return true;
            }
        });



        bTriggerTwoFace.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    triggerTwoFace(color1,color2,sbBrightness.getProgress(),globalBrightness);
                }
                return true;
            }
        });
    }

    public static void triggerOneColor(int pColor, int pBrightness, int globalBrightness){
        byte[] bytes;
        try {
            int tmpcolor = RGBUtils.rgbBrightness(pColor,(globalBrightness*pBrightness)/256);
            byte[] byColor1 = RGBUtils.rgbRound(tmpcolor,8);

            bytes = new byte[]{
                    (byte) 0x3F,
                    (byte) 128+2,
                    (byte) byColor1[0],
                    (byte) byColor1[1],
                    (byte) byColor1[2]
            };
            new SendPacket().execute(bytes);

        } catch (Exception e) {
            Log.d("t",e.toString());
        }
    }

    public static void triggerTwoFace(int pColor1,int pColor2, int pBrightness, int globalBrightness) {
        byte[] bytes;
        try {
            byte[] byColor1 = RGBUtils.rgbRound(pColor1,2);
            byte[] byColor2 = RGBUtils.rgbRound(pColor2,2);

            byte brightness = (byte) ((pBrightness*globalBrightness)/256);

            bytes = new byte[]{
                    (byte) 0x3F,
                    (byte) 128+4,
                    (byte) (brightness<<2 | (byColor1[0]&0xC0)>>6),
                    (byte) ((byColor1[0]&0x20)<<2 | (byColor1[1]&0xE0)>>1 | (byColor1[2]&0xE0)>>4 | (byColor2[0]&0x80)>>7),
                    (byte) ((byColor2[0]&0x60)<<1 | (byColor2[1]&0xE0)>>2 | (byColor2[2]&0xE0)>>5)
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
        intent.putExtra("Color1",color1);
        intent.putExtra("Color2",color2);
        intent.putExtra("Brightness",sbBrightness.getProgress());
        setResult(RESULT_OK, intent);
        finish();
    }

}
