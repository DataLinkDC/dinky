import {getData} from "@/components/Common/crud";

export function getTaskAPIAddress() {
  return getData("api/task/getTaskAPIAddress");
}
