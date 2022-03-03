import {getData} from "@/components/Common/crud";

export function getStatusCount() {
  return getData("api/jobInstance/getStatusCount");
}

export function getJobInfoDetail(id:number) {
  return getData("api/jobInstance/getJobInfoDetail",{id});
}

export function refreshJobInfoDetail(id:number) {
  return getData("api/jobInstance/refreshJobInfoDetail",{id});
}
