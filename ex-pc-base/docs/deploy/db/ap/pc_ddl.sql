CREATE TABLE `pc_active_position` (
  `id` bigint(20) NOT NULL COMMENT '仓位ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `asset` varchar(20) NOT NULL COMMENT '资产',
  `symbol` varchar(20) NOT NULL COMMENT '合约交易品种',
  `long_flag` int(11) NOT NULL COMMENT '多空',
  PRIMARY KEY (`id`),
  KEY `idx_userid` (`user_id`),
  KEY `idx_symbol` (`symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='永续合约_活动仓位';

