package myapi.utils;

import myapi.skeletons.responses.HTTPResponse;
import play.libs.F.Promise;

import java.util.HashMap;
import java.util.Map;

public class Logger
{
    private static Promise<HTTPResponse> log(String content, String type)
    {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("content", content);
        payload.put("source", System.getenv("LOGGER_SOURCE"));

        String url = System.getenv("LOGGER_API_ENDPOINT") + "logs";
        return Api.post(url, payload);
    }

    public static Promise<HTTPResponse> success(String content)
    {
        return log(content, "SUCCESS");
    }

    public static Promise<HTTPResponse> error(String content)
    {
        return log(content, "ERROR");
    }

    public static Promise<HTTPResponse> debug(String content)
    {
        return log(content, "DEBUG");
    }
}
