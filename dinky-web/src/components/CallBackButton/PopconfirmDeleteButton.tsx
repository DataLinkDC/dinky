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

import {l} from "@/utils/intl";
import {Button, Popconfirm} from "antd";
import React from "react";
import {DangerDeleteIcon} from "@/components/Icons/CustomIcons";

type PopconfirmProps = {
  onClick: () => void;
  description: string;
};

export const PopconfirmDeleteButton: React.FC<PopconfirmProps> = (props) => {
  const {onClick, description} = props;

  return (
    <Popconfirm
      placement="topRight"
      title={l("button.delete")}
      description={description}
      onConfirm={onClick}
      okText={l("button.confirm")}
      cancelText={l("button.cancel")}
    >
      <Button key={'DeleteIcon'} icon={<DangerDeleteIcon/>}/>
    </Popconfirm>
  );
};
