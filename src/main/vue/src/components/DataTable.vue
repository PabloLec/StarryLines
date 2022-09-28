<template>
  <EasyDataTable
    class="font-mono"
    :headers="headers"
    :items="items"
    :loading="loading"
    :header-item-class-name="getHeaderClassNameByIndex"
    :body-item-class-name="getItemClassNameByIndex"
    header-text-direction="center"
    body-text-direction="center"
    alternating
  >
    <template #item-name="{ name, url }">
      <a
        class="after:content-['_↗'] after:text-sm"
        :href="url"
        target="_blank"
        rel="noopener noreferrer"
        >{{ name }}</a
      >
    </template>
    <template #expand="item">
      <div style="padding: 15px">
        {{ item.description }}
      </div>
    </template>
  </EasyDataTable>
</template>

<script>
import Vue3EasyDataTable from "vue3-easy-data-table";
import "vue3-easy-data-table/dist/style.css";

const HEADER_REACTIVE_CLASSES = {
  1: "text-0 before:content-['#'] before:inline before:text-black before:text-sm sm:text-base sm:before:content-none",
  3: "hidden md:table-cell",
  4: "text-0 before:content-['★'] before:inline before:text-black before:text-sm sm:text-base sm:before:content-none",
  5: "text-0 before:content-['LoC'] before:inline before:text-black before:text-sm sm:text-base sm:before:content-none",
  all: "break-words sm:text-base lg:text-xl text-black",
};

const REACTIVE_CLASSES = {
  2: "break-all text-ellipsis",
  3: "hidden md:table-cell",
  all: "text-sm sm:text-base lg:text-l",
};

export default {
  props: {
    language: {
      type: String,
      required: true,
    },
  },
  components: {
    EasyDataTable: Vue3EasyDataTable,
  },
  data() {
    return {
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
    getHeaderClassNameByIndex(header, index) {
      if (index in HEADER_REACTIVE_CLASSES) {
        return (
          HEADER_REACTIVE_CLASSES[index] + " " + HEADER_REACTIVE_CLASSES.all
        );
      }
      return HEADER_REACTIVE_CLASSES.all;
    },
    getItemClassNameByIndex(object, index) {
      if (index in REACTIVE_CLASSES) {
        return REACTIVE_CLASSES[index] + " " + REACTIVE_CLASSES.all;
      }
      return REACTIVE_CLASSES.all;
    },
  },
  mounted() {
    this.headers = [
      { text: "RANK", value: "rank", sortable: true },
      { text: "NAME", value: "name", sortable: true },
      { text: "CREATED AT", value: "createdAt", sortable: true },
      { text: "STARGAZERS", value: "stargazers", sortable: true },
      { text: "LINES OF CODE", value: "loc", sortable: true },
      { text: "SCORE", value: "score", sortable: true },
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
