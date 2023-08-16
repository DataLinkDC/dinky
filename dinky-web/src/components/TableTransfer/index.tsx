/*
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


import {TransferProps} from "antd/es/transfer";
import {TableRowSelection} from "antd/es/table/interface";
import {Transfer} from "antd";
import {difference} from "lodash";
import {UserBaseInfo} from "@/types/User/data";
import {ProColumns, ProTable} from "@ant-design/pro-components";
import selected from "@antv/g2/src/interaction/action/element/selected";


/**
 * Customize Table Transfer Props
 */
interface TableTransferProps extends TransferProps<any> {
  dataSource: any[];
  leftColumns: ProColumns< UserBaseInfo.User>[] | ProColumns<UserBaseInfo.Role>[];
  rightColumns: ProColumns< UserBaseInfo.User>[] | ProColumns<UserBaseInfo.Role>[];
}


/**
 * Customize Table Transfer
 * @param leftColumns Transfer left table columns
 * @param rightColumns Transfer right table columns
 * @param restProps Transfer props
 * @constructor
 */
const TableTransfer = ({leftColumns, rightColumns, ...restProps}: TableTransferProps) => (

  <Transfer
    showSelectAll={false}
    showSearch={true}
    {...restProps}
  >
    {
      ({
         direction,
         filteredItems,
         onItemSelectAll,
         onItemSelect,
         selectedKeys: listSelectedKeys,
         disabled: enableFlag,
       }) => {

        const columns = direction === "left" ? leftColumns : rightColumns;
        const rowSelection: TableRowSelection<any> = {
          getCheckboxProps: item => ({disabled: enableFlag || item.isDelete || (item.hasOwnProperty("enabled") ? !item.enabled : false)}),
          onSelectAll:  (selected, selectedRows) => {

            const treeSelectedKeys = selectedRows.filter(item => !item.isDelete || !item.enabled).map(({id}) => id);

            const diffKeys = selected ? difference(treeSelectedKeys, listSelectedKeys ) : difference(listSelectedKeys, treeSelectedKeys);

            onItemSelectAll(diffKeys, selected);
          },
          onSelect:  (item, selected) => {
            onItemSelect(item.id, selected);
          },
          selectedRowKeys: listSelectedKeys,
        };
        return (<>
              <ProTable
                size="small" ghost
                toolBarRender={undefined}
                rowSelection={rowSelection}
                rowKey={record => record.id}
                columns={columns}
                search={false}
                options={false}
                tableAlertRender={false}
                dataSource={filteredItems}
                pagination={{pageSize: 10, showSizeChanger: false, hideOnSinglePage: true,}}
                style={{height: "50vh", overflowY: "auto", pointerEvents: enableFlag ? "none" : undefined}}
                onRow={ item => ({
                  onClick: () => {
                    // Since the attributes in different objects are different, it is necessary to determine whether there are corresponding attributes in the item
                    if ((item.hasOwnProperty("isDelete") ? item.isDelete : true) || ((item.hasOwnProperty("enabled") ? !item.enabled : false))) return;
                    onItemSelect(item.id, !listSelectedKeys.includes(item.id));
                  },
                })}
              />
          </>
        );
      }}
  </Transfer>
);

export default TableTransfer;
