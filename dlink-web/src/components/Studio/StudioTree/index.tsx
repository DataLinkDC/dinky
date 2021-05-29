import React from "react";
import {connect} from "umi";
import {StateType} from "@/pages/Demo/FormStepForm/model";
import {DownOutlined, FrownFilled, FrownOutlined, MehOutlined, SmileOutlined} from "@ant-design/icons";
import { Tree, Input } from 'antd';

const { Search } = Input;

type StudioTreeProps = {

};

const treeData = [
  {
    title: 'parent 1',
    key: '0-0',
    icon: <SmileOutlined />,
    children: [
      {
        title: 'leaf',
        key: '0-0-0',
        icon: <MehOutlined />,
      },
      {
        title: 'leaf',
        key: '0-0-1',
        icon: ({ selected }) => (selected ? <FrownFilled /> : <FrownOutlined />),
      },
    ],
  },
];




const StudioTree: React.FC<StudioTreeProps> = (props) => {
  // state = {
  //   expandedKeys: [],
  //   searchValue: '',
  //   autoExpandParent: true,
  // };


  const onExpand =()=>{
    // setState({
    //   expandedKeys,
    //   autoExpandParent: false,
    // })
  };

  const onChange =()=>{

  };


  return (
    <div>
      <Search style={{ marginBottom: 8 }} placeholder="Search" onChange={onChange} />
      <Tree
        onExpand={onExpand}
        // expandedKeys={expandedKeys}
        // autoExpandParent={autoExpandParent}
        showIcon
        showLine
        defaultExpandAll
        defaultSelectedKeys={['0-0-0']}
        switcherIcon={<DownOutlined />}
        treeData={treeData}
      />
    </div>
  );
};


export default connect(({ studio }: { studio: StateType }) => ({

}))(StudioTree);
