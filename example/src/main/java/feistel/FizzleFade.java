package feistel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.RED;
import static java.lang.Thread.sleep;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

final class FizzleFade extends SwingWorker<Void, Point> {

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

    @Override
    protected Void doInBackground() {
        List<Point> points = new ArrayList<>(50);
        IntFeistel feistel = IntFeistel.numeric2(7, width, height,
                (round, input) -> input * 11 + (input >> 5) + 7 * 127 ^ input);

        for (int i = 0, end = width * height; i < end; i++) {
            int j = feistel.applyAsInt(i);
            points.add(new Point(j % width, j / width));
            if (points.size() >= 100) {
                publish(points.toArray(new Point[0]));
                points.clear();
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }

        if (!points.isEmpty()) {
            publish(points.toArray(new Point[0]));
            points.clear();
        }

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
