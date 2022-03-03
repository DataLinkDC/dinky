import {AlertGroupTableListItem} from "@/pages/AlertGroup/data";

export const getFormData = (values: AlertGroupTableListItem) => {
  let alertInstanceIds:string [] = [];
  if(values&&values.alertInstanceIds&&values.alertInstanceIds!=''){
    alertInstanceIds = values.alertInstanceIds.split(',');
  }
  return {...values,alertInstanceIds:alertInstanceIds};
}

export const buildFormData = (values: AlertGroupTableListItem,params: any) => {
  let newValue = values;
  if(params.alertInstanceIds){
    newValue.alertInstanceIds = params.alertInstanceIds.join(',');
    delete params.alertInstanceIds;
  }
  return {...newValue,...params};
}
