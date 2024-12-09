#!/bin/bash

export RED='\033[31m'
export GREEN='\033[32m'
export YELLOW='\033[33m'
export BLUE='\033[34m'
export MAGENTA='\033[35m'
export CYAN='\033[36m'
export RESET='\033[0m'

if [ -f /etc/profile.d/dinky_env ]; then
    source /etc/profile.d/dinky_env
fi

if [ -z "$DINKY_HOME" ]; then
  echo -e "${RED}DINKY_HOME is not set, please check the environment variable...${RESET}"
  exit 1
else
  EXTENDS_HOME="$DINKY_HOME"/extends
  FLINK_VERSION_SCAN=$(ls -n "${EXTENDS_HOME}" | grep '^d' | grep flink | awk -F 'flink' '{print $2}')

  echo -e "${GREEN}>>>>>>>>>>>>>>>>>>>. Start cleaning up the environment... <<<<<<<<<<<<<<<<<<<<${RESET}"
  echo -e "${GREEN}Cleaning up the environment variables...${RESET}"
  rm -rf /etc/profile.d/dinky_*
  echo -e "${GREEN}Cleaning up the flink jar dependencies...${RESET}"
  rm -rf  "${EXTENDS_HOME}"/flink"${FLINK_VERSION_SCAN}"/flink-*
  echo -e "${GREEN}Cleaning up the flink shaded hadoop jar dependencies...${RESET}"
  rm -rf  "${EXTENDS_HOME}"/flink-shaded-hadoop-*
  echo -e "${GREEN}Cleaning up the mysql jar dependencies...${RESET}"
  rm -rf "$DINKY_HOME"/lib/mysql-connector-*
  echo -e "${GREEN}Cleaning up the environment variables of DINKY_HOME ...${RESET}"
  unset DINKY_HOME
  echo -e "${GREEN}Refresh environment variables...${RESET}"
  source /etc/profile
  echo -e "${GREEN}Environment cleanup completed...${RESET}"
fi
