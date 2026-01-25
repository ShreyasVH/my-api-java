package responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterResponse<T>
{
    private Integer offset = 0;
    private long totalCount = 0L;
    private List<T> list = new ArrayList<>();
}
