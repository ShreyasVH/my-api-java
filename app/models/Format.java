package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(value = { "_ebean_intercept" }, ignoreUnknown = true)
@Table(name = "formats")
public class Format extends Model
{
    @Id
    @Column
    public Long id;

    @Column
    public String name;
}
