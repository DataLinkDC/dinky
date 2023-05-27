import {l} from "@/utils/intl";
import {JOB_LIFE_CYCLE, JOB_STATUS} from "@/services/constants";

export const LIFECYCLE_FILTER = () => {
  return [
    {text: l('global.table.lifecycle.dev'), value: JOB_LIFE_CYCLE.DEVELOP},
    {text: l('global.table.lifecycle.publish'), value: JOB_LIFE_CYCLE.RELEASE},
    {text: l('global.table.lifecycle.online'), value: JOB_LIFE_CYCLE.ONLINE},
    {text: l('global.table.lifecycle.unknown'), value: JOB_LIFE_CYCLE.UNKNOWN},
  ]
};

export const JOB_STATUS_FILTER = () => {
  return [
    {text: JOB_STATUS.FINISHED, value: JOB_STATUS.FINISHED},
    {text: JOB_STATUS.RUNNING, value: JOB_STATUS.RUNNING},
    {text: JOB_STATUS.FAILED, value: JOB_STATUS.FAILED},
    {text: JOB_STATUS.CANCELED, value: JOB_STATUS.CANCELED},
    {text: JOB_STATUS.INITIALIZING, value: JOB_STATUS.INITIALIZING},
    {text: JOB_STATUS.RESTARTING, value: JOB_STATUS.RESTARTING},
    {text: JOB_STATUS.CREATED, value: JOB_STATUS.CREATED},
    {text: JOB_STATUS.FAILING, value: JOB_STATUS.FAILING},
    {text: JOB_STATUS.SUSPENDED, value: JOB_STATUS.SUSPENDED},
    {text: JOB_STATUS.CANCELLING, value: JOB_STATUS.CANCELLING},
    {text: JOB_STATUS.UNKNOWN, value: JOB_STATUS.UNKNOWN},

  ]
};

