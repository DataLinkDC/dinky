import { l } from '@/utils/intl';
import { ProFormSelect } from '@ant-design/pro-components';
import { ProFormSelectProps } from '@ant-design/pro-form/es/components/Select';
import { Divider, Typography } from 'antd';

const { Link } = Typography;

export type FlinkOptionsProps = ProFormSelectProps & {};

const FlinkOptionsSelect = (props: FlinkOptionsProps) => {
  const renderTemplateDropDown = (item: any) => {
    return (
      <>
        <Link href={'#/registration/document'}>+ {l('rc.cc.addConfig')}</Link>
        <Divider style={{ margin: '8px 0' }} />
        {item}
      </>
    );
  };

  return (
    <ProFormSelect
      {...props}
      fieldProps={{ dropdownRender: (item) => renderTemplateDropDown(item) }}
    />
  );
};

export default FlinkOptionsSelect;
