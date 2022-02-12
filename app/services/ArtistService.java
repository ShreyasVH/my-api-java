package services;

import exceptions.MyException;
import models.Artist;
import requests.ArtistRequest;
import responses.FilterResponse;

import java.util.List;

/**
 * Created by shreyas.hande on 12/11/17.
 */
public interface ArtistService
{
    Artist add(ArtistRequest artistRequest) throws MyException;

    Artist get(String id);

    List<Artist> get(List<String> ids);

    Artist update(String id, ArtistRequest artistRequest);

    FilterResponse<Artist> get(int offset, int count);
}
