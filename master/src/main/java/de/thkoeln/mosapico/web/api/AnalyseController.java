package de.thkoeln.mosapico.web.api;

import de.thkoeln.mosapico.data.model.Image;
import de.thkoeln.mosapico.data.repository.ImageRepository;
import de.thkoeln.mosapico.web.connector.SlaveServiceConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by szuster on 23.08.2015.
 */
@RestController
public class AnalyseController {

    @Autowired
    private SlaveServiceConnector slaveServiceConnector;

    @Autowired
    private ImageRepository imageRepository;

    private List<String> ids = new ArrayList<>();

    @RequestMapping("/getAllIdsToAnalyse")
    public List<String> getAllIdsToAnalyse() {
        if (ids.size() == 0) {
            resetIds();
        }
        return ids;
    }

    @RequestMapping("/resetIds")
    public void resetIds() {
        ids.clear();
        ids = imageRepository.findAllNotAnalyzedIds();
    }

    @RequestMapping("/resetAnalysed")
    public void resetAnalysed() {
        imageRepository.resetAnalysed();
        ids.clear();
        ids = imageRepository.findAllNotAnalyzedIds();
    }

    @RequestMapping("/allAnalyseImages")
    public void allAnalyseImages() {
        slaveServiceConnector.passCommand("analyse");
    }

    @RequestMapping("/singleAnalyseImages")
    public void singleAnalyseImages(@RequestParam(value = "uri") String uri) {
        slaveServiceConnector.passCommand(uri, "analyse");
    }

    @RequestMapping("/getIdsChunkToAnalyse")
    public synchronized List<String> getIdsChunkToAnalyse() {
        if (ids.size() == 0) {
            resetIds();
        }

        int toIndex = 10;
        if (ids.size() < 10) {
            toIndex = ids.size();
        }

        List<String> work = new ArrayList<>(ids.subList(0, toIndex));
        ids.removeAll(work);

        return work;
    }

    @RequestMapping("/uploadImages")
    public Map<String, String> uploadImages(@RequestParam(value = "dir") String dirName) {
        Map<String, String> retVal = new HashMap<>();
        if (StringUtils.isEmpty(dirName)) {
            return retVal;
        }

        File dir = new File(dirName);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.isFile()) {
                    String mimetype = new MimetypesFileTypeMap().getContentType(child);
                    String type = mimetype.split("/")[0];
                    if (type.equals("image")) {
                        Image img = new Image();
                        boolean init = img.init(child);
                        if (init == true) {
                            imageRepository.save(img);
                            retVal.put(child.getName(), "OK");
                        } else {
                            retVal.put(child.getName(), "ERROR");
                        }
                    }
                }
            }
        }
        return retVal;
    }

}
