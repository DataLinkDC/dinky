drop table if exists `metadata_database`
create  table if not exists `metadata_database` (
  `id` int(11) not null AUTO_INCREMENT COMMENT '主键',
  `database_name` varchar(255) NOT NULL COMMENT '名称',
  `description` varchar(255) null comment'描述',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='元数据对象信息'

drop table if exists `metadata_table`
create  table if not exists `metadata_table` (
  `id` int(11) not null AUTO_INCREMENT COMMENT '主键',
  `table_name` varchar(255) NOT NULL COMMENT '名称',
  `table_type` varchar(255) NOT null comment '对象类型，分为：database 和 table view',
  `database_id` int(11) not null COMMENT '数据库主键',
  `description` varchar(255) null comment'描述',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='元数据对象信息'

drop table if exists `metadata_database_property`
create  table if not exists `metadata_database_property` (
  `key` varchar(255) NOT NULL COMMENT '属性key',
  `value` varchar(255) NULL COMMENT '属性value',
  `database_id` int(11) not null COMMENT '数据库主键',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`key`, `database_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='元数据属性信息'

drop table if exists `metadata_table_property`
create  table if not exists `metadata_table_property` (
  `key` varchar(255) NOT NULL COMMENT '属性key',
  `value` varchar(255) NULL COMMENT '属性value',
  `table_id` int(11) not null COMMENT '数据表名称',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`key`, `table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='元数据属性信息'

drop table if exists metadata_column
create  table if not exists `metadata_column` (
  `column_name` varchar(255) NOT NULL COMMENT '列名',
  `column_type` varchar(255) NOT NULL COMMENT '列类型, 有Physical Metadata Computed WATERMARK ',
  `data_type` varchar(255) NOT NULL COMMENT '数据类型',
  `expr` varchar(255) NULL COMMENT '表达式',
  `description` varchar(255) NOT NULL COMMENT '字段描述',
  `table_id` int(11) not null COMMENT '数据表名称',
  `primary` bit null comment '主键',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`table_id`,`column_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据列信息'

drop table if exists `metadata_function`
create  table if not exists `metadata_function` (
  `id` int(11) not null AUTO_INCREMENT COMMENT '主键',
  `function_name` varchar(255) NOT NULL COMMENT '名称',
  `class_name` varchar(255) NOT null comment '类名',
  `database_id` int(11) not null COMMENT '数据库主键',
  `function_language` varchar(255) null comment'UDF语言',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='UDF信息'