/*
Navicat MySQL Data Transfer

Source Server         : 192.168.0.190-ex
Source Server Version : 50726
Source Host           : 192.168.0.190:3306
Source Database       : expv3_pc

Target Server Type    : MYSQL
Target Server Version : 50599
File Encoding         : 65001

Date: 2020-06-01 18:24:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `pc_account`
-- ----------------------------
CREATE TABLE `pc_account` (
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) NOT NULL COMMENT '资产类型' ,
`balance`  decimal(50,30) NOT NULL COMMENT '余额' ,
`version`  bigint(20) NOT NULL COMMENT '版本' ,
`modified`  bigint(20) NOT NULL COMMENT '创建时间' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
PRIMARY KEY (`user_id`, `asset`)
)
ENGINE=InnoDB
COMMENT='用户资金账户'

;

-- ----------------------------
-- Table structure for `pc_account_log`
-- ----------------------------
CREATE TABLE `pc_account_log` (
`id`  bigint(20) NOT NULL ,
`type`  int(11) NOT NULL COMMENT '事件类型' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户Id' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) NULL DEFAULT NULL COMMENT '交易品种' ,
`ref_id`  bigint(20) NOT NULL COMMENT '引用对象Id' ,
`time`  bigint(20) NOT NULL COMMENT '时间' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
COMMENT='账户日志'

;

-- ----------------------------
-- Table structure for `pc_account_record`
-- ----------------------------
CREATE TABLE `pc_account_record` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`sn`  varchar(64) NOT NULL COMMENT '流水号' ,
`type`  int(11) NOT NULL COMMENT '类型：1 收入,-1 支出' ,
`amount`  decimal(50,30) NOT NULL COMMENT '本笔金额' ,
`remark`  varchar(255) NULL DEFAULT NULL COMMENT '备注' ,
`balance`  decimal(50,30) NOT NULL COMMENT '本笔余额' ,
`trade_no`  varchar(64) NOT NULL COMMENT '调用方支付单号' ,
`trade_type`  int(11) NOT NULL COMMENT '交易类型：1-资金转入，2-资金转出，3-下订单，4-撤单，4-追加保证金，5-平仓收益' ,
`serial_no`  bigint(20) NOT NULL COMMENT '序号:' ,
`associated_id`  bigint(20) NULL DEFAULT NULL COMMENT '关联对象的ID' ,
`tx_id`  bigint(20) NULL DEFAULT NULL COMMENT '事务ID' ,
`request_id`  varchar(50) NULL DEFAULT NULL COMMENT '请求ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
COMMENT='永续合约_账户明细'

;

-- ----------------------------
-- Table structure for `pc_account_symbol`
-- ----------------------------
CREATE TABLE `pc_account_symbol` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) NOT NULL COMMENT '合约交易品种' ,
`margin_mode`  int(11) NOT NULL COMMENT '保证金模式,' ,
`short_leverage`  decimal(10,4) NOT NULL COMMENT '做空杠杆' ,
`long_leverage`  decimal(10,4) NOT NULL COMMENT '做多杠杆' ,
`long_max_leverage`  decimal(10,4) NOT NULL COMMENT '最大多仓杠杆' ,
`short_max_leverage`  decimal(10,4) NOT NULL COMMENT '最大多空杠杆' ,
`cross_leverage`  decimal(10,4) NOT NULL COMMENT '全仓杠杆' ,
`version`  bigint(20) NOT NULL COMMENT '版本' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
COMMENT='永续合约_账户设置'

;

-- ----------------------------
-- Table structure for `pc_active_order`
-- ----------------------------
CREATE TABLE `pc_active_order` (
`id`  bigint(20) NOT NULL COMMENT '订单ID' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) NOT NULL COMMENT '合约交易品种' ,
`long_flag`  int(11) NOT NULL COMMENT '多空' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
COMMENT='永续合约_活动订单（委托）'

;

-- ----------------------------
-- Table structure for `pc_active_position`
-- ----------------------------
CREATE TABLE `pc_active_position` (
`id`  bigint(20) NOT NULL COMMENT '仓位ID' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) NOT NULL COMMENT '合约交易品种' ,
`long_flag`  int(11) NOT NULL COMMENT '多空' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
COMMENT='永续合约_活动仓位'

;

-- ----------------------------
-- Table structure for `pc_collector_account`
-- ----------------------------
CREATE TABLE `pc_collector_account` (
`id`  bigint(20) NOT NULL ,
`asset`  varchar(20) NOT NULL COMMENT '资产类型' ,
`balance`  decimal(50,30) NOT NULL COMMENT '余额' ,
`version`  bigint(20) NOT NULL COMMENT '版本' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`, `asset`)
)
ENGINE=InnoDB
COMMENT='手续费'

;

-- ----------------------------
-- Table structure for `pc_collector_account_record`
-- ----------------------------
CREATE TABLE `pc_collector_account_record` (
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
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
COMMENT='币币手续费_账户明细'

;

-- ----------------------------
-- Table structure for `pc_liq_record`
-- ----------------------------
CREATE TABLE `pc_liq_record` (
`id`  bigint(20) NOT NULL ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) NOT NULL COMMENT '合约交易品种' ,
`pos_id`  bigint(20) NOT NULL COMMENT '仓位ID' ,
`long_flag`  int(11) NOT NULL COMMENT '是否多仓(side)' ,
`volume`  decimal(50,30) NOT NULL COMMENT '数量（张）' ,
`pos_margin`  decimal(50,30) NOT NULL COMMENT '保证金' ,
`bankrupt_price`  decimal(50,30) NOT NULL COMMENT '破产价' ,
`liq_price`  decimal(50,30) NOT NULL COMMENT '强平价' ,
`mean_price`  decimal(50,30) NOT NULL COMMENT '仓位均价' ,
`fee`  decimal(50,30) NOT NULL COMMENT '手续费' ,
`fee_ratio`  decimal(10,6) NOT NULL COMMENT '手续费率' ,
`filled_volume`  decimal(50,30) NOT NULL COMMENT '强平委托已成交量' ,
`pnl`  decimal(50,30) NOT NULL COMMENT '强平收益' ,
`status`  tinyint(4) NOT NULL ,
`version`  bigint(20) NOT NULL DEFAULT 0 COMMENT '版本' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
COMMENT='永续合约_仓位'

;

-- ----------------------------
-- Table structure for `pc_message_ext`
-- ----------------------------
CREATE TABLE `pc_message_ext` (
`id`  bigint(20) NOT NULL ,
`msg_id`  varchar(64) NOT NULL ,
`tags`  varchar(40) NOT NULL ,
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
-- Table structure for `pc_order`
-- ----------------------------
CREATE TABLE `pc_order` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(64) NOT NULL COMMENT '合约交易品种' ,
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
`create_operator`  varchar(50) NULL DEFAULT NULL ,
`cancel_operator`  varchar(50) NULL DEFAULT NULL ,
`remark`  varchar(255) NULL DEFAULT NULL COMMENT '备注' ,
`client_order_id`  varchar(64) NULL DEFAULT NULL COMMENT '客户自定义委托ID，用于与客户系统关联 （open api）' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
`version`  bigint(20) NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
COMMENT='永续合约_订单（委托）'

;

-- ----------------------------
-- Table structure for `pc_order_log`
-- ----------------------------
CREATE TABLE `pc_order_log` (
`id`  bigint(20) NOT NULL ,
`order_id`  bigint(20) NOT NULL ,
`type`  int(11) NOT NULL COMMENT '日志类型：pending_new、new、pendingcancel、cancelled、trade' ,
`trigger_type`  int(11) NOT NULL COMMENT '触发类型：1-用户，2-系统' ,
`tx_id`  bigint(20) NOT NULL COMMENT '事务ID' ,
`request_id`  varchar(255) NULL DEFAULT NULL COMMENT '请求ID' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
COMMENT='订单日志'

;

-- ----------------------------
-- Table structure for `pc_order_trade`
-- ----------------------------
CREATE TABLE `pc_order_trade` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) NOT NULL COMMENT '合约交易品种' ,
`order_id`  bigint(20) NOT NULL COMMENT '订单ID' ,
`price`  decimal(50,30) NOT NULL COMMENT '成交价' ,
`volume`  decimal(50,30) NOT NULL COMMENT '成交量（单位：张数）' ,
`trade_sn`  varchar(64) NOT NULL COMMENT '交易ID' ,
`trade_id`  bigint(20) NOT NULL COMMENT '交易ID' ,
`pos_id`  bigint(20) NOT NULL COMMENT '仓位ID' ,
`maker_flag`  int(11) NOT NULL COMMENT '1-marker， 0-taker' ,
`trade_time`  bigint(20) NOT NULL COMMENT '成交时间' ,
`opponent_order_id`  bigint(20) NULL DEFAULT NULL COMMENT '对手订单ID' ,
`fee_collector_id`  bigint(20) NOT NULL COMMENT '手续费收取人' ,
`fee_ratio`  decimal(50,30) NOT NULL COMMENT '手续费率' ,
`fee`  decimal(50,30) NOT NULL COMMENT '手续费' ,
`order_margin`  decimal(50,30) NULL DEFAULT NULL COMMENT '押金' ,
`pnl`  decimal(50,30) NOT NULL COMMENT '盈亏(此次成交的盈亏)' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
`remain_volume`  decimal(50,30) NULL DEFAULT NULL COMMENT '未成交量' ,
`match_tx_id`  bigint(20) NULL DEFAULT NULL COMMENT '撮合事务Id' ,
`tx_id`  bigint(20) NULL DEFAULT NULL COMMENT '事务ID' ,
`fee_synch_status`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
COMMENT='永续合约_用户订单成交记录'

;

-- ----------------------------
-- Table structure for `pc_position`
-- ----------------------------
CREATE TABLE `pc_position` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) NOT NULL COMMENT '合约交易品种' ,
`long_flag`  int(11) NOT NULL COMMENT '是否多仓(side)' ,
`margin_mode`  varchar(20) NOT NULL COMMENT '保证金模式:1-全仓,2-逐仓' ,
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
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
COMMENT='永续合约_仓位'

;

-- ----------------------------
-- Table structure for `pc_trade`
-- ----------------------------
CREATE TABLE `pc_trade` (
`id`  bigint(20) NOT NULL COMMENT 'id' ,
`asset`  varchar(20) NOT NULL COMMENT '资产' ,
`symbol`  varchar(20) NOT NULL COMMENT '交易对' ,
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
COMMENT='永续合约_成交(撮合结果)'

;

-- ----------------------------
-- Indexes structure for table pc_account
-- ----------------------------
CREATE UNIQUE INDEX `un_user_asset` ON `pc_account`(`user_id`, `asset`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table pc_account_log
-- ----------------------------
CREATE INDEX `idx_user_asset_symbol` ON `pc_account_log`(`user_id`, `asset`, `symbol`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table pc_account_record
-- ----------------------------
CREATE UNIQUE INDEX `un_sn` ON `pc_account_record`(`sn`) USING BTREE ;
CREATE UNIQUE INDEX `un_trade_no` ON `pc_account_record`(`trade_no`) USING BTREE ;
CREATE INDEX `idx_created` ON `pc_account_record`(`created`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table pc_account_symbol
-- ----------------------------
CREATE UNIQUE INDEX `user_asset_symbol` ON `pc_account_symbol`(`user_id`, `asset`, `symbol`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table pc_active_order
-- ----------------------------
CREATE INDEX `idx_userid_symbol` ON `pc_active_order`(`user_id`, `asset`, `symbol`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table pc_active_position
-- ----------------------------
CREATE UNIQUE INDEX `idx_user_act_pos` ON `pc_active_position`(`user_id`, `asset`, `symbol`, `long_flag`) USING BTREE ;
CREATE INDEX `idx_userid` ON `pc_active_position`(`user_id`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table pc_collector_account_record
-- ----------------------------
CREATE UNIQUE INDEX `un_trade_no` ON `pc_collector_account_record`(`trade_no`) USING BTREE ;
CREATE UNIQUE INDEX `un_sn` ON `pc_collector_account_record`(`sn`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table pc_liq_record
-- ----------------------------
CREATE INDEX `idx_created` ON `pc_liq_record`(`created`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table pc_order
-- ----------------------------
CREATE INDEX `idx_created` ON `pc_order`(`created`) USING BTREE ;
CREATE INDEX `idx_close_pos_id` ON `pc_order`(`close_pos_id`) USING BTREE ;
CREATE INDEX `idx_user_asset_symbol` ON `pc_order`(`user_id`, `asset`, `symbol`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table pc_order_log
-- ----------------------------
CREATE INDEX `idx_order_id` ON `pc_order_log`(`order_id`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table pc_order_trade
-- ----------------------------
CREATE UNIQUE INDEX `un_trade_no` ON `pc_order_trade`(`trade_sn`) USING BTREE ;
CREATE INDEX `idx_userid` ON `pc_order_trade`(`user_id`) USING BTREE ;
CREATE INDEX `idx_orderid` ON `pc_order_trade`(`order_id`) USING BTREE ;
CREATE INDEX `idx_posid` ON `pc_order_trade`(`pos_id`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table pc_position
-- ----------------------------
CREATE INDEX `idx_userid_asset_symbol` ON `pc_position`(`user_id`, `asset`, `symbol`) USING BTREE ;
CREATE INDEX `idx_volume` ON `pc_position`(`volume`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table pc_trade
-- ----------------------------
CREATE INDEX `idx_tk_account_id_tk_order_id` ON `pc_trade`(`tk_account_id`, `tk_order_id`) USING BTREE ;
CREATE INDEX `idx_trade_time_symbol` ON `pc_trade`(`trade_time`, `symbol`) USING BTREE ;
