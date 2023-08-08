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

import * as monaco from 'monaco-editor';
import keyWordJsonData from "./keyword.json";
import {Document, GlobalVar} from "@/types/RegCenter/data.d";
import {Column, ISuggestions, MetaData} from "@/components/CustomEditor/CodeEdit/data";




// todo: get sqlMetaData from interface
const buildMetaDataSuggestions = () => {
  let metaDataSuggestions: ISuggestions[] = [];
  let metaData: [] = []; // temp
  metaData.forEach((item: MetaData) => {
    metaDataSuggestions.push({
      label: item.table,
      kind: monaco.languages.CompletionItemKind.Constant,
      insertText: item.table,
      insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
      detail: "FlinkSQL Connector => " + item.connector
    });
    item.columns.forEach((column: Column) => {
      metaDataSuggestions.push({
        label: column.name,
        kind: monaco.languages.CompletionItemKind.Field,
        insertText: column.name,
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: "Column => " + column.type + " from " + item.table
      });
    });
  });
  return metaDataSuggestions;
};

// todo: get document from interface
const buildFullDocumentSuggestions = () => {
  let fullDocumentSuggestions: ISuggestions[] = [];
  let fillDocuments: [] = []; // temp
  fillDocuments.forEach((item: Document) => {
    fullDocumentSuggestions.push({
      label: item.name,
      kind: monaco.languages.CompletionItemKind.Snippet,
      insertText: item.fillValue,
      insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
      detail: item.description
    });
  });
  return fullDocumentSuggestions;
};

/**
 * get keyWordJson from {@link ./keyword.json}
 * @param type
 */
const parseKeyWordJson = (type: string) => {
  return JSON.parse(JSON.stringify(keyWordJsonData))[type];
};

/**
 *  build keyWordJsonSuggestions from {@link ./keyword.json}
 */
const buildKeyWordJsonSuggestions = () => {
  let keyWordJsonSuggestions: ISuggestions[] = [];
  // get all keys from keyWordJsonData
  Object.keys(keyWordJsonData).forEach((key: string) => {
    // parse data of key
    const array = parseKeyWordJson(key);
    // build suggestions
    array.forEach((item: string) => {
      keyWordJsonSuggestions.push({
        label: item,
        kind: monaco.languages.CompletionItemKind.Keyword,
        insertText: item,
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: key
      });
    });
  });
  return keyWordJsonSuggestions;
};


// todo: get globalVariable from interface
const buildGlobalVariableSuggestions = () => {
  let globalVariableSuggestions: ISuggestions[] = [];
  let globalVariables: [] = []; // temp
  globalVariables.forEach((item: GlobalVar) => {
    globalVariableSuggestions.push({
      label: item.name,
      kind: monaco.languages.CompletionItemKind.Variable,
      insertText: item.fragmentValue,
      insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
      detail: item.note
    });
  });
  return globalVariableSuggestions
};


/**
 * build all suggestions
 */
export const buildAllSuggestions = () => {
  let suggestions: ISuggestions[] = [];
  suggestions = suggestions
    .concat(buildMetaDataSuggestions()) // concat metaDataSuggestions
    .concat(buildFullDocumentSuggestions()) // concat fullDocumentSuggestions
    .concat(buildGlobalVariableSuggestions()) // concat globalVariableSuggestions
    .concat(buildKeyWordJsonSuggestions()); // concat keyWordJsonSuggestions
  return suggestions;
};
