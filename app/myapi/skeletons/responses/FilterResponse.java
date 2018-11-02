package myapi.skeletons.responses;

import lombok.Getter;
import lombok.Setter;
import myapi.constants.Constants;

import java.util.List;

/**
 * Created by shreyas.hande on 2/11/18.
 */
public class FilterResponse<T>
{
    @Getter
    @Setter
    Long totalCount = 0L;

    @Getter
    @Setter
    Long offset = Constants.DEFAULT_OFFSET;

    @Getter
    @Setter
    List docList;

    public FilterResponse(ElasticResponse elasticResponse)
    {
        totalCount = elasticResponse.getTotalCount();
        docList = elasticResponse.getDocList();
    }
}
