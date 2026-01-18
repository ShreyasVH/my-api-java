package enums;

import lombok.Getter;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public enum Status
{
    DEFAULT(0),

    ENABLED(1),

    DISABLED(2),

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
