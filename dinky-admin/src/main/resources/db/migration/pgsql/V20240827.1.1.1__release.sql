CREATE OR REPLACE FUNCTION public.drop_index_if_exists(
    schema_name text,
    index_name text,
    table_name text
)
    RETURNS void AS $$
DECLARE
    index_exists int;
BEGIN
    -- Check if the index exists
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


CREATE TABLE IF NOT EXISTS public.dinky_dashboard
(
    id               SERIAL PRIMARY KEY          NOT NULL,
    name             VARCHAR(255),
    remark TEXT,
    chart_theme      VARCHAR(255),
    layouts          TEXT,
    create_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN public.dinky_dashboard.id IS 'ID';
COMMENT ON COLUMN public.dinky_dashboard.name IS 'name';
COMMENT ON COLUMN public.dinky_dashboard.remark IS 'remark';
COMMENT ON COLUMN public.dinky_dashboard.chart_theme IS 'chart theme';
COMMENT ON COLUMN public.dinky_dashboard.layouts IS 'layouts';
COMMENT ON COLUMN public.dinky_dashboard.create_time IS 'create time';
COMMENT ON COLUMN public.dinky_dashboard.update_time IS 'update time';

CREATE OR REPLACE TRIGGER update_dinky_dashboard_update_time
              BEFORE UPDATE
                         ON public.dinky_dashboard
                         FOR EACH ROW
EXECUTE FUNCTION trigger_set_timestamp();