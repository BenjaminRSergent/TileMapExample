package com.developworlds.planetsexample.map;

import com.sudoplay.joise.Joise;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;

public class MapCreator {
    private final Joise base;
    private final Joise detail;
    private final Joise rain;

    private final double BASE_ZOOM_LEVEL = 200.0;
    private final double DETAIL_ZOOM_LEVEL = BASE_ZOOM_LEVEL / 4.0;
    private final double RAIN_ZOOM_LEVEL = BASE_ZOOM_LEVEL / 2.0;
    private final double DETAIL_EFFECT_LEVEL = 0.25;

    public MapCreator() {
        base = createSimplexGenerator(6, 2, DETAIL_EFFECT_LEVEL, 1 - DETAIL_EFFECT_LEVEL);
        detail = createSimplexGenerator(8, 2, -DETAIL_EFFECT_LEVEL, DETAIL_EFFECT_LEVEL);
        rain = createSimplexGenerator(6, 2, 0, 1);
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

    public void generateTileMap(TileMap map, int startX, int startY, int width, int height) {
        createBasicTerrain(map, startX, startY, width, height);
        addDetail(map, startX, startY, width, height);
        addRain(map, startX, startY, width, height);
    }

    private void createBasicTerrain(TileMap map, int startX, int startY, int width, int height) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                double tileHeight = base.get(x / BASE_ZOOM_LEVEL, y / BASE_ZOOM_LEVEL);
                map.setTileHeight(x - startX, y - startY, tileHeight);
            }
        }
    }

    private void addDetail(TileMap map, int startX, int startY, int width, int height) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                double tileHeight = detail.get(x / DETAIL_ZOOM_LEVEL, y / DETAIL_ZOOM_LEVEL);
                double currHeight = map.getTileHeightAt(x - startX, y - startY);
                map.setTileHeight(x - startX, y - startY, currHeight + tileHeight);
            }
        }
    }

    private void addRain(TileMap map, int startX, int startY, int width, int height) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                double rainLevel = rain.get(x / RAIN_ZOOM_LEVEL, y / RAIN_ZOOM_LEVEL);
                map.setRainLevel(x - startX, y - startY, rainLevel);
            }
        }
    }
}
