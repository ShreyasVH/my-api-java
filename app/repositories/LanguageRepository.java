package repositories;

import com.google.inject.Inject;
import models.Language;
import play.db.jpa.JPAApi;

import java.util.List;

public class LanguageRepository
{
    private final JPAApi jpaApi;

    @Inject
    public LanguageRepository
    (
        JPAApi jpaApi
    )
    {
        this.jpaApi = jpaApi;
    }

    public List<Language> getAll()
    {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT l FROM Language l", Language.class)
                    .getResultList();
        });
    }

    public Language get(Long id)
    {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT l FROM Language l WHERE l.id = :id", Language.class)
                    .setParameter("id", id)
                    .getSingleResultOrNull();
        });
    }
}
