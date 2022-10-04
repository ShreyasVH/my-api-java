package responses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FilterResponse<T>
{
    private Integer offset = 0;
    private long totalCount = 0L;
    private List<T> list = new ArrayList<>();
}
