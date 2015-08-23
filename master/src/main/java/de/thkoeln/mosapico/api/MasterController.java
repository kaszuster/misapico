package de.thkoeln.mosapico.api;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @RequestMapping("/run")
    public String run() {
        List<ServiceInstance> slaves = discoveryClient.getInstances("slave");
        StringBuilder ret = new StringBuilder();
        if (slaves.size() == 0) {
            ret.append("No Slaves found!");
        } else {
            slaves.forEach((ServiceInstance s) -> {
                ret.append(ToStringBuilder.reflectionToString(s.getUri()));
                ret.append(restTemplate.getForObject(s.getUri() + "/status", String.class));
                ret.append(restTemplate.getForObject(s.getUri() + "/run", String.class));
                ret.append(restTemplate.getForObject(s.getUri() + "/status", String.class));
                ret.append(restTemplate.getForObject(s.getUri() + "/stop", String.class));
                ret.append(restTemplate.getForObject(s.getUri() + "/status", String.class));
            });
        }

        return ret.toString();
    }

    @RequestMapping("/ids")
    public List<Integer> getIds() {
        List<Integer> work = new ArrayList<>();
        work.add(new Integer(1));
        work.add(new Integer(2));
        work.add(new Integer(3));
        work.add(new Integer(4));
        work.add(new Integer(5));
        work.add(new Integer(6));
        work.add(new Integer(7));
        work.add(new Integer(8));
        work.add(new Integer(9));
        work.add(new Integer(10));

        return work;
    }
}
