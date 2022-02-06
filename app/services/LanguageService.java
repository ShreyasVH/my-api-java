package services;

import models.Language;

import java.util.List;

public interface LanguageService
{
    List<Language> getAll();

    Language get(Long id);
}
