package de.thkoeln.mosapico.web.api;

import de.thkoeln.mosapico.data.model.Chunk;
import de.thkoeln.mosapico.data.model.Image;
import de.thkoeln.mosapico.data.repository.ImageRepository;
import de.thkoeln.mosapico.image.ImageHandler;
import de.thkoeln.mosapico.state.StateHandler;
import de.thkoeln.mosapico.web.connector.MasterServiceConnector;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by szuster on 23.08.2015.
 */
@RestController
public class BuildController {

    @Autowired
    private MasterServiceConnector masterServiceConnector;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private StateHandler stateHandler;

    @Autowired
    private ImageHandler imageHandler;

    @RequestMapping("/buildChunks")
    public void buildChunks() {
        stateHandler.build();
        System.out.println("incomming call: generatePartialMosaic");

        Chunk sourceImage = masterServiceConnector.getChunk();
        while (sourceImage != null) {
            Chunk finalImage = generateMosaic(sourceImage);
            masterServiceConnector.sendBuildChunk(finalImage);
            //check for external shutdown
            if (!stateHandler.isIdle()) {
                sourceImage = masterServiceConnector.getChunk();
            } else {
                break;
            }
        }

        stateHandler.idle();
    }

    private Chunk generateMosaic(Chunk sourceImage
    ) {
        Chunk finalImage = new Chunk();
        finalImage.setX(sourceImage.getX());
        finalImage.setY(sourceImage.getY());

        BufferedImage bufferedSourceImage = imageHandler.toBufferedImage(sourceImage.getBytes());
        int width = bufferedSourceImage.getWidth();
        int height = bufferedSourceImage.getHeight();
        BufferedImage finalBufferedImage = new BufferedImage(width, height, bufferedSourceImage.getType());
        long time = System.nanoTime();
        System.out.println("Started image generation!");
        List<String> notIds = new ArrayList<>();
        for (int x = 0; x < width; x = x + 32) {
            for (int y = 0; y < height; y = y + 20) {
                try {
                    int[] partialAverageColor = imageHandler.getPartialAverageColor(bufferedSourceImage, x, y, 32, 20);
                    Image img = imageRepository.findByAverageColor(partialAverageColor[0], partialAverageColor[1], partialAverageColor[2], notIds);
//                    img.setTimesUsed(img.getTimesUsed() + 1);
//                    imageRepository.save(img);
                    imageRepository.increaseTimesUsed(img);
                    notIds.add(img.getId());

                    BufferedImage bimg = img.getAnalysedImage().getBufferedImage();
                    finalBufferedImage.createGraphics().drawImage(bimg, x, y, null);
                } catch (VersionConflictEngineException e) {
                    System.out.println("Version conflict!");
                }
            }
        }
        time = System.nanoTime() - time;
        long sec = TimeUnit.SECONDS.convert(time, TimeUnit.NANOSECONDS);
        System.out.println("IMAGE done in " + sec);

        finalImage.setBytes(imageHandler.toByteArray(finalBufferedImage));
        return finalImage;
    }

}
