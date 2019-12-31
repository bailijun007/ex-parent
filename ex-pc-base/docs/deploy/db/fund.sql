/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : expv3-fund

Target Server Type    : MYSQL
Target Server Version : 50599
File Encoding         : 65001

Date: 2020-01-01 00:03:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `c2c_order`
-- ----------------------------
DROP TABLE IF EXISTS `c2c_order`;
CREATE TABLE `c2c_order` (
`id`  bigint(20) NOT NULL ,
`sn`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL ,
`pay_currency`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付币种' ,
`exchange_currency`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '兑换币种' ,
`type`  int(11) NOT NULL COMMENT '1 买入，-1 卖出' ,
`price`  decimal(10,0) NOT NULL COMMENT '价格' ,
`volume`  decimal(10,0) NOT NULL COMMENT '数量' ,
`amount`  decimal(10,0) NOT NULL COMMENT '总额（回调返回）' ,
`pay_status`  int(11) NOT NULL COMMENT '执行状态:0-pending，1-支付成功，2-支付失败, 3-同步余额, 4-审核中, 5-审核通过' ,
`pay_status_desc`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付状态描述' ,
`pay_time`  bigint(20) NOT NULL COMMENT '支付时间' ,
`pay_finish_time`  bigint(20) NOT NULL COMMENT '支付完成时间' ,
`synch_status`  int(11) NOT NULL COMMENT '同步状态' ,
`approval_status`  int(11) NOT NULL COMMENT '审批状态' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='c2c订单'

;

-- ----------------------------
-- Table structure for `deposit_addr`
-- ----------------------------
DROP TABLE IF EXISTS `deposit_addr`;
CREATE TABLE `deposit_addr` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`asset`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`address`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '充值地址' ,
`remark`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '备注' ,
`enabled`  int(1) NOT NULL COMMENT '启用/禁用' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='充值地址'

;

-- ----------------------------
-- Table structure for `deposit_record`
-- ----------------------------
DROP TABLE IF EXISTS `deposit_record`;
CREATE TABLE `deposit_record` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`sn`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付单号' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`amount`  decimal(10,0) NOT NULL COMMENT '支付/收款金额' ,
`channel_id`  int(11) NOT NULL COMMENT '支付渠道' ,
`account`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付/收款账号（支付服务商端账号）' ,
`transaction_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付交易ID（支付服务商端生成）' ,
`pay_status`  int(11) NOT NULL COMMENT '执行状态:0-pending，1-支付成功，2-支付失败, 3-同步余额' ,
`pay_status_desc`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付状态描述' ,
`pay_time`  bigint(20) NULL DEFAULT NULL COMMENT '支付时间' ,
`pay_finish_time`  bigint(20) NULL DEFAULT NULL COMMENT '支付完成时间' ,
`synch_status`  int(11) NOT NULL COMMENT '同步状态' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
`tx_hash`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='充值记录'

;

-- ----------------------------
-- Table structure for `fund_account`
-- ----------------------------
DROP TABLE IF EXISTS `fund_account`;
CREATE TABLE `fund_account` (
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产类型' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`balance`  decimal(10,0) NOT NULL COMMENT '余额' ,
`version`  bigint(20) NOT NULL COMMENT '版本' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`asset`, `user_id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='用户资金账户'

;

-- ----------------------------
-- Table structure for `fund_account_record`
-- ----------------------------
DROP TABLE IF EXISTS `fund_account_record`;
CREATE TABLE `fund_account_record` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`sn`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '流水号' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`type`  int(11) NOT NULL COMMENT '类型：1 收入,-1 支出' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`amount`  decimal(10,0) NOT NULL COMMENT '本笔金额' ,
`balance`  decimal(10,0) NOT NULL COMMENT '本笔余额' ,
`trade_no`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用方支付单号' ,
`trade_type`  int(11) NOT NULL COMMENT '订单类型:1-充值,2-消费,3-奖金,4-提现`' ,
`remark`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' ,
`serial_no`  bigint(20) NOT NULL COMMENT '序号:' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
`requestId`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='账变明细'

;

-- ----------------------------
-- Table structure for `fund_transfer`
-- ----------------------------
DROP TABLE IF EXISTS `fund_transfer`;
CREATE TABLE `fund_transfer` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`user_id`  bigint(20) NOT NULL COMMENT '成功' ,
`sn`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单号' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`amount`  decimal(10,0) NOT NULL COMMENT '支付/收款金额' ,
`src_account_type`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '源账户类型' ,
`target_account_type`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标账户类型' ,
`src_account_id`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '源账户ID' ,
`target_account_id`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标账户ID' ,
`remark`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' ,
`status`  int(11) NOT NULL COMMENT '状态' ,
`request_id`  varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求ID' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='资金划转'

;

-- ----------------------------
-- Table structure for `withdrawal_addr`
-- ----------------------------
DROP TABLE IF EXISTS `withdrawal_addr`;
CREATE TABLE `withdrawal_addr` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`user_id`  bigint(20) NULL DEFAULT NULL COMMENT '用户ID' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资产' ,
`address`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '充值地址' ,
`remark`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' ,
`enabled`  int(1) NULL DEFAULT NULL COMMENT '启用/禁用' ,
`created`  bigint(20) NULL DEFAULT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NULL DEFAULT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='提现地址'

;

-- ----------------------------
-- Table structure for `withdrawal_record`
-- ----------------------------
DROP TABLE IF EXISTS `withdrawal_record`;
CREATE TABLE `withdrawal_record` (
`id`  bigint(20) NOT NULL COMMENT '主键' ,
`user_id`  bigint(20) NOT NULL COMMENT '用户ID' ,
`sn`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付单号' ,
`asset`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资产' ,
`amount`  decimal(10,0) NOT NULL COMMENT '提现金额' ,
`channel_id`  int(11) NOT NULL COMMENT '支付渠道' ,
`account`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付/收款账号（支付服务商端账号）' ,
`transaction_id`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付交易ID（支付服务商端生成）' ,
`pay_status`  int(11) NOT NULL COMMENT '执行状态:0-pending，1-支付成功，2-支付失败, 3-同步余额, 4-审核中, 5-审核通过' ,
`pay_status_desc`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '支付状态描述' ,
`pay_time`  bigint(20) NULL DEFAULT NULL COMMENT '支付时间' ,
`pay_finish_time`  bigint(20) NULL DEFAULT NULL COMMENT '支付完成时间' ,
`approval_status`  int(11) NULL DEFAULT NULL COMMENT '审批状态' ,
`synch_status`  int(11) NULL DEFAULT NULL COMMENT '同步状态' ,
`created`  bigint(20) NOT NULL COMMENT '创建时间' ,
`modified`  bigint(20) NOT NULL COMMENT '修改时间' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
COMMENT='提现记录'

;

-- ----------------------------
-- Indexes structure for table fund_account_record
-- ----------------------------
CREATE UNIQUE INDEX `idx_sn` ON `fund_account_record`(`sn`) USING BTREE ;
CREATE INDEX `idx_userid_asset` ON `fund_account_record`(`user_id`, `asset`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table fund_transfer
-- ----------------------------
CREATE INDEX `idx_userid` ON `fund_transfer`(`user_id`) USING BTREE ;
CREATE INDEX `idx_sn` ON `fund_transfer`(`sn`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table withdrawal_addr
-- ----------------------------
CREATE INDEX `idx_userid` ON `withdrawal_addr`(`user_id`) USING BTREE ;
CREATE INDEX `idx _asset` ON `withdrawal_addr`(`asset`) USING BTREE ;

-- ----------------------------
-- Indexes structure for table withdrawal_record
-- ----------------------------
CREATE INDEX `idx_user_id_asset` ON `withdrawal_record`(`user_id`, `asset`) USING BTREE ;
CREATE INDEX `un_sn` ON `withdrawal_record`(`sn`) USING BTREE ;
