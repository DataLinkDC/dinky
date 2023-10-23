import { ResourceInfo } from '@/types/RegCenter/data';
import { parseByteStr, renderIcon } from '@/utils/function';
import { l } from '@/utils/intl';

const buildTitleLabel = (item: ResourceInfo) => {
  return (
    <>
      {item.fileName}
      {!item.isDirectory && (
        <span style={{ color: 'gray' }}>
          {' '}
          &nbsp;&nbsp;{l('global.size', '', { size: parseByteStr(item.size) })}
        </span>
      )}
    </>
  );
};

export const buildResourceTreeData = (data: ResourceInfo[]): any =>
  data.map((item: ResourceInfo) => {
    return {
      isLeaf: !item.isDirectory,
      name: item.fileName,
      parentId: item.pid,
      label: item.fullName + '/' + item.fileName,
      icon: renderIcon(item.fileName, '.', item.isDirectory),
      path: item.fullName,
      title: buildTitleLabel(item),
      fullInfo: item,
      key: item.id,
      id: item.id,
      children: item.children && buildResourceTreeData(item.children)
    };
  });
