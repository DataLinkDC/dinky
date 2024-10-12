import {Button, Col, Divider, Dropdown, Empty, Flex, MenuProps, Skeleton} from "antd";
import Search from "antd/es/input/Search";
import {l} from "@/utils/intl";
import {DownOutlined, SortAscendingOutlined} from "@ant-design/icons";
import React, {useEffect, useRef, useState} from "react";
import DirectoryTree from "antd/es/tree/DirectoryTree";
import {TreeVo} from "@/pages/DataStudio/model";
import {ItemType} from "antd/es/menu/interface";
import {useAsyncEffect} from "ahooks";
import {getTaskSortTypeData} from "@/pages/DataStudio/LeftContainer/Project/service";
import type {ButtonType} from "antd/es/button/buttonHelpers";
import {connect, useRequest} from "@@/exports";
import {API_CONSTANTS} from "@/services/endpoints";
import {buildProjectTree, generateList, searchInTree} from "@/pages/DataStudio/LeftContainer/Project/function";
import {TaskOwnerLockingStrategy} from "@/types/SettingCenter/data.d";
import {useModel} from "@umijs/max";
import {debounce} from "@/utils/function";
import {LayoutState} from "@/pages/DataStudioNew/model";
import {mapDispatchToProps} from "@/pages/DataStudioNew/DvaFunction";
import {useLayoutEffect} from "react-dev-inspector/es/Inspector/hooks";

export const Project: React.FC<connect> = (props: any) => {
  const {project: {expandKeys, selectedKeys}, updateProject, action: {actionType, params}} = props;
  const {initialState} = useModel('@@initialState');

  const [searchValue, setSearchValueValue] = useState('');
  const [treeData, setTreeData] = useState<[]>([])
  const [sortData, setSortData] = useState()
  const [sortState, setSortState] = useState<{
    sortIconType: ButtonType;
    selectedSortValue: string[];
  }>({
    sortIconType: 'text' as ButtonType,
    selectedSortValue: []
  });

  useEffect(() => {
    // 项目折叠
    if (actionType == 'project-collapse-all') {
      updateProject({expandKeys: []})
    } else if (actionType === "project-expand-all") {
      // 项目展开
      const expand =generateList(data, []).map((item) => item.key)
      updateProject({expandKeys:expand })
    }
  }, [actionType, params]);


  const [selectCatalogueSortTypeData, setSelectCatalogueSortTypeData] = useState<{
    sortValue: string;
    sortType: string;
  }>({
    sortValue: '',
    sortType: ''
  });

  const {loading, data, refresh} = useRequest(
    {
      url: API_CONSTANTS.CATALOGUE_GET_CATALOGUE_TREE_DATA,
      data: {...selectCatalogueSortTypeData},
      method: "post"
    }
  );
  // tree数据初始化
  useAsyncEffect(async () => {
    if (data) {
      setTreeData(buildProjectTree(
        data,
        searchValue,
        [],
        initialState?.currentUser?.user,
        TaskOwnerLockingStrategy.ALL,
        []
      ))
    }
  }, [data, searchValue])
  useEffect(() => {
    if (data) {
      refresh()
    }
  }, [selectCatalogueSortTypeData])


  // 数据初始化
  useAsyncEffect(async () => {
    const sortData = await getTaskSortTypeData()
    setSortData(sortData)
  }, [])


  const ref = useRef<HTMLDivElement>(null);
  const [tooltipHeight, setTooltipHeight] = useState(0);

  useLayoutEffect(() => {
    const {height} = ref.current!!.getBoundingClientRect();
    console.log(height)
    setTooltipHeight(height - 55);
  }, [])
  const onChangeSearch = (e: any) => {
    let {value} = e.target;
    if (!value) {
      // dispatch({
      //     type: STUDIO_MODEL.updateProjectExpandKey,
      //     payload: []
      // });
      updateProject({expandKeys: [], selectedKeys: []})
      setSearchValueValue(value);
      return;
    }
    const expandedKeys: string[] = searchInTree(
      generateList(data, []),
      data,
      String(value).trim(),
      'contain'
    );
    // dispatch({
    //     type: STUDIO_MODEL.updateProjectExpandKey,
    //     payload: expandedKeys
    // });
    updateProject({expandKeys: expandedKeys, selectedKeys: []})

    setSearchValueValue(value);
  };


  function buildSortTreeOptions(trees: TreeVo[] = []): ItemType[] {
    return trees.map((tree) => {
      return {
        key: tree.value,
        label: tree.name,
        children: tree?.children && buildSortTreeOptions(tree.children)
      };
    });
  }

  const onClick: MenuProps['onClick'] = (e) => {
    const selectSortValue = e.key;
    const sortField: string = selectSortValue.substring(0, selectSortValue.lastIndexOf('_'));
    const sortType: string = selectSortValue.substring(selectSortValue.lastIndexOf('_') + 1);
    if (
      sortField == selectCatalogueSortTypeData.sortValue &&
      sortType == selectCatalogueSortTypeData.sortType
    ) {
      setSortState((prevState) => ({
        ...prevState,
        selectedSortValue: []
      }));
      setSelectCatalogueSortTypeData({
        sortValue: '',
        sortType: ''
      })
    } else {
      setSortState((prevState) => ({
        ...prevState,
        selectedSortValue: [selectSortValue]
      }));
      setSelectCatalogueSortTypeData({
        sortValue: sortField,
        sortType: sortType
      })
    }
  };
  return (
    <Flex vertical style={{paddingInline: 5, height: 'inherit'}} ref={ref}><Skeleton loading={loading} active
                                                                                     title={false}
                                                                                     paragraph={{
                                                                                       rows: 10,
                                                                                       width: '100%'
                                                                                     }}>
      <Col>
        <Flex gap={8} justify={'center'} align={'center'}>
          <Search
            style={{margin: '8px 0px'}}
            placeholder={l('global.search.text')}
            onChange={debounce(onChangeSearch, 300)}
            allowClear={true}
            defaultValue={searchValue}
          />
          <Dropdown
            menu={{
              items: buildSortTreeOptions(sortData),
              selectable: true,
              onClick: onClick,
              selectedKeys: sortState.selectedSortValue
            }}
            placement='bottomLeft'
          >
            <Button icon={<SortAscendingOutlined/>} type={sortState.sortIconType}></Button>
          </Dropdown>
        </Flex>

        <Divider style={{margin: 3}}/>
      </Col>

      {data?.length ? (
        <DirectoryTree
          showLine
          switcherIcon={<DownOutlined/>}
          className={'treeList'}
          // height={tooltipHeight}
          // onSelect={(_, info) => onNodeClick(info)}
          // onRightClick={onRightClick}
          expandedKeys={expandKeys}
          expandAction={'doubleClick'}
          selectedKeys={selectedKeys}
          // onExpand={onExpand}
          treeData={treeData}
        />
      ) : (
        <Empty
          className={'code-content-empty'}
          description={l('datastudio.project.create.folder.tip')}
        />
      )}
    </Skeleton>
    </Flex>);
}
export default connect(
  ({DataStudio}: { DataStudio: LayoutState }) => ({
    project: DataStudio.toolbar.project,
    action: DataStudio.action
  }), mapDispatchToProps)(Project);
// export default Project;
