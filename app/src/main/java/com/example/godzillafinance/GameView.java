package com.example.godzillafinance;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Surface View when we want to change the contents quickly
public class GameView extends SurfaceView implements Runnable {

    // Actual logic of how the game works.

    private GameActivity activity;
    private Background background1, background2;
    public static float screenRatioX, screenRatioY;
    // 2 background instances to show the background moving.
    private Thread thread;
    private int screenX, screenY, score = 0;
    private Paint paint;
    private List<Bullet> bullets;
    private Bird[] birds;
    private Flight flight;
    private Random random;
    private boolean isPlaying, isGameOver = false;

    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);
        this.activity = activity;
        this.screenX = screenX;
        this.screenY = screenY;

        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        flight = new Flight(this, screenY, getResources());

        background1 = new Background(screenX,screenY,getResources());
        background2 = new Background(screenX,screenY,getResources());
        background2.x = screenX;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        bullets = new ArrayList<>();

        birds = new Bird[4];

        for (int i = 0;i < 4;i++) {

            Bird bird = new Bird(getResources());
            birds[i] = bird;

        }

        random = new Random();


    }

    @Override
    public void run() {
        // called when thread is started.
        while (isPlaying) {
            update ();
            draw ();
            sleep ();

        }
    }
    private void update () {

        // everytime this method is called background is moved by 10 pixels
        background1.x -= 10 * screenRatioX;
        background2.x -= 10 * screenRatioX;

        // check if background is off the screen.
        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = screenX;
        }

        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = screenX;
        }
        if (flight.isGoingUp)
            flight.y -= 30 * screenRatioY;
        else
            flight.y += 30 * screenRatioY;

        if (flight.y < 0)
            flight.y = 0;

        if (flight.y >= screenY - flight.height)
            flight.y = screenY - flight.height;
        List<Bullet> trash = new ArrayList<>();

        for (Bullet bullet : bullets) {

            if (bullet.x > screenX)
                trash.add(bullet);

            bullet.x += 50 * screenRatioX;

            for(Bird bird: birds){
                if (Rect.intersects(bird.getCollisionShape(),
                        bullet.getCollisionShape())) {
                    bird.x = -500;
                    bullet.x = screenX + 500;
                    bird.wasShot = true;

                }
            }

        }
        for (Bullet bullet : trash) {
            bullets.remove(bullet);
        }
        for(Bird bird:birds){
            bird.x -= bird.speed;
            if (bird.x + bird.width < 0) {
                if (!bird.wasShot) {
                    isGameOver = true;
                    return;
                }
                int bound = (int) (30 * screenRatioX);
                bird.speed = random.nextInt(bound);

                if (bird.speed < 10 * screenRatioX)
                    bird.speed = (int) (10 * screenRatioX);

                bird.x = screenX;
                bird.y = random.nextInt(screenY - bird.height);
                bird.wasShot = false ;
            }
            if(Rect.intersects(bird.getCollisionShape(),flight.getCollisionShape())){
                isGameOver = true;
                return;
            }

        }
        }

    private void draw () {
        // after we have the coordinates where we need to draw the new background,
        // we call the draw method.
        // canvas is present in the surfaceview class
        if (getHolder().getSurface().isValid()) {

            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);
            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);

            if(isGameOver){
                isPlaying = false;
                canvas.drawBitmap(flight.getDead(), flight.x, flight.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                return;
            }
            for(Bird bird:birds){
                canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);
            }

            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);

            for (Bullet bullet : bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }

    }
    private void sleep () {
        try {
            // 1000 / 60
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void resume () {
        // Starting the game.
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
        // run method is called here()

    }

    public void pause () {
        // Executed when game is paused.
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < screenX / 2) {
                    flight.isGoingUp = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                flight.isGoingUp = false;
                if (event.getX() > screenX / 2)
                    flight.toShoot++;
                break;
        }

        return true;
    }

    public void newBullet() {

        Bullet bullet = new Bullet(getResources());
        bullet.x = flight.x + flight.width;
        bullet.y = flight.y + (flight.height / 2);
        bullets.add(bullet);
    }
}
