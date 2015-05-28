package com.developworlds.planetsexample.map;

public class TileMap {
    private final int width;
    private final int height;
    private double[] heightMap;
    private double[] rainMap;

    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;

        heightMap = new double[width * height];
        rainMap = new double[width * height];
    }

    public void setTileHeight(int x, int y, double height) {
        heightMap[getIndex(x, y)] = height;
    }

    public void setRainLevel(int x, int y, double height) {
        rainMap[getIndex(x, y)] = height;
    }

    public TileType getTile(int x, int y) {
        if (x < 0 || x > width - 1 ||
                y < 0 || y > height - 1) {
            return TileType.Error;
        }

        int index = getIndex(x, y);
        TileType type = getTileType(heightMap[index]);

        if (type == TileType.Grass && rainMap[index] > 0.7f) {
            return TileType.Forest;
        }

        return type;
    }

    private static TileType getTileType(double tileHeight) {
        if (tileHeight < 0.5) {
            return TileType.Water;
        } else if (tileHeight < 0.75) {
            return TileType.Grass;
        } else if (tileHeight < 0.8) {
            return TileType.LowMountain;
        } else {
            return TileType.TallMoutain;
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
