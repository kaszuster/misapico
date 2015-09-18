package de.thkoeln.mosapico.web.api;

import de.thkoeln.mosapico.data.model.AnalysedImage;
import de.thkoeln.mosapico.data.model.Image;
import de.thkoeln.mosapico.data.repository.ImageRepository;
import de.thkoeln.mosapico.image.ImageHandler;
import de.thkoeln.mosapico.state.StateHandler;
import de.thkoeln.mosapico.web.connector.MasterServiceConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by szuster on 23.08.2015.
 */
@RestController
public class AnalyseController {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MasterServiceConnector master;

    @Autowired
    private ImageHandler imageHandler;

    @Autowired
    private StateHandler stateHandler;

    @RequestMapping("/analyse")
    public void analyse() {
        stateHandler.analyse();

        List<String> work = master.getIds();
        while (work != null && work.size() > 0) {
            work.forEach(id -> analyseImage(id));
            //check for external shutdown
            if (!stateHandler.isIdle()) {
                work = master.getIds();
            } else {
                break;
            }
        }

        stateHandler.idle();
    }

    @RequestMapping("/singleAnalyse")
    public void singleAnalyse(@RequestParam(value = "id") String id) {
        stateHandler.analyse();

        analyseImage(id);

        stateHandler.idle();
    }

    private void analyseImage(String id) {
        Image img = imageRepository.findOne(id);
        if (img != null && img.getByteArray() != null) {
            BufferedImage resized = imageHandler.resizeImage(img.getByteArray(), img.getType(), 32, 20);
            int[] averageColor = imageHandler.getAverageColor(resized);

            AnalysedImage analysedImage = new AnalysedImage();
            analysedImage.setWidth(32);
            analysedImage.setHeight(20);
            analysedImage.setBufferedImage(resized);
            analysedImage.setR(averageColor[0]);
            analysedImage.setG(averageColor[1]);
            analysedImage.setB(averageColor[2]);
            img.setAnalysedImage(analysedImage);

            resized = imageHandler.resizeImage(img.getByteArray(), img.getType(), 64, 40);
            averageColor = imageHandler.getAverageColor(resized);

            AnalysedImage analysedImage2 = new AnalysedImage();
            analysedImage2.setWidth(64);
            analysedImage2.setHeight(40);
            analysedImage2.setBufferedImage(resized);
            analysedImage2.setR(averageColor[0]);
            analysedImage2.setG(averageColor[1]);
            analysedImage2.setB(averageColor[2]);
            img.setAnalysedImage2(analysedImage2);

            img.setAnalyzed(true);

            imageRepository.save(img);
        } else {
            System.out.println("Image with ID " + id + " could not be analysed!");
        }
    }

}
