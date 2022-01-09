-- !Ups

INSERT INTO `languages` (`id`, `name`) VALUES
("1", "Kannada"),
("2", "Hindi"),
("3", "Telugu"),
("4", "Tamil"),
("5", "Malayalam"),
("6", "English");

INSERT INTO `formats` (`id`, `name`) VALUES
("1", "mkv"),
("2", "mp4"),
("3", "avi"),
("4", "flv"),
("5", "ts"),
("6", "divx"),
("7", "dat"),
("8", "m4v"),
("9", "vob");

-- !Downs

DELETE FROM `formats`;

DELETE FROM `languages`;