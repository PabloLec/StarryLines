<template>
  <Navbar @showReadme="showReadme" />
  <div class="mx-0 sm:mx-12 md:mx-16 lg:mx-20">
    <LangSlider @clickLanguage="changeLanguage" />
    <DataTable :language="language" />
    <Readme :showModal="showModal" @close="hideReadme" :isPrerender="isPrerender" />
  </div>
</template>

<style>
@font-face {
  font-family: "DancingScript";
  src: local("DancingScript"),
    url(./fonts/DancingScript/DancingScript-Regular.ttf) format("truetype");
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
import Navbar from "./components/Navbar.vue";
import LangSlider from "./components/LangSlider.vue";
import DataTable from "./components/DataTable.vue";
import Readme from "./components/Readme.vue";

export default defineComponent({
  name: "App",
  components: {
    Navbar,
    LangSlider,
    DataTable,
    Readme,
  },
  setup() {
    return {
      ua: window.navigator.userAgent.toLowerCase(),
    };
  },
  data() {
    return {
      language: "c",
      isPrerender: this.ua?.toString().includes("prerender"),
      showModal: this.ua?.toString().includes("prerender"),
    };
  },
  methods: {
    changeLanguage(lang: string) {
      this.language = lang;
    },
    showReadme() {
      this.showModal = true;
    },
    hideReadme() {
      this.showModal = false;
    },
  },
});
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
