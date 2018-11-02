package myapi.models;

import lombok.Getter;
import lombok.Setter;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
//import lombok.Getter;
/**
 * Created by shreyasvh on 7/22/17.
 */
@Entity
@Table(name = "languages_list")
public class Language extends Model {

    @Id
    @Column(name = "id")
    @Getter
    @Setter
    public Long id;

    @Column(name = "name")
    @Getter
    @Setter
    public String name;
}
