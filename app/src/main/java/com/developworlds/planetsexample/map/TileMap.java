package com.developworlds.planetsexample.map;

/**
 * Created by benjamin-sergent on 5/22/15.
 */
public class TileMap {
    private final int width;
    private final int height;
    private double[] heightMap;

    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;

        heightMap = new double[width * height];

        for (int index = 0; index < heightMap.length; index++) {
            heightMap[index] = 0;
        }
    }

    public void setTileHeight(int x, int y, double height) {
        heightMap[getIndex(x, y)] = height;
    }

    public TileType getTile(int x, int y) {
        if (x < 0 || x > width - 1 ||
                y < 0 || y > height - 1) {
            return TileType.Error;
        }

        TileType type = getTileType(heightMap[getIndex(x, y)]);

        //TODO: Forests

        return type;
    }

    private static TileType getTileType(double tileHeight) {
        if (tileHeight < 0.5) {
            return TileType.Water;
        } else if (tileHeight < 0.75) {
            return TileType.Grass;
        } else if (tileHeight < 0.8) {
            return TileType.LowMountian;
        } else {
            return TileType.HighMoutian;
        }
    }

    private int getIndex(int x, int y) {
        return x + y * width;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getTileHeight(int x, int y) {
        return heightMap[getIndex(x, y)];
    }
}
