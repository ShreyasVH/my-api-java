package myapi.skeletons.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by shreyas.hande on 12/5/17.
 */
public class ElasticResponse<T>
{
    @Getter
    @Setter
    Long totalCount = 0L;

    @Getter
    @Setter
    List docList;
}
