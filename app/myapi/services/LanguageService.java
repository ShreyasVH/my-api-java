package myapi.services;

import myapi.exceptions.MyException;
import myapi.models.Language;

import java.util.List;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public interface LanguageService
{
    List<Language> getAllLanguages();

    Language getLanguageById(Long languageId) throws MyException;

    Language getLanguageByName(String name) throws MyException;
}
