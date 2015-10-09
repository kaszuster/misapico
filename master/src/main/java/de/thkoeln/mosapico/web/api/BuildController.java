package de.thkoeln.mosapico.web.api;

import de.thkoeln.mosapico.data.model.Chunk;
import de.thkoeln.mosapico.data.model.ImageDimension;
import de.thkoeln.mosapico.data.repository.ImageRepository;
import de.thkoeln.mosapico.image.ImageHandler;
import de.thkoeln.mosapico.web.connector.SlaveServiceConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by szuster on 23.08.2015.
 */
@RestController
public class BuildController {

    private final List<ImageDimension> validImageDimensions;
    private final String writeToDir = System.getProperty("user.dir") + "\\generatedImages";
    private final String buildProcessFileName = "mosaic_process.jpg";
    @Autowired
    private SlaveServiceConnector slaveServiceConnector;
    private Stack<Chunk> chunks = new Stack<>();
    private List<Chunk> buildChunks = new ArrayList<>();
    private BufferedImage buildProcessImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    @Autowired
    private ImageHandler imageHandler;

    @Autowired
    private ImageRepository imageRepository;

    public BuildController() {
        validImageDimensions = new ArrayList<>();
        validImageDimensions.add(new ImageDimension(320, 200, 1));
        validImageDimensions.add(new ImageDimension(640, 400, 2));
        validImageDimensions.add(new ImageDimension(1280, 800, 4));
        validImageDimensions.add(new ImageDimension(1920, 1200, 6));
        validImageDimensions.add(new ImageDimension(2560, 1600, 8));
    }

    @RequestMapping("/writeChunks")
    public void writeChunks() {
        for (Chunk chunk : chunks) {
            imageHandler.write(imageHandler.toBufferedImage(chunk.getBytes()), writeToDir, chunk.getName() + ".jpg");
        }
    }

    @RequestMapping("/createChunks")
    public String createChunks(@RequestParam(value = "file") String filename) {
        BufferedImage sourceImage = getBufferedSourceImage(filename);
        if (sourceImage == null) {
            return "Passed file does not exist!";
        }

        ImageDimension dimension = getValidImageDimension(sourceImage);
        if (dimension == null) {
            return "Image dimension is not not valid. Image must be 320x200, 640x400, 1280x800, 1920x1200 or 2560x1600.";
        }

        chunks.addAll(cutImage(sourceImage, dimension));
        imageHandler.write(sourceImage, writeToDir, buildProcessFileName);
        buildProcessImage = imageHandler.copyBufferedImage(sourceImage);

        return "Chunks created!";
    }

    @RequestMapping("/getChunk")
    public Chunk partialImage() throws Exception {
        if (chunks.size() > 0) {
            return chunks.pop();
        }
        return null;
    }

    @RequestMapping("/restTimesUsed")
    public void restTimesUsed() {
        imageRepository.resetTimesUsed();
    }

    @RequestMapping("/allBuildChunks")
    public void buildChunks() {
        slaveServiceConnector.passCommand("buildChunks");
    }

    @RequestMapping("/singleBuildChunks")
    public void singleBuildChunks(@RequestParam(value = "uri") String uri) {
        slaveServiceConnector.passCommand(uri, "buildChunks");
    }

    @RequestMapping("/addBuildChunk")
    public synchronized void addBuildChunk(@RequestBody Chunk buildChunk) {
        buildChunks.add(buildChunk);
        writeToBuildProcessFile(buildChunk);

        imageHandler.wirteFile(buildChunk.getBytes(), writeToDir, "build_" + buildChunk.getName() + ".jpg");
    }

    private void writeToBuildProcessFile(Chunk buildChunk) {
        BufferedImage process = null;
        try {
            process = ImageIO.read(new File(writeToDir + "/" + buildProcessFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Graphics2D g2Source = process.createGraphics();
        BufferedImage bufferedChunk = imageHandler.toBufferedImage(buildChunk.getBytes());
        int x = buildChunk.getX();
        int y = buildChunk.getY();
        g2Source.drawImage(bufferedChunk, x, y, null);
        g2Source.dispose();

        imageHandler.write(process, writeToDir, buildProcessFileName);
        buildProcessImage = imageHandler.copyBufferedImage(process);
    }

    @RequestMapping(value = "/getBuildProcess", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getBuildProcess() throws IOException {
        return imageHandler.toByteArray(buildProcessImage);
    }

    public BufferedImage getBufferedSourceImage(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return null;
        }

        File file = new File(filename);
        BufferedImage sourceImage = null;
        try {
            sourceImage = ImageIO.read(file);
            return sourceImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ImageDimension getValidImageDimension(BufferedImage sourceImage) {
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        for (ImageDimension validImageDimension : validImageDimensions) {
            if (validImageDimension.equals(width, height)) {
                return validImageDimension;
            }
        }
        return null;
    }

    private List<Chunk> cutImage(BufferedImage sourceImage, ImageDimension dimension) {
        int rows = dimension.getSegments();
        int cols = dimension.getSegments();
        int chunkWidth = sourceImage.getWidth() / cols;
        int chunkHeight = sourceImage.getHeight() / rows;

        List<Chunk> imageChunks = new ArrayList<>();
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                int cx = chunkWidth * x;
                int cy = chunkHeight * y;
                BufferedImage bufferedImage = sourceImage.getSubimage(cx, cy, chunkWidth, chunkHeight);
                imageChunks.add(new Chunk(cx, cy, imageHandler.toByteArray(bufferedImage)));
            }
        }
        return imageChunks;
    }
}
