<template>
  <Navbar />
  <div class="mx-0 sm:mx-12 md:mx-16 lg:mx-20">
    <LangSlider @clickLanguage="changeLanguage" />
    <DataTable :language="language" />
  </div>
</template>

<style>
@font-face {
  font-family: "DancingScript";
  src: local("DancingScript"),
    url(./fonts/DancingScript/DancingScript-Regular.ttf)
      format("truetype");
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
import Navbar from "./components/Navbar.vue";
import LangSlider from "./components/LangSlider.vue";
import DataTable from "./components/DataTable.vue";

export default defineComponent({
  name: "App",
  components: {
    Navbar,
    LangSlider,
    DataTable,
  },
  data() {
    return {
      language: "c",
    };
  },
  methods: {
    changeLanguage(lang: string) {
      this.language = lang;
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
