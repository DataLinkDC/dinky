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
import {ColumnsType, TableRowSelection} from "antd/es/table/interface";
import {Table, Transfer} from "antd";
import difference from "lodash/difference";
import {NORMAL_TABLE_OPTIONS} from "@/services/constants";


/**
 * Customize Table Transfer Props
 */
interface TableTransferProps extends TransferProps<any> {
  dataSource: any[];
  leftColumns: ColumnsType<any>;
  rightColumns: ColumnsType<any>;
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
          onSelectAll: function (selected, selectedRows) {

            const treeSelectedKeys = selectedRows
              .filter(item => !item.isDelete || !item.enabled)
              .map(({id}) => id);

            const diffKeys = selected
              ? difference(treeSelectedKeys, listSelectedKeys as any)
              : difference(listSelectedKeys, treeSelectedKeys as any);

            onItemSelectAll(diffKeys as string[], selected);
          },

          onSelect(item, selected) {
            onItemSelect(item.id as any, selected);
          },
          selectedRowKeys: listSelectedKeys,
        };
        return (<>
              <Table
                {...NORMAL_TABLE_OPTIONS}
                size="large"
                rowSelection={rowSelection}
                columns={columns}
                dataSource={filteredItems}
                style={{
                  height: "50vh",
                  pointerEvents: enableFlag ? "none" : undefined
                }}
                onRow={(item) => ({
                  onClick: () => {
                    // Since the attributes in different objects are different, it is necessary to determine whether there are corresponding attributes in the item
                    if ((item.hasOwnProperty("isDelete") ? item.isDelete : true) || (item.hasOwnProperty("enabled") ? !item.enabled : false)) return;
                    onItemSelect(item.id as any, !listSelectedKeys.includes(item.id as any));
                  },
                })}
              />
          </>
        );
      }}
  </Transfer>
);

export default TableTransfer;
