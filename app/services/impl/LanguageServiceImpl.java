package services.impl;

import com.google.inject.Inject;
import exceptions.NotFoundException;
import models.Language;
import repositories.LanguageRepository;
import services.LanguageService;

import java.util.List;

public class LanguageServiceImpl implements LanguageService
{
    private final LanguageRepository languageRepository;

    @Inject
    public LanguageServiceImpl
    (
        LanguageRepository languageRepository
    )
    {
        this.languageRepository = languageRepository;
    }
    @Override
    public List<Language> getAll()
    {
        return this.languageRepository.getAll();
    }

    @Override
    public Language get(Long id)
    {
        Language language = this.languageRepository.get(id);

        if(null == language)
        {
            throw new NotFoundException("Language");
        }

        return language;
    }
}
