SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `system_user`;
CREATE TABLE `system_user` (
  `id` int(11) NOT NULL auto_increment,
  `timestamp` timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `name` varchar(120) default NULL,
  `contactway` varchar(255) default '',
  `username` varchar(48) default '',
  `password` varchar(48) default '',
  `enabled` tinyint(1) default '0' COMMENT '1 for YES or 0 for NO',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
ALTER TABLE `system_user` ADD INDEX  system_user_index_name(`name`);

DROP TABLE IF EXISTS `client_user`;
CREATE TABLE `client_user`(
  `id` int(11) NOT NULL auto_increment,
  `timestamp` timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `username` varchar(120) default NULL,
  `contactway` varchar(255) default '',
  `usermac` varchar(40) not NULL,
  PRIMARY KEY  (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DROP TABLE IF EXISTS `feedback_info`;
CREATE TABLE `feedback_info`(
  `id` int(11) NOT NULL auto_increment,
  `timestamp` timestamp default CURRENT_TIMESTAMP,
  `content` varchar(255) default '',
  `user_mac` varchar(40) default '',
  `status` varchar(10) default '',
  `fd_year`  char(4) default '',
  `fd_month`  char(2) default '',
  `fd_day`  char(2) default '',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



DROP TABLE IF EXISTS `tv_channel_info`;
CREATE TABLE `tv_channel_info`(
  `id` int(11) NOT NULL auto_increment,
  `timestamp` timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `tv_channel_name` varchar(255) default '',
  `tv_program_name` varchar(255) default '',
  `user_mac` varchar(255) default '',
  `appkey` varchar(255) default '',
  `status` varchar(10) default '',
  `fd_year`  varchar(255) default '',
  `fd_month`  varchar(255) default '',
  `fd_day`  varchar(255) default '',
  PRIMARY KEY(`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;




SET FOREIGN_KEY_CHECKS=0;




















