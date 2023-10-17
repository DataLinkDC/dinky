import { LogInfo } from '@/types/SettingCenter/data';
import { parseByteStr, renderIcon } from '@/utils/function';
import { l } from '@/utils/intl';

const buildTitleLabel = (item: LogInfo) => {
  return (
    <>
      {item.name}
      {!item.leaf && (
        <span style={{ color: 'gray' }}>
          {' '}
          &nbsp;&nbsp;{l('global.size', '', { size: parseByteStr(item.size) })}
        </span>
      )}
    </>
  );
};

export const buildLogInfoTreeData = (data: LogInfo[]): any =>
  data.map((item: LogInfo) => {
    return {
      isLeaf: !item.leaf,
      name: item.name,
      parentId: item.parentId,
      label: item.path,
      icon: item.leaf && renderIcon(item.name, '.', item.leaf),
      path: item.path,
      title: buildTitleLabel(item),
      fullInfo: item,
      key: item.id,
      id: item.id,
      children: buildLogInfoTreeData(item.children)
    };
  });
