package myapi.services;

import myapi.exceptions.MyException;
import myapi.models.Artist;
import myapi.skeletons.requests.AddArtistRequest;

import java.util.List;

/**
 * Created by shreyas.hande on 12/11/17.
 */
public interface ArtistService
{
    List<Artist> getAllActors();

    List<Artist> getAllDirectors();

    Artist addArtist(AddArtistRequest addArtistRequest) throws MyException;

    Artist getArtistByName(String name) throws MyException;

    Artist getArtistById(String id) throws MyException;

    List<Artist> getArtistsByKeyword(String keyword);

    List<Artist> getAllSingers();

    List<Artist> getAllComposers();

    List<Artist> getAllLyricists();
}
