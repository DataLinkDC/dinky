// todo 自定义语言不生效
export function LogLanguage(monaco: any) {
  // Register a new language
  monaco?.languages.register({
    id: 'javalog',
    extensions: ['.log'],
    aliases: ['javalog', 'Javalog', 'Javalog', 'jl', 'log']
  });
  monaco?.languages.setMonarchTokensProvider('javalog', {
    // todo: 如果需要分段展示不同的颜色 则需要在这里添加规则, 且正则必须是严格模式, 否则会匹配一整行,无法达到预期效果
    tokenizer: {
      root: [
        // 默认不区分大小写 //
        [/\[(\w*-\d*)+\]/, 'custom-thread'],
        [/(\w+(\.))+(\w+)(\(\d+\))?(:){1}/, 'custom-class'],
        [/(\w+(\.))+(\w+)(\(\d+\))?\s+/, 'custom-class'],
        [/error/, 'custom-error'],
        [/warring/, 'custom-warning'],
        [/warn/, 'custom-warning'],
        [/info/, 'custom-info'],
        [
          /^[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\s+(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d\.\d{3}/,
          'custom-date'
        ],
        [
          /[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\s+(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d\s(CST)/,
          'custom-date'
        ]
      ]
    },
    ignoreCase: true,
    unicode: true
  });
}
