package feistel;

import isomorphic.Isomorphism;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.IntStream;

import static feistel.MoreCollectors.split;
import static java.awt.Color.RED;
import static java.lang.Thread.sleep;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public final class FizzleFade extends SwingWorker<Void, Point> {

    private final int width;
    private final int height;
    private final JFrame frame;
    private final Graphics graphics;

    private FizzleFade(int width, int height, JFrame frame) {
        this.width = width;
        this.height = height;
        this.frame = frame;
        this.graphics = frame.getGraphics();
        this.graphics.setColor(RED);
    }

    private IntStream pixels() {
        RoundFunction.OfInt f = (round, value) -> value * 11 + (value >> 5) + 7 * 127 ^ value;
        Isomorphism.OfInt feistel = Feistel.ofIntNumeric(width, height, 3, f);
        return IntStream.range(0, width * height).map(feistel);
    }

    private Point toPoint(int i) {
        return new Point(i % width, i / width);
    }

    @Override
    protected Void doInBackground() {

        pixels().mapToObj(this::toPoint).collect(split(100, points -> {
            publish(points.toArray(new Point[0]));
            try {
                sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @Override
    protected void process(List<Point> points) {
        super.process(points);
        for (Point point : points) {
            graphics.drawRect(point.x, point.y, 0, 0);
        }
    }

    @Override
    protected void done() {
        super.done();
        frame.dispose();
    }

    public static void main(String[] args) {
        int width = 320;
        int height = 200;
        JFrame frame = new JFrame("FizzleFade");
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setVisible(true);
        new FizzleFade(width, height, frame).execute();
    }
}
