package de.thkoeln.mosapico.connector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by szuster on 23.08.2015.
 */
@Component
public class MasterServiceConnector {

    @Autowired
    private DiscoveryClient discoveryClient;

    private String masterUri = null;

    @Autowired
    private RestTemplate restTemplate;

    private String getUri() {
        if (masterUri == null) {
            List<ServiceInstance> masters = discoveryClient.getInstances("master");
            masterUri = masters.get(0).getUri().toString();
        }
        return masterUri;
    }

    public List<Integer> getIds() {
        return restTemplate.getForObject(getUri() + "/ids", List.class);
    }
}
