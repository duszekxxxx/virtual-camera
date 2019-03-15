package com.zarczukp.view;

import com.zarczukp.world.Camera;
import com.zarczukp.world.Figure;
import com.zarczukp.world.Reading;
import com.zarczukp.world.World;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class MainView {
    private JPanel viewPanel = new ViewPanel(1920, 1080);
    private World world;
    private Camera camera;


    public class ViewPanel extends JPanel {

        public ViewPanel() {
            this(1920, 1080);
        }

        public ViewPanel(int width, int height) {
            super.setPreferredSize(new Dimension(width, height));
            super.setBackground(Color.BLACK);

        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            camera.setDimension(super.getWidth(), super.getHeight());
            drawWorld(g2d);
        }

        private void drawWorld(Graphics2D g) {
            List<Figure> figureList = world.getFigures();
            List<Figure> projectedfigure = new ArrayList<>();
            for (Figure f : figureList) {
                projectedfigure.add(changeFigure(f));
            }

            for (Figure p : projectedfigure) {
                List<RealVector> pointList = p.getPoints();
                for (int i = 0; i < pointList.size(); i++) {
                    RealVector v1 = pointList.get(i);
                    RealVector v2 = pointList.get((i + 1) % pointList.size());
                    if (fronted(v1) && fronted(v2)) {
                        v1 = normalize(v1);
                        v2 = normalize(v2);
                        drawFigure(g, v1, v2);

                    } else if (fronted(v1) && !fronted(v2)) {

                        RealVector vc = intersect(v1, v2);
                        vc = normalize(vc);
                        v1 = normalize(v1);
                        drawFigure(g, v1, vc);

                    }

                    // System.out.println(String.format("Narysowana linie od: %d %d do: %d %d",x1,y1,x2,y2));
                }
            }

        }

        private RealVector intersect(RealVector v1, RealVector v2) {
            double w1 = v1.getEntry(v1.getMaxIndex());
            double w2 = v2.getEntry(v2.getMaxIndex());
            double n = (w1 - camera.znear) / (w1 - w2);
            double wc = (n * w1) + ((1 - n) * w2);
            double xc = (n * v1.getEntry(0)) + ((1 - n) * v1.getEntry(0));
            double yc = (n * v1.getEntry(1)) + ((1 - n) * v1.getEntry(1));
            double zc = (n * v2.getEntry(2)) + ((1 - n) * v1.getEntry(2));
            RealVector vc = new ArrayRealVector(new double[]{xc, yc, zc, wc});
            return vc;

        }

        private void drawFigure(Graphics2D g, RealVector v1, RealVector v2) {
            Integer x1 = Math.toIntExact(Math.round(v1.getEntry(0)));
            Integer x2 = Math.toIntExact(Math.round(v2.getEntry(0)));
            Integer y1 = Math.toIntExact(Math.round(v1.getEntry(1)));
            Integer y2 = Math.toIntExact(Math.round(v2.getEntry(1)));
            g.drawLine(x1, y1, x2, y2);
        }

        private RealVector normalize(RealVector v) {
            RealVector normalized = v.copy();
            if (normalized.getEntry(normalized.getMaxIndex()) != 0) {
                normalized.mapDivideToSelf(normalized.getEntry(normalized.getMaxIndex()));
            }
            normalized.setEntry(0, normalized.getEntry(0) + 1.0f);
            normalized.setEntry(1, normalized.getEntry(1) + 1.0f);
            normalized.setEntry(0, Math.round(normalized.getEntry(0) * 0.5d * super.getWidth()));
            normalized.setEntry(1, Math.round(normalized.getEntry(1) * 0.5d * super.getHeight()));

            return normalized;


        }

        private boolean fronted(RealVector v) {
            if (v.getEntry(v.getMaxIndex()) >= camera.znear) {
                return true;
            } else {
                return false;
            }

        }

        private Figure changeFigure(Figure f) {
            Figure changed = new Figure();
            List<RealVector> pointList = f.getPoints();
            for (RealVector p : pointList) {
                RealVector point = p.copy();
                point = camera.projectToScreen(point);
                changed.addPoint(point);
            }
            return changed;
        }


    }


    public MainView() {
        Reading r = new Reading();
        this.world = r.createWorld("./untitled.obj");
        this.camera = new Camera(1920, 1080);

        viewPanel.setFocusable(true);
        viewPanel.requestFocus();

        viewPanel.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                double angle = 1;
                double velocity = 0.5;
                System.out.println(keyCode);
                if (keyCode == KeyEvent.VK_UP) {
                    camera.moveUp(-velocity);
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    camera.moveUp(velocity);
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    camera.moveRight(-velocity);
                } else if (keyCode == KeyEvent.VK_LEFT) {
                    camera.moveRight(velocity);
                    ;
                } else if (keyCode == KeyEvent.VK_W) {
                    camera.moveForward(-velocity);
                } else if (keyCode == KeyEvent.VK_S) {
                    camera.moveForward(velocity);
                } else if (keyCode == KeyEvent.VK_A) {
                    camera.yaw(angle);
                } else if (keyCode == KeyEvent.VK_D) {
                    camera.yaw(-angle);
                } else if (keyCode == KeyEvent.VK_NUMPAD4) {
                    camera.Roll(-angle);
                } else if (keyCode == KeyEvent.VK_NUMPAD6) {
                    camera.Roll(angle);
                } else if (keyCode == KeyEvent.VK_NUMPAD8) {
                    camera.pitch(-angle);
                } else if (keyCode == KeyEvent.VK_NUMPAD2) {
                    camera.pitch(angle);
                } else if (keyCode == KeyEvent.VK_Q) {
                    camera.zoom(0.1);
                } else if (keyCode == KeyEvent.VK_E) {
                    camera.zoom(-0.1);
                }

                viewPanel.repaint();
                viewPanel.requestFocus();
                super.keyPressed(e);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainView");
        frame.setContentPane(new MainView().viewPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}
