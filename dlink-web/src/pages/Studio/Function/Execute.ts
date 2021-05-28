import {handleAddOrUpdate} from "@/components/Common/crud";

export const updateEnabled = (url:string, selectedRows: [], enabled: boolean) => {
  selectedRows.forEach((item) => {
    handleAddOrUpdate(url,{id: item.id, enabled: enabled})
  })
};
