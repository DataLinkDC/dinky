/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from "react";
import Icon, {DeleteTwoTone} from "@ant-design/icons";

/**
 *  This is a custom icon that is used to indicate a dangerous action.{@link DeleteTwoTone}
 * @constructor
 */
const IconStyle = {
  height: "16px",
  width: "16px",
};


export const DangerDeleteIcon = () => {
  return <>
    <DeleteTwoTone twoToneColor={"red"}/>
  </>;
};


export const ShowLogIcon = () => {
  return <>
    <Icon style={{...IconStyle}} component={() => (
      <svg className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
        <path
          d="M426.666667 834.133333c0-10.666667-8.533333-21.333333-21.333334-21.333333l-149.333333-2.133333c-23.466667 0-42.666667-19.2-42.666667-42.666667V256c0-23.466667 19.2-42.666667 42.666667-42.666667h426.666667c23.466667 0 42.666667 19.2 42.666666 42.666667v149.333333c0 12.8 8.533333 21.333333 21.333334 21.333334h42.666666c12.8 0 21.333333-8.533333 21.333334-21.333334v-192c0-46.933333-38.4-85.333333-85.333334-85.333333H213.333333c-46.933333 0-85.333333 38.4-85.333333 85.333333v597.333334c0 46.933333 38.4 85.333333 85.333333 85.333333h192c12.8 0 21.333333-10.666667 21.333334-21.333333v-40.533334z"
          fill="#1890ff"></path>
        <path
          d="M887.466667 868.266667l-91.733334-89.6c21.333333-34.133333 36.266667-74.666667 36.266667-117.333334 0-117.333333-96-213.333333-213.333333-213.333333s-213.333333 96-213.333334 213.333333 96 213.333333 213.333334 213.333334c42.666667 0 83.2-12.8 117.333333-36.266667l91.733333 89.6c8.533333 8.533333 21.333333 8.533333 29.866667 0l29.866667-29.866667c8.533333-8.533333 8.533333-21.333333 0-29.866666zM618.666667 789.333333c-70.4 0-128-57.6-128-128s57.6-128 128-128 128 57.6 128 128-57.6 128-128 128z"
          fill="#297AFF"></path>
        <path
          d="M576 384H320c-12.8 0-21.333333-8.533333-21.333333-21.333333v-42.666667c0-12.8 8.533333-21.333333 21.333333-21.333333h256c12.8 0 21.333333 8.533333 21.333333 21.333333v42.666667c0 12.8-8.533333 21.333333-21.333333 21.333333zM407.466667 512h-85.333334c-12.8 0-21.333333-8.533333-21.333333-21.333333v-42.666667c0-12.8 8.533333-21.333333 21.333333-21.333333h85.333334c12.8 0 21.333333 8.533333 21.333333 21.333333v42.666667c0 12.8-10.666667 21.333333-21.333333 21.333333z"
          fill="#1890ff"></path>
      </svg>
    )}/>
  </>;
};


export const ShowCodeTreeIcon = () => {
  return <>
    <Icon style={{...IconStyle}} component={() => (
      <svg className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
        <path
          d="M432.6912 784.6912h-199.68c-33.8944 0-61.44-25.6-61.44-57.088v-74.752a20.48 20.48 0 1 1 40.96 0v74.752c0 6.3488 8.7552 13.5168 20.48 13.5168h199.68a21.8112 21.8112 0 0 1 0 43.5712zM599.4496 784.6912h-61.44a21.8112 21.8112 0 0 1 0-43.5712h61.44a21.8112 21.8112 0 0 1 0 43.5712zM865.28 784.6912h-186.88a21.8112 21.8112 0 0 1 0-43.5712H865.28c11.6736 0 20.48-7.168 20.48-13.5168V180.4288c0-6.3488-8.7552-13.5168-20.48-13.5168H232.8576c-11.6736 0-20.48 7.168-20.48 13.5168v75.9296a20.48 20.48 0 1 1-40.96 0V180.4288c0-31.488 27.4432-57.088 61.184-57.088H865.28c33.792 0 61.44 25.6 61.44 57.088v547.1744c0 31.488-27.4432 57.088-61.44 57.088z"
          fill="#1296db"></path>
        <path
          d="M91.0336 405.3504h301.4144a20.48 20.48 0 0 1 20.48 20.48v133.7344a20.48 20.48 0 0 1-20.48 20.48H111.5136a20.48 20.48 0 0 1-20.48-20.48V405.3504zM311.3984 405.3504h-220.16v-48.7424a20.48 20.48 0 0 1 20.48-20.48h157.9008a20.48 20.48 0 0 1 19.2 13.0048z"
          fill="#1296db"></path>
        <path
          d="M369.9712 849.92m20.48 0l266.24 0q20.48 0 20.48 20.48l0 10.24q0 20.48-20.48 20.48l-266.24 0q-20.48 0-20.48-20.48l0-10.24q0-20.48 20.48-20.48Z"
          fill="#1296db"></path>
        <path
          d="M507.4944 307.1488m20.48 0l266.24 0q20.48 0 20.48 20.48l0 10.24q0 20.48-20.48 20.48l-266.24 0q-20.48 0-20.48-20.48l0-10.24q0-20.48 20.48-20.48Z"
          fill="#1296db"></path>
        <path
          d="M507.4944 428.3904m20.48 0l266.24 0q20.48 0 20.48 20.48l0 10.24q0 20.48-20.48 20.48l-266.24 0q-20.48 0-20.48-20.48l0-10.24q0-20.48 20.48-20.48Z"
          fill="#1296db"></path>
        <path
          d="M507.4944 549.6832m20.48 0l266.24 0q20.48 0 20.48 20.48l0 10.24q0 20.48-20.48 20.48l-266.24 0q-20.48 0-20.48-20.48l0-10.24q0-20.48 20.48-20.48Z"
          fill="#1296db"></path>
      </svg>
    )}/>
  </>;
};
