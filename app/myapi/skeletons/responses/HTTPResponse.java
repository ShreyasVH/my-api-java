package myapi.skeletons.responses;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HTTPResponse extends Response
{
    private final int status;

    private final JsonNode response;

    public HTTPResponse()
    {
        this.status = 0;
        this.response = null;
    }

    public HTTPResponse(int status, JsonNode response)
    {
        this.status = status;
        this.response = response;
    }
}
