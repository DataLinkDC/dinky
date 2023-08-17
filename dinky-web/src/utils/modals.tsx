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

// ================================ About Modal Compents ================================

import {l} from "@/utils/intl";
import React from "react";
import ErrorShowModal from "@/components/Modal/ErrorModalShow";
import {createRoot} from "react-dom/client";


/**
 * A function that displays a modal containing an error message in code editor.
 *
 * @param title The title of the modal.
 * @param content The content to display in the modal.
 */
export const ErrorModelWithCode = (title: any = l('global.error'), content: string) => {

  /** Create a new div element to mount the modal. */
  const modalRoot = document.createElement('div');

  /** Add the div element to the body of the document. */
  document.body.appendChild(modalRoot);
  const root = createRoot(modalRoot);
  root.render(
    <ErrorShowModal
      /** Function that runs after the modal is closed, to unmount the component and remove the modal from the DOM. */
      afterClose={() => root.unmount()}
      title={title}
      content={content}
    />
  )
}


// @ts-ignore
export const createModelTypes = <T extends any>(target: T): [{[K in keyof T['reducers']]: string}, {[K in keyof T['effects']]: string}] => {
  type TargetType = typeof target;

  // @ts-ignore
  const MODEL_SYNC: {[K in keyof TargetType['reducers']]: string} =  Object.fromEntries(// @ts-ignore
    new Map<keyof TargetType['reducers'], string>(Object.keys(target['reducers']).map((obj: string) => [obj, `${target.namespace}/${obj}`])));

  // @ts-ignore
  const MODEL_ASYNC: {[K in keyof TargetType['effects']]: string} =  Object.fromEntries(// @ts-ignore
    new Map<keyof TargetType['effects'], string>(Object.keys(target['effects']).map((obj: string) => [obj, `${target.namespace}/${obj}`])));

  return [MODEL_SYNC, MODEL_ASYNC];
};


