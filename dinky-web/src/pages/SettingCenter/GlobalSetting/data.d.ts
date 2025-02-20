import {BaseConfigProperties} from "@/types/SettingCenter/data.d";


export interface GeneralComponentConfigProps {
  data: BaseConfigProperties[];
  onSave: (data: BaseConfigProperties) => void;
  auth: string;
}
