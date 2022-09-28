<template>
  <carousel
    class="mx-4 my-16 sm:mx-12 md:mx-24"
    :wrap-around="true"
    :breakpoints="breakpoints"
  >
    <slide class="mx-2" v-for="(lang, index) in langs" :key="index">
      <img
        @click="clickLanguage(lang)"
        class="max-h-12 max-w-12 sm:max-h-14 sm:max-w-14 md:max-h-20 md:max-w-20"
        :alt="lang"
        :src="require(`@/assets/${lang}.svg`)"
      />
    </slide>

    <template #addons>
      <navigation />
    </template>
  </carousel>
</template>

<style>
.carousel__icon {
  stroke: black;
  fill: black;
}

.carousel__prev,
.carousel__next {
  background-color: white !important;
  border: black;
  border-style: solid;
}
</style>

<script>
import "vue3-carousel/dist/carousel.css";
import { Carousel, Slide, Navigation } from "vue3-carousel";

const LANGS = [
  "javascript",
  "typescript",
  "java",
  "python",
  "c",
  "cpp",
  "csharp",
  "go",
  "kotlin",
  "rust",
  "swift",
  "php",
  "scala",
  "powershell",
  "ruby",
];

export default {
  components: {
    Carousel,
    Slide,
    Navigation,
  },
  data() {
    return {
      langs: LANGS.sort(),
      breakpoints: {
        1: {
          itemsToShow: 3,
        },
        700: {
          itemsToShow: 7,
        },
        1024: {
          itemsToShow: 10,
        },
      },
    };
  },
  methods: {
    clickLanguage(lang) {
      this.$emit("clickLanguage", lang);
    },
  },
};
</script>
