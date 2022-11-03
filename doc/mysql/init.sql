drop database if exists `monitor`;
create database if not exists `monitor` character set utf8mb4 collate utf8mb4_unicode_ci;

use `monitor`;

drop table if exists `tb_user`;
create table if not exists `tb_user`
(
    `id`          bigint(20)   not null,
    `login_name`  varchar(50)  not null comment '登录名',
    `password`    varchar(500) not null comment '密码',
    `type`        varchar(1)   not null comment '类型 1超级管理员 0普通用户',
    `board`       varchar(255) not null comment '看板',
    `salt`        varchar(12)  not null comment '加密盐',
    `status`      varchar(1)   not null comment '状态, 1-封禁 0-正常',
    `create_time` datetime     not null comment '创建时间',
    `update_time` datetime     not null comment '更新时间',
    primary key (`id`) using btree
) engine = innodb
  default charset utf8mb4
  collate utf8mb4_unicode_ci comment '用户表';

BEGIN;
INSERT INTO `tb_user`
VALUES (1579398312721121281, 'mikuhuyo', '6ebad96d025ac53497d30eb95d48f460', '1', '超级看板', '%RKqMm', '0', '2021-10-10 17:07:56', '2021-10-10 17:07:56');
COMMIT;

drop table if exists `tb_alarm`;
create table if not exists `tb_alarm`
(
    `id`          bigint(20)   not null comment 'id',
    `name`        varchar(50)  not null comment '报警名称',
    `quota_id`    bigint(20)   not null comment '指标id',
    `operator`    varchar(10)  not null comment '运算符',
    `threshold`   varchar(50)  not null comment '报警阈值',
    `level`       varchar(1)   not null comment '报警级别: 1一般 2严重',
    `cycle`       int unsigned not null comment '沉默周期(分钟)',
    `webhook`     varchar(256) not null comment 'web钩子',
    `subject`     varchar(50)  not null comment '报文主题',
    `create_time` datetime     not null comment '创建时间',
    `update_time` datetime     not null comment '更新时间',
    primary key (`id`) using btree
) engine = innodb
  default charset utf8mb4
  collate utf8mb4_unicode_ci comment '报警配置表';

begin;
INSERT INTO `tb_alarm`(`id`, `name`, `quota_id`, `operator`, `threshold`, `level`, `cycle`, `webhook`, `subject`, `create_time`, `update_time`) VALUES (1582664649262305281, '温度过低', 1582662233267691521, '<', '20', '1', 10, 'https://github.com/mikuhuyo', 'device/temperature/BJ0001', '2022-10-19 17:27:12', '2022-11-01 09:25:02');
INSERT INTO `tb_alarm`(`id`, `name`, `quota_id`, `operator`, `threshold`, `level`, `cycle`, `webhook`, `subject`, `create_time`, `update_time`) VALUES (1582666321032204289, '温度异常', 1582662233267691521, '>', '30', '2', 5, 'https://github.com/mikuhuyo', 'device/temperature/BJ0001', '2022-10-19 17:33:50', '2022-11-01 09:25:15');
INSERT INTO `tb_alarm`(`id`, `name`, `quota_id`, `operator`, `threshold`, `level`, `cycle`, `webhook`, `subject`, `create_time`, `update_time`) VALUES (1583378395777474561, '温度过低', 1582662434636226561, '<', '20', '1', 10, 'https://github.com/mikuhuyo', 'device/temperature/BJ0002', '2022-10-21 16:43:22', '2022-11-01 09:25:09');
INSERT INTO `tb_alarm`(`id`, `name`, `quota_id`, `operator`, `threshold`, `level`, `cycle`, `webhook`, `subject`, `create_time`, `update_time`) VALUES (1583378827048394753, '温度异常', 1582662434636226561, '>', '30', '2', 5, 'https://github.com/mikuhuyo', 'device/temperature/BJ0002', '2022-10-21 16:45:05', '2022-11-01 09:25:24');
INSERT INTO `tb_alarm`(`id`, `name`, `quota_id`, `operator`, `threshold`, `level`, `cycle`, `webhook`, `subject`, `create_time`, `update_time`) VALUES (1583386574108237825, '转速过低', 1582662729126699009, '<', '3000', '1', 10, 'https://github.com/mikuhuyo', 'device/roundPerMinute/BJ0001', '2022-10-21 17:15:52', '2022-10-21 17:15:52');
INSERT INTO `tb_alarm`(`id`, `name`, `quota_id`, `operator`, `threshold`, `level`, `cycle`, `webhook`, `subject`, `create_time`, `update_time`) VALUES (1583386675606200321, '转速过低', 1582662949231190018, '<', '3000', '1', 10, 'https://github.com/mikuhuyo', 'device/roundPerMinute/BJ0002', '2022-10-21 17:16:16', '2022-10-21 17:16:16');
INSERT INTO `tb_alarm`(`id`, `name`, `quota_id`, `operator`, `threshold`, `level`, `cycle`, `webhook`, `subject`, `create_time`, `update_time`) VALUES (1583386752793976834, '转速过高', 1582662729126699009, '>', '6000', '2', 5, 'https://github.com/mikuhuyo', 'device/roundPerMinute/BJ0001', '2022-10-21 17:16:34', '2022-10-21 17:16:34');
INSERT INTO `tb_alarm`(`id`, `name`, `quota_id`, `operator`, `threshold`, `level`, `cycle`, `webhook`, `subject`, `create_time`, `update_time`) VALUES (1583386820351631362, '转速过高', 1582662949231190018, '>', '6000', '2', 5, 'https://github.com/mikuhuyo', 'device/roundPerMinute/BJ0002', '2022-10-21 17:16:51', '2022-10-21 17:16:51');
commit;

