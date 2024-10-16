import {Button, Col, Divider, Dropdown, Empty, Flex, MenuProps, Skeleton} from "antd";
import Search from "antd/es/input/Search";
import {l} from "@/utils/intl";
import {DownOutlined, SortAscendingOutlined} from "@ant-design/icons";
import React, {Key, useEffect, useRef, useState} from "react";
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
import {DataStudioActionType} from "@/pages/DataStudioNew/data.d";

export const Project: React.FC<connect> = (props: any) => {
  const {project: {expandKeys, selectedKeys}, action: {actionType, params},updateProject,updateAction,addCenterTab} = props;
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

  const ref = useRef<HTMLDivElement>(null);
  const [treeHeight, setTreeHeight] = useState(0);
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

  useEffect(() => {
    switch (actionType) {
      // 折叠
      case DataStudioActionType.PROJECT_COLLAPSE_ALL:
        updateProject({expandKeys: []})
        break;
      // 展开
      case DataStudioActionType.PROJECT_EXPAND_ALL:
        const expand = generateList(data, []).filter(item => !item.isLeaf).map((item) => item.key)
        updateProject({expandKeys: expand})
        break;
        // todo 右键没做
      case DataStudioActionType.PROJECT_RIGHT_CLICK:
        console.log('project-right-click', params)
        break;
    }

  }, [actionType, params]);

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
      // 这里需要再次设置expandKeys，因为网络延迟问题，导致第一次设置expandKeys无效
      updateProject({expandKeys: [...expandKeys]})
    }
  }, [data, searchValue])
  useEffect(() => {
    if (data) {
      refresh()
    }
  }, [selectCatalogueSortTypeData])


  // 数据初始化
  useEffect(() => {
    getTaskSortTypeData().then(setSortData)
    // 监控布局宽度高度变化，重新计算树的高度
    const element = ref.current!!;
    const observer = new ResizeObserver((entries) => {
      if (entries?.length === 1) {
        // 这里节点理应为一个，减去的高度是为搜索栏的高度
        setTreeHeight(entries[0].contentRect.height - 55);
      }
    });
    observer.observe(element);
    return () => observer.unobserve(element);
  }, [])


  const onChangeSearch = (e: any) => {
    let {value} = e.target;
    if (!value) {
      updateProject({expandKeys: [], selectedKeys: []})
      setSearchValueValue(value);
      return;
    }
    const expandedKeys: string[] = searchInTree(
      generateList(data, []),
      data,
      String(value).trim(),
      'contain'
    )
    updateProject({expandKeys: expandedKeys, selectedKeys: []})
    setSearchValueValue(value);
  };
  const onExpand = (expandedKeys: Key[]) => {
    updateProject({expandKeys: expandedKeys})
  };

  const onRightClick = (info: any) => {
    const {
      node: {isLeaf, key, fullInfo},
      node,
      event
    } = info;
    updateProject({selectedKeys: [key]})
    updateAction({actionType: DataStudioActionType.PROJECT_RIGHT_CLICK, params: {isLeaf, key}})
  };

  const onNodeClick = (info: any) => {
    // 选中的key
    const {
      node: {isLeaf, name, type, parentId, path, key, taskId}
    } = info;

    if (!isLeaf) {
      if (expandKeys?.includes(key)) {
        updateProject({expandKeys: expandKeys.filter((item: number) => item !== key)})
      } else {
        updateProject({expandKeys: [...expandKeys, key]})
      }
    } else {
      updateProject({selectedKeys: [key]})
      addCenterTab({
        id: 'project_' + key,
        title: name,
        tabType: "task",
        params:{
          taskId: taskId,
          key: key
        }
      })
    }
  }

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
    <Flex vertical style={{paddingInline: 5, height: 'inherit'}} ref={ref}>
      <Skeleton loading={loading} active
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
            height={treeHeight}
            onSelect={(_, info) => onNodeClick(info)}
            onRightClick={onRightClick}
            expandedKeys={expandKeys}
            expandAction={'doubleClick'}
            selectedKeys={selectedKeys}
            onExpand={onExpand}
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
