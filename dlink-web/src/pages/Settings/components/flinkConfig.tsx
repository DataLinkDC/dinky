import React, {useEffect, useState} from 'react';
import {List,Input,Switch,Form} from 'antd';
import {connect} from "umi";
import {SettingsStateType} from "@/pages/Settings/model";
import {saveSettings} from "@/pages/Settings/function";

type FlinkConfigProps = {
  sqlSubmitJarPath: SettingsStateType['sqlSubmitJarPath'];
  sqlSubmitJarParas: SettingsStateType['sqlSubmitJarParas'];
  sqlSubmitJarMainAppClass: SettingsStateType['sqlSubmitJarMainAppClass'];
  useRestAPI: SettingsStateType['useRestAPI'];
  dispatch: any;
};

const FlinkConfigView: React.FC<FlinkConfigProps> = (props) => {

  const {sqlSubmitJarPath, sqlSubmitJarParas, sqlSubmitJarMainAppClass,useRestAPI, dispatch} = props;
  const [editName, setEditName] = useState<string>('');
  const [formValues, setFormValues] = useState(props);
  const [form] = Form.useForm();

  useEffect(()=>{
    form.setFieldsValue(props);
  },[props]);

  const getData = () => [
    {
      title: '提交FlinkSQL的Jar文件路径',
      description: (
        editName!='sqlSubmitJarPath'?
          (sqlSubmitJarPath?sqlSubmitJarPath:'未设置'):(
            <Input
            id='sqlSubmitJarPath'
            defaultValue={sqlSubmitJarPath}
            onChange={onChange}
            placeholder="hdfs:///dlink/jar/dlink-app.jar" />)),
      actions: editName!='sqlSubmitJarPath'?[<a onClick={({}) => handleEditClick('sqlSubmitJarPath')}>修改</a>]:
        [<a onClick={({}) => handleSaveClick('sqlSubmitJarPath')}>保存</a>,
        <a onClick={({}) => handleCancelClick()}>取消</a>],
    },
    {
      title: '提交FlinkSQL的Jar的主类入参',
      description: (
        editName!='sqlSubmitJarParas'?
          (sqlSubmitJarParas?sqlSubmitJarParas:'未设置'):(<Input
            id='sqlSubmitJarParas'
            defaultValue={sqlSubmitJarParas}
            onChange={onChange}
            placeholder="" />)),
      actions: editName!='sqlSubmitJarParas'?[<a onClick={({}) => handleEditClick('sqlSubmitJarParas')}>修改</a>]:
        [<a onClick={({}) => handleSaveClick('sqlSubmitJarParas')}>保存</a>,
          <a onClick={({}) => handleCancelClick()}>取消</a>],
    },
    {
      title: '提交FlinkSQL的Jar的主类',
      description: (
        editName!='sqlSubmitJarMainAppClass'?
          (sqlSubmitJarMainAppClass?sqlSubmitJarMainAppClass:'未设置'):(<Input
            id='sqlSubmitJarMainAppClass'
            defaultValue={sqlSubmitJarMainAppClass}
            onChange={onChange}
            placeholder="com.dlink.app.MainApp" />)),
      actions: editName!='sqlSubmitJarMainAppClass'?[<a onClick={({}) => handleEditClick('sqlSubmitJarMainAppClass')}>修改</a>]:
        [<a onClick={({}) => handleSaveClick('sqlSubmitJarMainAppClass')}>保存</a>,
          <a onClick={({}) => handleCancelClick()}>取消</a>],
    },{
      title: '使用 RestAPI',
      description: '启用后，Flink 任务的 savepoint、停止等操作将通过 JobManager 的 RestAPI 进行',
      actions: [
        <Form.Item
          name="useRestAPI" valuePropName="checked"
        >
        <Switch checkedChildren="启用" unCheckedChildren="禁用"
                 checked={useRestAPI}
        /></Form.Item>],
    },
  ];

  const onChange = e => {
    let values = {};
    values[e.target.id]=e.target.value;
    setFormValues({...formValues,...values});
  };

  const onValuesChange = (change:any,all:any) => {
    let values = {};
    for(let key in change){
      values[key]=all[key];
    }
    saveSettings(values, dispatch);
  };

  const handleEditClick = (name:string)=>{
    setEditName(name);
  };

  const handleSaveClick = (name:string)=>{
    if(formValues[name]!=props[name]) {
      let values = {};
      values[name] = formValues[name];
      saveSettings(values, dispatch);
    }
    setEditName('');
  };

  const handleCancelClick = ()=>{
    setFormValues(props);
    setEditName('');
  };

  const data = getData();
  return (
    <>
      <Form
        form={form}
        layout="vertical"
        onValuesChange={onValuesChange}
      >
      <List
        itemLayout="horizontal"
        dataSource={data}
        renderItem={(item) => (
          <List.Item actions={item.actions}>
            <List.Item.Meta title={item.title} description={item.description}/>
          </List.Item>
        )}
      />
      </Form>
    </>
  );
};
export default connect(({Settings}: { Settings: SettingsStateType }) => ({
  sqlSubmitJarPath: Settings.sqlSubmitJarPath,
  sqlSubmitJarParas: Settings.sqlSubmitJarParas,
  sqlSubmitJarMainAppClass: Settings.sqlSubmitJarMainAppClass,
  useRestAPI: Settings.useRestAPI,
}))(FlinkConfigView);
