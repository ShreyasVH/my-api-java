package myapi.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by shreyas.hande on 12/6/17.
 */
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericResponse implements ResponseCode
{
    @Getter
    @Setter
    @JsonProperty("type")
    private ResponseType type;

    @Getter
    @Setter
    @JsonProperty("code")
    private Integer code;

    @Getter
    @Setter
    @JsonProperty("description")
    private String description;

    @JsonCreator
    public GenericResponse()
    {

    }
}
