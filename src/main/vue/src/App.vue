<template>
  <LangSlider @clickLanguage="changeLanguage" />
  <DataTable :language="language" />
</template>

<script lang="ts">
import { defineComponent } from "vue";
import DataTable from "./components/DataTable.vue";
import LangSlider from "./components/LangSlider.vue";

export default defineComponent({
  name: "App",
  components: {
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
