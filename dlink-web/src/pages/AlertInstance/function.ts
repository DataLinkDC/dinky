import {AlertInstanceTableListItem} from "@/pages/AlertInstance/data";

export const getJSONData = (values: AlertInstanceTableListItem) => {
  if(!values||!values.params||values.params==''){
    return {};
  }
  let data = JSON.parse(values.params);
  return {...data,...values};
}

export const buildJSONData = (values: AlertInstanceTableListItem,params: any) => {
  let newValue = values;
  if(params.name){
    newValue.name = params.name;
    delete params.name;
  }
  if(params.enabled){
    newValue.enabled = params.enabled;
    delete params.enabled;
  }
  let data: string = JSON.stringify(params);
  return {...newValue,params:data};
}
