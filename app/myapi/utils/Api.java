package myapi.utils;

import myapi.skeletons.responses.HTTPResponse;
import play.libs.F;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;

import java.util.HashMap;
import java.util.Map;

public class Api
{
    public static Promise<HTTPResponse> get(String url)
    {
        return get(url, new HashMap<>(), new HashMap<>());
    }

    public static Promise<HTTPResponse> get(String url, Map<String, Object> params)
    {
        return get(url, params, new HashMap<>());
    }

    public static Promise<HTTPResponse> get(String url, Map<String, Object> params, Map<String, String> headers)
    {
        Promise<HTTPResponse> finalResponse = F.Promise.promise(HTTPResponse::new);

        try
        {
            WSRequestHolder requestHolder = buildRequest(url, params, headers, "GET");
            Promise<WSResponse> response = requestHolder.get();
            finalResponse = buildResponse(response);
        }
        catch(Exception ex)
        {

        }

        return finalResponse;
    }

    public static Promise<HTTPResponse> post(String url, Map<String, Object> params)
    {
        return post(url, params, new HashMap<>());
    }

    public static Promise<HTTPResponse> post(String url, Map<String, Object> params, Map<String, String> headers)
    {
        Promise<HTTPResponse> finalResponse = F.Promise.promise(HTTPResponse::new);

        try
        {
            WSRequestHolder requestHolder = buildRequest(url, new HashMap<>(), headers, "POST");
            Promise<WSResponse> response = requestHolder.post(Json.toJson(params));
            finalResponse = buildResponse(response);
        }
        catch(Exception ex)
        {

        }

        return finalResponse;
    }

    private static WSRequestHolder buildRequest(String url, Map<String, Object> params, Map<String, String> headers, String methodType)
    {
        WSRequestHolder requestHolder = WS.url(url);

        for (Map.Entry<String, String> entry : headers.entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();

            requestHolder.setHeader(key, value);
        }

        switch (methodType)
        {
            case "GET":
                for (Map.Entry<String, Object> entry : params.entrySet())
                {
                    String key = entry.getKey();
                    String value = (String) entry.getValue();

                    requestHolder.setQueryParameter(key, value);
                }
                break;
        }


        return requestHolder;
    }

    private static Promise<HTTPResponse> buildResponse(Promise<WSResponse> response)
    {
        return response.map(wsResponse -> {
            HTTPResponse responseFromPromise;
            try
            {
                responseFromPromise = new HTTPResponse(wsResponse.getStatus(), wsResponse.asJson());
            }
            catch(Exception ex)
            {
                responseFromPromise = new HTTPResponse(wsResponse.getStatus(), Json.toJson(wsResponse.getBody()));
            }

            return responseFromPromise;
        });
    }
}
