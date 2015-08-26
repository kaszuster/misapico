package de.thkoeln.mosapico.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szuster on 23.08.2015.
 */
@RestController
public class MasterController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private List<Integer> ids;

    public MasterController() {
        ids = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            ids.add(i);
        }
    }

    @RequestMapping("/resetIds")
    public void resetIds() {
        ids = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            ids.add(i);
        }
    }

    @RequestMapping("/slaves")
    public List<String> getSlaves() {
        List<String> retVal = new ArrayList<>();

        List<ServiceInstance> slaves = discoveryClient.getInstances("slave");
        if (slaves.size() > 0) {
            slaves.forEach((ServiceInstance s) -> {
                retVal.add(s.getUri().toString());
            });
        }
        return retVal;
    }

    @RequestMapping("/runAll")
    public void runAll() {
        passCommand("run");
    }

    @RequestMapping("/run")
    public void run(@RequestParam(value = "uri") String uri) {
        passCommand(uri, "run");
    }

    @RequestMapping("/stopAll")
    public void stopAll() {
        passCommand("stop");
    }

    @RequestMapping("/stop")
    public void stop(@RequestParam(value = "uri") String uri) {
        passCommand(uri, "stop");
    }

    @RequestMapping("/ids")
    public List<Integer> getIds() {
        int toIndex = 10;
        if (ids.size() < 10) {
            toIndex = ids.size();
        }

        List<Integer> work = new ArrayList<>(ids.subList(0, toIndex));
        ids.removeAll(work);

        return work;
    }

    private void passCommand(String command) {
        List<ServiceInstance> slaves = discoveryClient.getInstances("slave");
        if (slaves.size() > 0) {
            slaves.forEach((ServiceInstance s) -> {
                passCommand(s.getUri().toString(), command);
            });
        }
    }

    private void passCommand(String uri, String command) {
        asyncRestTemplate.put(uri + "/" + command, null);
        //restTemplate.put(uri + "/" + command, null);
        //restTemplate.getForObject(uri + "/" + command, String.class);
    }
}
