package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "song_composer_map")
public class SongComposerMap
{
    @Id
    @Column
    private Long id;

    @Column
    private Long songId;

    @Column
    private Long composerId;
}
