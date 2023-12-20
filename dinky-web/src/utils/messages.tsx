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

import { l } from '@/utils/intl';
import { ErrorModelWithCode } from '@/utils/modals';
import { InfoCircleOutlined } from '@ant-design/icons';
import { Button, message, notification, Typography } from 'antd';

const { Paragraph } = Typography;
/**
 * default params
 */
const defaultDuration = 1;
const AsyncDuration = 1;

// ================================ About Notification Component ================================

/**
 * A function that displays an error notification with a short message and an option to view the full error message in a modal.
 *
 * @param description The short error message to display in the notification.
 * @param message The title of the notification. This parameter is optional, and if no value is provided, it defaults to a string.
 * @param duration The duration (in milliseconds) that the notification should be displayed for.
 *                  This parameter is optional, and if no value is provided, it defaults to a default duration.
 */
export const ErrorNotification = (
  description: string,
  message = l('global.error'),
  duration: number | null = defaultDuration
) => {
  /** The title of the notification, with an icon and the provided message. */
  const title = (
    <div style={{ display: 'flex' }}>
      <InfoCircleOutlined style={{ marginRight: 10, color: 'red' }} />
      <b>{message}</b>
    </div>
  );
  /**
   * A button that is displayed in the notification
   * if the error message is longer than 40 characters, click it will create a Modal to show more information detail
   */
  const moreBtn =
    description.length > 40 ? (
      <Button type='primary' danger onClick={() => ErrorModelWithCode(title, description)}>
        {l('global.notification.full-error')}
      </Button>
    ) : (
      <></>
    );

  /** Display the notification. */
  notification.open({
    /** The title of the notification. */
    message: title,

    /**
     * The short error message to display in the notification.
     * if the error message is longer than 40 characters,Parts beyond the length are hidden
     */
    description: <Paragraph ellipsis={{ rows: 2 }}>{description}</Paragraph>,
    duration: duration,
    btn: moreBtn
  });
};

/**
 * Success handler message
 * @param message
 * @param description
 * @param duration
 * @param placement
 * @constructor
 */
export const SuccessNotification = (
  description: string,
  message = l('global.success'),
  duration = defaultDuration
) =>
  notification.success({
    message,
    description,
    duration
  });

/**
 * Warning handler message
 * @param message
 * @param description
 * @param duration
 * @param placement
 * @constructor
 */
export const WarningNotification = (
  description: string,
  message = l('global.warning'),
  duration = defaultDuration
) =>
  notification.warning({
    message,
    description,
    duration
  });

/**
 * Info handler message
 * @param message
 * @param description
 * @param duration
 * @param placement
 * @constructor
 */
export const InfoNotification = (
  description: string,
  message = l('global.info'),
  duration = defaultDuration
) =>
  notification.info({
    message,
    description,
    duration
  });

// ================================ About Message Component ================================

// ------------------------------- About Message Success -------------------------------
/**
 *  message handler success
 * @param content
 * @param duration {@link defaultDuration}
 * @constructor
 */
export const SuccessMessage = (content: string, duration = defaultDuration) =>
  message.success(content, duration);

/**
 *  message handler success async
 * @param content
 * @param duration {@link AsyncDuration}
 * @constructor
 */
export const SuccessMessageAsync = async (content: string, duration = AsyncDuration) =>
  await message.success(content, duration);

// ------------------------------- About Message Info -------------------------------

/**
 *  message handler info
 * @param content
 * @param duration {@link defaultDuration}
 * @constructor
 */
export const InfoMessage = (content: string, duration = defaultDuration) =>
  message.info(content, duration);

/**
 *  message handler info async
 * @param content
 * @param duration {@link AsyncDuration}
 * @constructor
 */
export const InfoMessageAsync = async (content: string, duration = AsyncDuration) =>
  await message.info(content, duration);

// ------------------------------- About Message Error -------------------------------

/**
 *  message error
 * @param content
 * @param duration {@link defaultDuration}
 * @constructor
 */
export const ErrorMessage = (content: string, duration = defaultDuration) =>
  message.error(content, duration);

/**
 *  message handler error async
 * @param content
 * @param duration {@link AsyncDuration}
 * @constructor
 */
export const ErrorMessageAsync = async (content: string, duration = AsyncDuration) =>
  await message.error(content, duration);

// ------------------------------- About Message Warning -------------------------------

/**
 *  message warning
 * @param content
 * @param duration {@link defaultDuration}
 * @constructor
 */
export const WarningMessage = (content: string, duration = defaultDuration) =>
  message.warning(content, duration);

/**
 *  message handler warning async
 * @param content
 * @param duration {@link AsyncDuration}
 * @constructor
 */
export const WarningMessageAsync = async (content: string, duration = AsyncDuration) =>
  await message.warning(content, duration);

// ------------------------------- About Message Loading -------------------------------

/**
 *  message loading
 * @param content
 * @param duration {@link defaultDuration}
 * @constructor
 */
export const LoadingMessage = (content: string, duration = defaultDuration) =>
  message.loading(content, duration);

/**
 *  message handler loading async
 * @param content
 * @param duration {@link AsyncDuration}
 * @constructor
 */
export const LoadingMessageAsync = async (content: string, duration = AsyncDuration) =>
  await message.loading(content, duration);
