#!/bin/bash

export RED='\033[31m'
export GREEN='\033[32m'
export YELLOW='\033[33m'
export BLUE='\033[34m'
export MAGENTA='\033[35m'
export CYAN='\033[36m'
export RESET='\033[0m'

# debug mode: true or false If empty, defaults to false
USE_DEBUG=$1

if [ -z "${USE_DEBUG}" ]; then
    USE_DEBUG="false"
    echo -e "${YELLOW}Debug mode is not enabled, if you need to enable debug mode, please add the first parameter 'true' when executing the script. example: init_tools_main.sh true $RESET"
elif [ "${USE_DEBUG}" = "true" ]; then
    set -x
fi

ENV_FILE="/etc/profile.d/dinky_env"
if [ -f "${ENV_FILE}" ]; then
    source "${ENV_FILE}"
else
    echo "export PATH=/bin:/usr/bin:\$PATH" > "${ENV_FILE}" && source "${ENV_FILE}"
fi

DB_ENV_FILE="/etc/profile.d/dinky_db"
if [ -f "${DB_ENV_FILE}" ]; then
    source "${DB_ENV_FILE}"
else
    echo "export PATH=/bin:/usr/bin:\$PATH" > "${DB_ENV_FILE}" && source "${DB_ENV_FILE}"
fi
chmod 755 $ENV_FILE
chmod 755 $DB_ENV_FILE


echo -e "${GREEN}=====================================================================${RESET}"
echo -e "${GREEN}=====================================================================${RESET}"
echo -e "${GREEN}============ Welcome to the Dinky initialization script =============${RESET}"
echo -e "${GREEN}======================================================================${RESET}"
echo -e "${GREEN}======================================================================${RESET}"


