package models;

import enums.Status;
import io.ebean.SqlRow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.ebean.Model;

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
@Getter
@Setter
@Table(name = "movies")
public class Movie extends Model
{
	@Id
	@Column(name = "id")
	public Long id;

	@Column(name = "name")
	public String name;

	@Column(name = "language_id")
	public Long languageId;

	@Column(name = "size")
	public Long size;

	@Column(name = "format_id")
	public Long formatId;

	@Column(name = "quality")
	public String quality;

	@Column(name = "year")
	public Integer year;

	@Column(name = "subtitles")
	public Boolean subtitles;

	@Column(name = "seen_in_theatre")
	public Boolean seenInTheatre;

	@Column(name = "basename")
	public String basename;

	@Column(name = "status")
	public Status status = Status.ENABLED;

	@Column(name = "image_url")
	private String imageUrl;

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
		this.imageUrl = movie.getString("image_url");
	}
}
