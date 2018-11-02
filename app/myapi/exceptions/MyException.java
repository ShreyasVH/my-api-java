package myapi.exceptions;

import lombok.Getter;
import lombok.Setter;
import myapi.models.ResponseCode;
import myapi.models.ResponseType;
import play.libs.Json;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public abstract class MyException extends Exception implements ResponseCode
{
    @Getter
    private final ResponseCode responseCode;

    @Getter
    @Setter
    private String customMessage;

    public abstract int getHttpResponseCode();

    public MyException(ResponseCode responseCode) {
        super(Json.toJson(responseCode).toString());
        this.responseCode = responseCode;
    }

    public MyException(ResponseCode responseCode, Throwable cause) {
        super(Json.toJson(responseCode).toString(), cause);
        this.responseCode = responseCode;
    }

    public MyException(ResponseCode responseCode, String customMessage) {
        super(Json.toJson(responseCode).toString());
        this.responseCode = responseCode;
        this.customMessage = customMessage;
    }

    public MyException(ResponseCode responseCode, Throwable cause, String customMessage) {
        super(Json.toJson(responseCode).toString(), cause);
        this.responseCode = responseCode;
        this.customMessage = customMessage;
    }

    @Override
    public Integer getCode() {
        return responseCode.getCode();
    }

    @Override
    public ResponseType getType() {
        return responseCode.getType();
    }

    @Override
    public String getDescription() {
        if (this.customMessage != null) {
            return responseCode.getDescription() + ". Custom Message: " + this.customMessage;
        } else {
            return responseCode.getDescription();
        }
    }
}
