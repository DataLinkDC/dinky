CREATE OR REPLACE FUNCTION public.drop_index_if_exists(
    schema_name text,
    index_name text,
    table_name text
)
    RETURNS void AS $$
DECLARE
    index_exists int;
BEGIN
    -- 检查索引是否存在
    SELECT count(*)
    INTO index_exists
    FROM pg_indexes
    WHERE schemaname = schema_name
      AND tablename = table_name
      AND indexname = index_name;

    -- 如果索引存在，则删除它
    IF index_exists > 0 THEN
        EXECUTE format('DROP INDEX %I.%I', quote_ident(schema_name), quote_ident(index_name));
    END IF;
END;
$$ LANGUAGE plpgsql;


SELECT public.drop_index_if_exists('public', 'cluster_un_idx1', 'dinky_cluster');