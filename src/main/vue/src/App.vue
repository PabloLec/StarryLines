<template>
    <LangSlider @clickLanguage="changeLanguage" />
    <DataTable :language="language" />
  </template>

  <script>
  import DataTable from "./components/DataTable.vue";
  import LangSlider from "./components/LangSlider.vue";

  export default {
    name: "App",
    components: {
      LangSlider,
      DataTable,
    },
    data() {
      return {
        language: "kotlin",
      };
    },
    mounted() {
      document.body.classList.add("mx-0", "sm:mx-12", "md:mx-16", "lg:mx-20");
    },
    methods: {
      changeLanguage(lang) {
        this.language = lang;
      },
    },
  };
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
