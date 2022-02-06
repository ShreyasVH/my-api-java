package services;

import exceptions.MyException;
import models.Artist;
import requests.ArtistRequest;

/**
 * Created by shreyas.hande on 12/11/17.
 */
public interface ArtistService
{
    Artist add(ArtistRequest artistRequest) throws MyException;

    Artist get(String id);

    Artist update(String id, ArtistRequest artistRequest);
}
