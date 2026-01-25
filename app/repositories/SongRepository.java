package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import jakarta.persistence.EntityManager;
import models.*;

import java.util.*;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;
import play.db.jpa.JPAApi;


public class SongRepository
{
    private final JPAApi jpaApi;

    @Inject
    public SongRepository
    (
        JPAApi jpaApi
    )
    {
        this.jpaApi = jpaApi;
    }

    public Song save(Song song)
    {
        return jpaApi.withTransaction(em -> {
            return save(em, song);
        });
    }

    public Song save(EntityManager em, Song song)
    {
        em.persist(song);
        return song;
    }

    public List<SongSingerMap> saveSingerMaps(List<SongSingerMap> singerMaps)
    {
        return jpaApi.withTransaction(em -> {
            return saveSingerMaps(em, singerMaps);
        });
    }

    public List<SongSingerMap> saveSingerMaps(EntityManager em, List<SongSingerMap> singerMaps)
    {
        singerMaps.forEach(em::persist);
        return singerMaps;
    }

    public void deleteSingerMaps(List<SongSingerMap> singerMaps)
    {
        jpaApi.withTransaction(em -> {
            deleteSingerMaps(em, singerMaps);
        });
    }

    public void deleteSingerMaps(EntityManager em, List<SongSingerMap> singerMaps)
    {
        singerMaps.forEach(em::remove);
    }

    public List<SongComposerMap> saveComposerMaps(List<SongComposerMap> composerMaps)
    {
        return jpaApi.withTransaction(em -> {
            return saveComposerMaps(em, composerMaps);
        });
    }

    public List<SongComposerMap> saveComposerMaps(EntityManager em, List<SongComposerMap> composerMaps)
    {
        composerMaps.forEach(em::persist);
        return composerMaps;
    }

    public void deleteComposerMaps(List<SongComposerMap> composerMaps)
    {
        jpaApi.withTransaction(em -> {
            deleteComposerMaps(em, composerMaps);
        });
    }

    public void deleteComposerMaps(EntityManager em, List<SongComposerMap> composerMaps)
    {
        composerMaps.forEach(em::remove);
    }

    public List<SongLyricistMap> saveLyricistMaps(List<SongLyricistMap> lyricistMaps)
    {
        return jpaApi.withTransaction(em -> {
            return saveLyricistMaps(em, lyricistMaps);
        });
    }

    public List<SongLyricistMap> saveLyricistMaps(EntityManager em, List<SongLyricistMap> lyricistMaps)
    {
        lyricistMaps.forEach(em::persist);
        return lyricistMaps;
    }

    public void deleteLyricistMaps(List<SongLyricistMap> lyricistMaps)
    {
        jpaApi.withTransaction(em -> {
            deleteLyricistMaps(em, lyricistMaps);
        });
    }

    public void deleteLyricistMaps(EntityManager em, List<SongLyricistMap> lyricistMaps)
    {
        lyricistMaps.forEach(em::remove);
    }

    public List<SongSingerMap> getSingerMaps(Long songId)
    {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT ssm FROM SongSingerMap ssm WHERE ssm.songId = :songId", SongSingerMap.class)
                    .setParameter("songId", songId)
                    .getResultList();
        });
    }

    public List<SongComposerMap> getComposerMaps(Long songId)
    {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT scm FROM SongComposerMap scm WHERE scm.songId = :songId", SongComposerMap.class)
                    .setParameter("songId", songId)
                    .getResultList();
        });
    }

    public List<SongLyricistMap> getLyricistMaps(Long songId)
    {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT slm FROM SongLyricistMap slm WHERE slm.songId = :songId", SongLyricistMap.class)
                    .setParameter("songId", songId)
                    .getResultList();
        });
    }

    public List getDashboard()
    {
        List<Object[]> dbList = jpaApi.withTransaction(em -> {
            return em.createNativeQuery("SELECT l.name as language, count(*) as count, sum(s.size) as size FROM songs s inner join movies m on m.id = s.movie_id inner join languages l on l.id = m.language_id group by m.language_id order by l.id")
                    .getResultList();
        });
        List<Map<String, Object>> response = new ArrayList<>();
        for(Object[] row: dbList)
        {
            Map<String, Object> responseItem = Map.of(
                    "language", row[0],
                    "count", row[1],
                    "size", row[2]);

            response.add(responseItem);
        }
        return response;
    }

    public Song get(Long id)
    {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT s FROM Song s WHERE s.id = :id", Song.class)
                    .setParameter("id", id)
                    .getSingleResultOrNull();
        });
    }
}