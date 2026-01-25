package requests;

import co.elastic.clients.elasticsearch._types.SortOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FilterRequest
{
    private Integer count = 24;
    private Integer offset = 0;
    private Map<String, List<String>> filters = new HashMap<>();
    private Map<String, Boolean> booleanFilters = new HashMap<>();
    private Map<String, List<String>> andFilters = new HashMap<>();
    private Map<String, Map<String, String>> rangeFilters = new HashMap<>();
    private Map<String, SortOrder> sortMap = new HashMap<>();
}
