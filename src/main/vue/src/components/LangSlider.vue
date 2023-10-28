<template>
  <div class="my-16">
    <div id="lang-wrapper">
      <swiper
        :grabCursor="true"
        :slideToClickedSlide="true"
        :centeredSlides="true"
        :modules="modules"
        :navigation="true"
        :slides-per-view="5"
        :loop="true"
        :freeMode="true"
        :keyboard="{
          enabled: true,
        }"
        :breakpoints="{
          '1280': {
            slidesPerView: 7,
          },
        }"
      >
        <swiper-slide
          class="px-2 !mx-0"
          v-for="(lang, index) in langs"
          :key="index"
        >
          <img
            @click="clickLanguage(lang)"
            :class="`cursor-pointer max-h-16 max-w-16 sm:max-h-18 sm:max-w-18 md:max-h-20 md:max-w-20 lg:max-h-24 lg:max-w-24 xl:max-h-28 xl:max-w-28 lang-logo ${lang}-logo`"
            :alt="lang"
            :src="`/logos/${lang}.svg`"
          />
        </swiper-slide>
      </swiper>
    </div>
    <div id="swiper-buttons" class="flex justify-between mt-4 mx-4">
      <button @click="prev">
        <svg
          class="prev w-12 fill-gray hover:fill-mikado"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
        >
          <path d="M16 8v-4l8 8-8 8v-4h-16l8-8h8z" />
        </svg>
      </button>
      <button @click="next">
        <svg
          class="w-12 fill-gray hover:fill-mikado"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
        >
          <path d="M16 8v-4l8 8-8 8v-4h-16l8-8h8z" />
        </svg>
      </button>
    </div>
  </div>
</template>

<style>
#lang-wrapper {
  mask-image: linear-gradient(
    to right,
    transparent,
    #0c173c 5%,
    #0c173c 95%,
    transparent 100%
  );
}
.swiper-slide {
  display: flex;
  align-items: center;
  justify-content: center;
  align-self: center;
}
.swiper-button-prev,
.swiper-button-next {
  display: none;
}
.prev {
  transform: rotate(180deg);
}

@media all and (max-width: 640px) {
  #swiper-buttons {
    display: none !important;
  }
}
.lang-logo {
  opacity: 0.5;
}
.lang-logo:hover {
  opacity: 1;
}
.logo-selected {
  opacity: 1;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
import { Swiper, SwiperSlide } from "swiper/vue";
import { Keyboard, FreeMode, Navigation } from "swiper/modules";

// Import Swiper styles
import "swiper/css";

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
  "dart",
  "shell",
];

function highlightSelected(lang: string) {
  const logos = document.querySelectorAll(".lang-logo");
  logos.forEach((logo) => {
    logo.classList.remove("logo-selected");
  });
  document.querySelectorAll(`.${lang}-logo`).forEach((logo) => {
    logo.classList.add("logo-selected");
  });
}

export default defineComponent({
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
    clickLanguage(lang: string) {
      this.$emit("clickLanguage", lang);
      highlightSelected(lang);
    },
    next() {
      let button = document?.getElementsByClassName(
        "swiper-button-next"
      )[0] as HTMLElement;
      button.click();
    },
    prev() {
      let button = document?.getElementsByClassName(
        "swiper-button-prev"
      )[0] as HTMLElement;
      button.click();
    },
  },
  mounted() {
    highlightSelected("c");
  },
});
</script>
