/+ACo-
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : expv3+AF8-pc

Target Server Type    : MYSQL
Target Server Version : 50599
File Encoding         : 65000

Date: 2020-05-22 13:24:02
+ACo-/

SET FOREIGN+AF8-KEY+AF8-CHECKS+AD0-0+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-account+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-account+AGA- (
+AGA-user+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+dShiNw-ID' ,
+AGA-asset+AGA-  varchar(20) NOT NULL COMMENT '+jUROp3x7V4s-' ,
+AGA-balance+AGA-  decimal(50,30) NOT NULL COMMENT '+T1mYnQ-' ,
+AGA-version+AGA-  bigint(20) NOT NULL COMMENT '+ckhnLA-' ,
+AGA-modified+AGA-  bigint(20) NOT NULL COMMENT '+Uhte+mX2lfQ-' ,
+AGA-created+AGA-  bigint(20) NOT NULL COMMENT '+Uhte+mX2lfQ-' ,
PRIMARY KEY (+AGA-user+AF8-id+AGA-, +AGA-asset+AGA-),
UNIQUE INDEX +AGA-un+AF8-user+AF8-asset+AGA- (+AGA-user+AF8-id+AGA-, +AGA-asset+AGA-) USING BTREE 
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+dShiN41EkdGNJmI3-'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-account+AF8-log+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-account+AF8-log+AGA- (
+AGA-id+AGA-  bigint(20) NOT NULL ,
+AGA-type+AGA-  int(11) NOT NULL COMMENT '+TotO9nx7V4s-' ,
+AGA-user+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+dShiNw-Id' ,
+AGA-asset+AGA-  varchar(20) NOT NULL COMMENT '+jUROpw-' ,
+AGA-symbol+AGA-  varchar(20) NULL DEFAULT NULL COMMENT '+TqRmE1TBec0-' ,
+AGA-ref+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+XxV1KFv5jGE-Id' ,
+AGA-time+AGA-  bigint(20) NOT NULL COMMENT '+ZfaV9A-' ,
PRIMARY KEY (+AGA-id+AGA-),
INDEX +AGA-idx+AF8-user+AF8-asset+AF8-symbol+AGA- (+AGA-user+AF8-id+AGA-, +AGA-asset+AGA-, +AGA-symbol+AGA-) USING BTREE 
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+jSZiN2XlX9c-'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-account+AF8-record+AF8-trade+AF8-no+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-account+AF8-record+AF8-trade+AF8-no+AGA- (
+AGA-trade+AF8-no+AGA-  varchar(64) NOT NULL COMMENT '+jAN1KGW5ZS9O2FNVU/c-' ,
+AGA-record+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+i7BfVQ-Id' ,
+AGA-tx+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+TotSoQ-ID' ,
PRIMARY KEY (+AGA-trade+AF8-no+AGA-)
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+bDh+7VQIfqYAX40mYjdmDn7G-SN'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-account+AF8-symbol+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-account+AF8-symbol+AGA- (
+AGA-id+AGA-  bigint(20) NOT NULL COMMENT '+TjuVLg-' ,
+AGA-asset+AGA-  varchar(20) NOT NULL COMMENT '+jUROpw-' ,
+AGA-symbol+AGA-  varchar(20) NOT NULL COMMENT '+VAh+pk6kZhNUwXnN-' ,
+AGA-margin+AF8-mode+AGA-  int(11) NOT NULL COMMENT '+T92LwZHRaiFfDw-,' ,
+AGA-short+AF8-leverage+AGA-  decimal(10,4) NOT NULL COMMENT '+UFp6emdgZ0Y-' ,
+AGA-long+AF8-leverage+AGA-  decimal(10,4) NOT NULL COMMENT '+UFpZGmdgZ0Y-' ,
+AGA-long+AF8-max+AF8-leverage+AGA-  decimal(10,4) NOT NULL COMMENT '+ZwBZJ1kaTtNnYGdG-' ,
+AGA-short+AF8-max+AF8-leverage+AGA-  decimal(10,4) NOT NULL COMMENT '+ZwBZJ1kaenpnYGdG-' ,
+AGA-cross+AF8-leverage+AGA-  decimal(10,4) NOT NULL COMMENT '+UWhO02dgZ0Y-' ,
+AGA-version+AGA-  bigint(20) NOT NULL COMMENT '+ckhnLA-' ,
+AGA-user+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+dShiNw-ID' ,
+AGA-created+AGA-  bigint(20) NOT NULL COMMENT '+Uhte+mX2lfQ-' ,
+AGA-modified+AGA-  bigint(20) NOT NULL COMMENT '+T+5lOWX2lfQ-' ,
PRIMARY KEY (+AGA-id+AGA-),
UNIQUE INDEX +AGA-user+AF8-asset+AF8-symbol+AGA- (+AGA-user+AF8-id+AGA-, +AGA-asset+AGA-, +AGA-symbol+AGA-) USING BTREE 
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+bDh+7VQIfqYAX40mYjeLvn9u-'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-collector+AF8-account+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-collector+AF8-account+AGA- (
+AGA-id+AGA-  bigint(20) NOT NULL ,
+AGA-asset+AGA-  varchar(20) NOT NULL COMMENT '+jUROp3x7V4s-' ,
+AGA-balance+AGA-  decimal(50,30) NOT NULL COMMENT '+T1mYnQ-' ,
+AGA-version+AGA-  bigint(20) NOT NULL COMMENT '+ckhnLA-' ,
+AGA-created+AGA-  bigint(20) NOT NULL COMMENT '+Uhte+mX2lfQ-' ,
+AGA-modified+AGA-  bigint(20) NOT NULL COMMENT '+T+5lOWX2lfQ-' ,
PRIMARY KEY (+AGA-id+AGA-, +AGA-asset+AGA-)
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+Ykt+7Y05-'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-collector+AF8-account+AF8-record+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-collector+AF8-account+AF8-record+AGA- (
+AGA-id+AGA-  bigint(20) NOT NULL ,
+AGA-asset+AGA-  varchar(20) NOT NULL COMMENT '+jUROpw-' ,
+AGA-sn+AGA-  varchar(64) NOT NULL COMMENT '+bUFsNFP3-' ,
+AGA-type+AGA-  int(11) NOT NULL COMMENT '+fHtXi/8a-1 +ZTZRZQ-,-1 +ZS9R+g-' ,
+AGA-amount+AGA-  decimal(50,30) NOT NULL COMMENT '+Zyx7FJHRmJ0-' ,
+AGA-remark+AGA-  varchar(255) NOT NULL COMMENT '+WQds6A-' ,
+AGA-balance+AGA-  decimal(50,30) NOT NULL COMMENT '+Zyx7FE9ZmJ0-' ,
+AGA-trade+AF8-no+AGA-  varchar(64) NOT NULL COMMENT '+jAN1KGW5ZS9O2FNVU/c-' ,
+AGA-trade+AF8-type+AGA-  int(11) NOT NULL ,
+AGA-serial+AF8-no+AGA-  bigint(20) NOT NULL COMMENT '+Xo9T9w-:' ,
+AGA-associated+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+UXOAVFv5jGF2hA-ID' ,
+AGA-collector+AF8-id+AGA-  bigint(20) NOT NULL ,
+AGA-tx+AF8-id+AGA-  bigint(20) NULL DEFAULT NULL COMMENT '+TotSoQ-ID' ,
+AGA-request+AF8-id+AGA-  varchar(255) NULL DEFAULT NULL COMMENT '+i/dsQg-ID' ,
+AGA-user+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+dShiNw-ID' ,
+AGA-created+AGA-  bigint(20) NOT NULL COMMENT '+Uhte+mX2lfQ-' ,
+AGA-modified+AGA-  bigint(20) NOT NULL COMMENT '+T+5lOWX2lfQ-' ,
PRIMARY KEY (+AGA-id+AGA-)
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+XgFeAWJLfu2NOQBfjSZiN2YOfsY-'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-liq+AF8-record+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-liq+AF8-record+AGA- (
+AGA-id+AGA-  bigint(20) NOT NULL ,
+AGA-asset+AGA-  varchar(20) NOT NULL COMMENT '+jUROpw-' ,
+AGA-symbol+AGA-  varchar(20) NOT NULL COMMENT '+VAh+pk6kZhNUwXnN-' ,
+AGA-pos+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+TtNPTQ-ID' ,
+AGA-long+AF8-flag+AGA-  int(11) NOT NULL COMMENT '+Zi9UJlkaTtM-(side)' ,
+AGA-volume+AGA-  decimal(50,30) NOT NULL COMMENT '+ZXCRz/8IXyD/CQ-' ,
+AGA-pos+AF8-margin+AGA-  decimal(50,30) NOT NULL COMMENT '+T92LwZHR-' ,
+AGA-bankrupt+AF8-price+AGA-  decimal(50,30) NOT NULL COMMENT '+eDROp073-' ,
+AGA-liq+AF8-price+AGA-  decimal(50,30) NOT NULL COMMENT '+Xzpec073-' ,
+AGA-mean+AF8-price+AGA-  decimal(50,30) NOT NULL COMMENT '+TtNPTVdHTvc-' ,
+AGA-fee+AGA-  decimal(50,30) NOT NULL COMMENT '+Ykt+7Y05-' ,
+AGA-fee+AF8-ratio+AGA-  decimal(10,6) NOT NULL COMMENT '+Ykt+7Y05c4c-' ,
+AGA-filled+AF8-volume+AGA-  decimal(50,30) NOT NULL COMMENT '+Xzpec1nUYlhd8mIQTqSRzw-' ,
+AGA-pnl+AGA-  decimal(50,30) NOT NULL COMMENT '+Xzpec2U2dso-' ,
+AGA-status+AGA-  tinyint(4) NOT NULL ,
+AGA-version+AGA-  bigint(20) NOT NULL DEFAULT 0 COMMENT '+ckhnLA-' ,
+AGA-user+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+dShiNw-ID' ,
+AGA-created+AGA-  bigint(20) NOT NULL COMMENT '+Uhte+mX2lfQ-' ,
+AGA-modified+AGA-  bigint(20) NOT NULL COMMENT '+T+5lOWX2lfQ-' ,
PRIMARY KEY (+AGA-id+AGA-)
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+bDh+7VQIfqYAX07TT00-'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-message+AF8-ext+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-message+AF8-ext+AGA- (
+AGA-id+AGA-  bigint(20) NOT NULL ,
+AGA-msg+AF8-id+AGA-  varchar(64) NOT NULL ,
+AGA-tags+AGA-  varchar(40) NOT NULL ,
+AGA-keys+AGA-  varchar(64) NOT NULL ,
+AGA-msg+AF8-body+AGA-  varchar(2000) NOT NULL ,
+AGA-error+AF8-info+AGA-  varchar(3000) NULL DEFAULT NULL COMMENT '+XwJeOE/hYG8-' ,
+AGA-user+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+dShiNw-ID' ,
+AGA-asset+AGA-  varchar(20) NOT NULL COMMENT '+jUROpw-' ,
+AGA-symbol+AGA-  varchar(20) NOT NULL COMMENT '+TqRmE1v5/whUCH6mVMF5zf8J-' ,
+AGA-shard+AF8-id+AGA-  bigint(20) NOT NULL DEFAULT 0 ,
+AGA-created+AGA-  bigint(20) NOT NULL COMMENT '+Uhte+mX2lfQ-' ,
+AGA-status+AGA-  int(11) NOT NULL ,
PRIMARY KEY (+AGA-id+AGA-, +AGA-shard+AF8-id+AGA-)
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+XgFeAQBfZK5UCG2IYG8-'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-order+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-order+AGA- (
+AGA-id+AGA-  bigint(20) NOT NULL COMMENT '+TjuVLg-' ,
+AGA-user+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+dShiNw-ID' ,
+AGA-asset+AGA-  varchar(20) NOT NULL COMMENT '+jUROpw-' ,
+AGA-symbol+AGA-  varchar(64) NOT NULL COMMENT '+VAh+pk6kZhNUwXnN-' ,
+AGA-close+AF8-flag+AGA-  int(11) NOT NULL COMMENT '+Zi9UJg-:1-+XnNO0w-,0-+XwA-' ,
+AGA-long+AF8-flag+AGA-  int(11) NOT NULL COMMENT '+Zi9UJv8a-1-+WRpO0/8M-0-+enpO0w-' ,
+AGA-margin+AF8-mode+AGA-  int(11) NOT NULL COMMENT '+T92LwZHRaiFfDw-:1-+UWhO0w-,2-+kBBO0w-' ,
+AGA-leverage+AGA-  decimal(10,2) NOT NULL COMMENT '+Z2BnRg-' ,
+AGA-price+AGA-  decimal(50,30) NOT NULL COMMENT '+WdRiWE73aDw-' ,
+AGA-order+AF8-type+AGA-  int(11) NOT NULL COMMENT '+bDh+7VQIfqZZ1GJYfHtXiw-' ,
+AGA-volume+AGA-  decimal(50,20) NOT NULL COMMENT '+WdRiWGVwkc//DFIdWcuLvn9uVA7/DFQOfu1ODU8aT+5lOQ-' ,
+AGA-face+AF8-value+AGA-  decimal(50,30) NULL DEFAULT NULL ,
+AGA-status+AGA-  int(11) NOT NULL COMMENT '+WdRiWHK2YAE-' ,
+AGA-margin+AF8-ratio+AGA-  decimal(50,30) NOT NULL COMMENT '+T92LwZHRc4f/DFIdWctOOg- +Z2BnRnaEUBJlcA-' ,
+AGA-open+AF8-fee+AF8-ratio+AGA-  decimal(50,30) NOT NULL COMMENT '+XwBO02JLfu2NOXOH-' ,
+AGA-close+AF8-fee+AF8-ratio+AGA-  decimal(50,30) NOT NULL COMMENT '+Xzpec2JLfu2NOXOH-' ,
+AGA-fee+AF8-cost+AGA-  decimal(50,30) NOT NULL COMMENT '+W55lNmJLfu2NOQ-,+YhBOpFQOi6F7lw-(+U++A/ZDoUgZiEE6k/wxjCWvUT4tlNlPW-)' ,
+AGA-gross+AF8-margin+AGA-  decimal(50,30) NOT NULL COMMENT '+YDtivJHR/xpP3YvBkdE-+-+XwBO02JLfu2NOQ-+-+XnNO02JLfu2NOQ-' ,
+AGA-order+AF8-margin+AGA-  decimal(50,30) NOT NULL COMMENT '+WdRiWE/di8GR0Q-' ,
+AGA-open+AF8-fee+AGA-  decimal(50,30) NOT NULL COMMENT '+XwBO02JLfu2NOQ-,+YhBOpGX2T+5lOQ-(+U++A/ZDoUgZiEE6k/wxjCWvUT4uRymU+-)' ,
+AGA-close+AF8-fee+AGA-  decimal(50,30) NOT NULL COMMENT '+XnNO02JLfu2NOf8MVyhOC1nUYlhl9mPQUk1lNlPW-(+U++A/ZDoUgZiEE6k/wxjCWvUT4uRymU+-)' ,
+AGA-filled+AF8-volume+AGA-  decimal(50,30) NOT NULL COMMENT '+XfJiEE6kkc//CFNVT03/Gl8g/wk-' ,
+AGA-trade+AF8-mean+AF8-price+AGA-  decimal(10,0) NOT NULL COMMENT '+YhBOpFdHTvc-' ,
+AGA-cancel+AF8-volume+AGA-  decimal(50,30) NULL DEFAULT NULL COMMENT '+U9ZtiP8IU1VPTf8aXyD/CQ-' ,
+AGA-close+AF8-pos+AF8-id+AGA-  bigint(20) NULL DEFAULT NULL COMMENT '+XnNO01nUYlhb+V6UdoRO009N-Id' ,
+AGA-time+AF8-in+AF8-force+AGA-  int(11) NULL DEFAULT NULL COMMENT '+WdRiWGcJZUhl9pX0-' ,
+AGA-trigger+AF8-flag+AGA-  int(11) NULL DEFAULT NULL COMMENT '+Zi9UJl3yieZT0f8MdShOjmtidshrYmNfe0mJ5lPRXw9Z1GJY-' ,
+AGA-cancel+AF8-time+AGA-  bigint(20) NULL DEFAULT NULL COMMENT '+U9ZtiGX2lfQ-' ,
+AGA-visible+AF8-flag+AGA-  int(11) NULL DEFAULT NULL COMMENT '+U++JwWAn/wxfOl5zWdRiWP8MgepSqFHPTtNZ1GJY- +kP1ODVPvicE-' ,
+AGA-liq+AF8-flag+AGA-  int(11) NOT NULL COMMENT '+Zi9UJl86XnNZ1GJY-' ,
+AGA-active+AF8-flag+AGA-  int(11) NOT NULL COMMENT '+Zi9UJm07UqhZ1GJY-' ,
+AGA-create+AF8-operator+AGA-  varchar(50) NULL DEFAULT NULL ,
+AGA-cancel+AF8-operator+AGA-  varchar(50) NULL DEFAULT NULL ,
+AGA-remark+AGA-  varchar(255) NULL DEFAULT NULL COMMENT '+WQds6A-' ,
+AGA-client+AF8-order+AF8-id+AGA-  varchar(64) NULL DEFAULT NULL COMMENT '+W6JiN4HqW5pOSVnUYlg-ID+/wx1KE6OTg5bomI3fPt+31FzgFQ- +/wg-open api+/wk-' ,
+AGA-created+AGA-  bigint(20) NOT NULL COMMENT '+Uhte+mX2lfQ-' ,
+AGA-modified+AGA-  bigint(20) NOT NULL COMMENT '+T+5lOWX2lfQ-' ,
+AGA-version+AGA-  bigint(20) NULL DEFAULT 0 ,
PRIMARY KEY (+AGA-id+AGA-),
INDEX +AGA-idx+AF8-created+AGA- (+AGA-created+AGA-) USING BTREE ,
INDEX +AGA-idx+AF8-close+AF8-pos+AF8-id+AGA- (+AGA-close+AF8-pos+AF8-id+AGA-) USING BTREE 
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+bDh+7VQIfqYAX4uiU1X/CFnUYlj/CQ-'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-order+AF8-trade+AF8-sn+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-order+AF8-trade+AF8-sn+AGA- (
+AGA-trade+AF8-sn+AGA-  varchar(64) NOT NULL COMMENT '+TqRmE16PU/c-' ,
+AGA-id+AGA-  bigint(20) NOT NULL ,
+AGA-tx+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+TotSoQ-ID' ,
PRIMARY KEY (+AGA-trade+AF8-sn+AGA-)
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+XgFeAQBfdShiN4uiU1ViEE6ki7BfVQ-SN'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-position+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-position+AGA- (
+AGA-id+AGA-  bigint(20) NOT NULL COMMENT '+TjuVLg-' ,
+AGA-user+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+dShiNw-ID' ,
+AGA-asset+AGA-  varchar(20) NOT NULL COMMENT '+jUROpw-' ,
+AGA-symbol+AGA-  varchar(20) NOT NULL COMMENT '+VAh+pk6kZhNUwXnN-' ,
+AGA-long+AF8-flag+AGA-  int(11) NOT NULL COMMENT '+Zi9UJlkaTtM-(side)' ,
+AGA-margin+AF8-mode+AGA-  varchar(20) NOT NULL COMMENT '+T92LwZHRaiFfDw-:1-+UWhO0w-,2-+kBBO0w-' ,
+AGA-volume+AGA-  decimal(50,30) NOT NULL COMMENT '+TtNPTQ- +XyBlcA-posVolume' ,
+AGA-face+AF8-value+AGA-  decimal(50,8) NOT NULL COMMENT '+l2JQPA-(+U1VPTf8aYqVO940nXgE-)' ,
+AGA-base+AF8-value+AGA-  decimal(50,30) NOT NULL COMMENT '+TtNPTQ- +V/p4QI0nXgE- +YDtO91A8-posBaseValue' ,
+AGA-entry+AF8-leverage+AGA-  decimal(50,30) NOT NULL COMMENT '+XwBO02dgZ0Y-' ,
+AGA-leverage+AGA-  decimal(50,30) NOT NULL COMMENT '+X1NSTWdgZ0Y-' ,
+AGA-pos+AF8-margin+AGA-  decimal(50,30) NOT NULL COMMENT '+TtNPTU/di8GR0Q-' ,
+AGA-close+AF8-fee+AGA-  decimal(50,30) NOT NULL COMMENT '+XnNO02JLfu2NOQ-' ,
+AGA-mean+AF8-price+AGA-  decimal(50,30) NOT NULL COMMENT '+V0dO9/8MTtNPTU46-0+Zfb/DIhoeTpnAFQOTgBrIU7TT01T2FKoZfZ2hFdHTvc-meanPrice' ,
+AGA-accu+AF8-base+AF8-value+AGA-  decimal(50,30) NOT NULL COMMENT '+fS9572A7TvdQPA-' ,
+AGA-accu+AF8-volume+AGA-  decimal(50,30) NOT NULL COMMENT '+fS+LoWIQTqSRzw-' ,
+AGA-init+AF8-margin+AGA-  decimal(50,30) NOT NULL COMMENT '+Uh1Zy0/di8GR0f8MXnNO03aEZfZQGYmBUc9Tu1v5XpR2hGvUT4v/DE7lfvRjAWU2dspzh04AgfQ-' ,
+AGA-hold+AF8-margin+AF8-ratio+AGA-  decimal(50,30) NOT NULL COMMENT '+fvRjAU/di8GR0WvUc4c-' ,
+AGA-auto+AF8-add+AF8-flag+AGA-  int(11) NOT NULL COMMENT '+Zi9UJoHqUqiP/VKgT92LwZHRaAeLxg-' ,
+AGA-fee+AF8-cost+AGA-  decimal(50,30) NOT NULL COMMENT '+XfJiY2JLfu2NOQ-' ,
+AGA-realised+AF8-pnl+AGA-  decimal(50,30) NOT NULL COMMENT '+XfJbnnOwdshOjw-' ,
+AGA-liq+AF8-price+AGA-  decimal(50,30) NULL DEFAULT NULL COMMENT '+Xzpec073/wxO009NTjo-0+Zfb/DIhoeTpnAFQOTgBrIU7TT01T2FKoZfZ2hF86XnNO9w-' ,
+AGA-liq+AF8-mark+AF8-price+AGA-  decimal(50,30) NULL DEFAULT NULL COMMENT '+Xzpecw-' ,
+AGA-liq+AF8-mark+AF8-time+AGA-  bigint(20) NULL DEFAULT NULL COMMENT '+ieZT0V86XnN2hGgHi7Bl9pX0-' ,
+AGA-liq+AF8-status+AGA-  int(11) NULL DEFAULT NULL COMMENT '+TtNPTV86XnNytmAB/ww-0+/xpnKonmU9Fec07T/ww-1+/xpO009NiKtRu37T/ww-' ,
+AGA-version+AGA-  bigint(20) NOT NULL DEFAULT 0 ,
+AGA-created+AGA-  bigint(20) NOT NULL COMMENT '+Uhte+mX2lfQ-' ,
+AGA-modified+AGA-  bigint(20) NOT NULL COMMENT '+T+5lOWX2lfQ-' ,
PRIMARY KEY (+AGA-id+AGA-),
INDEX +AGA-idx+AF8-userid+AF8-asset+AF8-symbol+AGA- (+AGA-user+AF8-id+AGA-, +AGA-asset+AGA-, +AGA-symbol+AGA-) USING BTREE ,
INDEX +AGA-idx+AF8-volume+AGA- (+AGA-volume+AGA-) USING BTREE 
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+bDh+7VQIfqYAX07TT00-'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-riskfund+AF8-account+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-riskfund+AF8-account+AGA- (
+AGA-id+AGA-  bigint(20) NOT NULL ,
+AGA-asset+AGA-  varchar(20) NOT NULL COMMENT '+jUROp3x7V4s-' ,
+AGA-balance+AGA-  decimal(50,30) NOT NULL COMMENT '+T1mYnQ-' ,
+AGA-version+AGA-  bigint(20) NOT NULL COMMENT '+ckhnLA-' ,
+AGA-created+AGA-  bigint(20) NOT NULL COMMENT '+Uhte+mX2lfQ-' ,
+AGA-modified+AGA-  bigint(20) NOT NULL COMMENT '+T+5lOWX2lfQ-' ,
PRIMARY KEY (+AGA-id+AGA-),
UNIQUE INDEX +AGA-un+AF8-asset+AGA- (+AGA-asset+AGA-) USING BTREE 
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+mM6WaVf6kdGNJmI3-'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-riskfund+AF8-account+AF8-record+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-riskfund+AF8-account+AF8-record+AGA- (
+AGA-id+AGA-  bigint(20) NOT NULL ,
+AGA-asset+AGA-  varchar(20) NOT NULL COMMENT '+jUROpw-' ,
+AGA-sn+AGA-  varchar(64) NOT NULL COMMENT '+bUFsNFP3-' ,
+AGA-type+AGA-  int(11) NOT NULL COMMENT '+fHtXi/8a-1 +ZTZRZQ-,-1 +ZS9R+g-' ,
+AGA-amount+AGA-  decimal(10,0) NOT NULL COMMENT '+Zyx7FJHRmJ0-' ,
+AGA-remark+AGA-  varchar(255) NOT NULL COMMENT '+WQds6A-' ,
+AGA-balance+AGA-  decimal(10,0) NOT NULL COMMENT '+Zyx7FE9ZmJ0-' ,
+AGA-trade+AF8-no+AGA-  varchar(64) NOT NULL COMMENT '+jAN1KGW5ZS9O2FNVU/c-' ,
+AGA-trade+AF8-type+AGA-  int(11) NOT NULL ,
+AGA-serial+AF8-no+AGA-  bigint(20) NOT NULL COMMENT '+Xo9T9w-' ,
+AGA-associated+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+UXOAVFv5jGF2hA-ID' ,
+AGA-riskfund+AF8-account+AF8-id+AGA-  bigint(20) NOT NULL ,
+AGA-tx+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+TotSoQ-ID' ,
+AGA-created+AGA-  bigint(20) NOT NULL COMMENT '+Uhte+mX2lfQ-' ,
+AGA-modified+AGA-  bigint(20) NOT NULL COMMENT '+T+5lOWX2lfQ-' ,
PRIMARY KEY (+AGA-id+AGA-)
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+mM6WaVf6kdGNJmI3i7BfVQ-'

+ADs-

-- ----------------------------
-- Table structure for +AGA-pc+AF8-trade+AGA-
-- ----------------------------
CREATE TABLE +AGA-pc+AF8-trade+AGA- (
+AGA-id+AGA-  bigint(20) NOT NULL COMMENT 'id' ,
+AGA-asset+AGA-  varchar(20) NOT NULL COMMENT '+jUROpw-' ,
+AGA-symbol+AGA-  varchar(20) NOT NULL COMMENT '+TqRmE1v5-' ,
+AGA-match+AF8-tx+AF8-id+AGA-  bigint(20) NOT NULL COMMENT '+TotSoQ-Id' ,
+AGA-tk+AF8-bid+AF8-flag+AGA-  int(11) NOT NULL COMMENT 'taker+Zi9UJk5w/xo-1-+Zi//DA-0-+VCY-' ,
+AGA-tk+AF8-account+AF8-id+AGA-  bigint(20) NOT NULL COMMENT 'taker+jSZiNw-ID' ,
+AGA-tk+AF8-order+AF8-id+AGA-  bigint(20) NOT NULL COMMENT 'taker+i6JTVQ-ID' ,
+AGA-tk+AF8-close+AF8-flag+AGA-  int(11) NOT NULL COMMENT 'taker+Zi9UJl5zTtM-' ,
+AGA-mk+AF8-account+AF8-id+AGA-  bigint(20) NOT NULL COMMENT 'maker+jSZiNw-Id' ,
+AGA-mk+AF8-order+AF8-id+AGA-  bigint(20) NOT NULL COMMENT 'maker+i6JTVQ-ID' ,
+AGA-mk+AF8-close+AF8-flag+AGA-  int(11) NOT NULL COMMENT 'maker+Zi9UJl5zTtM-' ,
+AGA-price+AGA-  decimal(50,30) NOT NULL COMMENT '+YhBOpE73aDw-' ,
+AGA-number+AGA-  decimal(50,30) NOT NULL COMMENT '+ZXCRzw-' ,
+AGA-trade+AF8-time+AGA-  bigint(20) NOT NULL COMMENT '+YhBOpGX2lfQ-' ,
+AGA-created+AGA-  bigint(20) NULL DEFAULT NULL ,
+AGA-modified+AGA-  bigint(20) NULL DEFAULT NULL ,
PRIMARY KEY (+AGA-id+AGA-)
)
ENGINE+AD0-InnoDB
COMMENT+AD0-'+bDh+7VQIfqYAX2IQTqQ-(+ZK5UCH7TZ5w-)'

+ADs-
