-- !Ups
ALTER TABLE `movies` CHANGE `year` `year` SMALLINT NULL;

-- !Downs
ALTER TABLE `movies` CHANGE `year` `year` SMALLINT NOT NULL;