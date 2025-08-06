-- !Ups

ALTER TABLE `movies` ADD UNIQUE `uk_m_name_language_release_date` (`name`, `language_id`, `release_date`);

-- !Downs

ALTER TABLE `movies` DROP INDEX `uk_m_name_language_release_date`;