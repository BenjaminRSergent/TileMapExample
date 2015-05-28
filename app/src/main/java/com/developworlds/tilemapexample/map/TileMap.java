package com.developworlds.tilemapexample.map;

public class TileMap {
    private final int width;
    private final int height;

    // One dimensional arrays are generally better for the cache. Enough to provide a small
    // boost despite having to calculate the index from (x, y) pairs in this use case.
    private final double[] heightMap;
    private final double[] rainMap;

    // Decent default values
    private double waterLevel = 0.35;
    private double grassLevel = 0.70;
    private double mountainLevel = 0.8;

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

    private TileType getTileType(double tileHeight) {
        if (tileHeight < waterLevel) {
            return TileType.Water;
        } else if (tileHeight < grassLevel) {
            return TileType.Grass;
        } else if (tileHeight < mountainLevel) {
            return TileType.LowMountain;
        } else {
            return TileType.TallMountain;
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

    public void setRainLevel(int x, int y, double rainLevel) {
        int index = getIndex(x, y);
        if (index < 0 || index > width * height) {
            return;
        }
        rainMap[index] = rainLevel;
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

    public double getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(double waterLevel) {
        this.waterLevel = waterLevel;
    }

    public double getGrassLevel() {
        return grassLevel;
    }

    public void setGrassLevel(double grassLevel) {
        this.grassLevel = grassLevel;
    }

    public double getMountainLevel() {
        return mountainLevel;
    }

    public void setMountainLevel(double mountainLevel) {
        this.mountainLevel = mountainLevel;
    }
}
