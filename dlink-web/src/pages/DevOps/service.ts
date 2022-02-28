import {getData} from "@/components/Common/crud";

export function getStatusCount() {
  return getData("api/jobInstance/getStatusCount");
}
