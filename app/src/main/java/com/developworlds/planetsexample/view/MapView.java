package com.developworlds.planetsexample.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.developworlds.planetsexample.R;
import com.developworlds.planetsexample.activity.MainActivity;
import com.developworlds.planetsexample.map.TileMap;
import com.developworlds.planetsexample.map.TileType;

public class MapView extends SurfaceView implements SurfaceHolder.Callback {
    private static final long SIXTY_FPS_MS = 16;
    private static final String TAG = SurfaceView.class.getSimpleName();
    private float yPos;
    private float xPos;
    private boolean isRunning = true;
    private TileMap map;
    private Bitmap grass;
    private Bitmap lowMountian;
    private Bitmap highMountian;

    private Canvas canvas;
    private WaterTileHelper waterTileHelper;
    public static Bitmap blankBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

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
        lowMountian = BitmapFactory.decodeResource(getResources(), R.drawable.aw_mountian);
        highMountian = BitmapFactory.decodeResource(getResources(), R.drawable.aw_mountiantall);

        getHolder().addCallback(this);
    }


    private void startDrawThread() {
        Log.d(TAG, "Starting draw thread");
        new Thread() {
            public void run() {


                while (isRunning) {
                    try {
                        canvas = null;

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

    public void doDraw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawARGB(255, 0, 0, 0);
        drawMap(canvas);
    }

    public void drawMap(Canvas canvas) {
        if (map == null) {
            return;
        }

        final int TALL_MARGIN = -40;

        int xSubTileOffset = (int) -((xPos - (int) xPos) * MainActivity.TILE_SIZE);
        int ySubTileOffset = (int) -((yPos - (int) yPos) * MainActivity.TILE_SIZE);

        for (int x = (int) xPos - 2; x < xPos + canvas.getWidth() / MainActivity.TILE_SIZE + 2; x++) {
            for (int y = (int) yPos - 2; y < yPos + canvas.getHeight() / MainActivity.TILE_SIZE + 2; y++) {
                TileType type = map.getTile(x, y);
                int yMargin = 0;
                if (type == TileType.HighMoutian) {
                    yMargin = TALL_MARGIN;
                }

                int screenX = xSubTileOffset + (int) (x - xPos) * MainActivity.TILE_SIZE;
                int screenY = ySubTileOffset + (int) (y - yPos) * MainActivity.TILE_SIZE + yMargin;

                Bitmap bitmap = null;
                if (type == TileType.Water) {
                    bitmap = waterTileHelper.getWaterTile(map, x, y);;
                } else {
                    bitmap = getTile(type);
                }

                canvas.drawBitmap(bitmap, screenX, screenY, null);
            }
        }

    }

    private Bitmap getTile(TileType tile) {
        switch (tile) {

            case Error:
                return blankBitmap;
            case Grass:
                return grass;
            case LowMountian:
                return lowMountian;
            case HighMoutian:
                return highMountian;
            default:
                return blankBitmap;
        }
    }


    public void move(float x, float y) {
        synchronized (getHolder()) {
            xPos += x;
            yPos += y;

            xPos = Math.min(map.getWidth() - (getWidth() / MainActivity.TILE_SIZE), xPos);
            xPos = Math.max(0, xPos);

            yPos = Math.min(map.getHeight() - (getHeight() / MainActivity.TILE_SIZE), yPos);
            yPos = Math.max(0, yPos);
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Surface Created");
        isRunning = true;
        startDrawThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    public void setMap(TileMap map) {
        this.map = map;
    }


}
