import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PixelBlob implements Blob {
    private List<Point> points;
    private int label;

    public PixelBlob(){
        points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public List<Point> getPoints() {
        return points;
    }

    @Override
    public Point getCentroid() {
        int xAvg = 0, yAvg = 0;
        for(Point point : points){
            xAvg += point.getX();
            yAvg += point.getY();
        }
        return new Point(xAvg / points.size(), yAvg / points.size());
    }

    @Override
    public int getLabel() {
        return this.label;
    }

    @Override
    public void setLabel(int label) {
        this.label = label;
    }
}
