package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "movie_director_map")
public class MovieDirectorMap
{
    @Id
    @Column
    private Long id;

    @Column
    private Long movieId;

    @Column
    private Long directorId;
}
