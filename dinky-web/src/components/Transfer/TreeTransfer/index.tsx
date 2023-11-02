import React, {Key} from 'react';
import {theme, Transfer, Tree} from 'antd';
import type {TransferDirection, TransferItem} from 'antd/es/transfer';
import type {DataNode} from 'antd/es/tree';

interface TreeTransferProps {
    dataSource: DataNode[];
    targetKeys: Key[];
    onChange?: (targetKeys: Key[], direction: TransferDirection, moveKeys: string[]) => void;
}

const isChecked = (selectedKeys: React.Key[], eventKey: React.Key) =>
    selectedKeys.includes(eventKey);
const generateTree = (treeNodes: DataNode[] = [], checkedKeys: Key[] = []): DataNode[] =>
    treeNodes.map(({children, ...props}) => ({
        ...props,
        disabled: checkedKeys.includes(props.key as string),
        children: generateTree(children, checkedKeys),
    }));
export const TreeTransfer: React.FC<TreeTransferProps> = ({dataSource, targetKeys, ...restProps}) => {
    const {token} = theme.useToken();

    const transferDataSource: TransferItem[] = [];

    function flatten(list: DataNode[] = []) {
        list.forEach((item) => {
            transferDataSource.push(item as TransferItem);
            flatten(item.children);
        });
    }

    flatten(dataSource);

    return (
        <Transfer
            {...restProps}
            // @ts-ignore Don't worry about it here
            targetKeys={targetKeys}
            dataSource={transferDataSource}
            className="tree-transfer"
            render={(item) => item.path}
            showSelectAll={false}
        >
            {({direction, onItemSelect, selectedKeys}) => {
                if (direction === 'left') {
                    const checkedKeys = [...selectedKeys, ...targetKeys];
                    return (
                        <div style={{padding: token.paddingXS}}>
                            <Tree
                                blockNode
                                checkable
                                checkStrictly
                                defaultExpandAll
                                checkedKeys={checkedKeys}
                                treeData={generateTree(dataSource, targetKeys)}
                                onCheck={(_, {node: {key}}) => {
                                    onItemSelect(key as string, !isChecked(checkedKeys, key));
                                }}
                                onSelect={(_, {node: {key}}) => {
                                    onItemSelect(key as string, !isChecked(checkedKeys, key));
                                }}
                            />
                        </div>
                    );
                }
            }}
        </Transfer>
    );
};
