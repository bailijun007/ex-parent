/*
Navicat MySQL Data Transfer

Source Server         : 192.168.0.190-ex
Source Server Version : 50726
Source Host           : 192.168.0.190:3306
Source Database       : expv3_shard

Target Server Type    : MYSQL
Target Server Version : 50599
File Encoding         : 65001

Date: 2020-06-02 15:27:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `bb_account`
-- ----------------------------
CREATE TABLE `bb_account` (
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) NOT NULL COMMENT '资产类型' ,
`balance`  decimal(50,30) NOT NULL COMMENT '余额' ,
`frozen`  decimal(50,30) NOT NULL DEFAULT 0.000000000000000000000000000000 COMMENT '冻结余额' ,
`total`  decimal(50,30) NOT NULL DEFAULT 0.000000000000000000000000000000 COMMENT '总额' ,
`version`  bigint(20) NOT NULL COMMENT '版本' ,
`modified`  bigint(20) NOT NULL COMMENT '创建时间' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
PRIMARY KEY (`user_id`, `asset`),
UNIQUE INDEX `un_user_asset` (`user_id`, `asset`) USING BTREE 
)
ENGINE=InnoDB
COMMENT='用户资金账户'

;

-- ----------------------------
-- Table structure for `bb_account_log`
-- ----------------------------
CREATE TABLE `bb_account_log` (
`id`  bigint(20) NOT NULL ,
`type`  int(11) NOT NULL COMMENT '事件类型' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户Id' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) NULL DEFAULT NULL COMMENT '交易品种' ,
`ref_id`  bigint(20) NOT NULL COMMENT '引用对象Id' ,
`time`  bigint(20) NOT NULL COMMENT '时间' ,
PRIMARY KEY (`id`),
INDEX `idx_user_asset_symbol` (`user_id`, `asset`, `symbol`) USING BTREE 
)
ENGINE=InnoDB
COMMENT='账户日志'

;

-- ----------------------------
-- Table structure for `bb_account_record_trade_no`
-- ----------------------------
CREATE TABLE `bb_account_record_trade_no` (
`trade_no`  varchar(64) NOT NULL COMMENT '调用方支付单号' ,
`record_id`  bigint(20) NOT NULL COMMENT '记录Id' ,
`tx_id`  bigint(20) NOT NULL COMMENT '事务ID' ,
PRIMARY KEY (`trade_no`)
)
ENGINE=InnoDB
COMMENT='币币_账户明细SN'

;

-- ----------------------------
-- Table structure for `bb_collector_account`
-- ----------------------------
CREATE TABLE `bb_collector_account` (
`id`  bigint(20) NOT NULL ,
`asset`  varchar(20) NOT NULL COMMENT '资产类型' ,
`balance`  decimal(50,30) NOT NULL COMMENT '余额' ,
`version`  bigint(20) NOT NULL COMMENT '版本' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`, `asset`)
)
ENGINE=InnoDB
COMMENT='币币_币币手续费'

;

-- ----------------------------
-- Table structure for `bb_collector_account_record`
-- ----------------------------
CREATE TABLE `bb_collector_account_record` (
`id`  bigint(20) NOT NULL ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`sn`  varchar(64) NOT NULL COMMENT '流水号' ,
`type`  int(11) NOT NULL COMMENT '类型：1 收入,-1 支出' ,
`amount`  decimal(50,30) NOT NULL COMMENT '本笔金额' ,
`remark`  varchar(255) NOT NULL COMMENT '备注' ,
`balance`  decimal(50,30) NOT NULL COMMENT '本笔余额' ,
`trade_no`  varchar(64) NOT NULL COMMENT '调用方支付单号' ,
`trade_type`  int(11) NOT NULL ,
`serial_no`  bigint(20) NOT NULL COMMENT '序号:' ,
`associated_id`  bigint(20) NOT NULL COMMENT '关联对象的ID' ,
`collector_id`  bigint(20) NOT NULL ,
`tx_id`  bigint(20) NULL DEFAULT NULL COMMENT '事务ID' ,
`request_id`  varchar(255) NULL DEFAULT NULL COMMENT '请求ID' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`),
UNIQUE INDEX `un_trade_no` (`trade_no`) USING BTREE ,
UNIQUE INDEX `un_sn` (`sn`) USING BTREE 
)
ENGINE=InnoDB
COMMENT='币币手续费_账户明细'

;

-- ----------------------------
-- Table structure for `bb_message_ext`
-- ----------------------------
CREATE TABLE `bb_message_ext` (
`id`  bigint(20) NOT NULL ,
`msg_id`  varchar(64) NOT NULL ,
`tags`  varchar(50) NOT NULL ,
`keys`  varchar(64) NOT NULL ,
`msg_body`  varchar(2000) NOT NULL ,
`error_info`  varchar(3000) NULL DEFAULT NULL COMMENT '异常信息' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) NOT NULL COMMENT '交易对（合约品种）' ,
`shard_id`  bigint(20) NOT NULL DEFAULT 0 ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`status`  int(11) NOT NULL ,
PRIMARY KEY (`id`, `shard_id`)
)
ENGINE=InnoDB
COMMENT='币币_撮合消息'

;

-- ----------------------------
-- Table structure for `bb_message_offset`
-- ----------------------------
CREATE TABLE `bb_message_offset` (
`shard_id`  bigint(11) NOT NULL COMMENT '分片Id' ,
`readed_offset`  bigint(20) NULL DEFAULT NULL COMMENT '读取位置' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`shard_id`)
)
ENGINE=InnoDB
COMMENT='消息偏移量(系统表)'

;

-- ----------------------------
-- Table structure for `bb_mq_msg`
-- ----------------------------
CREATE TABLE `bb_mq_msg` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`message_id`  varchar(64) NULL DEFAULT NULL COMMENT 'messageId' ,
`tag`  varchar(64) NOT NULL ,
`key`  varchar(64) NULL DEFAULT NULL ,
`body`  varchar(2000) NOT NULL ,
`ex_message`  varchar(1000) NULL DEFAULT NULL COMMENT '异常信息' ,
`method`  varchar(255) NULL DEFAULT NULL COMMENT '处理方法' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) NOT NULL COMMENT '交易对（合约品种）' ,
`sort_id`  bigint(20) NOT NULL ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
PRIMARY KEY (`id`),
INDEX `idx_tag_key` (`tag`, `key`, `user_id`) USING BTREE ,
INDEX `idx_sort_id` (`sort_id`) USING BTREE 
)
ENGINE=InnoDB
COMMENT='未处理的_消息'
AUTO_INCREMENT=185446662444059137

;

-- ----------------------------
-- Table structure for `bb_order`
-- ----------------------------
CREATE TABLE `bb_order` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(64) NOT NULL COMMENT '合约交易品种' ,
`bid_flag`  int(11) NOT NULL COMMENT '买卖:1-买,0-卖' ,
`price`  decimal(50,30) NOT NULL COMMENT '委托价格' ,
`order_type`  int(11) NOT NULL COMMENT '委托类型' ,
`volume`  decimal(50,20) NOT NULL COMMENT '委托数量（个）' ,
`status`  int(11) NOT NULL COMMENT '委托状态：1-已创建未匹配,2-新建未成交,4-待取消,8-已取消,16-部分成交,32-全部成交,64-提交失败,128-已过期' ,
`fee_ratio`  decimal(50,30) NOT NULL COMMENT '手续费率' ,
`fee_cost`  decimal(50,30) NOT NULL COMMENT '实收手续费,成交后累加' ,
`order_margin`  decimal(50,30) NOT NULL COMMENT '委托押金' ,
`fee`  decimal(50,30) NOT NULL COMMENT '开仓手续费,成交时减少' ,
`filled_volume`  decimal(50,30) NOT NULL COMMENT '已成交量（单位：个）' ,
`trade_mean_price`  decimal(50,30) NULL DEFAULT NULL ,
`cancel_volume`  decimal(50,30) NULL DEFAULT NULL COMMENT '取消（单位：个）' ,
`time_in_force`  int(11) NULL DEFAULT NULL COMMENT '委托有效时间' ,
`cancel_time`  bigint(20) NULL DEFAULT NULL COMMENT '取消时间' ,
`active_flag`  int(11) NOT NULL COMMENT '是否活动委托' ,
`create_operator`  varchar(50) NULL DEFAULT NULL ,
`cancel_operator`  varchar(50) NULL DEFAULT NULL ,
`remark`  varchar(255) NULL DEFAULT NULL COMMENT '备注' ,
`client_order_id`  varchar(64) NULL DEFAULT NULL COMMENT '客户自定义委托ID，用于与客户系统关联 （open api）' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
`version`  bigint(20) NOT NULL DEFAULT 0 ,
`leverage`  decimal(6,2) NULL DEFAULT NULL ,
`order_margin_currency`  varchar(20) NOT NULL COMMENT '押金币种' ,
PRIMARY KEY (`id`),
INDEX `idx_userid_asset_symbol` (`user_id`, `asset`, `symbol`) USING BTREE ,
INDEX `idx_created` (`created`) USING BTREE 
)
ENGINE=InnoDB
COMMENT='币币_订单（活动委托）'

;

-- ----------------------------
-- Table structure for `bb_order_trade_sn`
-- ----------------------------
CREATE TABLE `bb_order_trade_sn` (
`trade_sn`  varchar(64) NOT NULL COMMENT '交易序号' ,
`id`  bigint(20) NOT NULL ,
`tx_id`  bigint(20) NOT NULL COMMENT '事务ID' ,
PRIMARY KEY (`trade_sn`)
)
ENGINE=InnoDB
COMMENT='币币_用户订单成交记录SN'

;

-- ----------------------------
-- Table structure for `bb_trade`
-- ----------------------------
CREATE TABLE `bb_trade` (
`id`  bigint(20) NOT NULL COMMENT 'id' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) NOT NULL COMMENT '交易对' ,
`match_tx_id`  bigint(20) NOT NULL COMMENT '事务Id' ,
`tk_bid_flag`  int(11) NOT NULL COMMENT 'taker是否买：1-是，0-否' ,
`tk_account_id`  bigint(20) NOT NULL COMMENT 'taker账户ID' ,
`tk_order_id`  bigint(20) NOT NULL COMMENT 'taker订单ID' ,
`mk_account_id`  bigint(20) NOT NULL COMMENT 'maker账户Id' ,
`mk_order_id`  bigint(20) NOT NULL COMMENT 'maker订单ID' ,
`price`  decimal(50,30) NOT NULL COMMENT '成交价格' ,
`number`  decimal(50,30) NOT NULL COMMENT '数量' ,
`trade_time`  bigint(20) NOT NULL COMMENT '成交时间' ,
`maker_handle_status`  int(11) NOT NULL ,
`taker_handle_status`  int(11) NOT NULL ,
`created`  bigint(20) NULL DEFAULT NULL ,
`modified`  bigint(20) NULL DEFAULT NULL ,
PRIMARY KEY (`id`),
INDEX `idx_trade_time` (`trade_time`) USING BTREE ,
INDEX `idx_tk_order_id` (`tk_order_id`) USING BTREE ,
INDEX `idx_mk_order_id` (`mk_order_id`) USING BTREE 
)
ENGINE=InnoDB
COMMENT='币币_成交(撮合结果)'

;
