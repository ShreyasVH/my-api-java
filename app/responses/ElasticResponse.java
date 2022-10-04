package responses;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ElasticResponse<T>
{
    @Getter
    @Setter
    Long totalCount = 0L;

    @Getter
    @Setter
    List<T> docList;
}
