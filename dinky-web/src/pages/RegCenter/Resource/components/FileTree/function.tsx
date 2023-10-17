import { Resource } from '@/types/RegCenter/data';
import { renderIcon } from '@/utils/function';
import { l } from '@/utils/intl';

const buildTitleLabel = (item: Resource) => {
  return (
    <>
      {item.fileName}
      {!item.isDirectory && (
        <span style={{ color: 'gray' }}>
          {' '}
          &nbsp;&nbsp;{l('global.size', '', { size: item.size })}
        </span>
      )}
    </>
  );
};

export const buildResourceTreeData = (data: Resource[]): any =>
  data.map((item: Resource) => {
    return {
      isLeaf: !item.children,
      name: item.fileName,
      parentId: item.pid,
      label: item.fullName + '/' + item.fileName,
      icon: item.leaf && renderIcon(item.fileName, '.', item.leaf),
      path: item.fullName,
      title: buildTitleLabel(item),
      fullInfo: item,
      key: item.id,
      id: item.id,
      children: buildResourceTreeData(item.children)
    };
  });
