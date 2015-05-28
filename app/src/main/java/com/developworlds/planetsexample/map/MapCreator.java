package com.developworlds.planetsexample.map;

import com.sudoplay.joise.Joise;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;

/**
 * Created by benjamin-sergent on 5/22/15.
 */
public class MapCreator {
    private static final Joise base;
    private static final Joise detail;
    private static final Joise rain;

    static {
        ModuleFractal gen = new ModuleFractal();
        gen.setAllSourceBasisTypes(ModuleBasisFunction.BasisType.SIMPLEX);
        gen.setNumOctaves(8);
        gen.setFrequency(2);
        gen.setType(ModuleFractal.FractalType.RIDGEMULTI);
        gen.setSeed(1);

        ModuleAutoCorrect correct = new ModuleAutoCorrect();
        correct.setSource(gen);
        correct.setRange(0.25, 0.75);
        correct.setSamples(100);
        correct.calculate();
        base = new Joise(correct);

        gen = new ModuleFractal();
        gen.setNumOctaves(8);
        gen.setFrequency(2);
        gen.setType(ModuleFractal.FractalType.RIDGEMULTI);
        gen.setSeed(2);

        correct = new ModuleAutoCorrect();
        correct.setSource(gen);
        correct.setRange(-0.25, 0.25);
        correct.setSamples(100);
        correct.calculate();

        detail = new Joise(correct);

        gen = new ModuleFractal();
        gen.setNumOctaves(8);
        gen.setFrequency(2);
        gen.setType(ModuleFractal.FractalType.RIDGEMULTI);
        gen.setSeed(3);

        correct = new ModuleAutoCorrect();
        correct.setSource(gen);
        correct.setRange(0, 1);
        correct.setSamples(100);
        correct.calculate();

        rain = new Joise(correct);
    }

    public static void generateTileMap(TileMap chunk, int startX, int startY, int width, int height) {
        // Make basic structure
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                double tileHeight = base.get(x / 200.0, y / 200.0);
                chunk.setTileHeight(x - startX, y - startY, tileHeight);
            }
        }

        // Increase complexity
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                double tileHeight = detail.get(x / 40.0, y / 40.0);
                double currHeight = chunk.getTileHeight(x - startX, y - startY);
                chunk.setTileHeight(x - startX, y - startY, currHeight + tileHeight);
            }
        }

        // Add Trees
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                // Offset so it doesn't match terrain
                double rainLevel = rain.get(x / 300.0, y / 300.0);
                chunk.setRainLevel(x - startX, y - startY, rainLevel);
            }
        }
    }

}
