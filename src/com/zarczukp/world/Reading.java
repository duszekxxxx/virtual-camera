package com.zarczukp.world;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Reading {
    public World createWorld(String filename) {
        World world = new World();
        try {
            File tmpfile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

            String path = tmpfile.getParent() + File.separator + "untitled.obj";
            File file = new File(path);
            System.out.println(file);
            try (Stream linesStream = Files.lines(file.toPath())) {

                List<RealVector> points = new ArrayList<>();
                linesStream.forEach(line -> {
                    String[] tmp = line.toString().split(" ");
                    if (tmp[0].equals("v")) {
                        Double x = Double.parseDouble(tmp[1]);
                        Double y = Double.parseDouble(tmp[2]);
                        Double z = Double.parseDouble(tmp[3]);
                        points.add(MatrixUtils.createRealVector(new double[]{x, y, z, 1.0}));
                    } else if ((tmp[0].equals("f")) || tmp[0].equals("l")) {
                        Figure f = new Figure();
                        for (int i = 1; i < tmp.length; i++) {
                            String value = tmp[i].split("//")[0];
                            int numberEdge = Integer.valueOf(value) - 1;
                            f.addPoint(points.get(numberEdge));
                        }
                        world.addFigure(f);
                    }
                });
            } catch (IOException e) {
                System.out.println("Brak pliku");
            }
        } catch (URISyntaxException e) {
        }
        System.out.println(world.getFigures().size());
        return world;
    }

}
