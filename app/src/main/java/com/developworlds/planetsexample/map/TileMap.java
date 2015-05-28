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
        if (tileHeight < 0.4) {
            return TileType.Water;
        } else if (tileHeight < 0.75) {
            return TileType.Grass;
        } else if (tileHeight < 0.9) {
            return TileType.LowMountain;
        } else {
            return TileType.TallMoutain;
        }
    }


    public double getTileHeightAt(int x, int y) {
        return heightMap[getIndex(x, y)];
    }

    public void setTileHeight(int x, int y, double tileHeight) {
        int index = getIndex(x, y);
        if (index < 0 || index > width * height) {
            return;
        }
        heightMap[index] = tileHeight;
    }

    public double getRainLevelAt(int x, int y) {
        return rainMap[getIndex(x, y)];

    }

    public void setRainLevel(int x, int y, double height) {
        int index = getIndex(x, y);
        if (index < 0 || index > width * height) {
            return;
        }
        rainMap[index] = height;
    }

    private int getIndex(int x, int y) {
        return x + y * width;
    }

    public int getMapWidth() {
        return width;
    }

    public int getMapHeight() {
        return height;
    }
}
