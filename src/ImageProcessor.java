import java.util.ArrayList;

/**
 * The methods required to process and display images
 */
public interface ImageProcessor {

    //all of my favorite colours
    //use this to color your images
    //pixels belonging to the same blob should
    //get the same color. add you own colors
    //or modify this as you see fit
    int[] COLORS = {
            0x80AA80,
            0xEEE8AA,
            0xDC143C,
            0xFF8C00,
            0xCD5C5C,
            0xFA8072,
            0x808000,
            0xFFD700,
            0x6B8E23,
            0xADFF2F};

    //how closely two neighbouring pixels should be
    //to be considered the same component
    double DEFAULT_THRESHOLD = .50; //50 centimeter threshold

    /**
     * Process the given image file
     * You should assume the order of these files is controlled
     * in other words subsequent calls to this method are for video sequences
     * @param fileName the name of the file to process
     *
*    * Note this method will be called before any of the get* methods
     */
    void processFile(String fileName);

    /**
     * Retrieve and return the raw gray image for the previously
     * specified frame. The gray image is exactly what it is in the file
     * @return the raw gray image that corresponds to the frame that was already set
     */
    double[][] getRawImg();

    /**
     * Retrieve and return the color image for the previously
     * specified frame. The color image is the image that has been segmented
     * and colored using the specified threshold to form blobs. All of the pixels
     * in the same blob should be colored the same color
     * @return a color image for the current frame
     */
    int[][] getColorImg();


    /**
     * Retrieve and return the blobs for the current frame
     * The blobs contains the centroid of each blob and the
     * label for that blob
     *
     * Note that the labels should be consistent from frame to frame
     * if the centroid doesn't change by more than 10 centimeters
     * it should be given the same label, else it can be given a
     * new label
     *
     * @return the blobs for the current frame
     */
    ArrayList<Blob> getBlobs();

    /**
     * set the threshold, which is used to determine whether
     * neighbouring pixels are part of the same blob or not
     * @param threshold the threshold for connecting neighbouring pixels to the same blob
     */
    void setThreshold(double threshold);

    /**
     * Retrieve the width for this image
     * @return the width
     */
    int getWidth();

    /**
     * Retrieve teh height for this image
     * @return the height
     */
    int getHeight();

    /**
     * Set the maximum distance a pixel can be from the camera
     * and still be processed. Anything beyond maxDist is ignored
     * @param maxDist
     */
    void setMaxDist(double maxDist);

    /**
     * Set the minimum distance a pixel can be from the camera
     * and still be processed. Anything closer than minDist is ignored
     * @param minDist
     */
    void setMinDist(double minDist);
}
