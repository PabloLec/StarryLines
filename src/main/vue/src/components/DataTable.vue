<template>
  <EasyDataTable
    :headers="headers"
    :items="items"
    alternating
    :loading="loading"
  >
    <template #item-name="{ name, url }">
      <a :href="url" target="_blank" rel="noopener noreferrer">{{ name }}</a>
    </template>
    <template #expand="item">
      <div style="padding: 15px">
        {{ item.description }}
      </div>
    </template>
    <template #loading>
      <img
        src="https://i.pinimg.com/originals/94/fd/2b/94fd2bf50097ade743220761f41693d5.gif"
        style="width: 100px; height: 80px"
      />
    </template>
  </EasyDataTable>
</template>

<script>
import Vue3EasyDataTable from "vue3-easy-data-table";
import "vue3-easy-data-table/dist/style.css";

export default {
  components: {
    EasyDataTable: Vue3EasyDataTable,
  },
  data() {
    return {
      language: "kotlin",
      headers: [],
      items: [],
      loading: true,
    };
  },
  methods: {
    async fetchData() {
      this.loading = true;
      this.items = null;
      const res = await fetch(
        `https://starrylines.pablolec.dev/api/${this.language}_top`
      );
      this.items = await res.json();
      this.loading = false;
    },
  },
  mounted() {
    this.headers = [
      { text: "rank", value: "rank", sortable: true },
      { text: "name", value: "name", sortable: true },
      { text: "createdAt", value: "createdAt", sortable: true },
      { text: "stargazers", value: "stargazers", sortable: true },
      { text: "loc", value: "loc", sortable: true },
      { text: "score", value: "score", sortable: true },
    ];
    this.fetchData();
  },
  watch: {
    language() {
      this.fetchData();
    },
  },
};
</script>
