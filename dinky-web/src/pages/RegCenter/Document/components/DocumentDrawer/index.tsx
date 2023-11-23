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

import DocumentDesc from '@/pages/RegCenter/Document/components/DocumentDrawer/DocumentDesc';
import { Document } from '@/types/RegCenter/data';
import { Drawer } from 'antd';
import React from 'react';

type DocumentDrawerProps = {
  onCancel: (flag?: boolean) => void;
  values: Partial<Document>;
  modalVisible: boolean;
  columns: any;
};
const DocumentDrawer: React.FC<DocumentDrawerProps> = (props) => {
  const { onCancel: handleCancel, values, modalVisible, columns } = props;

  return (
    <>
      <Drawer width={'50%'} open={modalVisible} onClose={() => handleCancel(false)}>
        <DocumentDesc values={values} columns={columns} />
      </Drawer>
    </>
  );
};

export default DocumentDrawer;