drop table if exists `tb_quota`;
create table if not exists `tb_quota`
(
    `id`              bigint(20)   not null comment 'id',
    `name`            varchar(50)  not null comment '指标名称',
    `unit`            varchar(20)  not null comment '指标单位',
    `subject`         varchar(50)  not null comment '报文主题',
    `value_key`       varchar(50)  not null comment '指标值字段',
    `sn_key`          varchar(50)  not null comment '设备识别码字段',
    `webhook`         varchar(256) not null comment 'web钩子',
    `value_type`      varchar(10)  not null comment '指标字段类型: Double, Integer',
    `reference_value` varchar(100) not null comment '参考值',
    `create_time`     datetime     not null comment '创建时间',
    `update_time`     datetime     not null comment '更新时间',
    primary key (`id`) using btree
) engine = innodb
  default charset utf8mb4
  collate utf8mb4_unicode_ci comment '指标配置表';

begin;
INSERT INTO `tb_quota`(`id`, `name`, `unit`, `subject`, `value_key`, `sn_key`, `webhook`, `value_type`, `reference_value`, `create_time`, `update_time`) VALUES (1582662233267691521, 'BJ0001-温度指标', '°C', 'device/temperature/BJ0001', 'temperature', 'BJ0001', 'https://github.com/mikuhuyo', 'Integer', '20-30', '2022-10-19 17:17:36', '2022-10-19 17:17:36');
INSERT INTO `tb_quota`(`id`, `name`, `unit`, `subject`, `value_key`, `sn_key`, `webhook`, `value_type`, `reference_value`, `create_time`, `update_time`) VALUES (1582662434636226561, 'BJ0002-温度指标', '°C', 'device/temperature/BJ0002', 'temperature', 'BJ0002', 'https://github.com/mikuhuyo', 'Integer', '20-30', '2022-10-19 17:18:24', '2022-10-19 17:18:41');
INSERT INTO `tb_quota`(`id`, `name`, `unit`, `subject`, `value_key`, `sn_key`, `webhook`, `value_type`, `reference_value`, `create_time`, `update_time`) VALUES (1582662729126699009, 'BJ0001-转速指标', 'r/s', 'device/roundPerMinute/BJ0001', 'roundPerMinute', 'BJ0001', 'https://github.com/mikuhuyo', 'Integer', '3000-6000', '2022-10-19 17:19:34', '2022-10-19 17:20:41');
INSERT INTO `tb_quota`(`id`, `name`, `unit`, `subject`, `value_key`, `sn_key`, `webhook`, `value_type`, `reference_value`, `create_time`, `update_time`) VALUES (1582662949231190018, 'BJ0002-转速指标', 'r/s', 'device/roundPerMinute/BJ0002', 'roundPerMinute', 'BJ0002', 'https://github.com/mikuhuyo', 'Integer', '3000-6000', '2022-10-19 17:20:26', '2022-10-19 17:20:26');
commit;

