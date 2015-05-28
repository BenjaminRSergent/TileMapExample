package com.developworlds.tilemapexample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.developworlds.tilemapexample.R;
import com.developworlds.tilemapexample.map.MapCreator;
import com.developworlds.tilemapexample.map.TileMap;
import com.developworlds.tilemapexample.view.MapView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends Activity {
    private static final float TOUCH_MOVEMENT_SCALE = 50.0f;
    private static int MAP_SIZE = 250;

    private TileMap map;
    private MapView mapView;
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private boolean generating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        if (map == null) {
            map = new TileMap(MAP_SIZE, MAP_SIZE);
            executor.execute(createMap);
        }

        mapView = (MapView) findViewById(R.id.map_image);
        mapView.setMap(map);
    }

    private final Runnable createMap = new Runnable() {
        public void run() {
            generating = true;
            MapCreator mapCreator = new MapCreator();
            mapCreator.generateTileMap(map, 0, 0, MAP_SIZE, MAP_SIZE);
            generating = false;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getHistorySize() == 0) {
                // Can't calculate movement amounts.
                return false;
            }
            float x = event.getX() - event.getHistoricalX(0);
            float y = event.getY() - event.getHistoricalY(0);

            // Move one tile for every TOUCH_MOVEMENT_SCALE pixels that the touch moves
            // on the screen.
            mapView.move(-x / TOUCH_MOVEMENT_SCALE, -y / TOUCH_MOVEMENT_SCALE);

            return true;
        }

        return false;
    }

    public void regen(View view) {
        if(!generating) {
            executor.execute(createMap);
        }
    }
}