RETURN_HOME_PATH=""
function get_home_path() {
    SOURCE="${BASH_SOURCE[0]}"
    while [ -h "$SOURCE" ]; do
        DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
        SOURCE="$(readlink "$SOURCE")"
        [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
    done
    DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
    local possible_path=$(dirname "$DIR")
    if [ -d "$possible_path" ]; then  # Verify that the path obtained is a directory, increasing robustness
        RETURN_HOME_PATH="$possible_path"
    else
        echo -e "${RED}Calculated path $possible_path is not a valid directory. Please check the script location or deployment setup.${RESET}"
        exit 1
    fi
}

get_home_path
APP_HOME="${RETURN_HOME_PATH}"

echo -e "${GREEN}Dinky root path: ${APP_HOME} ${RESET}"

sudo chmod +x "${APP_HOME}"/bin/init_*.sh

EXTENDS_HOME="${APP_HOME}/extends"
if [ ! -d "${EXTENDS_HOME}" ]; then
    echo -e "${RED} ${EXTENDS_HOME} Directory does not exist, please check${RESET}"
    exit 1
fi

FLINK_VERSION_SCAN=$(ls -n "${EXTENDS_HOME}" | grep '^d' | grep flink | awk -F 'flink' '{print $2}')
if [ -z "${FLINK_VERSION_SCAN}" ]; then
    echo -e "${RED}There is no Flink related version in ${EXTENDS_HOME} in the directory where Dinky is deployed. The initialization operation cannot be performed. Please check. ${RESET}"
    exit 1
fi

DINKY_TMP_DIR="${APP_HOME}/tmp"
if [ ! -d "${DINKY_TMP_DIR}" ]; then
    echo -e "${YELLOW}Create temporary directory ${DINKY_TMP_DIR}...${RESET}"
    mkdir -p "${DINKY_TMP_DIR}"
    echo -e "${GREEN}The temporary directory is created${RESET}"
fi

# LIB
DINKY_LIB="${APP_HOME}/lib"
if [ ! -d "${DINKY_LIB}" ]; then
    echo -e "${RED}${DINKY_LIB} Directory does not exist, please check. ${RESET}"
    exit 1
fi

# Function: Check whether the command exists, if not, try to install it
check_command() {
    local cmd="$1"
    echo -e "${BLUE}Check if command: $cmd exists...${RESET}"
    if ! command -v "$cmd" &> /dev/null; then
        if [ "$cmd" == "yum" ]; then
            echo -e "${YELLOW} Try using yum to install the missing command...${RESET}"
            sudo yum install -y "$cmd"
        elif [ "$cmd" == "apt-get" ]; then
            echo -e "${YELLOW}Try using apt-get to install the missing command...${RESET}"
            sudo apt-get install -y "$cmd"
        else
            echo -e "${RED} $cmd The command was not found. Please install it manually and then run this script.。${RESET}"
            exit 1
        fi
    fi
    echo -e "${GREEN}========== Command $cmd check completed. OK, continue executing the script. ==========${RESET}"
}

# Use source transparent transmission in debug mode
source "${APP_HOME}/bin/init_check_network.sh"

check_command "wget"

echo -e "${GREEN}The pre-check is completed. Welcome to use the Dinky initialization script. The current Dinky root path is：${APP_HOME} ${RESET}"

function download_file() {
    source_url=$1
    target_file_dir=$2
    echo -e "${GREEN}Start downloading $source_url to $target_file_dir...${RESET}"
    wget -P "${target_file_dir}" "${source_url}"
    if [ $? -ne 0 ]; then
        echo -e "${RED}Failed to download $source_url to $target_file_dir. Please check the network connection and try again.${RESET}"
        return 1
    fi
    echo -e "${GREEN}Download completed. The downloaded file storage address is: $target_file_dir ${RESET}"
    return 0
}

export -f download_file

add_to_env() {
    local var_name=$1
    local var_value=$2
    local file=$3
    echo "Adding $var_name to $file"
    echo "export $var_name=\"$var_value\"" >> "$file"
#
#    if ! grep -q "^export $var_name=" "$file"; then
#        echo "Adding $var_name to $file"
#        echo "export $var_name=\"$var_value\"" >> "$file"
#    else
#        echo "$var_name already exists in $file, skipping."
#    fi
}
export -f add_to_env



echo
echo

function init_env() {
    while true; do
        read -p "Do you need to configure the DINKY_HOME environment variable? (yes/no)：" is_init_dinky_home
        is_init_dinky_home=$(echo "$is_init_dinky_home" | tr '[:upper:]' '[:lower:]' | tr -d '[:space:]')
        case $is_init_dinky_home in
          yes | y)
            # Use source transparent transmission in debug mode
            source "${APP_HOME}"/bin/init_env.sh ${APP_HOME} ${ENV_FILE}
            echo -e "${GREEN}DINKY_HOME environment variable configuration completed. the configuration file is：${ENV_FILE} ${RESET}"
            break
            ;;
          no | n)
            echo -e "${GREEN}Skip DINKY_HOME environment variable configuration.${RESET}"
            break
            ;;
          *)
            echo -e "${RED}The entered value is incorrect, please rerun the script to select the correct value.${RESET}"
            ;;
        esac
      done
}



echo -e "${GREEN} ====================== Environment variable initialization script -> Start ====================== ${RESET}"
DINKY_HOME_TMP=$(echo $DINKY_HOME)
if [ -z "$DINKY_HOME_TMP" ]; then
  init_env
else
  if [ "$APP_HOME" != "$DINKY_HOME_TMP" ]; then
    echo -e "${RED}DINKY_HOME is not equal to the current path, The previous one was: ${DINKY_HOME_TMP}. The current one is: ${APP_HOME}, which needs to be reconfigured.${RESET}"
    init_env
  else
      echo -e "${GREEN}DINKY_HOME environment variable has been configured at ${DINKY_HOME_TMP}，Skip configuration.${RESET}"
  fi
fi


echo -e "${GREEN} ====================== Environment variable initialization script -> End ====================== ${RESET}"


echo
echo
echo -e "${GREEN} ====================== Data source driver initialization script -> Start ====================== ${RESET}"

