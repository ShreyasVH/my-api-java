package myapi.models;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public interface ResponseCode {

    Integer getCode();

    ResponseType getType();

    String getDescription();

}
