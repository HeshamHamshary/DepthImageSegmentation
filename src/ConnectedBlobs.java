import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ConnectedBlobs {
    private ArrayList<Blob> blobs;
    private int[] labels;
    private int[] size;
    private int count;

    public ConnectedBlobs(Graph graph, double[][] image, double threshold, double minDist, double maxDist) {
        blobs = new ArrayList<>();
        labels = new int[graph.V()];
        Arrays.fill(labels, -1);
        size = new int[graph.V()];
        count = 0;
        for(int v = 0; v < graph.V(); v++) {
            if(image[v/image[0].length][v%image[0].length] < minDist || image[v/image[0].length][v%image[0].length] > maxDist) continue;
            if(labels[v] == -1) {
                PixelBlob blob = new PixelBlob();
                blob.setLabel(count);
                blobs.add(blob);
                dfs(graph, v, image, threshold);
                count++;
            }
        }
    }

    private void dfs(Graph graph, int v, double[][] image, double threshold) {
        labels[v] = count;
        size[count]++;
        for (int w : graph.adj(v)) {
            if (labels[w] == -1 && withinThreshold(image, v, w, threshold)) {
                dfs(graph, w, image, threshold);
            }
        }
    }

    public ArrayList<Blob> getBlobs() {
        return blobs;
    }

    private boolean withinThreshold(double[][] image, int v, int w, double threshold) {
        return Math.abs(image[v/image[0].length][v%image[0].length] - image[w/image[0].length][w%image[0].length]) < threshold;
    }

    public int label(int v) {
        validateVertex(v);
        return labels[v];
    }

    public int size(int v) {
        return size[label(v)];
    }

    public int count() {
        return count;
    }

    public boolean connected(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return label(v) == label(w);
    }

    private void validateVertex(int v) {
        int amount = labels.length;
        if (v < 0 || v >= amount)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (amount-1));
    }
}
