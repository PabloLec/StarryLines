const tailwindcss = require("tailwindcss");
const purgecss = require("@fullhuman/postcss-purgecss");
const autoprefixer = require("autoprefixer");

module.exports = {
  plugins: [
    tailwindcss,
    autoprefixer,
    true &&
      purgecss({
        content: [`./public/**/*.html`, `./src/**/*.vue`, `./node_modules/vue3-easy-data-table/**/*.js`],
        defaultExtractor(content) {
          const contentWithoutStyleBlocks = content.replace(/<style[^]+?<\/style>/gi, "");
          return contentWithoutStyleBlocks.match(/[A-Za-z0-9-_/:]*[A-Za-z0-9-_/]+/g) || [];
        },
        safelist: [
          /-(leave|enter|appear)(|-(to|from|active))$/,
          /^(?!(|.*?:)cursor-move).+-move$/,
          /^router-link(|-exact)-active$/,
          /data-v-.*/,
          /swiper*/,
          /direction-center/,
        ],
      }),
  ],
};
