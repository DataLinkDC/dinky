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

interface BaseModelType {
  ['reducers']: {};
  ['effects']: {};
  ['namespace']: string;
}
type MemberType<T extends BaseModelType, U> = U extends 'effects' ? T['effects'] : T['reducers'];

function getModelTypes<T extends BaseModelType>(
  target: T,
  type: 'effects' | 'reducers'
): { [K in keyof MemberType<T, typeof type>]: string } {
  const reducers = Object.keys(target[type]).map((obj: string) => [
    obj,
    `${target.namespace}/${obj}`
  ]);
  // @ts-ignore
  return Object.fromEntries(new Map<keyof (typeof target)[type], string>(reducers));
}

export const createModelTypes = <T extends BaseModelType>(
  target: T
): [{ [K in keyof T['reducers']]: string }, { [K in keyof T['effects']]: string }] => {
  return [getModelTypes(target, 'reducers'), getModelTypes(target, 'effects')];
};
