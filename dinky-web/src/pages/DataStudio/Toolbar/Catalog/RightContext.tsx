/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { MenuInfo } from 'rc-menu/es/interface';
import React, { useEffect, useState } from 'react';
import { InitCatalogTreeState } from '@/types/Studio/init.d';
import { CatalogTreeState } from '@/types/Studio/state';
import { Modal, Typography } from 'antd';
import { RightContextMenuState } from '@/pages/DataStudio/data.d';
import { InitContextMenuPosition } from '@/pages/DataStudio/function';
import RightContextMenu from '@/pages/DataStudio/RightContextMenu';
import { l } from '@/utils/intl';
import { dropMSTable } from '@/pages/DataStudio/Toolbar/Catalog/service';
import { CatalogState } from '@/pages/DataStudio/Toolbar/Catalog/data';
import { TABLE_RIGHT_MENU } from '@/pages/DataStudio/Toolbar/Catalog/constant';

const { Text } = Typography;
export type RightContextProps = {
  refreshMetaStoreTables: () => Promise<any>;
  catalogState: CatalogState | undefined;
};
export const useRightContext = (props: RightContextProps) => {
  const { refreshMetaStoreTables, catalogState } = props;

  const [rightContextMenuState, setRightContextMenuState] = useState<RightContextMenuState>({
    show: false,
    position: InitContextMenuPosition
  });
  const [catalogTreeState, setCatalogTreeState] = useState<CatalogTreeState>(InitCatalogTreeState);
  useEffect(() => {
    setCatalogTreeState((prevState) => ({
      ...prevState,
      menuItems: []
    }));
  }, []);
  /**
   * the right click event
   * @param info
   */
  const handleCatalogRightClick = (info: any) => {
    const {
      node: { isLeaf, fullInfo, isTable, isView },
      node
    } = info;
    setCatalogTreeState((prevState) => ({
      ...prevState,
      isLeaf: isLeaf,
      menuItems: isTable || isView ? TABLE_RIGHT_MENU() : [],
      contextMenuOpen: true,
      rightClickedNode: { ...node, ...fullInfo },
      value: fullInfo
    }));
  };

  const handleContextCancel = () => {
    setCatalogTreeState((prevState) => ({
      ...prevState,
      contextMenuOpen: false
    }));
  };

  const handleDeleteSubmit = async () => {
    const { key: table, catalog, schema: database } = catalogTreeState.rightClickedNode;
    const { envId, dialect } = catalogState ?? {};

    handleContextCancel();
    Modal.confirm({
      title: l('datastudio.catalog.delete.table', '', { catalog, database, table }),
      width: '30%',
      content: (
        <Text className={'needWrap'} type='danger'>
          {l('datastudio.catalog.delete.table.confirm')}
        </Text>
      ),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        await dropMSTable({
          envId,
          catalog,
          database,
          table,
          dialect
        });
        await refreshMetaStoreTables();
      }
    });
  };
  const handleMenuClick = async (node: MenuInfo) => {
    setCatalogTreeState((prevState) => ({ ...prevState, rightActiveKey: node.key }));
    switch (node.key) {
      case 'delete':
        await handleDeleteSubmit();
        break;
      default:
        handleContextCancel();
        break;
    }
  };

  return {
    RightContent: (
      <>
        <RightContextMenu
          contextMenuPosition={rightContextMenuState.position}
          open={rightContextMenuState.show}
          openChange={() =>
            setRightContextMenuState((prevState) => ({ ...prevState, show: false }))
          }
          items={catalogTreeState.menuItems}
          onClick={handleMenuClick}
        />
      </>
    ),
    setRightContextMenuState,
    handleCatalogRightClick
  };
};
