CREATE TABLE `bb_mq_msg` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message_id` varchar(64) DEFAULT NULL,
  `tag` varchar(64) NOT NULL,
  `key` varchar(64) DEFAULT NULL,
  `body` varchar(2000) NOT NULL,
  `ex_message` varchar(1000) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `asset` varchar(20) NOT NULL,
  `symbol` varchar(20) NOT NULL,
  `sort_id` bigint(20) NOT NULL,
  `created` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tag_key` (`tag`,`key`,`user_id`),
  KEY `idx_sort_id` (`sort_id`)
);