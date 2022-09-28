const { defineConfig } = require("@vue/cli-service");
module.exports = defineConfig({
  transpileDependencies: true,
  configureWebpack: {
    optimization: {
      nodeEnv: "production",
      concatenateModules: true,
      mergeDuplicateChunks: true,
      minimize: true,
    },
  },
});
