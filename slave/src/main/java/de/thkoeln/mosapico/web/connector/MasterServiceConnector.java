package de.thkoeln.mosapico.web.connector;

import de.thkoeln.mosapico.data.model.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @Value("${master.connect}")
    private String masterIp = "";

    private String masterUri = null;

    @Autowired
    private RestTemplate restTemplate;

    private String getUri() {
        if (masterUri == null) {
            if (masterIp.contentEquals("not_set")) {
                List<ServiceInstance> masters = discoveryClient.getInstances("master");
                masterUri = masters.get(0).getUri().toString();
            } else {
                masterUri = "http://" + masterIp + ":9097";
            }
        }
        return masterUri;
    }

    public List<String> getIds() {
        return restTemplate.getForObject(getUri() + "/getIdsChunkToAnalyse", List.class);
    }

    public Chunk getChunk() {
        return restTemplate.getForObject(getUri() + "/getChunk", Chunk.class);
    }

    public void sendBuildChunk(Chunk buildChunk) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(buildChunk, headers);
        restTemplate.postForEntity(getUri() + "/addBuildChunk", entity, null);
    }
}
