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
public class ActorsCombinationMoviesRequest extends Request {

    private List<String> actorIds;
}
