package de.thkoeln.mosapico.image;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.List;

/**
 * Created by szuster on 02.09.2015.
 */
@Component
public class ImageHandler {

    public byte[] toByteArray(BufferedImage img) {
        byte[] bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "jpg", baos);
            baos.flush();
            bytes = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public BufferedImage toBufferedImage(byte[] img) {
        InputStream in = new ByteArrayInputStream(img);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

    public BufferedImage resizeImage(byte[] imageBytes, int type, int width, int height) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resizeImage(image, type, width, height);
    }

    public BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height) {
        //Image "Template" erstllen
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();

        //Neues Bild zeichnen
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    public void wirteFile(byte[] byteImage, String dir, String name) {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new ByteArrayInputStream(byteImage));
            write(bi, dir, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(BufferedImage img, String dir, String name) {
        File outputFile = new File(dir + "/" + name);
        try {
            ImageIO.write(img, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getPartialAverageColor(BufferedImage image, int rowStart, int colStart, int width, int height) {
        int i = 0;
        int redTotal = 0;
        int blueTotal = 0;
        int greenTotal = 0;
        int alphaTotal = 0;

        for (int x = rowStart; x < rowStart + width; x++)  //Breite
        {
            if (x < rowStart) {
                continue;
            }

            for (int y = colStart; y < colStart + height; y++) //Höhe
            {
                if (y < colStart) {
                    continue;
                }

                int color = image.getRGB(x, y); //Farbwert des einzelnen Pixels abfragen

                //Farbwerte von 0-255 für R G B erhalten
                int red = (color & 0x00ff0000) >> 16;
                int green = (color & 0x0000ff00) >> 8;
                int blue = color & 0x000000ff;
                int alpha = (color >> 24) & 0xff;   //ist eigentlich immer 255, hab es mal mitaufgenommen, wird aber nicht weiterverwendet

                //Farben zusammenählen
                redTotal = redTotal + red;
                blueTotal = blueTotal + blue;
                greenTotal = greenTotal + green;
                alphaTotal = alphaTotal + alpha;

                //Durchlauf +1
                i++;
            }
        }

        //Farbmittelwerte bestimmen
        int[] colorValues = new int[4];
        colorValues[0] = redTotal / i;
        colorValues[1] = greenTotal / i;
        colorValues[2] = blueTotal / i;
        colorValues[3] = alphaTotal / i;

        return colorValues;
    }

    public int[] getAverageColor(BufferedImage image) {
        return getPartialAverageColor(image, 0, 0, image.getWidth(), image.getHeight());
    }

    public BufferedImage concatImages(List<File> files) {
        int rows = 2;
        int cols = 2;
        int chunks = rows * cols;

        int chunkWidth, chunkHeight;
        int type;
        File[] imgFiles = new File[chunks];
        for (int i = 0; i < chunks; i++) {
            imgFiles[i] = files.get(i);
        }

        //creating a bufferd image array from image files
        BufferedImage[] buffImages = new BufferedImage[chunks];
        for (int i = 0; i < chunks; i++) {
            try {
                buffImages[i] = ImageIO.read(imgFiles[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        type = buffImages[0].getType();
        chunkWidth = buffImages[0].getWidth();
        chunkHeight = buffImages[0].getHeight();

        //Initializing the final image
        BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows, type);

        int num = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * j, chunkHeight * i, null);
                num++;
            }
        }

        return finalImg;
    }

    public BufferedImage copyBufferedImage(BufferedImage bufferedImageScource) {
        ColorModel cm = bufferedImageScource.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bufferedImageScource.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}
