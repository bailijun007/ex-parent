
ALTER TABLE `pc_position`
ADD COLUMN `version`  bigint NOT NULL DEFAULT 0 AFTER `liq_status`;

ALTER TABLE `pc_order`
ADD COLUMN `version`  bigint NOT NULL DEFAULT 0 AFTER `modified`;

ALTER TABLE `pc_account_symbol` ADD COLUMN `long_postion_id` bigint NOT NULL DEFAULT 0 COMMENT '����Id' AFTER `cross_leverage` ;

ALTER TABLE `pc_account_symbol` ADD COLUMN `short_postion_id` bigint NOT NULL DEFAULT 0 COMMENT '��ղ�Id' AFTER `long_postion_id` ;

ALTER TABLE `pc_account_symbol` ADD COLUMN `cross_max_leverage` decimal NOT NULL COMMENT '���ȫ�ָܸ�' AFTER `cross_leverage` ;
