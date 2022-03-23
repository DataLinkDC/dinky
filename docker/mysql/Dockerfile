FROM mysql:5.7 as production-stage
COPY ./dlink-doc/sql/dlink.sql /docker-entrypoint-initdb.d/01-dlink.sql
COPY ./dlink-doc/sql/dlink_history.sql /docker-entrypoint-initdb.d/02-dlink_history.sql
