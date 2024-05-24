
-- 创建存储过程/函数 用于添加表字段时判断字段是否存在, 如果字段不存在则添加字段, 如果字段存在则不执行任何操作,避免添加重复字段时抛出异常,从而终止Flyway执行, 在 Flyway 执行时, 如果你需要增加字段,必须使用该存储过程
-- Create a stored procedure to determine whether a field exists when adding table fields. If the field does not exist, add it. If the field exists, do not perform any operations to avoid throwing exceptions when adding duplicate fields. When executing in Flyway, if you need to add a field, you must use this stored procedure

CREATE OR REPLACE FUNCTION add_column_if_not_exists(model text, p_table_name text, p_column_name text, p_data_type text, p_default_value text, p_comment text)
RETURNS VOID AS $$
BEGIN
    IF NOT EXISTS (
      SELECT 1
      FROM   information_schema.columns
      WHERE  table_schema = model
      AND    table_name = p_table_name
      AND    column_name = p_column_name
   ) THEN
        EXECUTE format('ALTER TABLE %I ADD COLUMN %I %s DEFAULT %s', p_table_name, p_column_name, p_data_type, p_default_value);
        EXECUTE format('COMMENT ON COLUMN %I.%I IS %L', p_table_name, p_column_name, p_comment);
        RAISE NOTICE 'Column % added to table %.', p_column_name, p_table_name;
ELSE
      RAISE NOTICE 'Column % already exists in table %.', p_column_name, p_table_name;
END IF;
END;
$$ LANGUAGE plpgsql;

-- 创建函数/操作符用于比较整数和布尔值 用于解决PostgreSQL 查询时报错 `operator does not exist: boolean = integer` 的问题
-- Create a function to compare integers and booleans to solve the problem of `operator does not exist: boolean = integer` when querying in PostgreSQL
CREATE OR REPLACE FUNCTION equal_int_bool(x int, y bool)
    RETURNS BOOLEAN AS $$
begin
    return x = y::int;
end;
$$ LANGUAGE PLPGSQL IMMUTABLE STRICT;

-- 创建操作符 用于比较整数和布尔值 用于解决PostgreSQL 查询时报错 `operator does not exist: boolean = integer` 的问题
-- Create an operator to compare integers and booleans to solve the problem of `operator does not exist: boolean = integer` when querying in PostgreSQL
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_operator
            WHERE oprname = '=' AND
                oprleft = 'integer'::regtype AND
                oprright = 'boolean'::regtype
        ) THEN
            CREATE OPERATOR = (
                leftarg = INTEGER,
                rightarg = BOOLEAN,
                procedure = equal_int_bool);
        END IF;
    END;
$$ LANGUAGE plpgsql;


-- 创建函数/操作符用于比较整数和布尔值 用于解决PostgreSQL 查询时报错 `operator does not exist: boolean = integer` 的问题
-- Create a function to compare integers and booleans to solve the problem of `operator does not exist: boolean = integer` when querying in PostgreSQL
CREATE OR REPLACE FUNCTION equal_bool_int(x bool, y int)
    RETURNS BOOLEAN AS $$
begin
    return x::int = y;
end;
$$ LANGUAGE PLPGSQL IMMUTABLE STRICT;

-- 创建操作符 用于比较整数和布尔值 用于解决PostgreSQL 查询时报错 `operator does not exist: boolean = integer` 的问题
-- Create an operator to compare integers and booleans to solve the problem of `operator does not exist: boolean = integer` when querying in PostgreSQL
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_operator
            WHERE oprname = '=' AND
                oprleft = 'boolean'::regtype AND
                oprright = 'integer'::regtype
        ) THEN
            CREATE OPERATOR = (
                leftarg = BOOLEAN,
                rightarg = INTEGER,
                procedure = equal_bool_int);
        END IF;
    END;
$$ LANGUAGE plpgsql;




update public.dinky_sys_menu
set "path"='/registration/alert/rule',
    "component"='./RegCenter/Alert/AlertRule',
    "perms"='registration:alert:rule',
    "parent_id"=12
where "id" = 116;

update "public"."dinky_sys_menu"
set "path"='/registration/alert/rule/add',
    "perms"='registration:alert:rule:add'
where "id" = 117;

update public.dinky_sys_menu
set "path"='/registration/alert/rule/delete',
    "perms"='registration:alert:rule:delete'
where "id" = 118;

update public.dinky_sys_menu
set "path"='/registration/alert/rule/edit',
    "perms"='registration:alert:rule:edit'
where "id" = 119;



-- Increase class_name column's length from 50 to 100.
ALTER TABLE public.dinky_udf_manage ALTER COLUMN class_name TYPE VARCHAR(100);
COMMENT ON COLUMN public.dinky_udf_manage."class_name" IS 'Complete class name';


SELECT add_column_if_not_exists('public','dinky_task', 'first_level_owner', 'int', 'null', 'primary responsible person id');
SELECT add_column_if_not_exists('public','dinky_task', 'second_level_owners', 'varchar(128)', 'null', 'list of secondary responsible persons ids');


update public.dinky_task set "first_level_owner" = "creator";

UPDATE public.dinky_user SET "password" = 'f4b3a484ee745b98d64cd69c429b2aa2' WHERE "id" =1 and "password"= '21232f297a57a5a743894a0e4a801fc3';

ALTER TABLE public.dinky_resources ALTER COLUMN file_name TYPE TEXT;

SELECT add_column_if_not_exists('public','dinky_udf_manage', 'language', 'varchar(10)', 'null', 'udf language');

UPDATE
    dinky_udf_manage duml
SET
    "language" =
        CASE
            WHEN r.file_name LIKE '%.zip' OR r.file_name LIKE '%.py' THEN 'python'
            WHEN r.file_name LIKE '%.jar' THEN 'java'
            ELSE 'unknown'
            END
    FROM dinky_resources r
WHERE
    duml.resources_id = r.id;
