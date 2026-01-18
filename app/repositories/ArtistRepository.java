package repositories;

import com.google.inject.Inject;
import models.Artist;
import play.db.jpa.JPAApi;
import responses.FilterResponse;

import java.net.URLDecoder;
import java.util.List;

/**
 * Created by shreyas.hande on 12/6/17.
 */
public class ArtistRepository
{
    private final JPAApi jpaApi;
    @Inject
    public ArtistRepository
    (
        JPAApi jpaApi
    )
    {
        this.jpaApi = jpaApi;
    }

    public Artist saveArtist(Artist artist)
    {
        return jpaApi.withTransaction(em -> {
            em.persist(artist);
            return artist;
        });
    }

    public Artist getArtistByName(String name)
    {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT a FROM Artist a WHERE a.name = :name", Artist.class)
                    .setParameter("name", name)
                    .getSingleResultOrNull();
        });
    }

    public List<Artist> get(List<Long> ids)
    {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT a FROM Artist a WHERE a.id IN :ids", Artist.class)
                    .setParameter("ids", ids)
                    .getResultList();
        });
    }

    public Artist get(Long id)
    {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT a FROM Artist a WHERE a.id = :id", Artist.class)
                    .setParameter("id", id)
                    .getSingleResultOrNull();
        });
    }

    public FilterResponse<Artist> get(int offset, int count)
    {
        long totalCount = jpaApi.withTransaction(em -> {
            return em.createQuery(
                    "SELECT COUNT(a) FROM Artist a",
                    Long.class
            )
                    .getSingleResult();
        });

        List<Artist> artists = jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT a FROM Artist a ORDER BY a.name", Artist.class)
                    .setFirstResult(offset)
                    .setMaxResults(count)
                    .getResultList();
        });

        return new FilterResponse<>(offset, totalCount, artists);
    }

    public List<Artist> getArtistsByKeyword(String keyword)
    {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT a FROM Artist a WHERE LOWER(a.name) LIKE LOWER(:name)", Artist.class)
                    .setParameter("name", "%" + URLDecoder.decode(keyword) + "%")
                    .getResultList();
        });
    }
}
