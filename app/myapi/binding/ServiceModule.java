package myapi.binding;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import myapi.services.*;
import myapi.services.elastic.ElasticService;
import myapi.services.impl.*;

/**
 * Created by shreyas.hande on 12/10/17.
 */
public class ServiceModule extends AbstractModule
{
    protected void configure()
    {
        bind(MovieService.class).to(MovieServiceImpl.class).in(Scopes.SINGLETON);
        bind(MovieIndexService.class).to(MovieIndexServiceImpl.class).in(Scopes.SINGLETON);
        bind(ElasticService.class).in(Scopes.SINGLETON);
        bind(LanguageService.class).to(LanguageServiceImpl.class).in(Scopes.SINGLETON);
        bind(RedisService.class).in(Scopes.SINGLETON);
        bind(FormatService.class).to(FormatServiceImpl.class).in(Scopes.SINGLETON);
        bind(ArtistService.class).to(ArtistServiceImpl.class).in(Scopes.SINGLETON);
        bind(SongService.class).to(SongServiceImpl.class).in(Scopes.SINGLETON);
        bind(SongIndexService.class).to(SongIndexServiceImpl.class).in(Scopes.SINGLETON);
    }
}
