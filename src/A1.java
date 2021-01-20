import java.util.*;
import java.io.*;

/**
 * Driver for CS 261 Assignment 1
 * 
 * @author This file is provided with the Assignment
 */
public class A1 
{
    //how many images to process
    private final static int MAX_IMAGES = 93;
    //because we are only working with small images scale them up when we display them
    private final static int SCALE_FACTOR = 5;
    
    //distance two blobs can be apart by and still considered the same from one frame to next
    private final static double TRACKING_DISTANCE = 10;
    
    /*
     * Main driver for assignment 1
     * 
     * you may pass 4 arguments into main if desired:
     * the 4 arguments will be passed onto MyDepthImage::segment
     * Segment (dbl maxDepth, dbl minDepth, dbl maxZDelta, int minBlobSz) 
     */
    public static void main(String[] args)
    {        
        //segmentation parameters
        MyDisplay display = new MyDisplay(SCALE_FACTOR, MAX_IMAGES);
        display.loopForever();
    }
}
