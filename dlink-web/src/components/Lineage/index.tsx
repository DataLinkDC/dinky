import {LineageTable} from 'react-lineage-dag';
import {useEffect, useState, useRef} from "react";
import LineageOps from "@/components/Lineage/LineageOps";


export const getInit = () => {
  return {
    tables: [],
    relations: []
  }
};

const Lineage = (props: any) => {

  const {datas} = props;

  const cvsRef = useRef(null);
  const [data, setData] = useState(getInit());
  const [allData, setAllData] = useState(getInit());
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
    switch (action) {
      case 'expand': {
        const table = data.tables.find(t => t.id === tableId);
        table.isExpand = true;
        const children = getChildren(tableId);
        children.tables.forEach(table => {
          if(data.tables.some(t => t.id === table.id)) {
            return;
          }
          data.tables.push(table);
        });
        children.relations.forEach(relation => {
          if(data.relations.some(r => r.id === relation.id)) {
            return;
          }
          data.relations.push(relation);
        });
        setData({...data});
        break;
      }
      case 'shrink': {
        const table = data.tables.find(t => t.id === tableId);
        table.isExpand = false;
        const children = getChildren(tableId);
        children.tables.forEach(table => {
          const index = data.tables.findIndex(t => t.id === table.id);
          data.tables.splice(index, 1);
        });
        children.relations.forEach(relation => {
          const index = data.relations.findIndex(r => r.id === relation.id);
          data.relations.splice(index, 1);
        });
        setData({...data});
        break;
      }
      case 'fold': {
        data.tables.forEach(table => {
          if (table.id !== tableId) {
            return;
          }
          table.isFold = false;
        });
        data.tables = [...data.tables];
        setData({...data});
        break;
      }
      case 'unfold': {
        data.tables.forEach(table => {
          if (table.id !== tableId) {
            return;
          }
          table.isFold = true;
        });
        data.tables = [...data.tables];
        setData({...data});
        break;
      }
    }
  };

  const getData = () => {
    setData(datas);
    let newDatas = {
      tables: [...datas.tables],
      relations: [...datas.relations]
    };
    setAllData(newDatas);
  };

  useEffect(() => {
    getData();
  }, [datas]);

  data.tables.forEach(table => {
    table.operators = LineageOps({
      isExpand: !!table.isExpand,
      isFold: !!table.isFold,
      onAction,
      tableId: table.id
    })
  });

  return (<LineageTable {...data}
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
