import * as monaco from "monaco-editor";

const Completion =[
  /**   * 内置函数   */
  {
    label: 'SUM(number)',
    kind: monaco.languages.CompletionItemKind.Function,
    insertText: 'SUM(${1:})',
    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
    detail: '返回指定参数的求和'
  }
];

export default Completion;
