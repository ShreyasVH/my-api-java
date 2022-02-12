package requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MovieRequest
{
    private String name;
    private Long languageId;
    private Long size;
    private Long formatId;
    private String quality;
    private Integer year;
    private Boolean subtitles;
    private Boolean seenInTheatre;
    private String basename;
    private String imageUrl;
    private List<String> actors;
    private List<String> directors;
}
