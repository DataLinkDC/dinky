import React, {useEffect, useState} from "react";
import {connect} from "umi";
import {StateType} from "@/pages/Demo/FormStepForm/model";
import {DownOutlined, FrownFilled, FrownOutlined, MehOutlined, SmileOutlined} from "@ant-design/icons";
import {Tree, Input, Dropdown, Menu} from 'antd';
import {getCatalogueTreeData} from "@/pages/FlinkSqlStudio/service";
import {convertToTreeData, DataType, TreeDataNode} from "@/components/Studio/StudioTree/Function";

const {Search} = Input;

type StudioTreeProps = {};

/*const treeData = [
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
];*/

const StudioTree: React.FC<StudioTreeProps> = (props) => {
  // state = {
  //   expandedKeys: [],
  //   searchValue: '',
  //   autoExpandParent: true,
  // };
  const [treeData, setTreeData] = useState<TreeDataNode[]>();

  const getTreeData = async () => {
    const result = await getCatalogueTreeData();
    let data = result.datas;
    data = convertToTreeData(data, 0);
    setTreeData(data);
  };

  useEffect(() => {
    getTreeData();
  }, []);

  const onExpand = () => {
    // setState({
    //   expandedKeys,
    //   autoExpandParent: false,
    // })
  };

  const onChange = () => {

  };


  return (
    <div>
      <Search style={{marginBottom: 8}} placeholder="Search" onChange={onChange}/>
        <Tree
          onExpand={onExpand}
          // expandedKeys={expandedKeys}
          // autoExpandParent={autoExpandParent}
          showIcon
          showLine
          //defaultExpandAll
          switcherIcon={<DownOutlined/>}
          treeData={treeData}
          // treeData={treeData()}
        />

    </div>
  );
};


export default connect(({studio}: { studio: StateType }) => ({}))(StudioTree);
