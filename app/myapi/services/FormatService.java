package myapi.services;

import myapi.exceptions.MyException;
import myapi.models.MovieFormat;

import java.util.List;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public interface FormatService
{
    List<MovieFormat> getAllFormats();

    MovieFormat getFormatById(Long formatId) throws MyException;

    MovieFormat getFormatByName(String name) throws MyException;
}