drop table if exists `tb_board`;
create table if not exists `tb_board`
(
    `id`          bigint(20)   not null comment 'id',
    `user_id`     bigint(20)   not null comment '管理员id',
    `board_name`  varchar(50)  not null comment '看板名称',
    `quota`       varchar(100) not null comment '指标(趋势时设置), 默认0',
    `device`      varchar(100) not null comment '设备(累计), 默认0',
    `is_system`   varchar(1)   not null comment '是否是系统看板, 1系统看板, 0私有看板',
    `is_disable`  varchar(1)   not null comment '是否不显示, 0显示, 1不显示',
    `create_time` datetime     not null comment '创建时间',
    `update_time` datetime     not null comment '更新时间',
    primary key (`id`) using btree
) engine = innodb
  default charset utf8mb4
  collate utf8mb4_unicode_ci comment '面板配置表';

BEGIN;
INSERT INTO `tb_board`(`id`, `user_id`, `board_name`, `quota`, `device`, `is_system`, `is_disable`, `create_time`, `update_time`) VALUES (1, 1, '设备分布', '0', '0', '1', '0', '2021-10-27 11:11:36', '2021-10-27 11:11:39');
INSERT INTO `tb_board`(`id`, `user_id`, `board_name`, `quota`, `device`, `is_system`, `is_disable`, `create_time`, `update_time`) VALUES (2, 1, '异常设备监控', '0', '0', '1', '0', '2021-10-27 11:12:34', '2021-10-27 11:12:37');
INSERT INTO `tb_board`(`id`, `user_id`, `board_name`, `quota`, `device`, `is_system`, `is_disable`, `create_time`, `update_time`) VALUES (3, 1, '设备异常趋势', '0', '0', '1', '0', '2021-10-27 11:13:18', '2021-10-27 11:13:20');
INSERT INTO `tb_board`(`id`, `user_id`, `board_name`, `quota`, `device`, `is_system`, `is_disable`, `create_time`, `update_time`) VALUES (4, 1, '设备异常top10', '0', '0', '1', '0', '2021-10-27 13:46:28', '2021-10-27 13:46:31');
COMMIT;

drop table if exists `tb_gps`;
create table if not exists `tb_gps`
(
    `id`           bigint(20)  not null comment 'id',
    `subject`      varchar(50) not null comment '主题',
    `sn_key`       varchar(50) not null comment '设备识别码字段',
    `single_field` varchar(1)  not null comment '类型(单字段, 双字段), 1单字段, 2双字段',
    `value_key`    varchar(50) not null comment '经纬度字段',
    `separation`   varchar(10) not null comment '经纬度分隔符',
    `longitude`    varchar(20) not null comment '经度字段',
    `latitude`     varchar(20) not null comment '维度字段',
    `create_time`  datetime    not null comment '创建时间',
    `update_time`  datetime    not null comment '更新时间',
    primary key (`id`) using btree
) engine = innodb
  default charset utf8mb4
  collate utf8mb4_unicode_ci comment 'GPS配置表';

BEGIN;
INSERT INTO `tb_gps` VALUES (1, 'device/geo', 'sn', '2', 'gps', ',', 'lon', 'lat', '2021-10-14 15:05:39', '2021-10-14 15:05:44');
COMMIT;