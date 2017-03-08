package com.vos.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button bWhite, bRed, bGreen,bBlue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bWhite = (Button)  findViewById(R.id.bWhite);
        bRed = (Button) findViewById(R.id.bRed);
        bGreen = (Button)    findViewById(R.id.bGreen);
        bBlue = (Button)    findViewById(R.id.bBlue);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes;
                try {
                    bytes = new byte[]{
                            (byte) 0x3F,
                            (byte) 0x80,
                            (byte) 3,
                            (byte) 0,
                            (byte) 0
                    };
                    new SendPacket().execute(bytes);

                } catch (Exception e) {
                    Log.d("t",e.toString());
                }
            }
        });

        try {

        }
        catch(Exception e){}


        bWhite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                byte[] bytes;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        float length = 4/((event.getX() / bWhite.getWidth()));
                        if(length > 255){
                            length = 255;
                        }
                        byte r = (byte)0xFF;
                        byte g = (byte)0xFF;
                        byte b = (byte)0xFF;
                        bytes = new byte[]{
                                (byte) 0x3F,
                                (byte) 0x00,
                                (byte) ((int)length),
                                (byte) (r&0xF8 | (g&0xE0)>>5),
                                (byte) ((g&0x18)<<3 | (b&0xF8)>>2)
                        };
                        new SendPacket().execute(bytes);

                    } catch (Exception e) {
                        Log.d("t",e.toString());
                    }
                }
                return true;
            }
        });
        bRed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                byte[] bytes;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        float length = 4/((event.getX() / bWhite.getWidth()));
                        if(length > 255){
                            length = 255;
                        }                        byte r = (byte)0xFF;
                        byte g = (byte)0;
                        byte b = (byte)0;
                        bytes = new byte[]{
                                (byte) 0x3F,
                                (byte) 0x00,
                                (byte) ((int)length),
                                (byte) (r&0xF8 | (g&0xE0)>>5),
                                (byte) ((g&0x18)<<3 | (b&0xF8)>>2)
                        };
                        new SendPacket().execute(bytes);

                    } catch (Exception e) {
                        Log.d("t",e.toString());
                    }
                }
                return true;
            }
        });

        bGreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                byte[] bytes;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        float length = 4/((event.getX() / bWhite.getWidth()));
                        if(length > 255){
                            length = 255;
                        }
                        byte r = (byte)0;
                        byte g = (byte)0xFF;
                        byte b = (byte)0;
                        bytes = new byte[]{
                                (byte) 0x3F,
                                (byte) 0x00,
                                (byte) ((int)length),
                                (byte) (r&0xF8 | (g&0xE0)>>5),
                                (byte) ((g&0x18)<<3 | (b&0xF8)>>2)
                        };
                        new SendPacket().execute(bytes);

                    } catch (Exception e) {
                        Log.d("t",e.toString());
                    }
                }
                return true;
            }
        });

        bBlue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                byte[] bytes;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        float length = 4/((event.getX() / bWhite.getWidth()));
                        if(length > 255){
                            length = 255;
                        }                        byte r = (byte)0;
                        byte g = (byte)0;
                        byte b = (byte)0xFF;
                        bytes = new byte[]{
                                (byte) 0x3F,
                                (byte) 0x00,
                                (byte) ((int)length),
                                (byte) (r&0xF8 | (g&0xE0)>>5),
                                (byte) ((g&0x18)<<3 | (b&0xF8)>>2)
                        };
                        new SendPacket().execute(bytes);

                    } catch (Exception e) {
                        Log.d("t",e.toString());
                    }
                }
                return true;
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

