/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50716
Source Host           : localhost:3306
Source Database       : expv3-pc

Target Server Type    : MYSQL
Target Server Version : 50716
File Encoding         : 65001

Date: 2020-02-04 12:10:42
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
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
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
`id`  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`sn`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流水号' ,
`type`  int(11) NOT NULL COMMENT '类型：1 收入,-1 支出' ,
`amount`  decimal(50,30) NOT NULL COMMENT '本笔金额' ,
`remark`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' ,
`balance`  decimal(50,30) NOT NULL COMMENT '本笔余额' ,
`trade_no`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用方支付单号' ,
`trade_type`  int(11) NOT NULL COMMENT '交易类型：1-资金转入，2-资金转出，3-下订单，4-撤单，4-追加保证金，5-平仓收益' ,
`serial_no`  bigint(20) NOT NULL COMMENT '序号:' ,
`associated_id`  bigint(20) NULL DEFAULT NULL COMMENT '关联对象的ID' ,
`tx_id`  bigint(20) NULL DEFAULT NULL COMMENT '事务ID' ,
`request_id`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`),
UNIQUE INDEX `un_sn` (`sn`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='永续合约_账户明细'

;

-- ----------------------------
-- Table structure for `bb_account_symbol`
-- ----------------------------
DROP TABLE IF EXISTS `bb_account_symbol`;
CREATE TABLE `bb_account_symbol` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '合约交易品种' ,
`margin_mode`  int(11) NOT NULL COMMENT '保证金模式,' ,
`short_leverage`  decimal(10,4) NOT NULL COMMENT '做空杠杆' ,
`long_leverage`  decimal(10,4) NOT NULL COMMENT '做多杠杆' ,
`long_max_leverage`  decimal(10,4) NOT NULL COMMENT '最大多仓杠杆' ,
`short_max_leverage`  decimal(10,4) NOT NULL COMMENT '最大多空杠杆' ,
`cross_leverage`  decimal(10,4) NOT NULL COMMENT '全仓杠杆' ,
`cross_max_leverage`  decimal(10,0) NOT NULL COMMENT '最大全仓杠杆' ,
`version`  bigint(20) NOT NULL COMMENT '版本' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`),
UNIQUE INDEX `user_asset_symbol` (`user_id`, `asset`, `symbol`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='永续合约_账户设置'

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
`long_flag`  int(11) NOT NULL COMMENT '多空' ,
PRIMARY KEY (`id`),
INDEX `idx_userid` (`user_id`) USING BTREE ,
INDEX `idx_symbol` (`symbol`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='永续合约_活动订单（委托）'

;

-- ----------------------------
-- Table structure for `bb_active_position`
-- ----------------------------
DROP TABLE IF EXISTS `bb_active_position`;
CREATE TABLE `bb_active_position` (
`id`  bigint(20) NOT NULL COMMENT '仓位ID' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '合约交易品种' ,
`long_flag`  int(11) NOT NULL COMMENT '多空' ,
PRIMARY KEY (`id`),
INDEX `idx_userid` (`user_id`) USING BTREE ,
INDEX `idx_symbol` (`symbol`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='永续合约_活动仓位'

;

-- ----------------------------
-- Table structure for `bb_liq_record`
-- ----------------------------
DROP TABLE IF EXISTS `bb_liq_record`;
CREATE TABLE `bb_liq_record` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '合约交易品种' ,
`pos_id`  bigint(20) NOT NULL COMMENT '仓位ID' ,
`long_flag`  int(11) NOT NULL COMMENT '是否多仓(side)' ,
`volume`  decimal(50,30) NOT NULL COMMENT '数量（张）' ,
`pos_margin`  decimal(50,30) NOT NULL COMMENT '保证金' ,
`bankrupt_price`  decimal(50,30) NOT NULL COMMENT '破产价' ,
`filled_volume`  decimal(50,30) NOT NULL COMMENT '已成交量' ,
`liq_price`  decimal(50,30) NOT NULL COMMENT '强平价' ,
`mean_price`  decimal(50,30) NOT NULL DEFAULT 0.000000000000000000000000000000 COMMENT '仓位均价' ,
`fee`  decimal(50,30) NOT NULL COMMENT '手续费' ,
`fee_ratio`  decimal(10,4) NOT NULL COMMENT '手续费率' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='永续合约_仓位'

;

-- ----------------------------
-- Table structure for `bb_order`
-- ----------------------------
DROP TABLE IF EXISTS `bb_order`;
CREATE TABLE `bb_order` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`symbol`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '合约交易品种' ,
`close_flag`  int(11) NOT NULL COMMENT '是否:1-平仓,0-开' ,
`long_flag`  int(11) NOT NULL COMMENT '是否：1-多仓，0-空仓' ,
`margin_mode`  int(11) NOT NULL COMMENT '保证金模式:1-全仓,2-逐仓' ,
`leverage`  decimal(10,2) NOT NULL COMMENT '杠杆' ,
`price`  decimal(50,30) NOT NULL COMMENT '委托价格' ,
`order_type`  int(11) NOT NULL COMMENT '永续合约委托类型' ,
`volume`  decimal(50,20) NOT NULL COMMENT '委托数量，初始设置后，后续不会修改' ,
`face_value`  decimal(50,30) NULL DEFAULT NULL ,
`status`  int(11) NOT NULL COMMENT '委托状态' ,
`margin_ratio`  decimal(50,30) NOT NULL COMMENT '保证金率，初始为 杠杆的倒数' ,
`open_fee_ratio`  decimal(50,30) NOT NULL COMMENT '开仓手续费率' ,
`close_fee_ratio`  decimal(50,30) NOT NULL COMMENT '强平手续费率' ,
`fee_cost`  decimal(50,30) NOT NULL COMMENT '实收手续费,成交后计算(可能部分成交，按比例收取)' ,
`gross_margin`  decimal(50,30) NOT NULL COMMENT '总押金：保证金+开仓手续费+平仓手续费' ,
`order_margin`  decimal(50,30) NOT NULL COMMENT '委托保证金' ,
`open_fee`  decimal(50,30) NOT NULL COMMENT '开仓手续费,成交时修改(可能部分成交，按比例释放)' ,
`close_fee`  decimal(50,30) NOT NULL COMMENT '平仓手续费，在下委托时提前收取(可能部分成交，按比例释放)' ,
`filled_volume`  decimal(50,30) NOT NULL COMMENT '已成交量（单位：张）' ,
`cancel_volume`  decimal(50,30) NULL DEFAULT NULL COMMENT '取消（单位：张）' ,
`close_pos_id`  bigint(20) NULL DEFAULT NULL COMMENT '平仓委托对应的仓位Id' ,
`time_in_force`  int(11) NULL DEFAULT NULL COMMENT '委托有效时间' ,
`trigger_flag`  int(11) NULL DEFAULT NULL COMMENT '是否已触发，用于止盈止损等触发式委托' ,
`cancel_time`  bigint(20) NULL DEFAULT NULL COMMENT '取消时间' ,
`visible_flag`  int(11) NULL DEFAULT NULL COMMENT '可见性，强平委托，自动减仓委托 都不可见' ,
`liq_flag`  int(11) NOT NULL COMMENT '是否强平委托' ,
`active_flag`  int(11) NOT NULL COMMENT '是否活动委托' ,
`create_operator`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`cancel_operator`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`remark`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' ,
`client_order_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户自定义委托ID，用于与客户系统关联 （open api）' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
`version`  bigint(20) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
INDEX `idx_userid_asset_symbol` (`user_id`, `asset`, `symbol`) USING BTREE ,
INDEX `idx_created` (`created`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='永续合约_订单（委托）'

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
`id`  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '合约交易品种' ,
`order_id`  bigint(20) NOT NULL COMMENT '订单ID' ,
`price`  decimal(50,30) NOT NULL COMMENT '成交价' ,
`volume`  decimal(50,30) NOT NULL COMMENT '成交量（单位：张数）' ,
`trade_sn`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '交易ID' ,
`trade_id`  bigint(20) NOT NULL COMMENT '交易ID' ,
`pos_id`  bigint(20) NOT NULL COMMENT '仓位ID' ,
`maker_flag`  int(11) NOT NULL COMMENT '1-marker， 0-taker' ,
`trade_time`  bigint(20) NOT NULL COMMENT '成交时间' ,
`fee_collector_id`  bigint(20) NOT NULL COMMENT '手续费收取人' ,
`fee_ratio`  decimal(50,30) NOT NULL COMMENT '手续费率' ,
`fee`  decimal(50,30) NOT NULL COMMENT '手续费' ,
`pnl`  decimal(50,30) NOT NULL COMMENT '盈亏(此次成交的盈亏)' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
`remain_volume`  decimal(50,30) NULL DEFAULT NULL COMMENT '未成交量' ,
`match_tx_id`  bigint(20) NULL DEFAULT NULL COMMENT '撮合事务Id' ,
`tx_id`  bigint(20) NULL DEFAULT NULL COMMENT '事务ID' ,
PRIMARY KEY (`id`),
UNIQUE INDEX `un_trade_no` (`trade_sn`) USING BTREE ,
INDEX `idx_userid` (`user_id`) USING BTREE ,
INDEX `idx_orderid` (`order_id`) USING BTREE ,
INDEX `idx_posid` (`pos_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='永续合约_用户订单成交记录'

;

-- ----------------------------
-- Table structure for `bb_position`
-- ----------------------------
DROP TABLE IF EXISTS `bb_position`;
CREATE TABLE `bb_position` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '合约交易品种' ,
`long_flag`  int(11) NOT NULL COMMENT '是否多仓(side)' ,
`margin_mode`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '保证金模式:1-全仓,2-逐仓' ,
`volume`  decimal(50,30) NOT NULL COMMENT '仓位 张数posVolume' ,
`face_value`  decimal(50,8) NOT NULL COMMENT '面值(单位：报价货币)' ,
`base_value`  decimal(50,30) NOT NULL COMMENT '仓位 基础货币 总价值posBaseValue' ,
`entry_leverage`  decimal(50,30) NOT NULL COMMENT '开仓杠杆' ,
`leverage`  decimal(50,30) NOT NULL COMMENT '当前杠杆' ,
`pos_margin`  decimal(50,30) NOT NULL COMMENT '仓位保证金' ,
`close_fee`  decimal(50,30) NOT NULL COMMENT '平仓手续费' ,
`mean_price`  decimal(50,30) NOT NULL COMMENT '均价，仓位为0时，表示最后一次仓位变动时的均价meanPrice' ,
`accu_base_value`  decimal(50,30) NOT NULL COMMENT '累积总价值' ,
`accu_volume`  decimal(50,30) NOT NULL COMMENT '累计成交量' ,
`init_margin`  decimal(50,30) NOT NULL COMMENT '初始保证金，平仓的时候要减去对应的比例，以维持收益率一致' ,
`hold_margin_ratio`  decimal(50,30) NOT NULL COMMENT '维持保证金比率' ,
`auto_add_flag`  int(11) NOT NULL COMMENT '是否自动追加保证金标识' ,
`fee_cost`  decimal(50,30) NOT NULL COMMENT '已扣手续费' ,
`realised_pnl`  decimal(50,30) NOT NULL COMMENT '已实现盈亏' ,
`liq_price`  decimal(50,30) NULL DEFAULT NULL COMMENT '强平价，仓位为0时，表示最后一次仓位变动时的强平价' ,
`liq_mark_price`  decimal(50,30) NULL DEFAULT NULL COMMENT '强平' ,
`liq_mark_time`  bigint(20) NULL DEFAULT NULL COMMENT '触发强平的标记时间' ,
`liq_status`  int(11) NULL DEFAULT NULL COMMENT '仓位强平状态，0：未触发平仓，1：仓位被冻结，' ,
`version`  bigint(20) NOT NULL DEFAULT 0 ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`),
INDEX `idx_userid_asset_symbol` (`user_id`, `asset`, `symbol`) USING BTREE ,
INDEX `idx_volume` (`volume`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='永续合约_仓位'

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
`tk_close_flag`  int(11) NOT NULL COMMENT 'taker是否平仓' ,
`mk_account_id`  bigint(20) NOT NULL COMMENT 'maker账户Id' ,
`mk_order_id`  bigint(20) NOT NULL COMMENT 'maker订单ID' ,
`mk_close_flag`  int(11) NOT NULL COMMENT 'maker是否平仓' ,
`price`  decimal(50,30) NOT NULL COMMENT '成交价格' ,
`number`  decimal(50,30) NOT NULL COMMENT '数量' ,
`trade_time`  bigint(20) NOT NULL COMMENT '成交时间' ,
`created`  bigint(20) NULL DEFAULT NULL ,
`modified`  bigint(20) NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='永续合约_成交(撮合结果)'

;
