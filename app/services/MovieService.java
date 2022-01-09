package services;

import io.ebean.SqlRow;
import java.util.List;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public interface MovieService
{
    List<SqlRow> getDashboard();
}
