import styles from "./index.less";
import {Menu, Dropdown, Tooltip, Row, Col, Popconfirm, notification, Modal,message} from "antd";
import {PauseCircleTwoTone, CopyTwoTone, DeleteTwoTone,PlayCircleTwoTone,DiffTwoTone,
  FileAddTwoTone,FolderOpenTwoTone,SafetyCertificateTwoTone,SaveTwoTone,FlagTwoTone,
  EnvironmentOutlined,SmileOutlined,RocketTwoTone,QuestionCircleTwoTone} from "@ant-design/icons";
import Space from "antd/es/space";
import Divider from "antd/es/divider";
import Button from "antd/es/button/button";
import Breadcrumb from "antd/es/breadcrumb/Breadcrumb";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import { postDataArray} from "@/components/Common/crud";
import {executeSql} from "@/pages/FlinkSqlStudio/service";
import StudioHelp from "../StudioHelp";
import {showTables} from "@/components/Studio/StudioEvent/DDL";

const menu = (
  <Menu>
    <Menu.Item>敬请期待</Menu.Item>
  </Menu>
);


const StudioMenu = (props: any) => {

  const {tabs,current,currentPath,form,dispatch} = props;

  const execute = () => {
    let selectsql =null;
    if(current.monaco.current) {
      let selection = current.monaco.current.editor.getSelection();
      selectsql = current.monaco.current.editor.getModel().getValueInRange(selection);
    }
    if(selectsql==null||selectsql==''){
      selectsql=current.value;
    }
    let param ={
      session:current.task.session,
      statement:selectsql,
      clusterId:current.task.clusterId,
      checkPoint:current.task.checkPoint,
      parallelism:current.task.parallelism,
      maxRowNum:current.task.maxRowNum,
      fragment:current.task.fragment,
      savePointPath:current.task.savePointPath,
      jobName:current.task.jobName,
    };
    const key = current.key;
    const taskKey = (Math.random()*1000)+'';
    notification.success({
      message: `${param.clusterId+"_"+param.session} 新任务正在执行`,
      description: param.statement,
      duration:null,
      key:taskKey,
      icon: <SmileOutlined style={{ color: '#108ee9' }} />,
    });
    const result = executeSql(param);
    result.then(res=>{
      notification.close(taskKey);
      if(res.datas.success){
        message.success('执行成功');
      }else{
        message.success('执行失败');
      }
      let newTabs = tabs;
      for(let i=0;i<newTabs.panes.length;i++){
        if(newTabs.panes[i].key==key){
          let newResult = newTabs.panes[i].console.result;
          newResult.unshift(res.datas);
          newTabs.panes[i].console={
            result:newResult,
          };
          break;
        }
      }
      dispatch&&dispatch({
        type: "Studio/saveTabs",
        payload: newTabs,
      });
      showTables(current.task.clusterId,current.task.clusterName,current.task.session,dispatch);
    })
  };

  const submit= () =>{
    if(!current.task.id){
      message.error(`草稿【${current.title}】无法被提交，请创建或选择有效作业进行提交`);
      return;
    }
    const taskKey = (Math.random()*1000)+'';
    Modal.confirm({
      title: '异步提交作业',
      content: `确定异步提交作业【${current.task.alias}】到其配置的集群吗？请确认您的作业是否已经被保存！`,
      okText: '确认',
      cancelText: '取消',
      onOk:async () => {
        let task = {
          id:current.task.id,
        };
        notification.success({
          message: `任务【${current.task.alias} 】正在异步提交`,
          description: current.task.statement,
          duration:null,
          key:taskKey,
          icon: <SmileOutlined style={{ color: '#108ee9' }} />,
        });
        const res = await postDataArray('/api/task/submit',[task.id]);
          notification.close(taskKey);
          if(res.datas[0].success){
            message.success('异步提交成功');
          }else{
            message.success('异步提交失败');
          }
      }
    });
  };

  const saveSqlAndSettingToTask = async() => {
    const fieldsValue = await form.validateFields();
    if(current.task){
      let task = {
        ...current.task,
        statement:current.value,
        ...fieldsValue
      };
      dispatch&&dispatch({
        type: "Studio/saveTask",
        payload: task,
      });
    }else{

    }
  };

  const runMenu = (
    <Menu>
      <Menu.Item onClick={execute}>同步执行</Menu.Item>
      <Menu.Item onClick={submit}>异步提交</Menu.Item>
    </Menu>
  );

  const getPathItem = (paths)=>{
    let itemList = [];
    for(let item of paths){
      itemList.push(<Breadcrumb.Item>{item}</Breadcrumb.Item>)
    }
    return itemList;
  };

  const showHelp=()=>{
    Modal.info({
      title: '使用帮助',
      width:1000,
      content: (
        <StudioHelp />
      ),
      onOk() {},
    });
  };
  return (
    <Row className={styles.container}>
      <Col span={24}>
        <div>
          <Space>
            <Dropdown overlay={menu}>
              <Button type="text" onClick={e => e.preventDefault()}>
                文件
              </Button>
            </Dropdown>
            <Dropdown overlay={menu}>
              <Button type="text" onClick={e => e.preventDefault()}>
                编辑
              </Button>
            </Dropdown>
            <Dropdown overlay={runMenu}>
              <Button type="text" onClick={e => e.preventDefault()}>
                执行
              </Button>
            </Dropdown>
            <Dropdown overlay={menu}>
              <Button type="text" onClick={e => e.preventDefault()}>
                帮助
              </Button>
            </Dropdown>
          </Space>
        </div>
      </Col>
      <Divider className={styles["ant-divider-horizontal-0"]}/>
      <Col span={24}>
        <Row>
          <Col span={8}>
            <Breadcrumb className={styles["dw-path"]}>
              <EnvironmentOutlined />
              <Divider type="vertical" />
              {getPathItem(currentPath)}
            </Breadcrumb>
          </Col>
          <Col span={8} offset={8}>
            <Button
              type="text"
              icon={<FileAddTwoTone twoToneColor="#ddd" />}
            />
            <Button
              type="text"
              icon={<FolderOpenTwoTone twoToneColor="#ddd" />}
            />
            <Tooltip title="保存当前的 FlinkSql 及配置">
            <Button
              type="text"
              icon={<SaveTwoTone />}
              onClick={saveSqlAndSettingToTask}
            />
            </Tooltip>
            <Divider type="vertical" />
            <Button
              type="text"
              icon={<SafetyCertificateTwoTone twoToneColor="#ddd" />}
            />
            <Button
              type="text"
              icon={<FlagTwoTone twoToneColor="#ddd" />}
            />
            <Tooltip title="执行当前的 FlinkSql">
            <Button
              type="text"
              icon={<PlayCircleTwoTone />}
              //loading={loadings[2]}
              onClick={execute}
            />
            </Tooltip>
            <Tooltip title="提交当前的作业到集群">
            <Button
              type="text"
              icon={<RocketTwoTone />}
              onClick={submit}
            />
            </Tooltip>
            <Popconfirm
              title="您确定要停止所有的 FlinkSql 任务吗？"
             // onConfirm={confirm}
              //onCancel={cancel}
              okText="停止"
              cancelText="取消"
            >
              <Tooltip title="停止所有的 FlinkSql 任务，暂不可用">
                {/*<Badge size="small" count={1} offset={[-5, 5]}>*/}
            <Button
              type="text"
              icon={<PauseCircleTwoTone twoToneColor="#ddd" />}
            />
                {/*</Badge>*/}
              </Tooltip>
            </Popconfirm>
            <Divider type="vertical" />
            <Button
              type="text"
              icon={<DiffTwoTone twoToneColor="#ddd" />}
            />
            <Button
              type="text"
              icon={<CopyTwoTone twoToneColor="#ddd" />}
            />
            <Button
              type="text"
              icon={<DeleteTwoTone twoToneColor="#ddd" />}
            />
            <Tooltip title="查看使用帮助">
              <Button
                type="text"
                icon={<QuestionCircleTwoTone />}
                onClick={showHelp}
              />
            </Tooltip>
          </Col>
        </Row>
      </Col>
    </Row>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  currentPath: Studio.currentPath,
  tabs: Studio.tabs,
  // monaco: Studio.monaco,
}))(StudioMenu);
