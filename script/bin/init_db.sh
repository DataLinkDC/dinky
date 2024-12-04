#!/bin/bash

DINKY_HOME=$1

echo -e "${GREEN}====================== The database configuration file is initialized ======================${RESET}"

if [ -z "$DINKY_HOME" ]; then
  echo -e "${RED}The parameter is wrong, please check!${RESET}"
  exit 1
fi

while true; do
  read -p "Please select a database type (1.MySQL 2.PostgresSQL)：" db_type
  read -p "Please enter the database address (hostname or IP, default localhost): " -i "localhost" db_host
  read -p "Please enter the database port (default 3306): " -i 3306 db_port
  read -p "Please enter a database name (default dinky): " -i "dinky" db_name
  read -p "Please enter the database username (default dinky):" -i "dinky" db_username
  read -s -p "Please enter the database password (default dinky):" -i "dinky" db_password
  echo

  db_type=$(echo "$db_type" |  tr -d '[:space:]')
  db_host=$(echo "$db_host" | tr -d '[:space:]')
  db_port=$(echo "$db_port" | tr -d '[:space:]')
  db_name=$(echo "$db_name" |  tr -d '[:space:]')
  db_username=$(echo "$db_username"  | tr -d '[:space:]')
  db_password=$(echo "$db_password"  | tr -d '[:space:]')

  case $db_type in
    1)
      echo -e "${YELLOW}Configuring MySQL database related information...${RESET}"
      config_file="${DINKY_HOME}/config/application-mysql.yml"
      echo -e "${GREEN} The automatic initialization script uses the export environment variable method to support the loading of environment variables of the data source. The configuration file is：${config_file} ${RESET}"
      echo "export DB_ACTIVE=mysql" >> /etc/profile
      echo "export MYSQL_ADDR=${db_host}:${db_port}" >> /etc/profile
      echo "export MYSQL_DATABASE=${db_name}" >> /etc/profile
      echo "export MYSQL_USERNAME=${db_username}" >> /etc/profile
      echo "export MYSQL_PASSWORD=${db_password}" >> /etc/profile
      source /etc/profile

      echo -e "${GREEN}MySQLThe configuration of database related information is completed. Please confirm whether the following configuration is correct：${RESET}"
      grep -E '^(export DB_ACTIVE|export MYSQL_ADDR|export MYSQL_DATABASE|export MYSQL_USERNAME|export MYSQL_PASSWORD)' /etc/profile | grep -v "^#" | grep -v "^$"
      break
      ;;
    2)
      echo -e "${YELLOW}Configuring PostgresSQL database related information...${RESET}"
      config_file="${DINKY_HOME}/config/application-pgsql.yml"

      echo -e "${GREEN}The automatic initialization script uses the export environment variable method to support the loading of environment variables from the data source configuration file. The configuration file is：${config_file} ${RESET}"

      echo "export DB_ACTIVE=pgsql" >> /etc/profile
      echo "export POSTGRES_ADDR=${db_host}:${db_port}" >> /etc/profile
      echo "export POSTGRES_DB=${db_name}" >> /etc/profile
      echo "export POSTGRES_USER=${db_username}" >> /etc/profile
      echo "export POSTGRES_PASSWORD=${db_password}" >> /etc/profile
      source /etc/profile

      echo -e "${GREEN}PostgresSQL The configuration of database related information is completed. Please confirm whether the following configuration is correct：${RESET}"
      grep -E '^(export DB_ACTIVE|export POSTGRES_ADDR|export POSTGRES_DB|export POSTGRES_USER|export POSTGRES_PASSWORD)' /etc/profile | grep -v "^#" | grep -v "^$"
      break
      ;;
    *)
      echo -e "${RED}The entered database type is incorrect, please select the correct database type again.${RESET}"
      ;;
  esac
done