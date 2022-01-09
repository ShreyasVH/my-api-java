package enums;

import io.ebean.annotation.EnumValue;
import lombok.Getter;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public enum Status
{
    @EnumValue("0")
    DEFAULT(0),

    @EnumValue("1")
    ENABLED(1),

    @EnumValue("2")
    DISABLED(2),

    @EnumValue("3")
    DELETED(3);

    @Getter
    private int status;

    Status(int status)
    {
        this.status = status;
    }

    public static Status getStatus(int status)
    {
        Status st = null;
        for(Status s : Status.values())
        {
            if(s.getStatus() == status)
            {
                st = s;
                break;
            }
        }
        return st;
    }
}
