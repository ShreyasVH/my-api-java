package requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.libs.Json;

/**
 * Created by shreyasvh on 8/27/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {

    public String asText() {
        return Json.toJson(this).toString();
    }

    @JsonCreator
    public Request() {

    }
}
