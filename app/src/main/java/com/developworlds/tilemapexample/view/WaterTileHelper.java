package com.developworlds.tilemapexample.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.developworlds.tilemapexample.R;
import com.developworlds.tilemapexample.map.TileMap;
import com.developworlds.tilemapexample.map.TileType;

public class WaterTileHelper {
    private static final Bitmap blankBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

    private static final int LEFT = 1;
    private static final int RIGHT = 1 << 1;
    private static final int TOP = 1 << 2;
    private static final int BOTTOM = 1 << 3;
    private static final int ORTHO_SUM = LEFT + RIGHT + TOP + BOTTOM;
    private final Bitmap[] waterTiles = new Bitmap[ORTHO_SUM + 1];

    private static final int UPPER_LEFT = 0;
    private static final int UPPER_RIGHT = 1;
    private static final int LOWER_LEFT = 2;
    private static final int LOWER_RIGHT = 3;
    private final Bitmap[] cornerTiles = new Bitmap[4];


    public WaterTileHelper(Context context) {
        createWater(context);
    }

    private void createWater(Context context) {
        waterTiles[getWaterCode(true, true, true, true)] = getBitmap(context, R.drawable.aw_watercenter);
        waterTiles[getWaterCode(true, true, true, false)] = getBitmap(context, R.drawable.aw_waterlowermiddle);
        waterTiles[getWaterCode(true, true, false, true)] = getBitmap(context, R.drawable.aw_watermiddleright);
        waterTiles[getWaterCode(true, true, false, false)] = getBitmap(context, R.drawable.aw_waterlowerright);
        waterTiles[getWaterCode(true, false, true, true)] = getBitmap(context, R.drawable.aw_wateruppermiddle);
        waterTiles[getWaterCode(true, false, true, false)] = getBitmap(context, R.drawable.aw_waterchannelhorizontal);
        waterTiles[getWaterCode(true, false, false, true)] = getBitmap(context, R.drawable.aw_waterupperright);
        waterTiles[getWaterCode(true, false, false, false)] = getBitmap(context, R.drawable.aw_rightchannelend);

        waterTiles[getWaterCode(false, true, true, true)] = getBitmap(context, R.drawable.aw_watermiddleleft);
        waterTiles[getWaterCode(false, true, true, false)] = getBitmap(context, R.drawable.aw_waterlowerleft);
        waterTiles[getWaterCode(false, true, false, true)] = getBitmap(context, R.drawable.aw_waterchannelvertical);
        waterTiles[getWaterCode(false, true, false, false)] = getBitmap(context, R.drawable.aw_lowerchannelend);
        waterTiles[getWaterCode(false, false, true, true)] = getBitmap(context, R.drawable.aw_waterupperleft);
        waterTiles[getWaterCode(false, false, true, false)] = getBitmap(context, R.drawable.aw_leftchannelend);
        waterTiles[getWaterCode(false, false, false, true)] = getBitmap(context, R.drawable.aw_upperchannelend);
        waterTiles[getWaterCode(false, false, false, false)] = getBitmap(context, R.drawable.aw_waterisland);

        // I've already spent too much time on water logic. We'll only handle single corners.
        // The result is good enough.
        cornerTiles[UPPER_LEFT] = getBitmap(context, R.drawable.aw_waterupperlefttip);
        cornerTiles[UPPER_RIGHT] = getBitmap(context, R.drawable.aw_waterupperrighttip);
        cornerTiles[LOWER_LEFT] = getBitmap(context, R.drawable.aw_waterlowerlefttip);
        cornerTiles[LOWER_RIGHT] = getBitmap(context, R.drawable.aw_waterlowerrighttip);
    }

    private Bitmap getBitmap(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public Bitmap getWaterTile(TileMap map, int x, int y) {
        if (map.getTile(x, y) != TileType.Water) {
            return blankBitmap; // Not water
        }

        boolean leftIsWater = isWater(map.getTile(x - 1, y));
        boolean topIsWater = isWater(map.getTile(x, y - 1));
        boolean rightIsWater = isWater(map.getTile(x + 1, y));
        boolean bottomIsWater = isWater(map.getTile(x, y + 1));

        int waterCode = getWaterCode(leftIsWater, topIsWater, rightIsWater, bottomIsWater);
        if (waterCode == (ORTHO_SUM)) {
            // We either have a corner or a center piece
            return getCornerPiece(map, x, y);
        }

        return waterTiles[waterCode];
    }

    private Bitmap getCornerPiece(TileMap map, int x, int y) {
        if (!isWater(map.getTile(x + 1, y - 1))) {
            return cornerTiles[UPPER_RIGHT];
        } else if (!isWater(map.getTile(x + 1, y + 1))) {
            return cornerTiles[LOWER_RIGHT];
        } else if (!isWater(map.getTile(x - 1, y - 1))) {
            return cornerTiles[UPPER_LEFT];
        } else if (!isWater(map.getTile(x - 1, y + 1))) {
            return cornerTiles[LOWER_LEFT];
        } else {
            return waterTiles[ORTHO_SUM];
        }
    }

    // Generate a unique number from every combination of orthogonal water using binary powers
    private int getWaterCode(boolean waterToLeft, boolean waterToTop, boolean waterToRight, boolean waterToBottom) {
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

        return sum;
    }


    private boolean isWater(TileType tile) {
        // The edge of the map returns ERROR. Treating this as water looks better
        // then treating it as land.
        return tile == TileType.Water || tile == TileType.Error;
    }
}