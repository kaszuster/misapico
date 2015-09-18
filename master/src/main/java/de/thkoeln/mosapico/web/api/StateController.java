package de.thkoeln.mosapico.web.api;

import de.thkoeln.mosapico.web.connector.SlaveServiceConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by szuster on 23.08.2015.
 */
@RestController
public class StateController {

    @Autowired
    private SlaveServiceConnector slaveServiceConnector;

    @RequestMapping("/getSlaves")
    public List<String> getSlaves() {
        return slaveServiceConnector.getSlaves();
    }

    @RequestMapping("/allStop")
    public void allStop() {
        slaveServiceConnector.passCommand("stop");
    }

    @RequestMapping("/singleStop")
    public void singleStop(@RequestParam(value = "uri") String uri) {
        slaveServiceConnector.passCommand(uri, "stop");
    }

}
