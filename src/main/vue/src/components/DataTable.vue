<template>
  <EasyDataTable
    table-class-name="data-table"
    class="font-mono"
    :headers="headers"
    :items="items"
    :loading="loading"
    :header-item-class-name="getHeaderClassNameByIndex"
    :body-item-class-name="getItemClassNameByIndex"
    theme-color="#FFC40C"
    header-text-direction="center"
    body-text-direction="center"
    alternating
  >
    <template #item-name="{ name, url }">
      <a
        class="after:content-['_↗'] after:text-sm after:pb-2"
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

<style>
.sortable.none .sortType-icon {
  display: none !important;
}

.previous-page__click-button.first-page .arrow,
.next-page__click-button.last-page .arrow {
  border-color: black !important;
}

.easy-data-table__rows-selector ul.select-items li.selected {
  color: black !important;
}

.previous-page__click-button .arrow,
.next-page__click-button .arrow {
  border-color: white !important;
}
.data-table {
  --easy-table-border: 1px solid #445269;
  --easy-table-row-border: 1px solid #445269;

  --easy-table-header-font-size: 14px;
  --easy-table-header-height: 50px;
  --easy-table-header-font-color: #ffc40c;
  --easy-table-header-background-color: #0c173c;

  --easy-table-header-item-padding: 10px 15px;

  --easy-table-body-even-row-font-color: #fff;
  --easy-table-body-even-row-background-color: #4c5d7a;

  --easy-table-body-row-font-color: #c0c7d2;
  --easy-table-body-row-background-color: #0c173c;
  --easy-table-body-row-height: 50px;
  --easy-table-body-row-font-size: 14px;

  --easy-table-body-row-hover-font-color: #0c173c;
  --easy-table-body-row-hover-background-color: #eee;

  --easy-table-body-item-padding: 10px 15px;

  --easy-table-footer-background-color: #0c173c;
  --easy-table-footer-font-color: #c0c7d2;
  --easy-table-footer-font-size: 14px;
  --easy-table-footer-padding: 0px 10px;
  --easy-table-footer-height: 50px;

  --easy-table-rows-per-page-selector-width: 70px;
  --easy-table-rows-per-page-selector-option-padding: 10px;

  --easy-table-scrollbar-track-color: #0c173c;
  --easy-table-scrollbar-color: #0c173c;
  --easy-table-scrollbar-thumb-color: #4c5d7a;
  --easy-table-scrollbar-corner-color: #0c173c;

  --easy-table-loading-mask-background-color: #0c173c;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";
import Vue3EasyDataTable from "vue3-easy-data-table";
import "vue3-easy-data-table/dist/style.css";

const HEADER_REACTIVE_CLASSES: any = {
  1: "text-0 before:content-['#'] before:inline before:text-lg sm:text-base sm:before:content-none",
  2: "text-lg sm:text-xl",
  3: "hidden md:table-cell",
  4: "text-0 before:content-['★'] before:inline before:text-2xl sm:text-base sm:before:content-none",
  5: "text-0 before:content-['LoC'] before:inline before:text-lg sm:text-base sm:before:content-none",
  6: "text-lg sm:text-xl",
  all: "break-words sm:text-lg lg:text-xl text-black",
};

const REACTIVE_CLASSES: any = {
  2: "break-all text-ellipsis",
  3: "hidden md:table-cell",
  all: "text-sm sm:text-lg",
};

export default defineComponent({
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
      headers: [{}],
      items: [],
      loading: true,
    };
  },
  methods: {
    async fetchData() {
      this.loading = true;
      const res = await fetch(
        `https://starrylines.pablolec.dev/api/${this.language}_top`
      );
      this.items = await res.json();
      this.loading = false;
    },
    getHeaderClassNameByIndex(header: object, index: string) {
      if (index in HEADER_REACTIVE_CLASSES) {
        return (
          HEADER_REACTIVE_CLASSES[index] + " " + HEADER_REACTIVE_CLASSES.all
        );
      }
      return HEADER_REACTIVE_CLASSES.all;
    },
    getItemClassNameByIndex(object: object, index: number) {
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
});
</script>
