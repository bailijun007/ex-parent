CREATE TABLE `pc_active_position` (
  `id` bigint(20) NOT NULL COMMENT '��λID',
  `user_id` bigint(20) NOT NULL COMMENT '�û�ID',
  `asset` varchar(20) NOT NULL COMMENT '�ʲ�',
  `symbol` varchar(20) NOT NULL COMMENT '��Լ����Ʒ��',
  `long_flag` int(11) NOT NULL COMMENT '���',
  PRIMARY KEY (`id`),
  KEY `idx_userid` (`user_id`),
  KEY `idx_symbol` (`symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='������Լ_���λ';

