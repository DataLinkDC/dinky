import { handleAddOrUpdate, handleOption } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { DataSources } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';

/**
 * handle test
 * @param item
 */
export const handleTest = async (item: Partial<DataSources.DataSource>) => {
  await handleOption(API_CONSTANTS.DATASOURCE_TEST, l('button.test'), item);
};

/**
 * handle add or update
 * @param item
 */
export const saveOrUpdateHandle = async (item: Partial<DataSources.DataSource>) => {
  await handleAddOrUpdate(API_CONSTANTS.DATASOURCE, item);
};
