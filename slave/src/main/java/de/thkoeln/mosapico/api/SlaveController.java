package de.thkoeln.mosapico.api;

import de.thkoeln.mosapico.connector.MasterServiceConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by szuster on 23.08.2015.
 */
@RestController
public class SlaveController {

    @Autowired
    private MasterServiceConnector master;

    private Status status = Status.IDLE;

    @RequestMapping("/status")
    public String getStatus() {
        return status.getValue();
    }

    @RequestMapping("/run")
    public void run() {
        status = Status.BUSY;
        master.getIds();
    }

    @RequestMapping("/stop")
    public void stop() {
        status = Status.IDLE;
    }
}
