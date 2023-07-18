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


import {Dispatch} from "@@/plugin-dva/types";
import {DataSources} from "@/types/RegCenter/data";

export const mapDispatchToProps = (dispatch: Dispatch) => ({
  updateToolContentHeight: (key: number) => dispatch({
    type: "Studio/updateToolContentHeight",
    payload: key,
  }),
  updateCenterContentHeight: (key: number) => dispatch({
    type: "Studio/updateCenterContentHeight",
    payload: key,
  }),
  updateSelectLeftKey: (key: string) => dispatch({
    type: "Studio/updateSelectLeftKey",
    payload: key,
  }),
  updateLeftWidth: (width: number) => dispatch({
    type: "Studio/updateLeftWidth",
    payload: width,
  }), updateSelectRightKey: (key: string) => dispatch({
    type: "Studio/updateSelectRightKey",
    payload: key,
  }),
  updateRightWidth: (width: number) => dispatch({
    type: "Studio/updateRightWidth",
    payload: width,
  }), updateSelectBottomKey: (key: string) => dispatch({
    type: "Studio/updateSelectBottomKey",
    payload: key,
  }), updateSelectBottomSubKey: (key: string) => dispatch({
    type: "Studio/updateSelectBottomSubKey",
    payload: key,
  }),
  updateBottomHeight: (height: number) => dispatch({
    type: "Studio/updateBottomHeight",
    payload: height,
  }),
  saveDataBase: (data: DataSources.DataSource[]) => dispatch({
    type: "Studio/saveDataBase",
    payload: data,
  }),
  updateBottomConsole: (data: string) => dispatch({
    type: "Studio/updateBottomConsole",
    payload: data,
  }),

});
