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


import {LineageTable} from 'react-lineage-dag';
import {useState, useRef} from "react";
import LineageOps from "@/components/Lineage/LineageOps";
import * as _ from 'lodash';

export const getInit = () => {
  return {
    tables: [],
    relations: []
  }
};

const Lineage = (props: any) => {

  const {datas} = props;

  const cvsRef = useRef(null);
  const [data, setData] = useState(datas);
  const [allData, setAllData] = useState(datas);
  const [relayout, setRelayout] = useState(false);
  const [focus, setFocus] = useState(false);

  const getChildren = (tableId) => {
    const children = {
      tables: [],
      relations: []
    };
    allData.relations.forEach(relation => {
      if (relation.srcTableId !== tableId) {
        return;
      }
      children.relations.push(relation)
      const tgtTableId = relation.tgtTableId;
      if (children.tables.some(table => table.id === tgtTableId)) {
        return;
      }
      const table = allData.tables.find(table => table.id === tgtTableId);
      children.tables.push(table);
    });
    return children;
  };

  const onAction = (action, tableId) => {
    const newData = _.cloneDeep(data);
    switch (action) {
      case 'expand': {
        const table = newData.tables.find(t => t.id === tableId);
        table.isExpand = true;
        const children = getChildren(tableId);
        children.tables.forEach(table => {
          if (newData.tables.some(t => t.id === table.id)) {
            return;
          }
          newData.tables.push(table);
        });
        children.relations.forEach(relation => {
          if (newData.relations.some(r => r.id === relation.id)) {
            return;
          }
          newData.relations.push(relation);
        });
        setData({...newData});
        break;
      }
      case 'shrink': {
        const table = newData.tables.find(t => t.id === tableId);
        table.isExpand = false;
        const children = getChildren(tableId);
        children.tables.forEach(table => {
          const index = newData.tables.findIndex(t => t.id === table.id);
          newData.tables.splice(index, 1);
        });
        children.relations.forEach(relation => {
          const index = newData.relations.findIndex(r => r.id === relation.id);
          newData.relations.splice(index, 1);
        });
        setData({...newData});
        break;
      }
      case 'fold': {
        newData.tables.forEach(table => {
          if (table.id !== tableId) {
            return;
          }
          table.isFold = false;
        });
        newData.tables = [...newData.tables];
        setData({...newData});
        break;
      }
      case 'unfold': {
        newData.tables.forEach(table => {
          if (table.id !== tableId) {
            return;
          }
          table.isFold = true;
        });
        newData.tables = [...newData.tables];
        setData({...newData});
        break;
      }
    }
  };

  data.tables.forEach(table => {
    table.operators = LineageOps({
      isExpand: !!table.isExpand,
      isFold: !!table.isFold,
      onAction,
      tableId: table.id
    })
  });

  return (
    <LineageTable
      {...data}
      onLoaded={(canvas) => {
        cvsRef.current = canvas;
      }}
      onEachFrame={() => {
        if (!cvsRef.current) {
          return;
        }
        if (relayout) {
          cvsRef.current.relayout();
          setRelayout(false);
        }
        if (focus) {
          cvsRef.current.focusNode(focus);
          setFocus(false);
        }
      }}/>)
};

export default Lineage;
