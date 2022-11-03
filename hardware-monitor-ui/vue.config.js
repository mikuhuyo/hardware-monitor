const path = require('path')
const name = 'Vue Typescript Admin'

module.exports = {
  // TODO: Remember to change this to fit your need
  publicPath: process.env.NODE_ENV === 'production' ? './' : './',
  // lintOnSave: process.env.NODE_ENV === 'development',
  lintOnSave: false,
  pwa: {
    name: name
  },
  pluginOptions: {
    'style-resources-loader': {
      preProcessor: 'scss',
      patterns: [
        path.resolve(__dirname, 'src/styles/_variables.scss'),
        path.resolve(__dirname, 'src/styles/_mixins.scss')
      ]
    }
  },
  chainWebpack(config) {
    // Provide the app's title in webpack's name field, so that
    // it can be accessed in index.html to inject the correct title.
    config.set('name', name)
  },
  devServer: {
    open: true,
    overlay: {
      warnings: false,
      errors: true
    },
    proxy: {
      '/api': {
        target: `http://127.0.0.1:57100/api`,
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    },
  },
}
