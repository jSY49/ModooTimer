package com.jaysdevapp.modootimer;

import android.widget.NumberPicker;

public class SetNumberPicker{

    NumberPicker picker1, picker2,picker3;
    int hour,min,sec;

    SetNumberPicker(NumberPicker _picker1,NumberPicker _picker2,NumberPicker _picker3){
        picker1 = _picker1;
        picker2 = _picker2;
        picker3 = _picker3;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public int getSec() {
        return sec;
    }

    public void setting(){
        picker1.setMaxValue(23);
        picker2.setMaxValue(59);
        picker3.setMaxValue(59);

        picker1.setMinValue(0);
        picker2.setMinValue(0);
        picker3.setMinValue(0);

        picker1.setWrapSelectorWheel(true);
        picker2.setWrapSelectorWheel(true);
        picker3.setWrapSelectorWheel(true);

        picker1.setOnValueChangedListener((picker, oldVal, newVal) -> hour = newVal);
        picker2.setOnValueChangedListener((picker, oldVal, newVal) -> min = newVal);
        picker3.setOnValueChangedListener((picker, oldVal, newVal) -> sec = newVal);
    }

}
