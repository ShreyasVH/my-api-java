package myapi.models;

import lombok.Getter;
import lombok.Setter;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by shreyasvh on 7/25/17.
 */
@Entity
@Table(name = "formats_list")
public class MovieFormat extends Model {

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
