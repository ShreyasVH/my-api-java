package services;

import io.ebean.SqlRow;
import requests.FilterRequest;
import responses.FilterResponse;

import java.util.List;
import java.util.Map;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public interface MovieService
{
    List<SqlRow> getDashboard();

    FilterResponse<Map<String, Object>> filter(FilterRequest filterRequest);
}
