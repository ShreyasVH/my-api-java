package myapi.models;

import com.avaje.ebean.SqlRow;
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
 * Created by shreyasvh on 7/30/17.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movies")
public class Movie extends Model
{
    @Id
    @Column(name = "id")
    @Getter
    @Setter
    public Long id;

    @Column(name = "name")
    @Getter
    @Setter
    public String name;

    @Column(name = "language_id")
    @Getter
    @Setter
    public Long languageId;

    @Column(name = "size")
    @Getter
    @Setter
    public Long size;

    @Column(name = "format_id")
    @Getter
    @Setter
    public Long formatId;

    @Column(name = "quality")
    @Getter
    @Setter
    public String quality;

    @Column(name = "year")
    @Getter
    @Setter
    public Integer year;

    @Column(name = "subtitles")
    @Getter
    @Setter
    public Boolean subtitles;

    @Column(name = "seen_in_theatre")
    @Getter
    @Setter
    public Boolean seenInTheatre;

    @Column(name = "basename")
    @Getter
    @Setter
    public String basename;

    @Column(name = "status")
    @Getter
    @Setter
    public Status status = Status.ENABLED;

    public Movie(SqlRow movie)
    {
        this.id = movie.getLong("id");
        this.name = movie.getString("name");
        this.formatId = movie.getLong("format_id");
        this.languageId = movie.getLong("language_id");
        this.size = movie.getLong("size");
        this.quality = movie.getString("quality");
        this.year = movie.getInteger("year");
        this.subtitles = movie.getBoolean("subtitles");
        this.seenInTheatre = movie.getBoolean("seen_in_theatre");
        this.basename = movie.getString("basename");
        this.status = Status.getStatus(movie.getInteger("status"));
    }
}
