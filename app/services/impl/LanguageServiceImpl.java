package services.impl;

import com.google.inject.Inject;
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
}
