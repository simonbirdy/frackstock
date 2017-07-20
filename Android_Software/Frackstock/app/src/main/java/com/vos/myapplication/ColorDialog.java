package com.vos.myapplication;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vos.myapplication.R;


/**
 *
 *  DialogFragment for selecting a color effect
 *
 *  @author Beat Käfer, Simon Vogel
 *
 */
public class ColorDialog extends DialogFragment {

    /* GUI Elements */
    private Button okButton;
    private ColorWheel colorWheel;

    /* Value fields */
    private int direction;
    private float[] color1,color2;
    /* Listener */
    ColorDialogResultListener colorDialogResultListener;


    public void setColorDialogResultListener(ColorDialogResultListener colorDialogResultListener) {
        this.colorDialogResultListener = colorDialogResultListener;
    }

    /**
     * Set the attributes of the dialog
     *
     * @param color1       Start color
     * @param color2       End color
     * @param direction    Effect direction (use ColorWheel.DIR_MODE_CCW/CW)
     *
     *
     */
    public void setAttr(float[] color1, float[] color2, int direction){
        this.color1 = color1;
        this.color2 = color2;
        this.direction = direction;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_color, container, false);

        getDialog().setTitle("ColorPicker Dialog");
        //Get GUI Elements
        colorWheel = (ColorWheel) rootView.findViewById(R.id.dialogColorWheel);
        okButton = (Button) rootView.findViewById(R.id.okButton);

        //ColorWheel
        colorWheel.setMode(ColorWheel.MODE_ONE_COLOR);
        colorWheel.setColor(new float[][]{{1.0f,1.0f,1.0f},{1.0f,1.0f,1.0f}});



        //Ok Button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[][] colors = colorWheel.getColor();
                if(colorDialogResultListener != null) {
                    colorDialogResultListener.colorSelected(colors[0], colors[1], 1);
                }
                dismiss();
            }
        });

        return rootView;
    }

    public interface ColorDialogResultListener{
        /**
         * colorSelected is called after an effect is selected
         * @param Color1        Start Color
         * @param Color2        End color
         * @param direction     direction
         */
        void colorSelected(float[] Color1, float[] Color2, int direction);
    }
}

