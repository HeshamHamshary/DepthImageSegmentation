import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Complete this class as part of the assignment
 */
public class DepthImageProcessor implements ImageProcessor{
    int uniqueID = 0;
    double[][] rawImg;
    int[][] colorImg;
    int width, height;
    double threshold, minDist, maxDist;
    Graph pixelGraph;
    ConnectedBlobs connectedBlobs;

    public DepthImageProcessor() {
        pixelGraph = null;
        connectedBlobs = null;
    }


    @Override
    public void processFile(String fileString) {
        // Just so the numbers don't keep increasing infinitely (sorry)
        if(fileString.contains("im0.xy")) {
            uniqueID = 0;
            connectedBlobs = null;
        }
        try {
            Scanner sc = new Scanner(new File(fileString));
            width = sc.nextInt();
            height = sc.nextInt();
            rawImg = new double[height][width];
            colorImg = new int[height][width];

            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    rawImg[i][j] = sc.nextDouble();
                }
            }
            pixelGraph = new Graph(height * width);

            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    int[][] adj = new int[][]{{-1, 0}, {-1, -1}, {0, -1}, {-1, 1}};
                    for (int[] point : adj) {
                        int secondX = x + point[0];
                        int secondY = y + point[1];
                        if (!withinBounds(secondX, secondY)) continue;
                        if(!withinThreshold(x, y, secondX, secondY)) continue;
                        pixelGraph.addEdge(y * width + x, secondY * width + secondX);
                    }
                }
            }

            ConnectedBlobs connectedBlobs = new ConnectedBlobs(pixelGraph, rawImg, threshold, minDist, maxDist);

            for(int i = 0; i < pixelGraph.V(); i++) {
                int label = connectedBlobs.label(i);
                if(label == -1 || rawImg[i/width][i%width] < minDist || rawImg[i/width][i%width] > maxDist) {
                    colorImg[i/width][i%width] = 0x000000;
                    continue;
                }
                ((PixelBlob)connectedBlobs.getBlobs().get(label)).addPoint(new Point(i%width, i/width));
            }

            connectedBlobs.getBlobs().sort((first, second) -> {
                int firstCentroid = pointToValue(first.getCentroid());
                int secondCentroid = pointToValue(second.getCentroid());
                return Integer.compare(firstCentroid, secondCentroid);
            });

            // Since we know both blob lists are sorted we can do linear comparison
            if(this.connectedBlobs != null && !this.connectedBlobs.getBlobs().isEmpty()) {
                ArrayList<Blob> oldBlobs = this.connectedBlobs.getBlobs();
                ArrayList<Blob> newBlobs = connectedBlobs.getBlobs();
                int oldCursor = 0;
                int newCursor = 0;
                uniqueID = Math.max(uniqueID, oldBlobs.size());
                uniqueID = Math.max(uniqueID, newBlobs.size());

                while(newCursor < newBlobs.size()) {
                    Blob newBlob = newBlobs.get(newCursor);
                    if(oldCursor >= oldBlobs.size()) {
                        newBlob.setLabel(uniqueID++);
                        newCursor++;
                        continue;
                    }
                    Blob oldBlob = oldBlobs.get(oldCursor);
                    double distance = oldBlob.getCentroid().distanceSq(newBlob.getCentroid());
                    if(distance < 100) {
                        newBlob.setLabel(oldBlob.getLabel());
                        oldCursor++;
                        newCursor++;
                    } else if(pointToValue(oldBlob.getCentroid()) > pointToValue(newBlob.getCentroid())){
                        newBlob.setLabel(uniqueID++);
                        newCursor++;
                    } else {
                        oldCursor++;
                    }
                }
            }

            for(Blob blob : connectedBlobs.getBlobs()) {
                PixelBlob pixelBlob = (PixelBlob) blob;
                for(Point point : pixelBlob.getPoints()) {
                    colorImg[point.y][point.x] = COLORS[blob.getLabel() % COLORS.length];
                }
            }

            this.connectedBlobs = connectedBlobs;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int pointToValue(Point point) {
        return point.x + point.y * width;
    }

    private boolean withinBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    private boolean withinThreshold(int firstX, int firstY, int secondX, int secondY) {
        return Math.abs(rawImg[firstY][firstX] - rawImg[secondY][secondX]) < threshold;
    }

    @Override
    public double[][] getRawImg() {
        return this.rawImg;
    }

    @Override
    public int[][] getColorImg() {
        return this.colorImg;
    }

    @Override
    public ArrayList<Blob> getBlobs() { return connectedBlobs.getBlobs(); }

    @Override
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void setMinDist(double minDist) {
        this.minDist = minDist;
    }

    @Override
    public void setMaxDist(double maxDist) {
        this.maxDist = maxDist;
    }
}
