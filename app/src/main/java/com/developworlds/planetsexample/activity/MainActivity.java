package com.developworlds.planetsexample.activity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.developworlds.planetsexample.R;
import com.developworlds.planetsexample.map.MapCreator;
import com.developworlds.planetsexample.map.TileMap;
import com.developworlds.planetsexample.view.MapView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends Activity {
    public static final int TILE_SIZE = 128;
    private static int MAP_SIZE = 250;
    private int stepNum = 0;
    private TileMap map;
    private int width;
    private int height;

    ExecutorService executor = Executors.newFixedThreadPool(2);
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cacheScreenSize();

        map = new TileMap(MAP_SIZE, MAP_SIZE);

        mapView = (MapView) findViewById(R.id.map_image);
        mapView.setMap(map);
        mapView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });

        executor.execute(createMap);
    }

    public Runnable createMap = new Runnable() {
        public void run() {
            generateMap();
        }
    };
    

    private void cacheScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }

    private void generateMap() {
        MapCreator.generateTileMap(map, 0, 0, MAP_SIZE, MAP_SIZE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            if(event.getHistorySize() == 0) {
                return false;
            }
            float x = event.getX() - event.getHistoricalX(0);
            float y = event.getY() - event.getHistoricalY(0);


            mapView.move(-x / 50.0f, -y / 50.0f);


            return true;
        }

        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
