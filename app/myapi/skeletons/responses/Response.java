package myapi.skeletons.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import myapi.models.ErrorResponse;
import myapi.models.GenericResponse;
import myapi.models.ResponseType;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shreyas.hande on 12/6/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response
{
    private static final Logger.ALogger LOGGER = Logger.of(Response.class);

    /**
     * @param object
     * @return
     */
    public static JsonNode getErrorResponse(Object object) {
        Map<String, Object> response = new HashMap<>();
        response.put(ResponseType.ERROR.getName(), object);
        return Json.toJson(response);
    }

    public static GenericResponse getErrorResponse(Throwable exception) {

        GenericResponse errorResponse = new GenericResponse();
        errorResponse.setType(ResponseType.ERROR);
        errorResponse.setCode(ErrorResponse.API_FAILED.getCode());
        errorResponse.setDescription(exception.getMessage() + ". Trace: " + Json.toJson(exception.getStackTrace()));
        return errorResponse;
    }

    /**
     * @param object
     * @return
     */
    public static JsonNode publicApiResponse(Object object) {
        Map<String, Object> response = new HashMap<>();
        response.put(ResponseType.SUCCESS.getName(), object);
        return Json.toJson(response);
    }

    /**
     * @param object
     * @return
     */
    public static JsonNode success(Object object) {
        Map<String, Object> response = new HashMap<>();
        response.put(ResponseType.SUCCESS.getName(), object);
        return Json.toJson(response);
    }

    /**
     * @return
     */
    public JsonNode toJson() {
        return Json.toJson(this);
    }

    private static Result handleExceptionReturnResult(Exception e) {
        // Log all exceptions.
        if (e instanceof IllegalArgumentException || e instanceof IllegalStateException
                || e instanceof NoSuchAlgorithmException) {
            return Results.status(400, e.getMessage());
        }
        else {
            LOGGER.error("Internal Server Error Occurred while processing the request ", e);
            return Results.status(500, "Internal Server Error Occurred while processing the request");
        }
    }
}
