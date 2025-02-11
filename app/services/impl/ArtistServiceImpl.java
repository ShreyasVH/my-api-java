package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.ConflictException;
import exceptions.MyException;
import exceptions.NotFoundException;
import models.*;
import org.springframework.util.StringUtils;
import repositories.ArtistRepository;
import requests.ArtistRequest;
import responses.FilterResponse;
import services.ArtistService;
import utils.Utils;

import java.util.List;

/**
 * Created by shreyas.hande on 12/11/17.
 */
public class ArtistServiceImpl implements ArtistService
{
    private final ArtistRepository artistRepository;

    @Inject
    public ArtistServiceImpl
    (
        ArtistRepository artistRepository
    )
    {
        this.artistRepository = artistRepository;
    }


    @Override
    public Artist add(ArtistRequest artistRequest) throws MyException
    {
        artistRequest.validate();

        Artist existing = this.artistRepository.getArtistByName(artistRequest.getName());

        if(null != existing)
        {
            throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
        }

        Artist artist = new Artist(artistRequest);

        return artistRepository.saveArtist(artist);
    }

    @Override
    public Artist get(Long id)
    {
        Artist artist = this.artistRepository.get(id);
        if(null == artist)
        {
            throw new NotFoundException("Artist");
        }

        return artist;
    }

    @Override
    public List<Artist> get(List<Long> ids)
    {
        return this.artistRepository.get(ids);
    }


    @Override
    public Artist update(Long id, ArtistRequest artistRequest)
    {
        artistRequest.validateForUpdate();

        Artist existingArtist = this.artistRepository.get(id);
        if(existingArtist == null)
        {
            throw new NotFoundException("Artist");
        }

        boolean isUpdateRequired = false;

        if(StringUtils.hasText(artistRequest.getName()) && !artistRequest.getName().equals(existingArtist.getName()))
        {
            Artist existing = this.artistRepository.getArtistByName(artistRequest.getName());

            if(null != existing)
            {
                throw new ConflictException(ErrorCode.ALREADY_EXISTS.getCode(), ErrorCode.ALREADY_EXISTS.getDescription());
            }

            isUpdateRequired = true;
            existingArtist.setName(artistRequest.getName());
        }

        if(StringUtils.hasText(artistRequest.getGender()) && !artistRequest.getGender().equals(existingArtist.getGender()))
        {
            isUpdateRequired = true;
            existingArtist.setGender(artistRequest.getGender());
        }

        if(StringUtils.hasText(artistRequest.getImageUrl()) && !artistRequest.getImageUrl().equals(existingArtist.getImageUrl()))
        {
            isUpdateRequired = true;
            existingArtist.setImageUrl(artistRequest.getImageUrl());
        }

        if(isUpdateRequired)
        {
            existingArtist = this.artistRepository.saveArtist(existingArtist);
        }

        return existingArtist;
    }

    @Override
    public FilterResponse<Artist> get(int offset, int count)
    {
        return this.artistRepository.get(offset, count);
    }

    @Override
    public List<Artist> getArtistsByKeyword(String keyword) {
        return this.artistRepository.getArtistsByKeyword(keyword);
    }
}
