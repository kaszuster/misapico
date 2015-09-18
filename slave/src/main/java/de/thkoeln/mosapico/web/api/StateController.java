package de.thkoeln.mosapico.web.api;

import de.thkoeln.mosapico.state.StateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by szuster on 23.08.2015.
 */
@RestController
public class StateController {

    @Autowired
    private StateHandler stateHandler;

    @RequestMapping("/getState")
    public String getState() {
        return stateHandler.getCurrentState().getValue();
    }

    @RequestMapping("/stop")
    public void stop() {
        stateHandler.idle();
    }
}
