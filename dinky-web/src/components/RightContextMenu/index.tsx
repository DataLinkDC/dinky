/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import {Dropdown, Menu} from "antd";
import React from "react";
import {MenuInfo} from "rc-menu/es/interface";
import {MenuItemType} from "antd/es/menu/hooks/useItems";

type RightContextMenuProps = {
    onClick: (values: MenuInfo) => void;
    items: MenuItemType[];
    contextMenuPosition: any;
    open: boolean;
    openChange: () => void;
}

const RightContextMenu: React.FC<RightContextMenuProps> = (props) => {
    const {onClick,items,openChange,open,contextMenuPosition} = props;

    const menu = <Menu onClick={onClick} items={items}/>
    return <>
        <Dropdown
            arrow
            trigger={['contextMenu']}
            overlayStyle={{...contextMenuPosition}}
            overlay={menu}
            open={open}
            onVisibleChange={openChange}
        >
            {/*占位*/}
            <div style={{...contextMenuPosition}}/>
        </Dropdown>
    </>
}

export default RightContextMenu;