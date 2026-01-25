package models;

import enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import requests.MovieRequest;
import utils.Utils;

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
public class Movie
{
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	private boolean obtained;

	@Column
	private String imageUrl;

//	public Movie(SqlRow movie)
//	{
//		this.id = movie.getLong("id");
//		this.name = movie.getString("name");
//		this.formatId = movie.getLong("format_id");
//		this.languageId = movie.getLong("language_id");
//		this.size = movie.getLong("size");
//		this.quality = movie.getString("quality");
//		this.subtitles = movie.getBoolean("subtitles");
//		this.seenInTheatre = movie.getBoolean("seen_in_theatre");
//		this.basename = movie.getString("basename");
//		this.active = movie.getBoolean("active");
//		this.imageUrl = movie.getString("image_url");
//		this.obtained = movie.getBoolean("obtained");
//	}

	public Movie(MovieRequest request)
	{
		this.name = request.getName();
		this.languageId = request.getLanguageId();
		this.seenInTheatre = request.getSeenInTheatre();
		this.releaseDate =  Utils.parseDateString(request.getReleaseDate());
		this.imageUrl = request.getImageUrl();
	}
}
