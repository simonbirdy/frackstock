package com.vos.myapplication;

import android.graphics.Color;

/**
 * Created by simonvogel on 17.05.17.
 */
public class RGBUtils {
    public static byte[] rgbRound(int color,int bits){
        byte[] in = new byte[]{0,0,0};
        byte[] out = new byte[]{0,0,0};


        byte pattern = (byte)(((1<<bits)-1)<<(8-bits));

        in[0] = (byte)Color.red(color);
        in[1] = (byte)Color.green(color);
        in[2] = (byte)Color.blue(color);


        out[0] = (byte)(in[0]&pattern);
        if((in[0]&(1<<(7-bits))) != 0){
            if(out[0] != pattern){
                out[0] += 1<<(8-bits);
            }
        }
        out[1] = (byte)(in[1]&pattern);
        if((in[1]&(1<<(7-bits))) != 0){
            if(out[1] != pattern){
                out[1] += 1<<(8-bits);
            }
        }
        out[2] = (byte)(in[2]&pattern);
        if((in[2]&(1<<(7-bits))) != 0){
            if(out[2] != pattern){
                out[2] += 1<<(8-bits);
            }
        }

        return out;
    }

    public static int rgbBrightness(int pColor, int pBrightness){
        float[] floatcol = new float[3];
        Color.colorToHSV(pColor,floatcol);
        floatcol[2] = ((float)pBrightness) / 255.0f;
        int tmpcolor = Color.HSVToColor(floatcol);
        return  tmpcolor;
    }

}
