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

module.exports = {
  extends: [require.resolve('@umijs/lint/dist/config/eslint')],
  globals: {
    page: true,
    REACT_APP_ENV: true,
  },
  rules: {
    'quote-props': 0,
    'dot-notation': 0,
    'consistent-return': 0,
    'no-multiple-empty-lines': 0,
    'prefer-arrow-callback': 0,
    'wrap-iife': 0,
    'no-undef-init': 0,
    'no-new-wrappers': 0,
    'linebreak-style': 0,
    'max-len': [0, 160],
    'lines-between-class-members': 0,
    'prefer-const': 0,
    'no-plusplus': 0,
    'comma-dangle': 0,
    'func-names': 0,
    'space-before-function-paren': 0,
    'prefer-template': 0,
    'no-else-return': 0,
    'object-curly-newline': 0,
    'no-bitwise': 0,
    'max-classes-per-file': 0,
    'class-methods-use-this': 0,
    'no-useless-constructor': 0,
    'no-unused-expressions': 0,
    'no-param-reassign': 0,
    'no-empty-function': 0,
    'no-console': 0,
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    'no-unused-vars': 0,
    'no-underscore-dangle': 0,
    'arrow-body-style': 0,
    'one-var': 0,
    'one-var-declaration-per-line': 0,
    'prefer-promise-reject-errors': 0,
    eqeqeq: 0,
    camelcase: 0,
    'object-shorthand': 0,
  },
};
