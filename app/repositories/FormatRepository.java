package repositories;

import com.google.inject.Inject;
import models.Format;
import play.db.jpa.JPAApi;

import java.util.List;

public class FormatRepository
{
    private final JPAApi jpaApi;
    @Inject
    public FormatRepository
    (
        JPAApi jpaApi
    )
    {
        this.jpaApi = jpaApi;
    }

    public List<Format> getAll()
    {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT f FROM Format f", Format.class)
                    .getResultList();
        });
    }

    public Format get(Long id)
    {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT f FROM Format f WHERE id = :id", Format.class)
                    .setParameter("id", id)
                    .getSingleResultOrNull();
        });
    }
}
