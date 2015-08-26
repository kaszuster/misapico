package de.thkoeln.mosapico.api;

import de.thkoeln.mosapico.connector.MasterServiceConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

/**
 * Created by szuster on 23.08.2015.
 */
@RestController
public class SlaveController {

    @Autowired
    private MasterServiceConnector master;

    private Random random = new Random();

    private Status status = Status.IDLE;

    @RequestMapping("/status")
    public String getStatus() {
        return status.getValue();
    }

    @RequestMapping("/run")
    public void run() {
        status = Status.BUSY;
        List<Integer> work = master.getIds();

        while (work.size() > 0) {
            //TODO: DO THE MAGIC
            try {
             Thread.sleep(random.nextInt(3) * 1000);
             } catch (InterruptedException e) {
             e.printStackTrace();
             }
            System.out.println(work);
            work = master.getIds();
        }
        status = Status.IDLE;
    }

    @RequestMapping("/stop")
    public void stop() {
        status = Status.IDLE;
    }
}
