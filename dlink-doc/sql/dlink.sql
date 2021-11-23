create table if not exists dlink_catalogue
(
	id int auto_increment comment 'ID'
		primary key,
	task_id int null comment '任务ID',
	name varchar(100) not null comment '名称',
	type varchar(50) null comment '类型',
	parent_id int default 0 not null comment '父ID',
	enabled tinyint(1) default 1 not null comment '启用',
	is_leaf tinyint(1) not null comment '是否为文件夹',
	create_time datetime null comment '创建时间',
	update_time datetime null comment '最近修改时间',
	constraint idx_name
		unique (name, parent_id)
)
comment '目录';

create table if not exists dlink_cluster
(
	id int auto_increment comment 'ID'
		primary key,
	name varchar(255) not null comment '名称',
	alias varchar(255) null comment '别名',
	type varchar(50) null comment '类型',
	hosts text null comment 'HOSTS',
	job_manager_host varchar(255) null comment 'JMhost',
	version varchar(20) null comment '版本',
	status int(1) null comment '状态',
	note varchar(255) null comment '注释',
	enabled tinyint(1) default 1 not null comment '是否启用',
	create_time datetime null comment '创建时间',
	update_time datetime null comment '更新时间',
	constraint idx_name
		unique (name)
)
comment '集群';

create table if not exists dlink_cluster_configuration
(
	id int auto_increment comment 'ID'
		primary key,
	name varchar(255) not null comment '名称',
	alias varchar(255) null comment '别名',
	type varchar(50) null comment '类型',
	config_json text null comment '配置JSON',
	is_available varchar(255) null comment '是否可用',
	note varchar(255) null comment '注释',
	enabled tinyint(1) default 1 not null comment '是否启用',
	create_time datetime null comment '创建时间',
	update_time datetime null comment '更新时间'
);

create table if not exists dlink_database
(
	id int auto_increment comment 'ID'
		primary key,
	name varchar(30) not null comment '数据源名',
	alias varchar(50) not null comment '数据源标题',
	group_name varchar(255) default 'Default' null comment '数据源分组',
	type varchar(50) not null comment '类型',
	ip varchar(255) null comment 'IP',
	port int null comment '端口号',
	url varchar(255) null comment 'url',
	username varchar(50) null comment '用户名',
	password varchar(50) null comment '密码',
	note varchar(255) null comment '注释',
	auto_registers tinyint(1) default 0 null comment '是否自动注册',
	db_version varchar(255) null comment '版本，如oracle的11g，hbase的2.2.3',
	status tinyint(1) default 0 null comment '状态',
	health_time datetime null comment '最近健康时间',
	heartbeat_time datetime null comment '最近心跳监测时间',
	enabled tinyint(1) default 1 not null comment '启用',
	create_time datetime null comment '创建时间',
	update_time datetime null comment '最近修改时间',
	constraint db_index
		unique (name)
);

create table if not exists dlink_flink_document
(
	id int auto_increment comment '主键'
		primary key,
	category varchar(255) null comment '文档类型',
	type varchar(255) null comment '类型',
	subtype varchar(255) null comment '子类型',
	name varchar(255) null comment '信息',
	description varchar(255) null comment '描述',
	fill_value varchar(255) null comment '填充值',
	version varchar(255) null comment '版本号',
	like_num int(255) default 0 null comment '喜爱值',
	enabled tinyint(1) default 0 not null comment '是否启用',
	create_time datetime null comment '创建时间',
	update_time datetime null comment '更新时间'
)
comment '文档管理';

create table if not exists dlink_history
(
	id int auto_increment comment 'ID'
		primary key,
	cluster_id int default 0 not null comment '集群ID',
	cluster_configuration_id int null,
	session varchar(255) null comment '会话',
	job_id varchar(50) null comment 'JobID',
	job_name varchar(255) null comment '作业名',
	job_manager_address varchar(255) null comment 'JM地址',
	status int(1) default 0 not null comment '状态',
	type varchar(50) null comment '类型',
	statement text null comment '语句集',
	error text null comment '异常信息',
	result text null comment '结果集',
	config text null comment '配置',
	start_time datetime null comment '开始时间',
	end_time datetime null comment '结束时间',
	task_id int null comment '作业ID'
)
comment '执行历史';

create index cluster_index
	on dlink_history (cluster_id);

create index task_index
	on dlink_history (task_id);

create table if not exists dlink_jar
(
	id int auto_increment comment 'ID'
		primary key,
	name varchar(255) not null comment '名称',
	alias varchar(255) null comment '别名',
	type varchar(50) null comment '类型',
	path text null comment '路径',
	main_class varchar(255) null comment '启动类',
	paras varchar(255) null comment '启动类入参',
	note varchar(255) null comment '注释',
	enabled tinyint(1) default 1 not null comment '是否启用',
	create_time datetime null comment '创建时间',
	update_time datetime null comment '更新时间'
);

create table if not exists dlink_schema_history
(
	installed_rank int not null
		primary key,
	version varchar(50) null,
	description varchar(200) not null,
	type varchar(20) not null,
	script varchar(1000) not null,
	checksum int null,
	installed_by varchar(100) not null,
	installed_on timestamp default CURRENT_TIMESTAMP not null,
	execution_time int not null,
	success tinyint(1) not null
);

create index dlink_schema_history_s_idx
	on dlink_schema_history (success);

create table if not exists dlink_sys_config
(
	id int auto_increment comment 'ID'
		primary key,
	name varchar(255) not null comment '配置名',
	value text null comment '值',
	create_time datetime null comment '创建时间',
	update_time datetime null comment '更新时间'
);

create table if not exists dlink_task
(
	id int auto_increment comment 'ID'
		primary key,
	name varchar(255) not null comment '名称',
	alias varchar(255) null comment '别名',
	type varchar(50) null comment '类型',
	check_point int null comment 'CheckPoint ',
	save_point_path varchar(255) null comment 'SavePointPath',
	parallelism int(4) null comment 'parallelism',
	fragment tinyint(1) null comment 'fragment',
	statement_set tinyint(1) null comment '启用语句集',
	cluster_id int null comment 'Flink集群ID',
	cluster_configuration_id int null comment '集群配置ID',
	config text null comment '配置',
	note varchar(255) null comment '注释',
	enabled tinyint(1) default 1 not null comment '是否启用',
	create_time datetime null comment '创建时间',
	update_time datetime null comment '更新时间',
	constraint idx_name
		unique (name)
)
comment '作业';

create table if not exists dlink_task_statement
(
	id int not null comment 'ID'
		primary key,
	statement text null comment '语句'
)
comment '语句';

