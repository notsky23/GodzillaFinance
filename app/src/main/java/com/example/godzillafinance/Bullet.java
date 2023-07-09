package com.example.godzillafinance;

import static com.example.godzillafinance.GameView.screenRatioX;
import static com.example.godzillafinance.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Bullet {
    int x, y, width, height;
    Bitmap bullet;
    public boolean wasShot = true;
    Bullet (Resources res) {

        bullet = BitmapFactory.decodeResource(res, R.drawable.bullet);

        width = bullet.getWidth();
        height = bullet.getHeight();

        width /= 4;
        height /= 4;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);

    }
    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }

}
