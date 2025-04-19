SELECT add_column_if_not_exists('public','dinky_cluster', 'config', 'varchar(255)', 'null', 'A JSON string that contains configuration information such as the ResourceManager and ApplicationID');
