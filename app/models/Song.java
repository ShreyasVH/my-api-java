package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.ebean.Model;
import requests.SongRequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "songs")
public class Song extends Model
{
    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private Long movieId;

    @Column
    private Long size;

    @Column
    private Boolean active;

    @Column
    private boolean obtained;

    public Song(SongRequest request)
    {
        this.name = request.getName();
        this.movieId = request.getMovieId();
        this.size = request.getSize();
    }
}
