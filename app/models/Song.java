package models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import requests.SongRequest;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "songs")
public class Song
{
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
