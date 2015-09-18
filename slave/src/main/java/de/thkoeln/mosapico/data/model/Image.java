package de.thkoeln.mosapico.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by szuster on 28.08.2015.
 */
@Document(indexName = "mosapico", type = "image")
public class Image {

    @Id
    private String id;

    @Field(index = FieldIndex.not_analyzed)
    private byte[] byteArray;

    @Field(index = FieldIndex.not_analyzed)
    private String name;

    @Field(index = FieldIndex.not_analyzed)
    private int type;

    @Field(index = FieldIndex.not_analyzed)
    private boolean analyzed = false;

    @Field(type = FieldType.Nested, index = FieldIndex.not_analyzed)
    private AnalysedImage analysedImage;

    @Field(type = FieldType.Nested, index = FieldIndex.not_analyzed)
    private AnalysedImage analysedImage2;

    @Field(index = FieldIndex.not_analyzed)
    private int timesUsed = 0;

    public boolean init(File image) {
        setName(image.getName());

        //set img type
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bufferedImage == null) {
                return false;
            }
        }
        int type = bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
        setType(type);

        //save byte array
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try {

            ImageIO.write(bufferedImage, "jpg", b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setByteArray(b.toByteArray());

        return true;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
        this.timesUsed = timesUsed;
    }

    public AnalysedImage getAnalysedImage2() {
        return analysedImage2;
    }

    public void setAnalysedImage2(AnalysedImage analysedImage2) {
        this.analysedImage2 = analysedImage2;
    }

    public AnalysedImage getAnalysedImage() {
        return analysedImage;
    }

    public void setAnalysedImage(AnalysedImage analysedImage) {
        this.analysedImage = analysedImage;
    }

    public boolean isAnalyzed() {
        return analyzed;
    }

    public void setAnalyzed(boolean analyzed) {
        this.analyzed = analyzed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
