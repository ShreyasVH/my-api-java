package myapi.skeletons.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by shreyasvh on 8/27/17.
 */

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class MovieRequest extends Request {

    private Long id;

    private String name;

    private Long languageId;

    private Long size;

    private Long formatId;

    private String quality;

    private Integer year;

    private Boolean subtitles;

    private Boolean seenInTheatre;

    private String basename;

    private List<String> actorIds;

    private List<String> directorIds;
}
