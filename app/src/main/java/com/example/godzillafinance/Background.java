package com.example.godzillafinance;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background {

    // Supporting class for changing the way the background of the game changes.

    int x = 0, y = 0;
    Bitmap background;

    Background (int screenX, int screenY, Resources res) {

        background = BitmapFactory.decodeResource(res, R.drawable.sky);
        background = Bitmap.createScaledBitmap(background, screenX, screenY, false);

    }
}
