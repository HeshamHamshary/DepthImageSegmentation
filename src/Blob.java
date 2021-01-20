import java.awt.*;

/**
 * Required for labeling the blobs within an
 * image
 */
public interface Blob {

    /**
     * Get the centroid for this blob. where the centroid is
     * simply the floor of the average x position of a pixel in the blob
     * and the floor of the average y position of a pixel in the blob
     * @return the centroid
     */
    Point getCentroid();


    /**
     * What is the label of this blob? You should try to be consistent
     * as possible with your labelling from frame to frame. However just
     * know this is a challenging problem in computer science
     * @return the label used to denote this blob
     */
    int getLabel();


    /**
     * Set the label of this blob
     * @param label the label
     */
    void setLabel(int label);
}
