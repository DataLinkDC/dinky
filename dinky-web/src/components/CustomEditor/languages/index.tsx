import { FlinkSQLLanguage } from "@/components/CustomEditor/languages/flinksql";
import { FLINK_SQL_KEYWORD } from "@/components/CustomEditor/languages/flinksql/keyword";
import { LogLanguage } from "@/components/CustomEditor/languages/javalog";
import { Monaco } from "@monaco-editor/react";

/**
 * 加载自定义语言 关键字
 * @param monaco
 * @constructor
 */
//  function LoadLanguagesKeyWord(monaco: Monaco | null) {
//      if (!monaco) {
//          return;
//      }
//      monaco?.languages?.getLanguages().forEach((language) => {
//          return monaco?.languages?.registerCompletionItemProvider(language.id , {
//              provideCompletionItems: function (model, position) {
//                  const word = model.getWordUntilPosition(position);
//                  const range = {
//                      startLineNumber: position.lineNumber,
//                      endLineNumber: position.lineNumber,
//                      startColumn: word.startColumn,
//                      endColumn: word.endColumn
//                  };
//                  return {
//                      suggestions: FLINK_SQL_KEYWORD.map((item) => {
//                          return {
//                              label: item,
//                              range: range,
//                              kind: monaco.languages.CompletionItemKind.Keyword,
//                              insertText: item
//                          };
//                      })
//                  };
//              }
//          });
//      })
// }


export function LoadCustomEditorLanguage(monaco: Monaco | null) {
    // LoadLanguagesKeyWord(monaco);
    LogLanguage(monaco);
    FlinkSQLLanguage(monaco);
}
