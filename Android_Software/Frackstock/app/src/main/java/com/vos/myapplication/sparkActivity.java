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

public class sparkActivity extends AppCompatActivity {


    sparkActivity thisActivity;
    SeekBar sbModulo, sbSpeed;
    Button bColor1, bStartSpark;

    int color1;
    int globalBrightness;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spark);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        thisActivity = this;

        bColor1 = (Button) findViewById(R.id.bSparkSetColor1);
        bStartSpark = (Button) findViewById(R.id.bStartSparks);
        sbModulo = (SeekBar) findViewById(R.id.sbSparkModulo);
        sbSpeed = (SeekBar) findViewById(R.id.sbSparkSpeed);

        color1 = getIntent().getIntExtra("Color1",0);

        sbModulo.setProgress(getIntent().getIntExtra("Modulo",5));
        sbSpeed.setProgress(getIntent().getIntExtra("Speed",1));

        bColor1.setBackgroundColor(color1);



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




        bStartSpark.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                trigger(color1,sbModulo.getProgress(),sbSpeed.getProgress(),globalBrightness);
            }
            return true;
            }
        });
    }

    public static void trigger(int pColor,int pModulo, int pSpeed, int globalBrightness) {
        byte[] bytes;
        try {
            byte[] byColor1 = RGBUtils.rgbRound(RGBUtils.rgbBrightness(pColor,globalBrightness),5);
            byte modulo = (byte)pModulo;
            byte speed = (byte)pSpeed;

            bytes = new byte[]{
                    (byte) 0x3F,
                    (byte) 128+5,
                    (byte) (speed<<4 | modulo),
                    (byte) ((byColor1[0]&0xF8) | (byColor1[1]&0xE0)>>5),
                    (byte) ((byColor1[1]&0x18)<<3 | (byColor1[2]&0xF8)>>2)
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
        intent.putExtra("Modulo",sbModulo.getProgress());
        intent.putExtra("Speed",sbSpeed.getProgress());
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
