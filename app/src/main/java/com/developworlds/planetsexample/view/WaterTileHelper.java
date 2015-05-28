package com.developworlds.planetsexample.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.developworlds.planetsexample.R;
import com.developworlds.planetsexample.map.TileMap;
import com.developworlds.planetsexample.map.TileType;

public class WaterTileHelper {
    private static final int LEFT = 1 << 0;
    private static final int RIGHT = 1 << 1;
    private static final int TOP = 1 << 2;
    private static final int BOTTOM = 1 << 3;
    private static final int ORTHO_SUM = LEFT + RIGHT + TOP + BOTTOM;
    private Bitmap[] waterTiles = new Bitmap[ORTHO_SUM];

    private static int UPPER_LEFT = 0;
    private static int UPPER_RIGHT = 1;
    private static int LOWER_LEFT = 2;
    private static int LOWER_RIGHT = 3;
    private Bitmap[] tipTiles = new Bitmap[4];


    public WaterTileHelper(Context context) {
        createWater(context);
    }

    private void createWater(Context context) {
        waterTiles[getWaterCode(true, true, true, true)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_watercenter);
        waterTiles[getWaterCode(true, true, true, false)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_waterlowermiddle);
        waterTiles[getWaterCode(true, true, false, true)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_watermiddleright);
        waterTiles[getWaterCode(true, true, false, false)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_waterlowerright);
        waterTiles[getWaterCode(true, false, true, true)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_wateruppermiddle);
        waterTiles[getWaterCode(true, false, true, false)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_waterchannelhorizontal);
        waterTiles[getWaterCode(true, false, false, true)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_waterupperright);
        waterTiles[getWaterCode(true, false, false, false)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_rightchannelend);

        waterTiles[getWaterCode(false, true, true, true)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_watermiddleleft);
        waterTiles[getWaterCode(false, true, true, false)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_waterlowerleft);
        waterTiles[getWaterCode(false, true, false, true)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_waterchannelvertical);
        waterTiles[getWaterCode(false, true, false, false)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_lowerchannelend);
        waterTiles[getWaterCode(false, false, true, true)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_waterupperleft);
        waterTiles[getWaterCode(false, false, true, false)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_leftchannelend);
        waterTiles[getWaterCode(false, false, false, true)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_upperchannelend);
        waterTiles[getWaterCode(false, false, false, false)] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_waterisland);

        tipTiles[UPPER_LEFT] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_waterupperlefttip);
        tipTiles[UPPER_RIGHT] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_waterupperrighttip);
        tipTiles[LOWER_LEFT] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_waterlowerlefttip);
        tipTiles[LOWER_RIGHT] = BitmapFactory.decodeResource(context.getResources(), R.drawable.aw_waterlowerrighttip);
    }

    // Generate a unique number from each combination using binary powers
    public int getWaterCode(boolean waterToLeft, boolean waterToTop, boolean waterToRight, boolean waterToBottom) {
        int sum = 0;
        if (waterToLeft) {
            sum += LEFT;
        }
        if (waterToTop) {
            sum += TOP;
        }
        if (waterToRight) {
            sum += RIGHT;
        }
        if (waterToBottom) {
            sum += BOTTOM;
        }

        return sum == 0 ?  sum : sum - 1;
    }

    public Bitmap getWaterTile(TileMap map, int x, int y) {
        if (map.getTile(x, y) != TileType.Water) {
            return MapView.blankBitmap; // Not water
        }

        boolean leftIsWater = isWater(map.getTile(x - 1, y));
        boolean topIsWater = isWater(map.getTile(x, y - 1));
        boolean rightIsWater = isWater(map.getTile(x + 1, y));
        boolean bottomIsWater = isWater(map.getTile(x, y + 1));

        int waterCode = getWaterCode(leftIsWater, topIsWater, rightIsWater, bottomIsWater);
        if (waterCode == (ORTHO_SUM - 1)) {
            // We either have a tip or a center piece
            return getTipPiece(map, x, y);
        }

        return waterTiles[waterCode];
    }

    // Treat the edge of the map as water
    private boolean isWater(TileType tile) {
        return tile  == TileType.Water || tile == TileType.Error;
    }

    private Bitmap getTipPiece(TileMap map, int x, int y) {
        if (!isWater(map.getTile(x + 1, y - 1))) {
            return tipTiles[UPPER_RIGHT];
        } else if (!isWater(map.getTile(x + 1, y + 1))) {
            return tipTiles[LOWER_RIGHT];
        } else if (!isWater(map.getTile(x - 1, y - 1))) {
            return tipTiles[UPPER_LEFT];
        } else if (!isWater(map.getTile(x - 1, y + 1))) {
            return tipTiles[LOWER_LEFT];
        } else {
            return waterTiles[ORTHO_SUM - 1];
        }
    }
}
