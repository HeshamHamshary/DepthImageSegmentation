import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.*;
import java.io.File;

/**
 * Help control the display
 * 
 * 
 * @author CS2920
 * @version W2020
 */
public class MyDisplay
{
    private JFrame frame;
    private JPanel outerPanel;
    
    private BufferedImage grayIms;
    private BufferedImage colorIms;
    
    private JLabel grayLabel;
    private JLabel colorLabel;
    
    private int scaleFactor;
    private int maxFrames;
    private int frameCount;

    private double threshold;

    private ImageProcessor imgProcessor;

    /**
     * Constructor for objects of class MyDisplay
     */
    public MyDisplay(int SCALE_FACTOR, int MAX_FRAMES)
    {
        frameCount = 0;
        imgProcessor = new DepthImageProcessor();
        imgProcessor.setMaxDist(3); //nothing farther than 3m
        imgProcessor.setMinDist(0.65); //nothing closer than 65 cm
        imgProcessor.setThreshold(ImageProcessor.DEFAULT_THRESHOLD);

        //grayIms = new BufferedImage();
        //colorIms = new BufferedImage();
        scaleFactor = SCALE_FACTOR;
        maxFrames = MAX_FRAMES;
        frame = new JFrame("Painting Images");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        outerPanel = new JPanel();

        frame.add(outerPanel, BorderLayout.CENTER);
        makeControls();
        grayLabel = new JLabel("Place Holder for Gray Image");
        colorLabel = new JLabel("Place Holder for Color Image");

        outerPanel.add(grayLabel);
        outerPanel.add(colorLabel);

        // Make it all visible
        frame.pack();
        frame.setVisible(true);
    }


    /*
     * Convert an array of doubles to a grayScale image
     * 
     * @param gImg the input image stored in raw depth measurments (cm) at each pixel  
     * @param width the width dimension of the image
     * @param height the height dimension of the image
     */
    public BufferedImage toGrayImage(double [][] gImg, int width, int height)
    {
        if(width == 0 || height == 0)
            return null;

        //crudely convert dbl to gray values
        int [] grayImg = new int [width*height];
        for (int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                grayImg[y*width+x] = (int) gImg[y][x] * 8000;
            }

        }
        
        //create a small bufferedimage and then scale it
        //there is almost certainly an easier way to do this
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
        bi.setRGB(0,0,width, height, grayImg, 0, width);
        if (scaleFactor == 1)
        {
            return bi;
        }
        BufferedImage fullSizeImage = new BufferedImage(width*scaleFactor, height*scaleFactor, BufferedImage.TYPE_USHORT_GRAY);
        Graphics2D bGr = fullSizeImage.createGraphics();
        bGr.drawImage(bi.getScaledInstance(width*scaleFactor, height*scaleFactor, 0), 0, 0, null);
        bGr.dispose();
        
        return fullSizeImage;
    }
    
    /*
     * Convert an array of ints to a color image
     * @param cImg the color image with integer color values stored at each pixel
     * @param width the width of the image
     * @param height the height of the image
     */
    public BufferedImage toColorImage(int [][] cImg, int width, int height)
    {
        if (height == 0 || width == 0)
            return null;
        int [] colorImg = new int[cImg.length*cImg[0].length];
        for(int y = 0; y< cImg.length; y++) {
            for(int x = 0; x < cImg[0].length; x++) {
                colorImg[y*width+x] = cImg[y][x];
            }
        }

        //create a small bufferedimage and then scale it
        //there is almost certainly a better way to do this 
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bi.setRGB(0,0,width, height, colorImg, 0, width);
        
        if (scaleFactor == 1)
        {
            return bi;
        }

        BufferedImage fullSizeImage = new BufferedImage(width*scaleFactor, height*scaleFactor, BufferedImage.TYPE_INT_RGB);
        Graphics2D bGr = fullSizeImage.createGraphics();
        bGr.drawImage(bi.getScaledInstance(width*scaleFactor, height*scaleFactor, 0), 0, 0, null);
        bGr.dispose();
        
        return fullSizeImage;
    }
    
    
    public void updateImages(double [][] raw, int [][] color, int width, int height) {
        grayIms = toGrayImage(raw, width, height);
        colorIms = toColorImage(color, width, height);
    }

     //display a specific frame
     //frm: the frame to display - must be between 0 and frameCount -1
    private void refresh(int frm)
    {
        if (frm < 0 || frm >= maxFrames)
        {
            return;
        }
        String fileName = "m" + File.separator + "im" + frm + ".xy";
        imgProcessor.processFile(fileName);
        updateImages(imgProcessor.getRawImg(), imgProcessor.getColorImg(), imgProcessor.getWidth(), imgProcessor.getHeight());

        outerPanel.remove(grayLabel);
        outerPanel.remove(colorLabel);
        
        grayLabel = new JLabel(new ImageIcon(grayIms));
        colorLabel = new JLabel(new ImageIcon(colorIms));
        
        outerPanel.add(grayLabel);
        outerPanel.add(colorLabel);
        for(Blob blob : imgProcessor.getBlobs()) {
            addLabel(blob);
        }
        frame.pack();
        frame.repaint();
    }
    
    //add a label to the color image from a Blob
    public void addLabel(Blob b)
    {
        Point p = b.getCentroid();
        double x = p.x * scaleFactor;
        double y = p.y * scaleFactor;
        
        Graphics2D g2d = colorIms.createGraphics();
        g2d.setPaint(Color.white);
        g2d.setFont(new Font("Serif", Font.BOLD, 20));
        
        String s = new String (Integer.toString(b.getLabel() ));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(s,(int) x,(int) y);
        g2d.dispose();
        
    }
    
    public void loopForever( )
    {
        while(true)
        {
            for (int i = 0; i < maxFrames; i++)
            {
                refresh(i);

                //pass control back (this class should really implement runnable)
                try 
                {
                    Thread.sleep(100);                 //10 milliseconds is .1 second.
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                    System.out.println("error");
                    Thread.currentThread().interrupt();
                }
            } 
        }
        
    }

    public void makeControls() {
        JLabel cmLabel = new JLabel("Distance neighbouring pixels may be apart but still part of same blob (threshold)");

        int defThres = (int) (ImageProcessor.DEFAULT_THRESHOLD*100);
        //add a slider for control of the size of blobs (2cm to 100cm)
        JSlider slider = new JSlider(JSlider.HORIZONTAL,
                1, 300, defThres);

        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(100);

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                threshold = slider.getValue()/100.0;
                imgProcessor.setThreshold(threshold);
                System.out.println("Change threshold to " + threshold);
            }
        });

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        northPanel.add(cmLabel);
        northPanel.add(slider);

        frame.add(northPanel, BorderLayout.NORTH);
    }
    

}
    