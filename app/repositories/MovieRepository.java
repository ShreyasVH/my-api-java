package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlQuery;
import io.ebean.SqlRow;
import models.Movie;
import models.MovieActorMap;
import models.MovieDirectorMap;
import org.elasticsearch.search.sort.SortOrder;
import play.db.ebean.EbeanConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;

import play.db.ebean.EbeanDynamicEvolutions;
import requests.FilterRequest;
import responses.FilterResponse;

public class MovieRepository
{
	private final EbeanServer db;
	private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
	private final DatabaseExecutionContext databaseExecutionContext;

	@Inject
	public MovieRepository
	(
		EbeanConfig ebeanConfig,
		EbeanDynamicEvolutions ebeanDynamicEvolutions,
		DatabaseExecutionContext databaseExecutionContext
	)
	{
		this.ebeanDynamicEvolutions = ebeanDynamicEvolutions;
		this.db = Ebean.getServer(ebeanConfig.defaultServer());
		this.databaseExecutionContext = databaseExecutionContext;
	}

	public List<SqlRow> getDashboard()
	{
		List<SqlRow> dbList = new ArrayList<>();
		try
		{
			String sql = "select l.id as language_id, l.name as language, count(*) as count, sum(m.size) as size from movies m inner join languages l on m.language_id = l.id where m.active = 1 group by m.language_id";
			SqlQuery query = db.createSqlQuery(sql);
			dbList = query.findList();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
		return dbList;
	}

	public String getFieldNameWithTablePrefix(String field)
	{
		String fieldName = "";

		switch(field)
		{
			case "language":
				fieldName = "m.language_id";
				break;
			case "format":
				fieldName = "m.format_id";
				break;
			case "active":
				fieldName = "m.active";
				break;
			case "quality":
				fieldName = "m.quality";
				break;
			case "subtitles":
				fieldName = "m.subtitles";
				break;
			case "seenInTheatre":
				fieldName = "m.seen_in_theatre";
				break;
		}

		return fieldName;
	}

	public String getFieldNameForDisplay(String field)
	{
		String fieldName = "";

		switch(field)
		{
			case "language":
				fieldName = "language";
				break;
			case "format":
				fieldName = "format";
				break;
			case "year":
				fieldName = "year";
				break;
			case "id":
				fieldName = "id";
				break;
			case "name":
				fieldName = "name";
				break;
		}

		return fieldName;
	}

	public Movie get(Long id)
	{
		Movie movie = null;

		try
		{
			movie = this.db.find(Movie.class).where().eq("id", id).eq("active", true).findOne();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
		return movie;
	}

	public Movie get(String name, Long languageId, Integer year)
	{
		Movie movie = null;

		try
		{
			movie = this.db.find(Movie.class).where().eq("name", name).eq("languageId", languageId).eq("year", year).findOne();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
		return movie;
	}

	public List<MovieActorMap> getActorMaps(Long movieId)
	{
		List<MovieActorMap> actorMaps = new ArrayList<>();

		try
		{
			actorMaps = this.db.find(MovieActorMap.class).where().eq("movieId", movieId).findList();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
		return actorMaps;
	}

	public List<MovieActorMap> saveActorMaps(List<MovieActorMap> actorMaps)
	{
		try
		{
			this.db.saveAll(actorMaps);
			return actorMaps;
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
	}

	public void removeActorMaps(List<MovieActorMap> actorMaps)
	{
		try
		{
			this.db.deleteAll(actorMaps);
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
	}

	public List<MovieDirectorMap> saveDirectorMaps(List<MovieDirectorMap> directorMaps)
	{
		try
		{
			this.db.saveAll(directorMaps);
			return directorMaps;
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
	}

	public void removeDirectorMaps(List<MovieDirectorMap> directorMaps)
	{
		try
		{
			this.db.deleteAll(directorMaps);
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
	}

	public List<MovieDirectorMap> getDirectorMaps(Long movieId)
	{
		List<MovieDirectorMap> directorMaps = new ArrayList<>();

		try
		{
			directorMaps = this.db.find(MovieDirectorMap.class).where().eq("movieId", movieId).findList();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
		return directorMaps;
	}

	public Movie save(Movie movie)
	{
		try
		{
			this.db.save(movie);
			return movie;
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
	}
}