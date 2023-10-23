import { TableDataNode } from '@/pages/DataStudio/LeftContainer/Catalog/data';
import { MetaStoreTableType } from '@/pages/DataStudio/model';
import { BtnRoute } from '@/pages/DataStudio/route';
import ColumnInfo from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SchemaDesc/ColumnInfo';
import TableInfo from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SchemaDesc/TableInfo';
import { l } from '@/utils/intl';
import { useRequest } from '@@/exports';
import {
  AppstoreOutlined,
  BlockOutlined,
  CodepenOutlined,
  DownOutlined,
  FunctionOutlined,
  TableOutlined
} from '@ant-design/icons';
import { Button, Col, Empty, Modal, Row, Select, Spin, Tabs } from 'antd';
import { DataNode } from 'antd/es/tree';
import DirectoryTree from 'antd/es/tree/DirectoryTree';
import { DefaultOptionType } from 'rc-select/lib/Select';
import React, { useEffect, useState } from 'react';
import { getMSCatalogs, getMSFlinkColumns, getMSSchemaInfo } from './service';

export const Catalog: React.FC = (props: any) => {
  const data = useRequest({
    url: '/api/task/listFlinkSQLEnv'
  });
  const [envId, setEnvId] = useState<number>();
  const [catalogSelect, setCatalogSelect] = useState<DefaultOptionType[]>([]);
  const [catalog, setCatalog] = useState<string>('default_catalog');
  const [database, setDatabase] = useState<string>('');
  const [table, setTable] = useState<string>('');
  const [treeData, setTreeData] = useState<DataNode[]>([]);
  const [modalVisit, setModalVisit] = useState(false);
  const [row, setRow] = useState<TableDataNode>();
  const [loading, setLoading] = useState<boolean>(false);
  const [columnData, setColumnData] = useState([]);

  BtnRoute['menu.datastudio.catalog'][0].onClick = () => {
    refreshMetaStoreTables();
  };

  useEffect(() => {
    if (envId) {
      setLoading(true);
      setTreeData([]);
      setCatalogSelect([]);
      setDatabase('');
      let param = {
        envId: envId,
        fragment: true,
        dialect: 'FlinkSqlEnv'
      };
      getMSCatalogs(param).then((d) => {
        setCatalogSelect(
          (d as any[]).map((item) => {
            setLoading(false);
            return {
              label: item.name,
              options: (item.schemas as any[]).map((schema) => {
                return {
                  label: schema.name,
                  value: item.name + '.' + schema.name
                };
              })
            };
          })
        );
      });
    }
  }, [envId]);

  useEffect(() => {
    if (table) {
      setLoading(true);
      setColumnData([]);
      getMSFlinkColumns({
        envId,
        catalog,
        database,
        table
      }).then((res) => {
        setLoading(false);
        setColumnData(res);
      });
    }
  }, [table]);

  const onRefreshTreeData = (catalogAndDatabase: string) => {
    setTreeData([]);
    setLoading(true);
    const names = catalogAndDatabase.split('.');
    let catalogTmp = 'default_catalog';
    let databaseTmp = 'default_database';
    if (names.length > 1) {
      catalogTmp = names[0];
      databaseTmp = names[1];
    } else if (names.length == 1) {
      databaseTmp = names[0];
    }
    setCatalog(catalogTmp);
    setDatabase(databaseTmp);
    let param = {
      envId: envId,
      fragment: true,
      dialect: 'FlinkSqlEnv',
      catalog: catalogTmp,
      database: databaseTmp
    };
    const result = getMSSchemaInfo(param);
    result.then((res) => {
      setLoading(false);
      const tables: MetaStoreTableType[] = [];
      if (res.tables) {
        for (let i = 0; i < res.tables.length; i++) {
          tables.push({
            name: res.tables[i].name,
            columns: res.tables[i].columns
          });
        }
      }
      const treeDataTmp: DataNode[] = [];
      const tablesData: TableDataNode[] = [];
      for (let i = 0; i < tables.length; i++) {
        tablesData.push({
          title: tables[i].name,
          key: tables[i].name,
          icon: <TableOutlined />,
          isLeaf: true,
          isTable: true,
          name: tables[i].name,
          schema: databaseTmp,
          catalog: catalog,
          comment: '-',
          type: '',
          engine: 'Flink',
          options: '',
          rows: -1,
          createTime: '',
          updateTime: ''
        });
      }
      treeDataTmp.push({
        title: 'tables',
        key: 'tables',
        children: tablesData
      });

      const viewsData: DataNode[] = [];
      if (res.views) {
        for (let i = 0; i < res.views.length; i++) {
          viewsData.push({
            title: res.views[i],
            key: res.views[i],
            icon: <BlockOutlined />,
            isLeaf: true
          });
        }
      }
      treeDataTmp.push({
        title: 'views',
        key: 'views',
        children: viewsData
      });

      const functionsData: DataNode[] = [];
      if (res.functions) {
        for (let i = 0; i < res.functions.length; i++) {
          functionsData.push({
            title: res.functions[i],
            key: res.functions[i],
            icon: <FunctionOutlined />,
            isLeaf: true
          });
        }
      }
      treeDataTmp.push({
        title: 'functions',
        key: 'functions',
        children: functionsData
      });

      const userFunctionsData: DataNode[] = [];
      if (res.userFunctions) {
        for (let i = 0; i < res.userFunctions.length; i++) {
          userFunctionsData.push({
            title: res.userFunctions[i],
            key: res.userFunctions[i],
            icon: <FunctionOutlined />,
            isLeaf: true
          });
        }
      }
      treeDataTmp.push({
        title: 'user functions',
        key: 'userFunctions',
        children: userFunctionsData
      });

      const modulesData: DataNode[] = [];
      if (res.modules) {
        for (let i = 0; i < res.modules.length; i++) {
          modulesData.push({
            title: res.modules[i],
            key: res.modules[i],
            icon: <AppstoreOutlined />,
            isLeaf: true
          });
        }
      }
      treeDataTmp.push({
        title: 'modules',
        key: 'modules',
        children: modulesData
      });

      setTreeData(treeDataTmp);
    });
  };

  const refreshMetaStoreTables = () => {
    onRefreshTreeData(catalog + '.' + database);
  };

  const onChangeMetaStoreCatalogs = (value: string) => {
    onRefreshTreeData(value);
  };

  const openColumnInfo = (e: React.MouseEvent, node: TableDataNode) => {
    if (node.isLeaf && node.isTable) {
      setTable(node.name);
      setRow(node);
      setModalVisit(true);
    }
  };

  const cancelHandle = () => {
    setRow(undefined);
    setModalVisit(false);
  };

  return (
    <Spin spinning={loading}>
      <div style={{ paddingInline: 10 }}>
        <Row>
          <Col span={24}>
            <Select
              placeholder={l('pages.datastudio.catalog.flinkSqlEnvSelect')}
              style={{ width: '100%' }}
              onChange={setEnvId}
              options={(data.data as any[])?.map((x) => {
                return { value: x.id, label: x.name };
              })}
            />
            <Select
              value={database ? database : null}
              style={{ width: '100%' }}
              placeholder={l('pages.datastudio.catalog.catalogSelect')}
              optionLabelProp='label'
              onChange={onChangeMetaStoreCatalogs}
              options={catalogSelect}
            />
          </Col>
        </Row>

        {treeData.length > 0 ? (
          <DirectoryTree
            showIcon
            switcherIcon={<DownOutlined />}
            treeData={treeData}
            onRightClick={({ event, node }: any) => {
              openColumnInfo(event, node);
            }}
          />
        ) : (
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
        )}
      </div>
      <Modal
        title={<>{row?.key}</> ?? <></>}
        open={modalVisit}
        width={10000}
        onCancel={() => {
          cancelHandle();
        }}
        footer={[
          <Button
            key='back'
            onClick={() => {
              cancelHandle();
            }}
          >
            {l('button.close')}
          </Button>
        ]}
      >
        <Tabs
          defaultActiveKey='tableInfo'
          size='small'
          items={[
            {
              key: 'tableInfo',
              label: (
                <span>
                  <TableOutlined />
                  {l('pages.datastudio.catalog.tableInfo')}
                </span>
              ),
              children: row ? (
                <TableInfo tableInfo={row} />
              ) : (
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              )
            },
            {
              key: 'columnsInfo',
              label: (
                <span>
                  <CodepenOutlined />
                  {l('pages.datastudio.catalog.fieldInformation')}
                </span>
              ),
              children: <Spin spinning={loading}>{<ColumnInfo columnInfo={columnData} />}</Spin>
            }
          ]}
        />
      </Modal>
    </Spin>
  );
};
