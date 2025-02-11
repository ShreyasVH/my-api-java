package requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SongRequest extends Request
{
    private String id;

    private String name;

    private Long size = 0L;

    private Long movieId;

    private List<Long> singerIds = new ArrayList<>();

    private List<Long> composerIds = new ArrayList<>();

    private List<Long> lyricistIds = new ArrayList<>();
}


