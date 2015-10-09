package de.thkoeln.mosapico.data.repository;

import de.thkoeln.mosapico.data.model.Image;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.NotFilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;

import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.FilterBuilders.*;

/**
 * Created by szuster on 31.08.2015.
 */
public class ImageRepositoryImpl implements ImageRepositoryCustom {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public List<String> findAllIds() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("mosapico")
                .withTypes("image")
                .withPageable(new PageRequest(0, Integer.MAX_VALUE))
                .build();

        return elasticsearchTemplate.queryForIds(searchQuery);
    }

    @Override
    public List<String> findAllNotAnalyzedIds() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("mosapico")
                .withTypes("image")
                .withQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("analyzed", false)))
                .withPageable(new PageRequest(0, Integer.MAX_VALUE))
                .build();

        return elasticsearchTemplate.queryForIds(searchQuery);
    }

    private Image findByAverageColor(int r, int g, int b, List<String> notIds, int variance) {
        AndFilterBuilder andFilterBuilder = andFilter(rangeFilter("analysedImage.r").from(r - variance).to(r + variance));
        andFilterBuilder.add(rangeFilter("analysedImage.g").from(g - variance).to(g + variance));
        andFilterBuilder.add(rangeFilter("analysedImage.b").from(b - variance).to(b + variance));

        NotFilterBuilder notFilterBuilder = notFilter(inFilter("_id", notIds.toArray(new String[0])));
        andFilterBuilder.add(notFilterBuilder);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(new PageRequest(0, 1))
                .withFilter(andFilterBuilder)
                .withSort(new FieldSortBuilder("timesUsed").order(SortOrder.ASC).missing("_first"))
                .build();

        Page<Image> sampleEntities =
                elasticsearchTemplate.queryForPage(searchQuery, Image.class);

        if (sampleEntities.hasContent()) {
            List<Image> result = sampleEntities.getContent();
            return result.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Image findByAverageColor(int r, int g, int b, List<String> notIds) {
        Image img = findByAverageColor(r, g, b, notIds, 20);
        if (img == null) {
            img = findByAverageColor(r, g, b, notIds, 40);
            if (img == null) {
                List<String> emptyList = new ArrayList<>();
                img = findByAverageColor(r, g, b, notIds, 80);
                if (img == null) {
                    img = imageRepository.findByName("filler.jpg");
                }
            }
        }

        return img;
    }

    @Override
    public void increaseTimesUsed(Image img) {
        UpdateRequest updateRequest = new UpdateRequest()
                .index("mosapico")
                .type("image")
                .id(img.getId())
                .doc("timesUsed", img.getTimesUsed() + 1);
        UpdateQuery updateQuery = new UpdateQueryBuilder()
                .withId(img.getId())
                .withClass(Image.class)
                .withUpdateRequest(updateRequest)
                .build();
        elasticsearchTemplate.update(updateQuery);
    }

    @Override
    public void resetTimesUsed() {
        List<String> ids = findAllIds();
        for (String id : ids) {
            UpdateRequest updateRequest = new UpdateRequest()
                    .index("mosapico")
                    .type("image")
                    .id(id)
                    .doc("timesUsed", 0);
            UpdateQuery updateQuery = new UpdateQueryBuilder()
                    .withId(id)
                    .withClass(Image.class)
                    .withUpdateRequest(updateRequest)
                    .build();
            elasticsearchTemplate.update(updateQuery);
        }
    }

    @Override
    public void resetAnalysed() {
        List<String> ids = findAllIds();
        for (String id : ids) {
            UpdateRequest updateRequest = new UpdateRequest()
                    .index("mosapico")
                    .type("image")
                    .id(id)
                    .doc("analyzed", false);
            UpdateQuery updateQuery = new UpdateQueryBuilder()
                    .withId(id)
                    .withClass(Image.class)
                    .withUpdateRequest(updateRequest)
                    .build();
            elasticsearchTemplate.update(updateQuery);
        }
    }

    private CriteriaQuery getColorCriteria(int variance, int r, int g, int b) {
        Criteria rCriteria = new Criteria("analysedImage.r").between(r - variance, r + variance);
        Criteria gCriteria = new Criteria("analysedImage.g").between(g - variance, g + variance);
        Criteria bCriteria = new Criteria("analysedImage.b").between(b - variance, b + variance);
        return new CriteriaQuery(rCriteria).addCriteria(bCriteria).addCriteria(gCriteria);
    }
}
