package repositories;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import models.Movie;
import models.MovieActorMap;
import models.MovieDirectorMap;
import models.SongSingerMap;

import java.math.BigDecimal;
import java.util.*;

import com.google.inject.Inject;

import play.db.jpa.JPAApi;
import utils.Utils;

public class MovieRepository
{
	private final JPAApi jpaApi;

	@Inject
	public MovieRepository(JPAApi jpaApi) {
		this.jpaApi = jpaApi;
	}

	public List getDashboard()
	{
		List<Object[]> dbList = jpaApi.withTransaction(em -> {
			return em.createNativeQuery("select l.id as language_id, l.name as language, count(*) as count, count(case when obtained = 1 then 1 end) as obtained_count, sum(case when m.obtained = 1 then m.size else 0 end) as size from movies m inner join languages l on m.language_id = l.id where m.active = 1 group by m.language_id")
					.getResultList();
		});
		List<Map<String, Object>> response = new ArrayList<>();
		for(Object[] row: dbList)
		{
			Map<String, Object> responseItem = Map.of(
					"language_id", row[0],
					"language", row[1],
					"count", row[2],
					"obtained_count", row[3],
					"size", row[4]);

			response.add(responseItem);
		}
		return response;
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
		return jpaApi.withTransaction(em -> {
			return em.createQuery("SELECT m FROM Movie m WHERE m.id = :id", Movie.class)
					.setParameter("id", id)
					.getSingleResultOrNull();
		});
	}

	public Movie get(String name, Long languageId, String releaseDate)
	{
		return jpaApi.withTransaction(em -> {
			return em.createQuery("SELECT m FROM Movie m WHERE m.name = :name AND m.languageId = :languageId AND m.releaseDate = :releaseDate", Movie.class)
				.setParameter("name", name)
				.setParameter("languageId", languageId)
				.setParameter("releaseDate", Utils.parseDateString(releaseDate))
				.getSingleResultOrNull();
		});

	}

	public List<MovieActorMap> getActorMaps(Long movieId)
	{
		return jpaApi.withTransaction(em -> {
			return em.createQuery("SELECT mam FROM MovieActorMap mam WHERE mam.movieId = :movieId", MovieActorMap.class)
					.setParameter("movieId", movieId)
					.getResultList();
		});
	}

	public List<MovieActorMap> saveActorMaps(List<MovieActorMap> actorMaps)
	{
		return jpaApi.withTransaction(em -> {
			return saveActorMaps(em, actorMaps);
		});
	}

	public List<MovieActorMap> saveActorMaps(EntityManager em, List<MovieActorMap> actorMaps)
	{
		actorMaps.forEach(em::persist);
		return actorMaps;
	}


	public void removeActorMaps(List<MovieActorMap> actorMaps)
	{
		jpaApi.withTransaction(em -> {
			actorMaps.forEach(em::remove);
		});
	}

	public List<MovieDirectorMap> saveDirectorMaps(List<MovieDirectorMap> directorMaps)
	{
		return jpaApi.withTransaction(em -> {
			return saveDirectorMaps(em, directorMaps);
		});
	}

	public List<MovieDirectorMap> saveDirectorMaps(EntityManager em, List<MovieDirectorMap> directorMaps)
	{
		directorMaps.forEach(em::persist);

		return directorMaps;
	}

	public void removeDirectorMaps(List<MovieDirectorMap> directorMaps)
	{
		jpaApi.withTransaction(em -> {
			directorMaps.forEach(em::remove);
		});
	}

	public List<MovieDirectorMap> getDirectorMaps(Long movieId)
	{
		return jpaApi.withTransaction(em -> {
			return em.createQuery("SELECT mdm FROM MovieDirectorMap mdm WHERE mdm.movieId = :movieId", MovieDirectorMap.class)
					.setParameter("movieId", movieId)
					.getResultList();
		});
	}

	public Movie save(Movie movie)
	{
		return jpaApi.withTransaction(em -> {
			return save(em, movie);
		});
	}

	public Movie save(EntityManager em, Movie movie)
	{
		em.persist(movie);
		return movie;
	}
}