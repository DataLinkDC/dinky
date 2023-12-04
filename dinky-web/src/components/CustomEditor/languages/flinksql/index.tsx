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

import { CustomEditorLanguage } from '@/components/CustomEditor/languages/constants';
import {
  buildFlinkSQLConfiguration,
  buildMonarchTokensProvider,
  registerFlinkSQLCompilation
} from '@/components/CustomEditor/languages/flinksql/function';
import { Monaco } from '@monaco-editor/react';

export function FlinkSQLLanguage(
  monacoLanguages?: Monaco['languages'] | undefined,
  monacoEditor?: Monaco['editor'] | undefined,
  registerCompletion = false
) {
  // Register a new language
  monacoLanguages?.register({
    id: CustomEditorLanguage.FlinkSQL,
    extensions: ['.sql'],
    mimetypes: ['text/x-flinksql', 'text/x-flinksql', 'text/x-flinksql', 'text/flinksql'],
    aliases: ['flinksql', 'fsql', 'flinkSQL', 'FlinkSQL']
  });
  buildMonarchTokensProvider(monacoLanguages);

  // Register a completion item provider for the new language
  if (registerCompletion) {
    registerFlinkSQLCompilation(monacoLanguages);
  }
  buildFlinkSQLConfiguration(monacoLanguages);

  monacoLanguages?.onLanguageEncountered(CustomEditorLanguage.FlinkSQL, () => {
    monacoEditor?.getModels().forEach((model) => {
      model.onDidChangeLanguage(() => {
        if (model.getLanguageId() === CustomEditorLanguage.FlinkSQL) {
          buildFlinkSQLConfiguration(monacoLanguages);
        }
      });
    });
    buildMonarchTokensProvider(monacoLanguages);
  });
}
