package myapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by shreyasvh on 10/22/17.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "artists_list")
public class Artist extends Model {
    @Id
    @Column(name = "id")
    @Getter
    @Setter
    public String id;

    @Column(name = "name")
    @Getter
    @Setter
    public String name;

    @Column(name = "gender")
    @Getter
    @Setter
    public String gender;
}
