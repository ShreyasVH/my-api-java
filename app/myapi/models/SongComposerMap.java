package myapi.models;

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
 * Created by shreyas.hande on 1/8/18.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "song_composer_map")
public class SongComposerMap extends Model
{
    @Id
    @Column(name = "id")
    @Getter
    @Setter
    public String id;

    @Column(name = "song_id")
    @Getter
    @Setter
    public String songId;

    @Column(name = "composer_id")
    @Getter
    @Setter
    public String composerId;
}
