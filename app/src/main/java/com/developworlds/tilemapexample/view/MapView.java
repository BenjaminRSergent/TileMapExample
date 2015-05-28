package com.developworlds.tilemapexample.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.developworlds.tilemapexample.R;
import com.developworlds.tilemapexample.map.TileMap;
import com.developworlds.tilemapexample.map.TileType;

public class MapView extends SurfaceView implements SurfaceHolder.Callback, TileMap.MapModifiedListener {
    private static final String TAG = SurfaceView.class.getSimpleName();
    private static final long SIXTY_FPS_MS = 1000 / 60;
    private static final Bitmap blankBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    private static final int TILE_SIZE = 16;
    // The tall mountain is 10 px higher when the tile size is 32
    private static final int TALL_MOUNTAIN_OFFSET = TILE_SIZE * 10 / 32;

    private boolean isDrawThreadRunning = true;
    private final PointF tilePosition = new PointF(0, 0);
    private TileMap map;

    private Bitmap grass;
    private Bitmap forest;
    private Bitmap lowMountain;
    private Bitmap tallMountain;
    private WaterTileHelper waterTileHelper;

    private Bitmap cachedScreen;
    private Rect fullScreen;
    private boolean needToRedraw = true;
    private Canvas cachedBitmapCanvas;


    public MapView(Context context) {
        super(context);
        init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        waterTileHelper = new WaterTileHelper(getContext());
        grass = BitmapFactory.decodeResource(getResources(), R.drawable.aw_grass);
        lowMountain = BitmapFactory.decodeResource(getResources(), R.drawable.aw_mountian);
        tallMountain = BitmapFactory.decodeResource(getResources(), R.drawable.aw_mountiantall);
        forest = BitmapFactory.decodeResource(getResources(), R.drawable.aw_forest);

        getHolder().addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Surface Created");

        cachedScreen = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        cachedBitmapCanvas = new Canvas(cachedScreen);
        fullScreen = new Rect(0, 0, getWidth(), getHeight());

        isDrawThreadRunning = true;
        startDrawThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawThreadRunning = false;
    }

    private void startDrawThread() {
        Log.d(TAG, "Starting draw thread");
        new Thread() {
            public void run() {
                while (isDrawThreadRunning) {
                    try {
                        Canvas canvas = null;

                        try {
                            synchronized (getHolder()) {
                                canvas = getHolder().lockCanvas(null);
                                if (canvas != null) {
                                    doDraw(canvas);
                                }
                            }
                        } finally {
                            if (canvas != null) {
                                getHolder().unlockCanvasAndPost(canvas);
                            }
                        }

                        try {
                            Thread.sleep(SIXTY_FPS_MS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                Log.d(TAG, "Stopping draw thread");
            }
        }.start();
    }

    private void doDraw(Canvas canvas) {
        super.draw(canvas);

        if (needToRedraw) {

            canvas.drawARGB(255, 0, 0, 0);
            drawMap();
            needToRedraw = false;
        }

        canvas.drawBitmap(cachedScreen, null, fullScreen, null);
    }

    private void drawMap() {
        if (map == null) {
            return;
        }

        int xSubTileOffset = (int) -((tilePosition.x - (int) tilePosition.x) * TILE_SIZE);
        int ySubTileOffset = (int) -((tilePosition.y - (int) tilePosition.y) * TILE_SIZE);

        for (int x = (int) tilePosition.x - 2; x < tilePosition.x + cachedBitmapCanvas.getWidth() / TILE_SIZE + 2; x++) {
            for (int y = (int) tilePosition.y - 2; y < tilePosition.y + cachedBitmapCanvas.getHeight() / TILE_SIZE + 2; y++) {
                int screenX = xSubTileOffset + (int) (x - tilePosition.x) * TILE_SIZE;
                int screenY = ySubTileOffset + (int) (y - tilePosition.y) * TILE_SIZE;

                TileType type = map.getTile(x, y);
                if (type == TileType.TallMountain) {
                    screenY -= TALL_MOUNTAIN_OFFSET;
                }

                Bitmap bitmap;
                if (type == TileType.Water) {
                    bitmap = waterTileHelper.getWaterTile(map, x, y);
                } else {
                    bitmap = getTile(type);
                }


                int tileWidth = TILE_SIZE;
                int tileHeight = bitmap.getHeight() * TILE_SIZE / bitmap.getWidth();
                cachedBitmapCanvas.drawBitmap(bitmap, null, new Rect(screenX, screenY, screenX + tileWidth, screenY + tileHeight), null);
            }
        }

    }

    private Bitmap getTile(TileType tile) {
        switch (tile) {
            case Grass:
                return grass;
            case LowMountain:
                return lowMountain;
            case TallMountain:
                return tallMountain;
            case Forest:
                return forest;
            default:
                return blankBitmap;
        }
    }

    public void move(float x, float y) {
        if (x == 0 && y == 0) {
            return;
        }
        synchronized (getHolder()) {
            tilePosition.x += x;
            tilePosition.y += y;

            tilePosition.x = Math.min(map.getMapWidth() - (getWidth() / TILE_SIZE), tilePosition.x);
            tilePosition.x = Math.max(0, tilePosition.x);

            tilePosition.y = Math.min(map.getMapHeight() - (getHeight() / TILE_SIZE), tilePosition.y);
            tilePosition.y = Math.max(0, tilePosition.y);
        }

        needToRedraw = true;
    }

    public void setMap(TileMap map) {
        this.map = map;
        map.setMapModifiedListener(this);
    }


    @Override
    public void onMapModified() {
        needToRedraw = true;
    }
}
