/*
Navicat MySQL Data Transfer

Source Server         : 192.168.0.190-ex
Source Server Version : 50726
Source Host           : 192.168.0.190:3306
Source Database       : expv3_bb

Target Server Type    : MYSQL
Target Server Version : 50599
File Encoding         : 65001

Date: 2020-04-02 15:54:46
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `bb_account`
-- ----------------------------
DROP TABLE IF EXISTS `bb_account`;
CREATE TABLE `bb_account` (
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产类型' ,
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
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='用户资金账户'

;

-- ----------------------------
-- Table structure for `bb_account_log`
-- ----------------------------
DROP TABLE IF EXISTS `bb_account_log`;
CREATE TABLE `bb_account_log` (
`id`  bigint(20) NOT NULL ,
`type`  int(11) NOT NULL COMMENT '事件类型' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户Id' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易品种' ,
`ref_id`  bigint(20) NOT NULL COMMENT '引用对象Id' ,
`time`  bigint(20) NOT NULL COMMENT '时间' ,
PRIMARY KEY (`id`),
INDEX `idx_user_asset_symbol` (`user_id`, `asset`, `symbol`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='账户日志'

;

-- ----------------------------
-- Table structure for `bb_account_record`
-- ----------------------------
DROP TABLE IF EXISTS `bb_account_record`;
CREATE TABLE `bb_account_record` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`sn`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流水号' ,
`type`  int(11) NOT NULL COMMENT '类型：1 收入,-1 支出' ,
`amount`  decimal(50,30) NOT NULL COMMENT '本笔金额' ,
`remark`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' ,
`balance`  decimal(50,30) NOT NULL COMMENT '本笔余额' ,
`frozen`  decimal(50,30) NOT NULL DEFAULT 0.000000000000000000000000000000 COMMENT '冻结余额' ,
`total`  decimal(50,30) NOT NULL DEFAULT 0.000000000000000000000000000000 COMMENT '总额' ,
`trade_no`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用方支付单号' ,
`trade_type`  int(11) NOT NULL COMMENT '交易类型：1-资金转入，2-资金转出，3-下订单，4-撤单，4-追加保证金，5-平仓收益' ,
`serial_no`  bigint(20) NOT NULL COMMENT '序号:' ,
`associated_id`  bigint(20) NULL DEFAULT NULL COMMENT '关联对象的ID' ,
`tx_id`  bigint(20) NULL DEFAULT NULL COMMENT '事务ID' ,
`request_id`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`),
UNIQUE INDEX `un_sn` (`sn`) USING BTREE ,
UNIQUE INDEX `un_trade_no` (`trade_no`) USING BTREE ,
INDEX `idx_created` (`created`) USING BTREE ,
INDEX `idx_userid_asset` (`user_id`, `asset`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='币币_账户明细'

;

-- ----------------------------
-- Table structure for `bb_active_order`
-- ----------------------------
DROP TABLE IF EXISTS `bb_active_order`;
CREATE TABLE `bb_active_order` (
`id`  bigint(20) NOT NULL COMMENT '订单ID' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '合约交易品种' ,
`bid_flag`  int(11) NOT NULL COMMENT '多空' ,
PRIMARY KEY (`id`),
INDEX `idx_userid` (`user_id`) USING BTREE ,
INDEX `idx_symbol` (`symbol`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='币币_活动订单（委托）'

;

-- ----------------------------
-- Table structure for `bb_collector_account`
-- ----------------------------
DROP TABLE IF EXISTS `bb_collector_account`;
CREATE TABLE `bb_collector_account` (
`id`  bigint(20) NOT NULL ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产类型' ,
`balance`  decimal(50,30) NOT NULL COMMENT '余额' ,
`version`  bigint(20) NOT NULL COMMENT '版本' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`, `asset`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='币币_币币手续费'

;

-- ----------------------------
-- Table structure for `bb_collector_account_record`
-- ----------------------------
DROP TABLE IF EXISTS `bb_collector_account_record`;
CREATE TABLE `bb_collector_account_record` (
`id`  bigint(20) NOT NULL ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`sn`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流水号' ,
`type`  int(11) NOT NULL COMMENT '类型：1 收入,-1 支出' ,
`amount`  decimal(50,30) NOT NULL COMMENT '本笔金额' ,
`remark`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '备注' ,
`balance`  decimal(50,30) NOT NULL COMMENT '本笔余额' ,
`trade_no`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用方支付单号' ,
`trade_type`  int(11) NOT NULL ,
`serial_no`  bigint(20) NOT NULL COMMENT '序号:' ,
`associated_id`  bigint(20) NOT NULL COMMENT '关联对象的ID' ,
`collector_id`  bigint(20) NOT NULL ,
`tx_id`  bigint(20) NULL DEFAULT NULL COMMENT '事务ID' ,
`request_id`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求ID' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`),
UNIQUE INDEX `un_trade_no` (`trade_no`) USING BTREE ,
UNIQUE INDEX `un_sn` (`sn`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='币币手续费_账户明细'

;

CREATE TABLE `bb_mq_msg` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message_id` varchar(64) DEFAULT NULL COMMENT 'messageId',
  `tag` varchar(64) NOT NULL,
  `key` varchar(64) DEFAULT NULL,
  `body` varchar(2000) NOT NULL,
  `ex_message` varchar(1000) DEFAULT NULL COMMENT '异常信息',
  `method` varchar(255) DEFAULT NULL COMMENT '处理方法',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `asset` varchar(20) NOT NULL COMMENT '资产',
  `symbol` varchar(20) NOT NULL COMMENT '交易对（合约品种）',
  `sort_id` bigint(20) NOT NULL,
  `created` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_tag_key` (`tag`,`key`,`user_id`),
  KEY `idx_sort_id` (`sort_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6199 DEFAULT CHARSET=utf8mb4 COMMENT='未处理的_消息';



-- ----------------------------
-- Table structure for `bb_order`
-- ----------------------------
DROP TABLE IF EXISTS `bb_order`;
CREATE TABLE `bb_order` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`symbol`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '合约交易品种' ,
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
`cancel_volume`  decimal(50,30) NULL DEFAULT NULL COMMENT '取消（单位：个）' ,
`time_in_force`  int(11) NULL DEFAULT NULL COMMENT '委托有效时间' ,
`cancel_time`  bigint(20) NULL DEFAULT NULL COMMENT '取消时间' ,
`active_flag`  int(11) NOT NULL COMMENT '是否活动委托' ,
`create_operator`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`cancel_operator`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`remark`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' ,
`client_order_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户自定义委托ID，用于与客户系统关联 （open api）' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
`version`  bigint(20) NOT NULL DEFAULT 0 ,
`leverage`  decimal(6,2) NULL DEFAULT NULL ,
`order_margin_currency`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '押金币种',
PRIMARY KEY (`id`),
INDEX `idx_userid_asset_symbol` (`user_id`, `asset`, `symbol`) USING BTREE ,
INDEX `idx_created` (`created`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='币币_订单（委托）'

;

-- ----------------------------
-- Table structure for `bb_order_log`
-- ----------------------------
DROP TABLE IF EXISTS `bb_order_log`;
CREATE TABLE `bb_order_log` (
`order_id`  bigint(20) NOT NULL ,
`type`  int(11) NOT NULL ,
`trigger_type`  int(11) NOT NULL ,
`tx_id`  bigint(20) NOT NULL COMMENT '事务ID' ,
`request_id`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求ID' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`id`  bigint(20) NOT NULL ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`),
INDEX `idx_order_id` (`order_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='订单日志'

;

-- ----------------------------
-- Table structure for `bb_order_trade`
-- ----------------------------
DROP TABLE IF EXISTS `bb_order_trade`;
CREATE TABLE `bb_order_trade` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '合约交易品种' ,
`bid_flag`  int(11) NULL DEFAULT NULL ,
`order_id`  bigint(20) NOT NULL COMMENT '订单ID' ,
`price`  decimal(50,30) NOT NULL COMMENT '成交价' ,
`volume`  decimal(50,30) NOT NULL COMMENT '成交量（单位：张数）' ,
`trade_sn`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '交易ID' ,
`trade_id`  bigint(20) NOT NULL COMMENT '交易ID' ,
`maker_flag`  int(11) NOT NULL COMMENT '1-marker， 0-taker' ,
`trade_time`  bigint(20) NOT NULL COMMENT '成交时间' ,
`opponent_order_id`  bigint(20) NULL DEFAULT NULL COMMENT '对手订单ID' ,
`fee_collector_id`  bigint(20) NOT NULL COMMENT '手续费收取人' ,
`fee_ratio`  decimal(50,30) NOT NULL COMMENT '手续费率' ,
`fee`  decimal(50,30) NOT NULL COMMENT '手续费' ,
`order_margin`  decimal(50,30) NULL DEFAULT NULL COMMENT '押金' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
`remain_volume`  decimal(50,30) NULL DEFAULT NULL COMMENT '未成交量' ,
`remain_order_margin`  decimal(10,0) NOT NULL COMMENT '订单剩余保证金' ,
`remain_fee`  decimal(10,0) NOT NULL COMMENT '订单剩余手续费' ,
`match_tx_id`  bigint(20) NULL DEFAULT NULL COMMENT '撮合事务Id' ,
`tx_id`  bigint(20) NULL DEFAULT NULL COMMENT '事务ID' ,
`fee_synch_status`  int(11) NOT NULL COMMENT '手续费同步状态' ,
PRIMARY KEY (`id`),
UNIQUE INDEX `un_trade_no` (`trade_sn`) USING BTREE ,
INDEX `idx_userid` (`user_id`) USING BTREE ,
INDEX `idx_orderid` (`order_id`) USING BTREE ,
INDEX `idx_trade_time` (`trade_time`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='币币_用户订单成交记录'

;

-- ----------------------------
-- Table structure for `bb_trade`
-- ----------------------------
DROP TABLE IF EXISTS `bb_trade`;
CREATE TABLE `bb_trade` (
`id`  bigint(20) NOT NULL COMMENT 'id' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '交易对' ,
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
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='币币_成交(撮合结果)'

;
