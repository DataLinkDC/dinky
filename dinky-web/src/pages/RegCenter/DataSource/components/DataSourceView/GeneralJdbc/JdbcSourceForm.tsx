import { ProForm, ProFormText } from '@ant-design/pro-components';
import { l } from '@/utils/intl';
import React from 'react';
import { AutoComplete, Form } from 'antd';
import { AUTO_COMPLETE_TYPE } from '@/pages/RegCenter/DataSource/components/constants';
import TextArea from 'antd/es/input/TextArea';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { Values } from 'async-validator';

type DataSourceJdbcProps = {
  form: FormInstance<Values>;
};

const JdbcSourceForm: React.FC<DataSourceJdbcProps> = (props) => {
  const { form } = props;

  return (
    <ProForm.Group>
      <ProFormText
        name={['connectConfig', 'username']}
        width={'sm'}
        label={l('rc.ds.username')}
        rules={[{ required: true, message: l('rc.ds.usernamePlaceholder') }]}
        placeholder={l('rc.ds.usernamePlaceholder')}
      />
      <ProFormText.Password
        name={['connectConfig', 'password']}
        width={'sm'}
        label={l('rc.ds.password')}
        placeholder={l('rc.ds.passwordPlaceholder')}
      />
      <ProForm.Group>
        <Form.Item
          name={['connectConfig', 'url']}
          label={l('rc.ds.url')}
          rules={[{ required: true, message: l('rc.ds.urlPlaceholder') }]}
        >
          <AutoComplete
            virtual
            placement={'topLeft'}
            autoClearSearchValue
            options={AUTO_COMPLETE_TYPE}
            style={{
              width: parent.innerWidth / 2 - 80
            }}
            filterOption
            onSelect={(value) => form && form.setFieldsValue({ url: value })}
          >
            <TextArea placeholder={l('rc.ds.urlPlaceholder')} />
            {/*<ProFormTextArea*/}
            {/*  name='url'*/}
            {/*  width={parent.innerWidth / 2 - 80}*/}
            {/*  label={l('rc.ds.url')}*/}
            {/*  rules={[{ required: true, message: l('rc.ds.urlPlaceholder') }]}*/}
            {/*  placeholder={l('rc.ds.urlPlaceholder')}*/}
            {/*/>*/}
          </AutoComplete>
        </Form.Item>
      </ProForm.Group>
    </ProForm.Group>
  );
};

export default JdbcSourceForm;
