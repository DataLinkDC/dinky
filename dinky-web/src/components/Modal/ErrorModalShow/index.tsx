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

import CodeShow, { CodeShowFormProps } from '@/components/CustomEditor/CodeShow';
import { MonacoEditorOptions } from '@/types/Public/data';
import { l } from '@/utils/intl';
import { Button, Modal, ModalFuncProps, Typography } from 'antd';
import { useState } from 'react';

const { Paragraph } = Typography;

/**
 * In this interface, we define the props for the ShowModal component, which extends the ModalFuncProps interface. Here are the details of the props:
 *
 * codeShowProps (optional): An object containing properties for configuring the code editor that displays in the modal.
 * This prop is optional, so it's marked with the ? character.
 * content: The content to display in the modal. This prop is required and doesn't have a default value.
 * */
export interface ShowModalProps extends ModalFuncProps {
  /** Properties for configuring the code editor that displays in the modal. */
  codeShowProps?: CodeShowFormProps;

  /** The content to display in the code editor. */
  content: string;
}

const ErrorShowModal = (props: ShowModalProps) => {
  const {
    /** The content of the code editor. */
    content = 'no code to show',

    /** The width of the Modal. */
    width = '80vh',

    /** Control the Modal show or close */
    open = true,

    /** The title of the Modal.*/
    title = l('global.error'),

    /** Default disable close icon at top right, it's Not needed */
    closable = false,

    /**
     * The properties of the code editor, including its default options.
     */
    codeShowProps = {
      /** Whether line numbers should be shown in the code editor. Default is "off". */
      lineNumbers: 'off',

      /** The height of the code editor. */
      height: '50vh',

      /**
       * The options to configure the Monaco Editor instance. The default options are specified
       * in a separate object named MonacoEditorOptions, and the minimap is disabled by default.
       */
      options: {
        ...MonacoEditorOptions, // set default options
        minimap: { enabled: false }
      }
    }
  } = props;

  const [showModal, setShowModal] = useState<boolean>(open);

  return (
    <Modal
      {...props}
      width={width}
      open={showModal}
      title={title}
      closable={closable}
      footer={
        <Button type='primary' htmlType={'submit'} autoFocus onClick={() => setShowModal(false)}>
          {l('global.notification.iknow')}
        </Button>
      }
    >
      <Paragraph ellipsis={{ rows: 1, expandable: false }}>
        <blockquote>{content}</blockquote>
      </Paragraph>

      <CodeShow {...codeShowProps} code={content} />
    </Modal>
  );
};

export default ErrorShowModal;
