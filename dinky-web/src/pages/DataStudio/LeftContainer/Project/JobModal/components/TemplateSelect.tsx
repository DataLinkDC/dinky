import CodeShow from '@/components/CustomEditor/CodeShow';
import { queryList } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { Document } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { ProCard, ProList } from '@ant-design/pro-components';
import { Typography } from 'antd';
import React, { useEffect, useState } from 'react';

const TemplateSelect: React.FC<{ type: string; onChange: (v: string) => void }> = (props) => {
  const { type, onChange } = props;
  const [currentSelect, setCurrentSelect] = useState<Document>();

  useEffect(() => {
    setCurrentSelect(undefined);
    onChange('');
  }, [type]);

  const renderItem = (item: Document) => {
    return (
      <div style={{ padding: 10, background: '#F8F9FA' }}>
        <ProCard
          checked={item.id == currentSelect?.id}
          hoverable
          bordered
          title={
            <Typography.Text ellipsis={true} style={{ width: '150px' }}>
              {item.description}
            </Typography.Text>
          }
          bodyStyle={{ padding: 8 }}
          onClick={() => {
            setCurrentSelect(item);
            onChange(item.fillValue);
          }}
        >
          <CodeShow
            code={item.fillValue}
            language={'sql'}
            height={'15vh'}
            lineNumbers={'off'}
            options={{
              renderSideBySide: false,
              fontSize: 9,
              scrollbar: {
                vertical: 'hidden',
                horizontal: 'hidden'
              }
            }}
          />
        </ProCard>
      </div>
    );
  };

  return (
    <ProCard
      bodyStyle={{ padding: 0 }}
      collapsible
      defaultCollapsed={true}
      title={<a>{l('catalog.useTemplate')}</a>}
      collapsibleIconRender={() => <a>{'>'}</a>}
      size={'small'}
      ghost
    >
      <ProList<Document>
        pagination={{
          pageSize: 6,
          size: 'small',
          showTotal: () => (
            <Typography.Link href={'#/registration/document'}>
              + {l('rc.cc.addConfig')}
            </Typography.Link>
          )
        }}
        params={{ subtype: type }}
        grid={{ gutter: 24, column: 3 }}
        renderItem={renderItem}
        request={(params) => queryList(API_CONSTANTS.DOCUMENT, { ...params })}
      />
    </ProCard>
  );
};

export default TemplateSelect;
