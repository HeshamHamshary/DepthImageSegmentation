import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ImageTests {

    @Test
    public void sampleTest() {
        ImageProcessor dip = new DepthImageProcessor();
        dip.setThreshold(0.5);
        dip.setMinDist(0.5);
        dip.setMaxDist(6);

        String fileName = "m" + File.separator + "t.xy";
        dip.processFile(fileName);

        //there's 5 components in the sample image
        assertEquals(5, dip.getBlobs().size());
    }

    @Test
    public void sampleWithThresholdTest() {
        ImageProcessor dip = new DepthImageProcessor();
        dip.setThreshold(1.5);
        dip.setMinDist(0.5);
        dip.setMaxDist(6);

        String fileName = "m" + File.separator + "t.xy";
        dip.processFile(fileName);

        //there's 2 components in the sample image
        //when the threshold is 1.5
        assertEquals(2, dip.getBlobs().size());
    }

    @Test
    public void sampleWithMinMaxTest() {
        ImageProcessor dip = new DepthImageProcessor();
        dip.setThreshold(0.5);
        dip.setMinDist(1.5);
        dip.setMaxDist(4.5);

        String fileName = "m" + File.separator + "t.xy";
        dip.processFile(fileName);

        //there's 3 components in the sample image
        //when the anything less than 1.5 is ignored
        //and anything more than 4.5 is ignored
        assertEquals(3, dip.getBlobs().size());
    }

    @Test
    public void sampleTestTheLimitsBlobs() {

        ImageProcessor dip = new DepthImageProcessor();
        dip.setThreshold(-1);//a negative threshold means everything should be too far apart
        dip.setMinDist(-1000);
        dip.setMaxDist(1000);

        String fileName = "m" + File.separator + "im0.xy";
        dip.processFile(fileName);

        //there's 4800 components if we segment the image
        //really strictly
        assertEquals(4800, dip.getBlobs().size());
    }

    @Test
    public void sampleTestTheLimitsOneBlob() {

        ImageProcessor dip = new DepthImageProcessor();
        dip.setThreshold(100); //huge threshold - everything should be close enough
        dip.setMinDist(-1000);
        dip.setMaxDist(1000);

        String fileName = "m" + File.separator + "im0.xy";
        dip.processFile(fileName);

        //there's 4800 components if we segment the image
        //really strictly
        assertEquals(1, dip.getBlobs().size());
    }

}
