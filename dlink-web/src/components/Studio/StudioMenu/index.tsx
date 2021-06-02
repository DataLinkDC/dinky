import styles from "./index.less";
import {Menu, Dropdown, Tooltip, Row, Col,Popconfirm,Badge} from "antd";
import {PauseCircleTwoTone, CopyTwoTone, DeleteTwoTone,PlayCircleTwoTone,DiffTwoTone,
  FileAddTwoTone,FolderOpenTwoTone,SafetyCertificateTwoTone,SaveTwoTone,FlagTwoTone,EnvironmentOutlined} from "@ant-design/icons";
import Space from "antd/es/space";
import Divider from "antd/es/divider";
import Button from "antd/es/button/button";
import Breadcrumb from "antd/es/breadcrumb/Breadcrumb";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {useEffect, useState} from "react";
import {handleAddOrUpdate, postAll} from "@/components/Common/crud";

const {SubMenu} = Menu;
//<Button shape="circle" icon={<CaretRightOutlined />} />
const menu = (
  <Menu>
    <Menu.Item>1st menu item</Menu.Item>
    <Menu.Item>2nd menu item</Menu.Item>
    <SubMenu title="sub menu">
      <Menu.Item>3rd menu item</Menu.Item>
      <Menu.Item>4th menu item</Menu.Item>
    </SubMenu>
    <SubMenu title="disabled sub menu" disabled>
      <Menu.Item>5d menu item</Menu.Item>
      <Menu.Item>6th menu item</Menu.Item>
    </SubMenu>
  </Menu>
);


const StudioMenu = (props: any) => {

  const {tabs,current,currentPath,form,dispatch} = props;
  const [pathItem, setPathItem] = useState<[]>();

  const executeSql = () => {
    let param ={
      session:current.task.session,
      statement:current.value,
      clusterId:current.task.clusterId,
      checkPoint:current.task.checkPoint,
      parallelism:current.task.parallelism,
      maxRowNum:current.task.maxRowNum,
      fragment:current.task.fragemnt,
      savePointPath:current.task.savePointPath,
    };
    const key = current.key;
    const result = postAll('api/studio/executeSql',param);
    result.then(res=>{
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
      console.log(newTabs);
      dispatch&&dispatch({
        type: "Studio/saveTabs",
        payload: newTabs,
      });
    })
  };

  const buildMsg=(res)=>{
    const result = res.datas;
    let msg=`[${result.sessionId}:${result.flinkHost}:${result.flinkPort}] ${result.finishDate} ${result.success?'Success':'Error'} 
    [${result.time}ms] ${result.msg?result.msg:''} ${result.error?result.error:''} \r\n
    Statement: ${result.statement}`;
    return msg;
  };

  const saveSqlAndSettingToTask = async() => {
    const fieldsValue = await form.validateFields();
    if(current.task){
      let task = {
        id:current.key,
        statement:current.value,
        ...fieldsValue
      };
      dispatch&&dispatch({
        type: "Studio/saveTask",
        payload: task,
      });
      /*const success = handleAddOrUpdate('api/task',task);
      console.log(success);
      console.log(tabs);*/
    }else{

    }
  };

  const runMenu = (
    <Menu>
      <Menu.Item onClick={executeSql}>执行</Menu.Item>
    </Menu>
  );

  const getPathItem = (paths)=>{
    let itemList = [];
    for(let item of paths){
      itemList.push(<Breadcrumb.Item>{item}</Breadcrumb.Item>)
    }
    return itemList;
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
              icon={<FileAddTwoTone />}
            />
            <Button
              type="text"
              icon={<FolderOpenTwoTone />}
            />
            <Tooltip title="保存当前的 FlinkSql">
            <Button
              type="text"
              icon={<SaveTwoTone />}
              onClick={saveSqlAndSettingToTask}
            />
            </Tooltip>
            <Divider type="vertical" />
            <Button
              type="text"
              icon={<SafetyCertificateTwoTone />}
            />
            <Button
              type="text"
              icon={<FlagTwoTone />}
            />
            <Tooltip title="执行当前的 FlinkSql">
            <Button
              type="text"
              icon={<PlayCircleTwoTone />}
              //loading={loadings[2]}
              onClick={executeSql}
            />
            </Tooltip>
            <Popconfirm
              title="您确定要停止所有的 FlinkSql 任务吗？"
             // onConfirm={confirm}
              //onCancel={cancel}
              okText="停止"
              cancelText="取消"
            >
              <Tooltip title="停止所有的 FlinkSql 任务">
                <Badge size="small" count={1} offset={[-5, 5]}>
            <Button
              type="text"
              icon={<PauseCircleTwoTone />}
            />
                </Badge>
              </Tooltip>
            </Popconfirm>
            <Divider type="vertical" />
            <Button
              type="text"
              icon={<DiffTwoTone />}
            />
            <Button
              type="text"
              icon={<CopyTwoTone />}
            />
            <Button
              type="text"
              icon={<DeleteTwoTone />}
            />
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
}))(StudioMenu);
