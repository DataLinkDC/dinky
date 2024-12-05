#!/bin/bash
set -x

DINKY_HOME_PARAMS=$1
ENV_FILE=$2


echo -e "${GREEN}Start configuring DINKY_HOME environment variable...${RESET}"

echo -e "${GREEN}DINKY_HOME environment variable has not been configured, please choose whether to configure.${RESET}"
echo -e "${GREEN}1. Use the automatically obtained DINKY_HOME environment variable${RESET}"
echo -e "${GREEN}2. Manually enter the path of DINKY_HOME${RESET}"
echo -e "${GREEN}3. Cancel configuration${RESET}"
while true; do
    read -p "Please enter your choice(1/2/3):" DINKY_HOME_CHOICE
    case $DINKY_HOME_CHOICE in
      1)
        echo -e "${GREEN}Use the automatically obtained DINKY_HOME environment variable${RESET}"
        echo -e "${GREEN} The currently obtained path is: $DINKY_HOME_PARAMS to perform automatic configuration${RESET}"
        add_to_env "DINKY_HOME" "$DINKY_HOME_PARAMS" "$ENV_FILE"
        add_to_env "PATH" "\$DINKY_HOME/bin" "$ENV_FILE"
        sleep 2
        source $ENV_FILE
        echo -e "${GREEN}DINKY_HOME environment variable configuration completed. Please confirm whether the following configuration is correct：${RESET}"
        grep -E '^(export DINKY_HOME)' $ENV_FILE | grep -v "^#" | grep -v "^$"
        break
        ;;
      2)
        read -p "Please enter the path of DINKY_HOME:" dinky_home_path
        dinky_home_path=$(echo "$dinky_home_path" | tr -d '[:space:]')
        if [ ! -d "$dinky_home_path" ]; then
          echo -e "${RED}The path does not exist, please re-enter${RESET}"
          read -p "Please enter the path of DINKY_HOME:" dinky_home_path
        else
            echo -e "${GREEN}The path you entered is: $dinky_home_path${RESET}"
            add_to_env "DINKY_HOME" "$dinky_home_path" "$ENV_FILE"
            add_to_env "PATH" "\$DINKY_HOME/bin" "$ENV_FILE"
            sleep 2
            source $ENV_FILE
            echo -e "${GREEN}DINKY_HOME environment variable configuration completed. Please confirm whether the following configuration is correct：${RESET}"
            grep -E '^(export DINKY_HOME)' $ENV_FILE | grep -v "^#" | grep -v "^$"
        fi
        break
        ;;
      3)
        echo -e "${GREEN}Cancel configuration${RESET}"
        break
        ;;
      *)
        echo -e "${RED}Invalid input, please re-run the script to select the correct option.${RESET}"
        ;;
      esac
done