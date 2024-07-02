-- !Ups
ALTER TABLE `movies` ADD `obtained` int(1) NOT NULL DEFAULT '0' AFTER `image_url`;
ALTER TABLE `movies` CHANGE `size` `size` bigint(20) unsigned NULL;
ALTER TABLE `movies` CHANGE `format_id` `format_id` int unsigned NULL;
ALTER TABLE `movies` CHANGE `quality` `quality` varchar(10) NULL;
ALTER TABLE `movies` CHANGE `subtitles` `subtitles` int(1) NULL;

-- !Downs
ALTER TABLE `movies` DROP `obtained`;
ALTER TABLE `movies` CHANGE `size` `size` bigint(20) unsigned NOT NULL;
ALTER TABLE `movies` CHANGE `format_id` `format_id` int unsigned NOT NULL;
ALTER TABLE `movies` CHANGE `quality` `quality` varchar(10) NOT NULL;
ALTER TABLE `movies` CHANGE `subtitles` `subtitles` int(1) NULL;
