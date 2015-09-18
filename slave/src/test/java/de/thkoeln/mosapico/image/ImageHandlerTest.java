package de.thkoeln.mosapico.image;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by szuster on 07.09.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class ImageHandlerTest {

    @Autowired
    private ImageHandler imageHandler;

    @Test
    public void testGetAverageColor() throws Exception {
        testColorByFile("/red.jpg", new int[]{254, 0, 0, 255});
        testColorByFile("/green.jpg", new int[]{0, 255, 1, 255});
        testColorByFile("/blue.jpg", new int[]{0, 0, 254, 255});
        testColorByFile("/gray.jpg", new int[]{200, 200, 200, 255});
        testColorByFile("/mixed.jpg", new int[]{90, 89, 90, 255});

        testColorByFile("/dark_img.jpg", new int[]{37, 38, 37, 255});
        testColorByFile("/blue_img.jpg", new int[]{37, 140, 209, 255});
        testColorByFile("/red_img.jpg", new int[]{142, 41, 13, 255});
        testColorByFile("/green_img.jpg", new int[]{114, 141, 14, 255});
    }

    @Test
    public void testGetPartialAverageColor() throws Exception {
        testPartialColorByFile("/red.jpg", new int[]{254, 0, 0, 255}, 0, 0, 1, 1);

        testPartialColorByFile("/mixed.jpg", new int[]{90, 89, 90, 255}, 0, 0, 2, 2);

        testPartialColorByFile("/mixed.jpg", new int[]{254, 0, 0, 255}, 0, 0, 1, 1);
        testPartialColorByFile("/mixed.jpg", new int[]{5, 4, 255, 255}, 0, 1, 1, 1);
        testPartialColorByFile("/mixed.jpg", new int[]{4, 254, 6, 255}, 1, 0, 1, 1);
        testPartialColorByFile("/mixed.jpg", new int[]{100, 100, 100, 255}, 1, 1, 1, 1);
    }

    private void testPartialColorByFile(String filename, int[] expectedColor, int rowStart, int colStart, int width, int height) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(getTestFile(filename));
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not read test file!");
        } finally {
            assertNotNull(bufferedImage);
        }
        int[] rgb = imageHandler.getPartialAverageColor(bufferedImage, rowStart, colStart, width, height);

        assertArrayEquals(expectedColor, rgb);
    }

    private void testColorByFile(String filename, int[] expectedColor) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(getTestFile(filename));
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not read test file!");
        } finally {
            assertNotNull(bufferedImage);
        }
        int[] rgb = imageHandler.getAverageColor(bufferedImage);

        assertArrayEquals(expectedColor, rgb);
    }

    private File getTestFile(String name) {
        return new File(getClass().getResource(name).getFile());
    }

    @Configuration
    static class ContextConfiguration {

        @Bean
        public ImageHandler getImageHandler() {
            ImageHandler imageHandler = new ImageHandler();
            return imageHandler;
        }
    }
}