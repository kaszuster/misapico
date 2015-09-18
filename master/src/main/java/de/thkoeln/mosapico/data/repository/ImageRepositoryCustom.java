package de.thkoeln.mosapico.data.repository;

import de.thkoeln.mosapico.data.model.Image;

import java.util.List;

public interface ImageRepositoryCustom {

    List<String> findAllIds();

    List<String> findAllNotAnalyzedIds();

    Image findByAverageColor(int r, int g, int b);

    void increaseTimesUsed(Image img);

    void resetTimesUsed();

    void resetAnalysed();

}
