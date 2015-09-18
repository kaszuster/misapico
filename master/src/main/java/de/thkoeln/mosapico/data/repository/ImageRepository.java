package de.thkoeln.mosapico.data.repository;

import de.thkoeln.mosapico.data.model.Image;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * Created by szuster on 28.08.2015.
 */
@Component
public interface ImageRepository extends ElasticsearchRepository<Image, String>, ImageRepositoryCustom {

    Image findByName(String name);

}
