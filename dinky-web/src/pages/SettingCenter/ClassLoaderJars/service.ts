import {queryDataByParams} from "@/services/BusinessCrud";


export const queryClassLoaderJars = async () => {
  return await queryDataByParams<string[]>('/api/system/queryAllClassLoaderJarFiles') ?? [];
}
