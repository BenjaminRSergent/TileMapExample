package com.developworlds.planetsexample.map;

import com.sudoplay.joise.Joise;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;

/**
 * Created by benjamin-sergent on 5/22/15.
 */
public class MapCreator {
    private static final Joise joise;
    private static final Joise joiseHigh;

    static {
        ModuleFractal gen = new ModuleFractal();
        gen.setAllSourceBasisTypes(ModuleBasisFunction.BasisType.SIMPLEX);
        gen.setNumOctaves(8);
        gen.setFrequency(2);
        gen.setType(ModuleFractal.FractalType.RIDGEMULTI);
        gen.setSeed(27);

        ModuleFractal gen2 = new ModuleFractal();
        gen2.setNumOctaves(8);
        gen2.setFrequency(2);
        gen2.setType(ModuleFractal.FractalType.RIDGEMULTI);
        gen2.setSeed(100);

        ModuleAutoCorrect correct1 = new ModuleAutoCorrect();
        correct1.setSource(gen);
        correct1.setRange(0.25, 0.75);
        correct1.setSamples(10);
        correct1.calculate();

        ModuleAutoCorrect correct2 = new ModuleAutoCorrect();
        correct2.setSource(gen2);
        correct2.setRange(-0.25, 0.25);
        correct2.setSamples(10);
        correct2.calculate();


        joise = new Joise(correct1);
        joiseHigh = new Joise(correct2);
    }

    // Two loops allow us to watch the effect of a second high frequency pass
    // and makes to map visible quicker
    public static void generateTileMap(TileMap chunk, int startX, int startY, int width, int height) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                double tileHeight = joise.get(x / 200.0, y / 200.0);
                chunk.setTileHeight(x - startX, y - startY, tileHeight);
            }
        }

        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                double tileHeight = joiseHigh.get(x / 40.0, y / 40.0);
                double currHeight = chunk.getTileHeight(x - startX, y - startY);
                chunk.setTileHeight(x - startX, y - startY, currHeight + tileHeight);
            }
        }
    }


}
