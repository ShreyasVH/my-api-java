package services.impl;

import com.google.inject.Inject;
import models.Format;
import repositories.FormatRepository;
import services.FormatService;

import java.util.List;

public class FormatServiceImpl implements FormatService
{

    private final FormatRepository formatRepository;

    @Inject
    public FormatServiceImpl
    (
        FormatRepository formatRepository
    )
    {
        this.formatRepository = formatRepository;
    }
    @Override
    public List<Format> getAll()
    {
        return this.formatRepository.getAll();
    }
}
