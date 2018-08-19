package com.ch.cbptest.Binding;

import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class AnalysisBind {
    private final int SBP, MBP, DBP;
    private final int minWidth, maxWidth;

    public AnalysisBind(int SBP, int MBP, int DBP, int minWidth, int maxWidth) {
        this.DBP = DBP;
        this.SBP = SBP;
        this.MBP = MBP;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
    }

    public int getSBP() {
        return SBP;
    }

    public int getMBP() {
        return MBP;
    }

    public int getDBP() {
        return DBP;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    @BindingAdapter("android:layout_width")
    public static void setLayoutWidth(View view, float width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) width;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:progressTint")
    public static void setProgressTint(ProgressBar view, int value) {

        if(value < 80)
            view.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        else if(value <= 120)
            view.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        else
            view.setProgressTintList(ColorStateList.valueOf(Color.RED));
    }
}