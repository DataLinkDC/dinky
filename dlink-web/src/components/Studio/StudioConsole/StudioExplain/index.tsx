import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {Button, Tag, Space, Typography, Modal,} from 'antd';
import {ConsoleSqlOutlined} from "@ant-design/icons";
import ProList from '@ant-design/pro-list';
import {explainSql} from "@/pages/FlinkSqlStudio/service";
import {useRef, useState, useEffect} from "react";

const {Paragraph,Text} = Typography;

type ExplainItem = {
  index: number;
  type: string;
  sql: string;
  parse: string;
  explain: string;
  error: string;
  parseTrue: string;
  explainTrue: string;
  explainTime: number;
};

export type StudioExplainProps = {
  onCancel: (flag?: boolean) => void;
  modalVisible: boolean;
  data: Partial<ExplainItem>;
}
const StudioExplain = (props: any) => {
  const [explainData, setExplainData] = useState([]);
  const [result, setResult] = useState(<Text>正在校验中...</Text>);
  const {
    onClose,
    modalVisible,
    current,
    currentSession,
  } = props;

  useEffect(() => {
    if (!modalVisible) {
      return;
    }
    let selectsql = null;
    if (current.monaco.current) {
      let selection = current.monaco.current.editor.getSelection();
      selectsql = current.monaco.current.editor.getModel().getValueInRange(selection);
    }
    if (selectsql == null || selectsql == '') {
      selectsql = current.value;
    }
    let useSession = !!currentSession.session;
    let param = {
      ...current.task,
      useSession: useSession,
      session: currentSession.session,
      configJson: JSON.stringify(current.task.config),
      statement: selectsql,
    };
    setResult(<Text>正在校验中...</Text>);
    setExplainData([]);
    const result = explainSql(param);
    result.then(res => {
      setExplainData(res.datas);
      let errorCount: number = 0;
      for (let i in res.datas) {
        if (!res.datas[i].explainTrue || !res.datas[i].parseTrue) {
          errorCount++;
        }
      }
      if (errorCount == 0) {
        setResult(<Text type="success">全部正确</Text>);
      } else {
        setResult(<Text type="danger">存在错误，共计{errorCount}个</Text>);
      }
    })
  }, [modalVisible]);

  const renderFooter = () => {
    return (
      <>
        <Button onClick={onClose}>关闭</Button>
      </>
    );
  };

  const renderContent = () => {
    return (
      <>
        <ProList<ExplainItem>
          toolBarRender={false}
          search={{
            filterType: 'light',
          }}
          rowKey="id"
          dataSource={explainData}
          pagination={{
            pageSize: 5,
          }}
          showActions="hover"
          metas={{
            avatar: {
              dataIndex: 'index',
              search: false,
            },
            title: {
              dataIndex: 'type',
              title: 'type',
              render: (_, row) => {
                return (
                  <Space size={0}>
                    <Tag color="blue" key={row.type}>
                      <ConsoleSqlOutlined/> {row.type}
                    </Tag>
                  </Space>
                );
              },
            },
            description: {
              search: false,
              render: (_, row) => {
                return (
                  <>
                    {row.sql ?
                      (<Paragraph ellipsis={{rows: 2, expandable: true, symbol: 'more'}}>
                        {row.sql}
                      </Paragraph>) : null
                    }
                    {row.explain ?
                      (<Paragraph>
                    <pre style={{height: '80px'}}>
                      {row.explain}
                    </pre>
                      </Paragraph>) : null
                    }
                    {row.error ?
                      (<Paragraph>
                      <pre style={{height: '80px'}}>
                        {row.error}
                      </pre>
                      </Paragraph>) : null
                    }
                  </>
                )
              }
            },
            subTitle: {
              render: (_, row) => {
                return (
                  <Space size={0}>
                    {row.parseTrue ?
                      (<Tag color="#44b549">语法正确</Tag>) :
                      (<Tag color="#ff4d4f">语法有误</Tag>)}
                    {row.explainTrue ?
                      (<Tag color="#108ee9">逻辑正确</Tag>) :
                      (<Tag color="#ff4d4f">逻辑有误</Tag>)}
                    {row.explainTime}
                  </Space>
                );
              },
              search: false,
            },
          }}
          options={{
            search: false,
            setting: false
          }}
        />
      </>)
  };

  return (
    <Modal
      width={800}
      destroyOnClose
      title="FlinkSql 语法和逻辑检查"
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={onClose}
    >
      <Paragraph>
        <blockquote>{result}</blockquote>
      </Paragraph>
      {renderContent()}
    </Modal>
  );
};

export default connect(({Studio}: {Studio: StateType}) => ({
  current: Studio.current,
  currentSession: Studio.currentSession,
}))(StudioExplain);
