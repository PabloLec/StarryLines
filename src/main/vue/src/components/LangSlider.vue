<template>
  <swiper
    class="my-16 md:mx-4 lg:mx-12 xl:mx-24 2xl:mx-32"
    :modules="modules"
    :navigation="true"
    :slides-per-view="5"
    :loop="true"
    :space-between="5"
    :freeMode="true"
    :keyboard="{
      enabled: true,
    }"
    :breakpoints="{
      '640': {
        slidesPerView: 5,
        spaceBetween: 10,
      },
      '768': {
        slidesPerView: 5,
        spaceBetween: 20,
      },
      '1280': {
        slidesPerView: 7,
        spaceBetween: 30,
      },
    }"
  >
    <swiper-slide class="mx-2" v-for="(lang, index) in langs" :key="index">
      <img
        @click="clickLanguage(lang)"
        class="cursor-pointer max-h-16 max-w-16 sm:max-h-18 sm:max-w-18 md:max-h-20 md:max-w-20"
        :alt="lang"
        :src="getIcon(lang)"
      />
    </swiper-slide>
  </swiper>
</template>

<style>
.swiper-button-prev {
  transform: translateX(-50%);
}
.swiper-button-next {
  transform: translateX(50%);
}
.swiper-button-next::after,
.swiper-button-prev::after {
  font-size: 2rem;
  color: black;
}
@media all and (max-width: 640px) {
  .swiper-button-next,
  .swiper-button-prev {
    display: none;
  }
}
</style>

<script lang="ts">
import { Swiper, SwiperSlide } from "swiper/vue";
import { Keyboard, FreeMode, Navigation } from "swiper";

// Import Swiper styles
import "swiper/css";
import "swiper/css/navigation";

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
    Swiper,
    SwiperSlide,
  },
  data() {
    return {
      langs: LANGS.sort(),
      modules: [Keyboard, Navigation, FreeMode],
    };
  },
  methods: {
    clickLanguage(lang: any) {
      this.$emit("clickLanguage", lang);
    },
    getIcon(lang: any) {
        return new URL(`../../src/assets/${lang}.svg`, import.meta.url).href
  },
}
}
</script>