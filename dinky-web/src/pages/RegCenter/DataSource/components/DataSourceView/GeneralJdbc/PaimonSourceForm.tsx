/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import {
  ProFormGroup,
  ProFormList,
  ProFormRadio,
  ProFormSegmented,
  ProFormText
} from '@ant-design/pro-components';
import { l } from '@/utils/intl';
import React, { useEffect } from 'react';
import { Space } from 'antd';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { Values } from 'async-validator';

type DataSourceJdbcProps = {
  form: FormInstance<Values>;
};
export const FileSystemType = {
  S3: 'S3',
  HDFS: 'HDFS',
  LOCAL: 'LOCAL'
};

export const CatalogType = {
  FileSystem: 'FileSystem',
  JDBC: 'JDBC',
  Hive: 'Hive'
};

const PaimonSourceForm: React.FC<DataSourceJdbcProps> = (props) => {
  const { form } = props;
  const [fileSystemType, setFileSystemType] = React.useState<string>();
  const [catalogType, setCatalogType] = React.useState<string>();

  useEffect(() => {
    setCatalogType(form.getFieldsValue()?.connectConfig?.catalogType ?? CatalogType.FileSystem);
    setFileSystemType(form.getFieldsValue()?.connectConfig?.fileSystemType ?? FileSystemType.LOCAL);
  });

  const renderConfig = () => {
    return (
      <Space direction={'horizontal'} size={60}>
        <ProFormSegmented
          name={['connectConfig', 'catalogType']}
          label='Catalog Type'
          request={async () => [
            { label: CatalogType.FileSystem, value: CatalogType.FileSystem, disabled: false },
            { label: CatalogType.JDBC, value: CatalogType.JDBC, disabled: true },
            { label: CatalogType.Hive, value: CatalogType.Hive, disabled: false }
          ]}
          required
          fieldProps={{
            onChange: (value) => setCatalogType(value + '')
          }}
        />

        {catalogType == CatalogType.FileSystem && (
          <ProFormSegmented
            name={['connectConfig', 'fileSystemType']}
            label='File System Type'
            request={async () => [
              { label: FileSystemType.LOCAL, value: FileSystemType.LOCAL, disabled: false },
              { label: FileSystemType.S3, value: FileSystemType.S3, disabled: false },
              { label: FileSystemType.HDFS, value: FileSystemType.HDFS, disabled: false }
            ]}
            required
            fieldProps={{
              onChange: (value) => setFileSystemType(value + '')
            }}
          />
        )}
      </Space>
    );
  };

  const renderS3Config = () => {
    return (
      <>
        <ProFormText
          name={['connectConfig', 's3', 'endpoint']}
          label='s3.endpoint'
          width={'md'}
          required={true}
        />
        <ProFormText
          name={['connectConfig', 's3', 'accessKey']}
          label='s3.access-key'
          width={'md'}
          required={true}
        />
        <ProFormText
          name={['connectConfig', 's3', 'secretKey']}
          label='s3.secret-key'
          width={'md'}
          required={true}
        />
        <ProFormRadio.Group
          name={['connectConfig', 's3', 'pathStyle']}
          label='s3.path.style.access'
          required
          options={[
            {
              label: 'true',
              value: true
            },
            {
              label: 'false',
              value: false
            }
          ]}
        />
      </>
    );
  };

  const renderHiveConfig = () => {
    return (
      <>
        <ProFormText
          name={['connectConfig', 'hadoop', 'hiveConfDir']}
          label='hive-conf-dir'
          width={'md'}
          required={true}
        />
        <ProFormText
          name={['connectConfig', 'hadoop', 'hadoopConfDir']}
          label='hadoop-conf-dir'
          width={'md'}
        />
        <ProFormText name={['connectConfig', 'hadoop', 'uri']} label='Uri' width={'md'} />
      </>
    );
  };
  const renderHdfsConfig = () => {
    return (
      <>
        <ProFormText
          name={['connectConfig', 'hadoop', 'hadoopConfDir']}
          label='hadoop-conf-dir'
          width={'md'}
          required={true}
        />
      </>
    );
  };

  return (
    <div>
      {renderConfig()}
      <br />
      <ProFormGroup>
        {catalogType != CatalogType.Hive && (
          <ProFormText
            name={['connectConfig', 'warehouse']}
            label='warehouse'
            width={'md'}
            required={true}
          />
        )}
        {catalogType === CatalogType.FileSystem &&
          fileSystemType === FileSystemType.S3 &&
          renderS3Config()}
        {catalogType === CatalogType.FileSystem &&
          fileSystemType === FileSystemType.HDFS &&
          renderHdfsConfig()}
        {catalogType === CatalogType.Hive && renderHiveConfig()}
      </ProFormGroup>

      <ProFormList
        label={'Paimon Other Config Options'}
        name={['connectConfig', 'paimonConfig']}
        copyIconProps={false}
        deleteIconProps={{
          tooltipText: l('rc.cc.deleteConfig')
        }}
        creatorButtonProps={{
          creatorButtonText: l('rc.cc.addConfig')
        }}
      >
        <ProFormGroup key='headersGroup' style={{ width: '100%' }}>
          <Space key={'config'}>
            <ProFormText name='name' width={'md'} placeholder={l('rc.cc.key')} />
            <ProFormText name='value' width={'xl'} placeholder={l('rc.cc.value')} />
          </Space>
        </ProFormGroup>
      </ProFormList>
    </div>
  );
};

export default PaimonSourceForm;
