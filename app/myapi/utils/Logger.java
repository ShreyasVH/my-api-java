package myapi.utils;

import java.util.HashMap;
import java.util.Map;

public class Logger
{
    private static void log(String content, String type)
    {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("content", content);
        payload.put("source", System.getenv("LOGGER_SOURCE"));

        String url = System.getenv("LOGGER_API_ENDPOINT");
        Api.post(url, payload);
    }

    public static void success(String content)
    {
        log(content, "SUCCESS");
    }

    public static void error(String content)
    {
        log(content, "ERROR");
    }

    public static void debug(String content)
    {
        log(content, "DEBUG");
    }
}
