FROM mysql:5.7 as production-stage
COPY ./dinky-doc/sql/dinky.sql /docker-entrypoint-initdb.d/01-dinky.sql
COPY ./dinky-doc/sql/dinky_history.sql /docker-entrypoint-initdb.d/02-dinky_history.sql
