package com.droids.tamada.filemanager.Utils;

import android.content.res.Resources;

/**
 * Created by satish on 28/10/16.
 */

public class Utils {

    private Utils() {
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
