package com.phyte.sanraphindustries.wallpaper;




import android.graphics.Canvas;
import android.graphics.Movie;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.IOException;



/**
 * Created by jamb on 2/20/2018.
 */


public class GIFWallpaperService extends WallpaperService {

    @Override
    public GIFWallpaperService.Engine onCreateEngine() {
        try {
            Movie movie = Movie.decodeStream(getResources().getAssets().open("motion.gif"));
            return new GIFWallpaperEngine(movie);
        }catch (IOException e){
            Toast.makeText(this, "Couldn't load resources", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private class GIFWallpaperEngine extends GIFWallpaperService.Engine{
        private final int frameDuration = 20;

        private SurfaceHolder holder;
        private Movie movie;
        private boolean visible = true;
        private android.os.Handler handler;

        private int height;
        int width;

        private GIFWallpaperEngine(Movie movie){
            this.movie = movie;
            handler = new android.os.Handler();

        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
        }


        private Runnable drawGIF = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };
        private void draw(){
            if(visible){
                Canvas canvas = holder.lockCanvas();
                canvas.save();
                //adjust size and position of your wallpaper image
                canvas.scale(3f, 3f);
                movie.draw(canvas, -100, 0);
                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
                movie.setTime((int) (System.currentTimeMillis() % movie.duration() ));


                handler.removeCallbacks(drawGIF);
                handler.postDelayed(drawGIF, frameDuration);

            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if(visible){
                handler.post(drawGIF);
            }else {
                handler.removeCallbacks(drawGIF);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            this.width = width;
            this.height = height;
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            this.visible = false;
            handler.removeCallbacks(drawGIF);
        }
    }
}
