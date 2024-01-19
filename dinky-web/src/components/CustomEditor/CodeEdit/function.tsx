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

import { SuggestionInfo } from '@/types/Public/data';

import { loader } from '@monaco-editor/react';
import * as monaco from 'monaco-editor';
import { editor, languages } from 'monaco-editor';
import keyWordJsonData from './keyword.json';
import ITextModel = editor.ITextModel;
import ProviderResult = languages.ProviderResult;
import CompletionList = languages.CompletionList;
import CompletionItem = languages.CompletionItem;
// 导入 lodash
loader.config({ monaco });

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
export const buildKeyWordJsonSuggestions = (range: monaco.Range) => {
  // get all keys from keyWordJsonData

  let keyWordJsonSuggestions: CompletionItem[] = [];
  Object.keys(keyWordJsonData).map((key: string) => {
    // parse data of key
    const array = parseKeyWordJson(key);
    // build suggestions
    return array.forEach((item: string) => {
      keyWordJsonSuggestions.push({
        label: {
          label: item,
          detail: key + ' ' + item
        },
        range: range,
        kind: monaco.languages.CompletionItemKind.Keyword,
        insertText: item,
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: key
      });
    });
  });

  return keyWordJsonSuggestions;
};

/**
 * Method = 0,
 *         Function = 1,
 *         Constructor = 2,
 *         Field = 3,
 *         Variable = 4,
 *         Class = 5,
 *         Struct = 6,
 *         Interface = 7,
 *         Module = 8,
 *         Property = 9,
 *         Event = 10,
 *         Operator = 11,
 *         Unit = 12,
 *         Value = 13,
 *         Constant = 14,
 *         Enum = 15,
 *         EnumMember = 16,
 *         Keyword = 17,
 *         Text = 18,
 *         Color = 19,
 *         File = 20,
 *         Reference = 21,
 *         Customcolor = 22,
 *         Folder = 23,
 *         TypeParameter = 24,
 *         User = 25,
 *         Issue = 26,
 *         Snippet = 27
 * @param kind
 */
const mappingKind = (kind: number) => {
  switch (kind) {
    case 0:
      return monaco.languages.CompletionItemKind.Method;
    case 1:
      return monaco.languages.CompletionItemKind.Function;
    case 2:
      return monaco.languages.CompletionItemKind.Constructor;
    case 3:
      return monaco.languages.CompletionItemKind.Field;
    case 4:
      return monaco.languages.CompletionItemKind.Variable;
    case 5:
      return monaco.languages.CompletionItemKind.Class;
    case 6:
      return monaco.languages.CompletionItemKind.Struct;
    case 7:
      return monaco.languages.CompletionItemKind.Interface;
    case 8:
      return monaco.languages.CompletionItemKind.Module;
    case 9:
      return monaco.languages.CompletionItemKind.Property;
    case 10:
      return monaco.languages.CompletionItemKind.Event;
    case 11:
      return monaco.languages.CompletionItemKind.Operator;
    case 12:
      return monaco.languages.CompletionItemKind.Unit;
    case 13:
      return monaco.languages.CompletionItemKind.Value;
    case 14:
      return monaco.languages.CompletionItemKind.Constant;
    case 15:
      return monaco.languages.CompletionItemKind.Enum;
    case 16:
      return monaco.languages.CompletionItemKind.EnumMember;
    case 17:
      return monaco.languages.CompletionItemKind.Keyword;
    case 18:
      return monaco.languages.CompletionItemKind.Text;
    case 19:
      return monaco.languages.CompletionItemKind.Color;
    case 20:
      return monaco.languages.CompletionItemKind.File;
    case 21:
      return monaco.languages.CompletionItemKind.Reference;
    case 22:
      return monaco.languages.CompletionItemKind.Customcolor;
    case 23:
      return monaco.languages.CompletionItemKind.Folder;
    case 24:
      return monaco.languages.CompletionItemKind.TypeParameter;
    case 25:
      return monaco.languages.CompletionItemKind.User;
    case 26:
      return monaco.languages.CompletionItemKind.Issue;
    case 27:
      return monaco.languages.CompletionItemKind.Snippet;
    default:
      return monaco.languages.CompletionItemKind.Keyword;
  }
};

/**
 * build all suggestions to editor
 * @param model ITextModel
 * @param position monaco.Position
 * @param suggestionsData SuggestionInfo[]
 * @returns ProviderResult<CompletionList>
 */
export const buildAllSuggestionsToEditor = (
  model: ITextModel,
  position: monaco.Position,
  suggestionsData: SuggestionInfo[]
): ProviderResult<CompletionList> => {
  const valueRange = model.getWordUntilPosition(position); //get inout word range
  //  get range
  const range = new monaco.Range(
    position.lineNumber,
    valueRange.startColumn,
    position.lineNumber,
    valueRange.endColumn
  );
  const subgraphOptions: CompletionItem[] = suggestionsData.map((item) => {
    return {
      key: item.key,
      label: {
        label: item.label.label,
        // detail: item.label?.detail ?? '',
        description: item.label?.description ?? ''
      },
      range: range,
      kind: mappingKind(item.kind),
      insertText: item.insertText,
      insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
      detail: item?.label?.description || ''
    };
  });
  //todo: 补充补全建议 关键词建议

  return {
    suggestions: subgraphOptions
  };
};
