package myapi.skeletons.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyas.hande on 1/13/18.
 */
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SongRequest extends Request
{
    private String id;

    private String name;

    private Long size = 0L;

    private Long movie_id;

    private List<String> singer_ids = new ArrayList<>();

    private List<String> composer_ids = new ArrayList<>();

    private List<String> lyricist_ids = new ArrayList<>();
}


