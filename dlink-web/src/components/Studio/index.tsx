import React, {useEffect, useState} from "react";
import {connect} from "umi";
import PageContainer from "@ant-design/pro-layout/es/components/PageContainer";
import styles from './index.less';
import StudioMenu from "./StudioMenu";
import {Row,Col,Card} from "antd";
import StudioTree from "./StudioTree";
import StudioTabs from "./StudioTabs";
import {StateType} from "@/pages/Studio/model";

type StudioProps = {
  sql: StateType['sql'];
};
const Studio: React.FC<StudioProps> = ({ sql }) => {

  const [console, setConsole] = useState<boolean>(false);
  const [sqls, setSqls] = useState<String>();

  useEffect(() => {
    setSqls(sql);
  }, [sql]);

  return (

    <PageContainer
      title={false}
      content={<StudioMenu />}
      className={styles.main}
    >
      <Card bordered={false} className={styles.card}>
        <Row>
          <Col span={4}>
            <StudioTree />
          </Col>
          <Col span={20}>
            <StudioTabs />
          </Col>
        </Row>
      </Card>
    </PageContainer>
  )
};


/*function mapStateToProps(state) {
  // 这个state是所有model层的state，这里只用到其中一个，所以state.testPage把命名空间为testPage这个model层的state数据取出来
  // es6语法解构赋值
  debugger;
  const { data } = state.Studio;
  // 这里return出去的数据，会变成此组件的props，在组件可以通过props.num取到。props变化了，会重新触发render方法，界面也就更新了。
  return {
    data,
  };
}*/

// export default connect(mapStateToProps)(Studio);

export default connect(({ Studio }: { Studio: StateType }) => ({
  sql: Studio.sql,
}))(Studio);

// export default Studio;
