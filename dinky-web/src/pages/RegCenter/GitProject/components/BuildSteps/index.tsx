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


import {Modal} from "antd";
import React from "react";
import {GitProject} from "@/types/RegCenter/data";
import {l} from "@/utils/intl";
import {AutoSteps} from "@/pages/RegCenter/GitProject/components/BuildSteps/AutoSteps";
import {createBroswerContext} from "use-sse";


/**
 * props
 */
type BuildStepsProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<GitProject>) => void;
  modalVisible: boolean;
  values: Partial<GitProject>;
};
const BroswerDataContext = createBroswerContext();


export const BuildSteps: React.FC<BuildStepsProps> = (props) => {


  /**
   * extract props
   */
  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
    values,
  } = props;


  return <>
    <Modal
      title={l("rc.gp.build")}
      width={"75%"}
      open={modalVisible}
      onCancel={() => handleModalVisible()}
      okButtonProps={{style: {display: "none"}}}
    >
      <BroswerDataContext>
        <AutoSteps/>
      </BroswerDataContext>
    </Modal>
  </>;
};
