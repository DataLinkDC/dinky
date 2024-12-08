#!/bin/bash

DINKY_HOME_PARAMS=$1
DB_ENV_FILE=$2

echo -e "${GREEN}====================== The database configuration file is initialized ======================${RESET}"

if [ -z "$DINKY_HOME_PARAMS" ]; then
  echo -e "${RED}The parameter is wrong, please check!${RESET}"
  exit 1
fi

while true; do
  read -e -p "Please select a database type (1.MySQL 2.PostgresSQL)：" db_type
  while [ -z "$db_type" ]; do
     read -e -p "Please select a database type (1.MySQL 2.PostgresSQL)：" db_type
  done
  read -e -p "Please enter the database address (hostname or IP, default localhost): " -i "localhost" db_host
  read -p "Please enter the database port : " db_port
  read -e -p "Please enter a database name (default dinky): " -i "dinky" db_name
  read -e -p "Please enter the database username (default dinky):" -i "dinky" db_username
  read -s -p "Please enter the database password (default dinky):"  db_password
  echo


  db_type=$(echo ${db_type} |  tr -d '[:space:]')
  db_host=$(echo ${db_host:-"localhost"} | tr -d '[:space:]')
  db_name=$(echo ${db_name:-"dinky"}|  tr -d '[:space:]')
  db_username=$(echo ${db_username:-"dinky"}  | tr -d '[:space:]')
  db_password=$(echo ${db_password:-"dinky"}  | tr -d '[:space:]')
  if [ "$db_type" == 1 ]; then
     db_port=$(echo ${db_port:-"3306"} | tr -d '[:space:]')
  else
    db_port=$(echo ${db_port:-"5432"} | tr -d '[:space:]')
  fi


  case $db_type in
    1)
      echo -e "${YELLOW}Configuring MySQL database related information...${RESET}"
      config_file="${DINKY_HOME_PARAMS}/config/application-mysql.yml"
      echo -e "${GREEN} The automatic initialization script uses the export environment variable method to support the loading of environment variables of the data source. The configuration file is：${config_file} ${RESET}"

      add_to_env "DB_ACTIVE" "mysql" "$DB_ENV_FILE"
      add_to_env "MYSQL_ADDR" "${db_host}:${db_port}" "$DB_ENV_FILE"
      add_to_env "MYSQL_DATABASE" "${db_name}" "$DB_ENV_FILE"
      add_to_env "MYSQL_USERNAME" "${db_username}" "$DB_ENV_FILE"
      add_to_env "MYSQL_PASSWORD" "${db_password}" "$DB_ENV_FILE"


      sleep 2
      source "$DB_ENV_FILE"

      echo -e "${GREEN}MySQLThe configuration of database related information is completed. Please confirm whether the following configuration is correct：${RESET}"
      grep -E '^(export DB_ACTIVE|export MYSQL_ADDR|export MYSQL_DATABASE|export MYSQL_USERNAME|export MYSQL_PASSWORD)' $DB_ENV_FILE | grep -v "^#" | grep -v "^$"
      break
      ;;
    2)
       echo -e "${YELLOW}Configuring PostgresSQL database related information...${RESET}"
       config_file="${DINKY_HOME_PARAMS}/config/application-postgresql.yml"

        echo -e "${GREEN}The automatic initialization script uses the export environment variable method to support the loading of environment variables from the data source configuration file. The configuration file is：${config_file} ${RESET}"

       add_to_env "DB_ACTIVE" "postgresql" "$DB_ENV_FILE"
       add_to_env "POSTGRES_ADDR" "${db_host}:${db_port}" "$DB_ENV_FILE"
       add_to_env "POSTGRES_DB" "${db_name}" "$DB_ENV_FILE"
       add_to_env "POSTGRES_USER" "${db_username}" "$DB_ENV_FILE"
       add_to_env "POSTGRES_PASSWORD" "${db_password}" "$DB_ENV_FILE"
       sleep 2
       source $DB_ENV_FILE

       echo -e "${GREEN}PostgresSQL The configuration of database related information is completed. Please confirm whether the following configuration is correct：${RESET}"
       grep -E '^(export DB_ACTIVE|export POSTGRES_ADDR|export POSTGRES_DB|export POSTGRES_USER|export POSTGRES_PASSWORD)' $DB_ENV_FILE | grep -v "^#" | grep -v "^$"

       break
      ;;
    *)
      echo -e "${RED}The entered database type is incorrect, please select the correct database type again.${RESET}"
      ;;
  esac
done