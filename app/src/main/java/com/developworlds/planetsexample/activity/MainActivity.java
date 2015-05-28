package com.developworlds.planetsexample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.developworlds.planetsexample.R;
import com.developworlds.planetsexample.map.MapCreator;
import com.developworlds.planetsexample.map.TileMap;
import com.developworlds.planetsexample.view.MapView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends Activity {
    private static final float TOUCH_MOVEMENT_SCALE = 50.0f;
    private static int MAP_SIZE = 250;
    private TileMap map;
    private MapView mapView;
    ExecutorService executor = Executors.newFixedThreadPool(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = new TileMap(MAP_SIZE, MAP_SIZE);

        mapView = (MapView) findViewById(R.id.map_image);
        mapView.setMap(map);

        executor.execute(createMap);
    }

    public Runnable createMap = new Runnable() {
        public void run() {
            MapCreator.generateTileMap(map, 0, 0, MAP_SIZE, MAP_SIZE);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getHistorySize() == 0) {
                return false;
            }
            float x = event.getX() - event.getHistoricalX(0);
            float y = event.getY() - event.getHistoricalY(0);

            mapView.move(-x / TOUCH_MOVEMENT_SCALE, -y / TOUCH_MOVEMENT_SCALE);

            return true;
        }

        return false;
    }
}
