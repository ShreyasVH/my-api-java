package myapi.models;

import lombok.Getter;
import lombok.Setter;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by shreyasvh on 7/30/17.
 */
@Entity
@Table(name = "movie_director_map")
public class MovieDirectorMap extends Model {

    @Id
    @Column(name = "id")
    @Getter
    @Setter
    public String id;

    @Column(name = "movie_id")
    @Getter
    @Setter
    public Long movieId;

    @Column(name = "director_id")
    @Getter
    @Setter
    public String directorId;
}
