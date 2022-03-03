import {handleAddOrUpdate} from "@/components/Common/crud";
import {AlertInstanceTableListItem} from "@/pages/AlertInstance/data";

export async function createOrModifyAlertInstance(alertInstance: AlertInstanceTableListItem) {
  return handleAddOrUpdate('/api/alertInstance', alertInstance);
}
