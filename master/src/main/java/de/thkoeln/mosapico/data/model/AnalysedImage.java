package de.thkoeln.mosapico.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by szuster on 04.09.2015.
 */
public class AnalysedImage {

    @Field(index = FieldIndex.not_analyzed)
    private int width;

    @Field(index = FieldIndex.not_analyzed)
    private int height;

    @Field(index = FieldIndex.not_analyzed)
    private int r;

    @Field(index = FieldIndex.not_analyzed)
    private int g;

    @Field(index = FieldIndex.not_analyzed)
    private int b;

    @Field(index = FieldIndex.not_analyzed)
    private byte[] byteArray;

    public BufferedImage getBufferedImage() {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new ByteArrayInputStream(getByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bi;
    }

    @JsonIgnore
    public void setBufferedImage(BufferedImage bufferedImage) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setByteArray(b.toByteArray());
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }
}
