package com.developworlds.tilemapexample.map;

import com.sudoplay.joise.Joise;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;

public class MapCreator {
    private Joise base;
    private Joise detail;
    private Joise rain;

    // Decent default values
    private double detailWeight = 0.25;
    private double baseZoomLevel = 800.0;
    private double detailZoomDivisor = 2.0;
    private double rainZoomDivisor = 8.0;

    public void generateTileMap(TileMap map, int startX, int startY, int width, int height) {
        createNoiseGenerators();

        /*
        For each of these layers, the x and y values are divided by positive integers which
        can be though of as zoom levels.

        Simplex produces white noise if you only poll it at integer values. The smoothness
        occurs between the integers. Thus, the larger your divisor, the higher you noise will
        "zoom into" the smooth area between the white noise..
        */
        createBasicTerrain(map, startX, startY, width, height);
        addDetail(map, startX, startY, width, height);
        addRain(map, startX, startY, width, height);
    }

    private void createBasicTerrain(TileMap map, int startX, int startY, int width, int height) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                double tileHeight = base.get(x / baseZoomLevel, y / baseZoomLevel);
                map.setTileHeight(x - startX, y - startY, tileHeight);
            }
        }
    }

    private void addDetail(TileMap map, int startX, int startY, int width, int height) {
        double detailZoomLevel = baseZoomLevel / detailZoomDivisor;
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                double tileHeight = detail.get(x / detailZoomLevel, y / detailZoomLevel);
                double currHeight = map.getTileHeightAt(x - startX, y - startY);
                map.setTileHeight(x - startX, y - startY, currHeight + tileHeight);
            }
        }
    }

    private void addRain(TileMap map, int startX, int startY, int width, int height) {
        double rainZoomLevel = baseZoomLevel / rainZoomDivisor;
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                double rainLevel = rain.get(x / rainZoomLevel, y / rainZoomLevel);
                map.setRainLevel(x - startX, y - startY, rainLevel);
            }
        }
    }

    private void createNoiseGenerators() {
        // The base will be used to create the outline of the terrain. It will be used
        // at a higher zoom level then the detail and requires fewer octaves to work well.
        base = createSimplexGenerator(6, 2, detailWeight, 1 - detailWeight);

        // The detail makes the basic terrain more varied with a high zoom level, but low
        // weight.
        detail = createSimplexGenerator(8, 2, -detailWeight, detailWeight);

        // We want the forests to have soft edges, so the rain generator uses a low
        // number of octaves.
        rain = createSimplexGenerator(4, 2, 0, 1);
    }

    private Joise createSimplexGenerator(int octaves, int frequency, double min, double max) {
        long seed = (long) (Math.random() * Long.MAX_VALUE);

        ModuleFractal gen = new ModuleFractal();
        gen.setAllSourceBasisTypes(ModuleBasisFunction.BasisType.SIMPLEX);
        gen.setNumOctaves(octaves);
        gen.setFrequency(frequency);
        gen.setType(ModuleFractal.FractalType.RIDGEMULTI);
        gen.setSeed(seed);

        ModuleAutoCorrect correct = new ModuleAutoCorrect();
        correct.setSource(gen);
        correct.setRange(min, max);
        correct.setSamples(100);
        correct.calculate();
        return new Joise(correct);
    }


    public double getDetailWeight() {
        return detailWeight;
    }

    public void setDetailWeight(double detailWeight) {
        this.detailWeight = detailWeight;
    }

    public double getBaseZoomLevel() {
        return baseZoomLevel;
    }

    public void setBaseZoomLevel(double baseZoomLevel) {
        this.baseZoomLevel = baseZoomLevel;
    }

    public double getDetailZoomDivisor() {
        return detailZoomDivisor;
    }

    public void setDetailZoomDivisor(double detailZoomDivisor) {
        this.detailZoomDivisor = detailZoomDivisor;
    }

    public double getRainZoomDivisor() {
        return rainZoomDivisor;
    }

    public void setRainZoomDivisor(double rainZoomDivisor) {
        this.rainZoomDivisor = rainZoomDivisor;
    }
}