while true; do
    echo -e "${BLUE} ========================= Please enter your database type ================================ ${RESET}"
    echo -e "${BLUE} ======== (h2 comes with it by default and does not need to perform this step)===========  ${RESET}"
    echo -e "${BLUE} ============================== Please select 1, 2, 3 ======================================  ${RESET}"
    echo -e "${BLUE} ==================================== 1. mysql =============================================  ${RESET}"
    echo -e "${BLUE} ==================================== 2. postgresql =========================================  ${RESET}"
    echo -e "${BLUE} ================================ 3. Skip this step ==========================================  ${RESET}"
    echo -e "${BLUE} ================================ Enter number selection ==================================  ${RESET}"
    read -p "Please enter your database type：" db_type
    case $db_type in
        1)
             # Use source transparent transmission in debug mode
            source  "${APP_HOME}"/bin/init_jdbc_driver.sh "${DINKY_LIB}"
            break
            ;;
        2)
            echo -e "${GREEN}It seems that postgresql has been integrated by default, so there is no need to perform this step. Please perform subsequent installation and configuration operations as needed.${RESET}"
            break
            ;;
        3)
            echo -e "${GREEN}Skip this step。${RESET}"
            break
            ;;
        *)
            echo -e "${RED}The entered database type is incorrect, please rerun the script to select the correct database type.${RESET}"
            ;;
    esac
done
echo -e "${GREEN} ====================== Data source driver initialization script -> end====================== ${RESET}"

echo
echo

echo -e "${GREEN} ====================== Flink depends on initialization script -> start ====================== ${RESET}"

declare -A version_map
version_map["1.14"]="1.14.6"
version_map["1.15"]="1.15.4"
version_map["1.16"]="1.16.3"
version_map["1.17"]="1.17.2"
version_map["1.18"]="1.18.1"
version_map["1.19"]="1.19.1"
version_map["1.20"]="1.20.0"

FLINK_VERSION_SCAN=$(ls -n "${EXTENDS_HOME}" | grep '^d' | grep flink | awk -F 'flink' '{print $2}')
if [ -z "${FLINK_VERSION_SCAN}" ]; then
    echo -e "${RED}There is no Flink related version in ${EXTENDS_HOME} in the directory where Dinky is deployed. The initialization operation cannot be performed. Please check.${RESET}"
    exit 1
else
    echo -e "${GREEN}The current Flink version number deployed by Dinky:${FLINK_VERSION_SCAN}${RESET}"
fi

CURRENT_FLINK_FULL_VERSION=${version_map[$FLINK_VERSION_SCAN]}

echo -e "${GREEN}Obtain the version number corresponding to the deployed Flink (full version number) based on the scanned current Flink version number: flink-${CURRENT_FLINK_FULL_VERSION}${RESET}"

while true; do
    read -p "It is detected that the Flink version number deployed by Dinky is: ${FLINK_VERSION_SCAN}, and the Flink installation package version number that needs to be downloaded is: flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz. Please choose whether to initialize Flink related dependencies?（yes/no/exit）" is_init_flink
    is_init_flink=$(echo "$is_init_flink" | tr '[:upper:]' '[:lower:]' | tr -d '[:space:]')

    case $is_init_flink in
        yes | y )
             # Use source transparent transmission in debug mode
            source  "${APP_HOME}"/bin/init_flink_dependences.sh "${CURRENT_FLINK_FULL_VERSION}" "${FLINK_VERSION_SCAN}" "${DINKY_TMP_DIR}" "${EXTENDS_HOME}" "${APP_HOME}"
            break
            ;;
        no | n )
            echo -e "${GREEN}The Flink installation package download operation has been skipped. Please download manually${RESET}"
            break
            ;;
        exit | e )
            echo -e "${GREEN}If you choose exit, the program will exit。${RESET}"
            exit 0
            ;;
        *)
            echo -e "${RED}Invalid input, please re-enter yes/no/exit。${RESET}"
            ;;
    esac
done
echo -e "${GREEN} ====================== Flink depends on initialization script -> end ====================== ${RESET}"

echo
echo

echo -e "${GREEN} ====================== Hadoop dependency initialization script -> Start ====================== ${RESET}"

