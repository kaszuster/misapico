package de.thkoeln.mosapico.web.connector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szuster on 15.09.2015.
 */
@Component
public class SlaveServiceConnector {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

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

    public void passCommand(String command) {
        List<ServiceInstance> slaves = discoveryClient.getInstances("slave");
        if (slaves.size() > 0) {
            slaves.forEach((ServiceInstance s) -> {
                passCommand(s.getUri().toString(), command);
            });
        }
    }

    public void passCommand(String uri, String command) {
        asyncRestTemplate.put(uri + "/" + command, null);
    }
}
