package myapi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by shreyas.hande on 1/7/18.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "songs")
public class Song extends Model
{
    @Id
    @Column(name = "id")
    @Getter
    @Setter
    public String id;

    @Column(name = "name")
    @Getter
    @Setter
    public String name;

    @Column(name = "movie_id")
    @Getter
    @Setter
    public Long movieId;

    @Column(name = "size")
    @Getter
    @Setter
    public Long size;
}