while true; do
    read -p "Is your deployment environment a Hadoop environment?？（yes/no/exit）" is_hadoop
    is_hadoop=$(echo "$is_hadoop" | tr '[:upper:]' '[:lower:]' | tr -d '[:space:]')
    case $is_hadoop in
        yes | y )
             # Use source transparent transmission in debug mode
            source  "${APP_HOME}/bin/init_hadoop_dependences.sh" "${EXTENDS_HOME}"
            break
            ;;
        no | n )
            echo -e "${GREEN}Hadoop related operations skipped ${RESET}"
            break
            ;;
        exit | e )
            echo -e "${GREEN}If you choose exit, the program will exit${RESET}"
            exit 0
            ;;
        *)
            echo -e "${RED}Invalid input, please re-enter yes/no/exit。${RESET}"
            ;;
    esac
done
echo -e "${GREEN} ======================Hadoop dependency initialization script -> end ====================== ${RESET}"
echo

echo -e "${GREEN} === After the environment initialization is completed, you can configure the application configuration file in Dinky's config directory to perform database-related configuration, or execute the initialization configuration file.。====  ${RESET}"
echo

echo -e "${GREEN} ====================== Database configuration file initialization script -> Start ====================== ${RESET}"

while true; do
    read -p "Do you need to initialize the database configuration file?？(yes/no)：" is_init_db
    is_init_db=$(echo "$is_init_db" | tr '[:upper:]' '[:lower:]' | tr -d '[:space:]')
    case $is_init_db in
        yes | y )
             # Use source transparent transmission in debug mode
            source  "${APP_HOME}/bin/init_db.sh" "${DINKY_HOME}" "${DB_ENV_FILE}"
            echo -e "${GREEN}The database configuration file initialization script has been executed successfully the configuration file is：${DB_ENV_FILE} ${RESET}"
            break
            ;;
        no | n )
            echo -e "${GREEN}The database initialization operation has been skipped, please manually configure the database ${APP_HOME}/config/application.yml file and ${DINKY_HOME}/config/application-[mysql/postgresql].yml file。${RESET}"
            break
            ;;
        exit | e )
            echo -e "${GREEN}The script has exited, please manually configure the database ${APP_HOME}/config/application.yml file and ${APP_HOME}/config/application-[mysql/postgresql].yml file。${RESET}"
            exit 0
            ;;
        *)
            echo -e "${RED}Invalid input, please re-enter yes/no/exit。${RESET}"
            ;;
    esac
done
echo -e "${GREEN} ====================== Database configuration file initialization script -> End ====================== ${RESET}"

function echo_warning_msg() {
  echo -e "${RED}Note: To make these changes permanent, you may need to restart your terminal or run 'source $DB_ENV_FILE && source $ENV_FILE' ${RESET}"
  echo -e "${RED}Note: To make these changes permanent, you may need to restart your terminal or run 'source $DB_ENV_FILE && source $ENV_FILE' ${RESET}"
  echo -e "${RED}Note: To make these changes permanent, you may need to restart your terminal or run 'source $DB_ENV_FILE && source $ENV_FILE' ${RESET}"
}

echo -e "${GREEN} ====================== Dinky service startup script -> Start ====================== ${RESET}"

while true; do
    read -p "Do you need to start the Dinky service?（yes/no/exit）" is_start
    is_start=$(echo "$is_start" | tr '[:upper:]' '[:lower:]' | tr -d '[:space:]')
    case $is_start in
        yes | y )
             # Use source transparent transmission in debug mode
            source "${APP_HOME}"/bin/auto.sh restart
            break
            ;;
        no | n )
            echo_warning_msg
            echo
            echo -e "${GREEN}The Dinky service startup script has been skipped, Please execute the above command first, and then start the service manually -> ${APP_HOME}/bin/auto.sh restart | start。${RESET}"
            break
            ;;
        exit | e )
          echo_warning_msg
          echo -e "${GREEN}If you choose exit, the program will exit。${RESET}"
          exit 0
          ;;
        *)
          echo -e "${RED}Invalid input, please re-enter yes/no/exit。${RESET}"
          ;;
        esac
done
echo -e "${GREEN} ====================== Dinky service startup script -> End ====================== ${RESET}"
echo
echo
echo -e "${GREEN} ====================== Dinky initialization script execution completed ====================== ${RESET}"


set +x
