package myapi.skeletons.requests;

import lombok.Getter;
import lombok.Setter;
import myapi.models.Status;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class UpdateStatusRequest extends Request
{
    @Getter
    @Setter
    private Object id;

    @Getter
    @Setter
    private Status status;
}
