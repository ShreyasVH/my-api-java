package myapi.services.impl;

import com.google.inject.Inject;
import myapi.dao.ArtistDao;
import myapi.exceptions.BadRequestException;
import myapi.exceptions.MyException;
import myapi.exceptions.NotFoundException;
import myapi.models.Artist;
import myapi.models.ErrorResponse;
import myapi.models.ValidationResponse;
import myapi.services.ArtistService;
import myapi.skeletons.requests.ArtistRequest;
import myapi.utils.Utils;

import java.util.List;

/**
 * Created by shreyas.hande on 12/11/17.
 */
public class ArtistServiceImpl implements ArtistService
{
    private final ArtistDao artistDao;

    @Inject
    public ArtistServiceImpl(ArtistDao artistDao)
    {
        this.artistDao = artistDao;
    }


    @Override
    public List<Artist> getAllActors()
    {
        return artistDao.getAllActors();
    }

    @Override
    public List<Artist> getAllDirectors()
    {
        return artistDao.getAllDirectors();
    }

    @Override
    public Artist addArtist(ArtistRequest artistRequest) throws MyException
    {
        artistRequest.validate();

        Artist existing = null;
        try
        {
            existing = getArtistByName(artistRequest.getName());
        }
        catch(NotFoundException ex)
        {

        }

        if(null != existing)
        {
            throw new BadRequestException(ValidationResponse.DUPLICATE_ENTRY);
        }

        Artist artist = new Artist();
        artist.setId(Utils.generateUniqueIdByParam("ar"));
        artist.setName(artistRequest.getName());
        artist.setGender(artistRequest.getGender());
        artist.setImageUrl(artistRequest.getImageUrl());

        Boolean isSuccess = artistDao.saveArtist(artist);
        if(!isSuccess)
        {
            throw new BadRequestException(ErrorResponse.API_FAILED);
        }
        return artist;
    }

    @Override
    public Artist updateArtist(ArtistRequest artistRequest) throws MyException
    {
        artistRequest.validateUpdation();

        Artist existingArtist = getArtistById(artistRequest.getId());
        boolean isUpdateRequired = false;


        if(null != artistRequest.getName())
        {
            existingArtist.setName(artistRequest.getName());
            isUpdateRequired = true;
        }

        if(null != artistRequest.getGender())
        {
            existingArtist.setGender(artistRequest.getGender());
            isUpdateRequired = true;
        }

        if(null != artistRequest.getImageUrl())
        {
            existingArtist.setImageUrl(artistRequest.getImageUrl());
            isUpdateRequired = true;
        }

        if(isUpdateRequired)
        {
            boolean isSuccess = artistDao.saveArtist(existingArtist);
            if(!isSuccess)
            {
                throw new BadRequestException(ErrorResponse.API_FAILED);
            }
        }
        return existingArtist;
    }

    @Override
    public Artist getArtistByName(String name) throws MyException
    {
        Artist artist = artistDao.getArtistByName(name);
        if(null == artist)
        {
            throw new NotFoundException(ValidationResponse.ARTIST_NOT_FOUND);
        }
        return artist;
    }

    @Override
    public Artist getArtistById(String id) throws MyException
    {
        Artist artist = artistDao.getArtistById(id);
        if(null == artist)
        {
            throw new NotFoundException(ValidationResponse.ARTIST_NOT_FOUND);
        }
        return artist;
    }

    @Override
    public List<Artist> getArtistsByKeyword(String keyword)
    {
        return artistDao.getArtistsByKeyword(keyword);
    }

    @Override
    public List<Artist> getAllSingers()
    {
        return artistDao.getAllSingers();
    }

    @Override
    public List<Artist> getAllComposers()
    {
        return artistDao.getAllComposers();
    }

    @Override
    public List<Artist> getAllLyricists()
    {
        return artistDao.getAllLyricists();
    }
}
