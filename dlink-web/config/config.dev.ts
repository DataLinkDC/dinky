// https://umijs.org/config/
import {defineConfig} from 'umi';

const HappyPack = require('happypack');
const happyThreadPool = HappyPack.ThreadPool({ size: require('os').cpus().length })
const TerserPlugin = require('terser-webpack-plugin');

export default defineConfig({
  plugins: [
    // https://github.com/zthxxx/react-dev-inspector
    'react-dev-inspector/plugins/umi/react-inspector',
  ],
  // https://github.com/zthxxx/react-dev-inspector#inspector-loader-props
  inspectorConfig: {
    exclude: [],
    babelPlugins: [],
    babelOptions: {},
  },
  chainWebpack: (memo, { webpack }) => {
    memo.plugin('HappyPack').use(HappyPack, [{
      id: 'js',
      loaders: ['babel-loader'],
      threadPool: happyThreadPool,
    },
      memo.plugin('TerserPlugin').use(TerserPlugin, [{
        parallel: require('os').cpus().length - 1,
        terserOptions: {
          compress: {
            inline: false
          },
          mangle: {
            safari10: true
          }
        }
      }])
    ])
  }
  /*webpack5: {
      //: {},
  },*/
});
