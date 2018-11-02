package myapi.skeletons.requests;

import myapi.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.search.sort.SortOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shreyas.hande on 12/7/17.
 */
public class FilterRequest extends Request
{
    @Getter
    @Setter
    private Map<String, List<String>> filters = new HashMap<>();

    @Getter
    @Setter
    private Integer offset = Constants.DEFAULT_ELASTIC_OFFSET;

    @Getter
    @Setter
    private Integer count = Constants.DEFAULT_ELASTIC_COUNT;

    @Getter
    @Setter
    private Map<String, SortOrder> sortMap = new HashMap<>();

    @Getter
    @Setter
    private Boolean includeDeleted = false;
}
