package models;

import enums.Status;
import io.ebean.SqlRow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.ebean.Model;
import requests.MovieRequest;
import utils.Utils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

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
	@Column
	private Long id;

	@Column
	private String name;

	@Column
	private Long languageId;

	@Column
	private Long size;

	@Column
	private Long formatId;

	@Column
	private String quality;

	@Column
	private Date releaseDate;

	@Column
	private Boolean subtitles;

	@Column
	private Boolean seenInTheatre;

	@Column
	private String basename;

	@Column
	private Boolean active;

	@Column
	private String imageUrl;

	public Movie(SqlRow movie)
	{
		this.id = movie.getLong("id");
		this.name = movie.getString("name");
		this.formatId = movie.getLong("format_id");
		this.languageId = movie.getLong("language_id");
		this.size = movie.getLong("size");
		this.quality = movie.getString("quality");
		this.subtitles = movie.getBoolean("subtitles");
		this.seenInTheatre = movie.getBoolean("seen_in_theatre");
		this.basename = movie.getString("basename");
		this.active = movie.getBoolean("active");
		this.imageUrl = movie.getString("image_url");
	}

	public Movie(MovieRequest request)
	{
		this.name = request.getName();
		this.size = request.getSize();
		this.languageId = request.getLanguageId();
		this.formatId = request.getFormatId();
		this.subtitles = request.getSubtitles();
		this.seenInTheatre = request.getSeenInTheatre();
		this.quality = request.getQuality();
		this.releaseDate =  Utils.parseDateString(request.getReleaseDate());
		this.basename = request.getBasename();
		this.imageUrl = request.getImageUrl();
	}
}
