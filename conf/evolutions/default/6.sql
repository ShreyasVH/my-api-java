-- !Ups
CREATE TABLE `songs` (
 `id` int unsigned NOT NULL AUTO_INCREMENT,
 `name` varchar(100) NOT NULL,
 `movie_id` int unsigned NOT NULL,
 `size` int unsigned NOT NULL,
 `active` int(1) NOT NULL DEFAULT '1',
 `obtained` int(1) NOT NULL DEFAULT '0',
 PRIMARY KEY (`id`),
 UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `song_composer_map` (
 `id` int unsigned NOT NULL AUTO_INCREMENT,
 `song_id` int unsigned NOT NULL,
 `composer_id` int unsigned NOT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `uk_scm_song_composer` (`song_id`, `composer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `song_lyricist_map` (
 `id` int unsigned NOT NULL AUTO_INCREMENT,
 `song_id` int unsigned NOT NULL,
 `lyricist_id` int unsigned NOT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `uk_slm_song_lyricist` (`song_id`, `lyricist_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `song_singer_map` (
   `id` int unsigned NOT NULL AUTO_INCREMENT,
   `song_id` int unsigned NOT NULL,
   `singer_id` int unsigned NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `uk_scm_song_singer` (`song_id`, `singer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE song_composer_map ADD CONSTRAINT fk_scm_song FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE song_composer_map ADD CONSTRAINT fk_scm_composer FOREIGN KEY (`composer_id`) REFERENCES `artists` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE song_lyricist_map ADD CONSTRAINT fk_slm_song FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE song_lyricist_map ADD CONSTRAINT fk_slm_lyricist FOREIGN KEY (`lyricist_id`) REFERENCES `artists` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE song_singer_map ADD CONSTRAINT fk_ssm_song FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE song_singer_map ADD CONSTRAINT fk_ssm_singer FOREIGN KEY (`singer_id`) REFERENCES `artists` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

-- !Downs
DROP TABLE `song_singer_map`;
DROP TABLE `song_lyricist_map`;
DROP TABLE `song_composer_map`;
DROP TABLE `songs`;