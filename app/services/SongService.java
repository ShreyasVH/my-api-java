package services;

import io.ebean.SqlRow;
import models.Song;
import requests.FilterRequest;
import requests.SongRequest;
import responses.*;

import java.util.List;

public interface SongService {
    Song add(SongRequest request, MovieResponse movieResponse);

    List<SqlRow> dashboard();

    FilterResponse<SongElasticDocument> filter(FilterRequest filterRequest);

    SongElasticDocument get(Long id);

    Song edit(long id, SongRequest request, MovieResponse movieResponse);
}
