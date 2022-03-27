import React, {useState} from 'react';
import {Card, Image, List, Modal} from 'antd';

import {DataBaseItem} from '../data.d';
import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {FALLBACK, getDBImage} from "@/pages/DataBase/DB";
import DataBaseForm from "@/pages/DataBase/components/DataBaseForm";
import {createOrModifyDatabase, testDatabaseConnect} from "@/pages/DataBase/service";

export type UpdateFormProps = {
  onCancel: (flag?: boolean, formVals?: Partial<DataBaseItem>) => void;
  onSubmit: (values: Partial<DataBaseItem>) => void;
  modalVisible: boolean;
  values: Partial<DataBaseItem>;
};

const data:any = [
  {
    type: 'MySql',
  },
  {
    type: 'Oracle',
  },
  {
    type: 'PostgreSql',
  },
  {
    type: 'ClickHouse',
  },
  {
    type: 'SqlServer',
  },
  {
    type: 'Doris',
  },
  {
    type: 'Phoenix',
  },
  {
    type: 'Hive',
  },
];

const DBForm: React.FC<UpdateFormProps> = (props) => {

  const {
    onSubmit: handleUpdate,
    onCancel: handleChooseDBModalVisible,
    modalVisible,
    values
  } = props;

  const [dbType, setDbType] = useState<string>();

  const chooseOne = (item:DataBaseItem)=>{
    setDbType(item.type);
  };

  const onSubmit = async (value:any)=>{
    const success = await createOrModifyDatabase(value);
    if (success) {
      handleChooseDBModalVisible();
      setDbType(undefined);
      handleUpdate(value);
    }
  };

  const onTest = async (value:any)=>{
    await testDatabaseConnect(value);
  };

  return (
    <Modal
      width={800}
      bodyStyle={{padding: '32px 40px 48px'}}
      title={values.id?'编辑数据源':'创建数据源'}
      visible={modalVisible}
      onCancel={() => {
        setDbType(undefined);
        handleChooseDBModalVisible();
      }}
      maskClosable = {false}
      destroyOnClose = {true}
      footer={null}
    >{
      (!dbType&&!values.id)&&(<List
        grid={{
          gutter: 16,
          xs: 1,
          sm: 2,
          md: 4,
          lg: 4,
          xl: 4,
          xxl: 4,
        }}
        dataSource={data}
        renderItem={(item:DataBaseItem) => (
          <List.Item onClick={()=>{chooseOne(item)}}>
            <Card>
              <Image
                height={80}
                preview={false}
                src={getDBImage(item.type)}
                fallback={FALLBACK}
              />
            </Card>
          </List.Item>
        )}
      />)
    }
      <DataBaseForm
        onCancel={() => setDbType(undefined)}
        modalVisible={!!values.type||!!dbType}
        type={(!values.type)?dbType:values.type}
        values={values}
        onSubmit={(value) => {
          onSubmit(value);
        }}
        onTest={(value) => {
          onTest(value);
        }}
      />
    </Modal>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  cluster: Studio.cluster,
}))(DBForm);
