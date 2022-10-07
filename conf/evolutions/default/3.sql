-- !Ups
ALTER TABLE `movies` ADD `release_date` DATE NULL AFTER `image_url`;

-- !Downs
ALTER TABLE `movies` DROP `release_date`;
