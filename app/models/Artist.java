package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.ebean.Model;
import requests.ArtistRequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by shreyasvh on 10/22/17.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(value = { "_ebean_intercept" }, ignoreUnknown = true)
@Table(name = "artists")
public class Artist extends Model {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    private String gender;

    @Column(name = "image_url")
    private String imageUrl;

    public Artist(ArtistRequest artistRequest)
    {
        this.name = artistRequest.getName();
        this.gender = artistRequest.getGender();
        this.imageUrl = artistRequest.getImageUrl();
    }
}
