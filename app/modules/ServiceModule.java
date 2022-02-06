package modules;

import services.*;
import services.impl.*;

import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule
{
	@Override
	public void configure()
	{
		bind(MovieService.class).to(MovieServiceImpl.class).asEagerSingleton();
		bind(ArtistService.class).to(ArtistServiceImpl.class).asEagerSingleton();
		bind(LanguageService.class).to(LanguageServiceImpl.class).asEagerSingleton();
		bind(FormatService.class).to(FormatServiceImpl.class).asEagerSingleton();
	}
}