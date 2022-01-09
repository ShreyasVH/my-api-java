package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ErrorCode
{
    NOT_FOUND(4001, "%s Not Found"),
    INVALID_REQUEST(4002, "Invalid Request"),
    DB_INTERACTION_FAILED(4003, "DB Interaction Failed"),
    ALREADY_EXISTS(4004, "Already Exists"),
    INTERNAL_SERVER_ERROR(4005, "Internal Server Error");

    @Getter
    private int code;

    @Getter
    private String description;
}
