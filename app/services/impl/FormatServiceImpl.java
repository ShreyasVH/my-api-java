package services.impl;

import com.google.inject.Inject;
import exceptions.NotFoundException;
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

    @Override
    public Format get(Long id)
    {
        Format format = this.formatRepository.get(id);
        if(null == format)
        {
            throw new NotFoundException("Format");
        }

        return format;
    }
}
