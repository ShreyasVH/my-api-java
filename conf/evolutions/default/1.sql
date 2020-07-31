CREATE TABLE `artists_list` (
 `id` varchar(20) NOT NULL,
 `name` varchar(50) NOT NULL,
 `gender` enum('M','F') NOT NULL,
 `image_url` varchar(500) DEFAULT '',
 PRIMARY KEY (`id`),
 UNIQUE KEY `id` (`id`),
 UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `formats_list` (
 `id` int unsigned AUTO_INCREMENT NOT NULL,
 `name` varchar(20) NOT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `languages_list` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `movies` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `language_id` int unsigned NOT NULL,
  `size` bigint(20) unsigned NOT NULL,
  `format_id` int unsigned NOT NULL,
  `quality` varchar(10) NOT NULL DEFAULT 'normal',
  `year` smallint(6) NOT NULL,
  `subtitles` int(1) NOT NULL DEFAULT '0',
  `seen_in_theatre` int(1) NOT NULL DEFAULT '0',
  `basename` varchar(150) DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `image_url` varchar(500) DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_m_name_year_language` (`name`, `year`, `language_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `movie_actor_map` (
    `id` varchar(20) NOT NULL,
    `movie_id` int unsigned NOT NULL,
    `actor_id` varchar(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_mam_move_actor` (`movie_id`, `actor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `movie_director_map` (
  `id` varchar(20) NOT NULL,
  `movie_id` int unsigned NOT NULL,
  `director_id` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mdm_move_director` (`movie_id`, `director_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `songs` (
    `id` varchar(20) NOT NULL,
    `name` varchar(100) NOT NULL,
    `movie_id` int unsigned NOT NULL,
    `size` int unsigned NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `song_composer_map` (
 `id` varchar(20) NOT NULL,
 `song_id` varchar(20) NOT NULL,
 `composer_id` varchar(20) NOT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `uk_scm_song_composer` (`song_id`, `composer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `song_lyricist_map` (
 `id` varchar(20) NOT NULL,
 `song_id` varchar(20) NOT NULL,
 `lyricist_id` varchar(20) NOT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `uk_slm_song_lyricist` (`song_id`, `lyricist_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `song_singer_map` (
   `id` varchar(20) NOT NULL,
   `song_id` varchar(20) NOT NULL,
   `singer_id` varchar(20) NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `uk_scm_song_singer` (`song_id`, `singer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE movies ADD CONSTRAINT fk_movies_language FOREIGN KEY (`language_id`) REFERENCES `languages_list` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE movies ADD CONSTRAINT fk_movies_format FOREIGN KEY (`format_id`) REFERENCES `formats_list` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE movie_actor_map ADD CONSTRAINT fk_mam_movie FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE movie_actor_map ADD CONSTRAINT fk_mam_actor FOREIGN KEY (`actor_id`) REFERENCES `artists_list` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE movie_director_map ADD CONSTRAINT fk_mdm_movie FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE movie_director_map ADD CONSTRAINT fk_mdm_director FOREIGN KEY (`director_id`) REFERENCES `artists_list` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE song_composer_map ADD CONSTRAINT fk_scm_song FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE song_composer_map ADD CONSTRAINT fk_scm_composer FOREIGN KEY (`composer_id`) REFERENCES `artists_list` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE song_lyricist_map ADD CONSTRAINT fk_slm_song FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE song_lyricist_map ADD CONSTRAINT fk_slm_lyricist FOREIGN KEY (`lyricist_id`) REFERENCES `artists_list` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE song_singer_map ADD CONSTRAINT fk_ssm_song FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE song_singer_map ADD CONSTRAINT fk_ssm_singer FOREIGN KEY (`singer_id`) REFERENCES `artists_list` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
