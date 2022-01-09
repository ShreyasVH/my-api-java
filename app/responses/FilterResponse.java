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
    private Integer totalCount = 0;
    private List<T> list = new ArrayList<>();
}
