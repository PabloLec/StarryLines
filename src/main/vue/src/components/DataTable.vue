<template>
  <EasyDataTable
    ref="data-table"
    table-class-name="data-table"
    class="font-mono"
    :headers="headers"
    :items="items"
    :loading="loading"
    :header-item-class-name="getHeaderClassNameByIndex"
    :body-item-class-name="getItemClassNameByIndex"
    updatePage
    @click-row="expandRow"
    theme-color="#FFC40C"
    header-text-direction="center"
    body-text-direction="center"
    alternating
  >
    <template #item-name="{ name, url }">
      <a
        class="repo-link after:content-['_↗'] after:text-sm after:pb-2"
        :href="url"
        target="_blank"
        rel="noopener noreferrer"
        >{{ name }}</a
      >
    </template>
    <template #expand="item">
      <div class="p-5">
        <h2 class="font-bold">Description</h2>
        <p v-if="!item.description">None 😢</p>
        <p v-else-if="item.descriptionLanguage && item.descriptionLanguage !== 'EN'">
          {{ item.translatedDescription }}
          <span class="italic font-bold block"
            >(Auto-translated from
            {{ getFlagEmoji(item.descriptionLanguage) }})
          </span>
        </p>
        <p v-else>{{ item.description }}</p>
        <div class="block md:hidden mt-2">
          <h2 class="inline font-bold mt-2">Last update:</h2>
          <p class="inline ml-2">{{ item.updatedAt }}</p>
        </div>
        <div class="block lg:hidden mt-2">
          <h2 class="inline font-bold mt-2">Created at:</h2>
          <p class="inline ml-2">{{ item.createdAt }}</p>
        </div>
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
import Vue3EasyDataTable, { Header } from "vue3-easy-data-table";
import type { ClickRowArgument } from "vue3-easy-data-table";
import "vue3-easy-data-table/dist/style.css";

const HEADER_REACTIVE_CLASSES: any = {
  "expand": "!px-0 sm:px-0",
  "rank": "!pl-2 !pr-0 sm:!px-3 text-0 before:content-['#'] before:inline before:text-lg sm:text-base sm:before:content-none",
  "name": "text-lg sm:text-xl",
  "updatedAt": "hidden md:table-cell",
  "createdAt": "hidden lg:table-cell",
  "stargazers": "text-0 before:content-['★'] before:inline before:text-2xl sm:text-base sm:before:content-none",
  "loc": "text-0 before:content-['LoC'] before:inline before:text-lg sm:text-base sm:before:content-none",
  "score": "text-lg sm:text-xl",
  all: "break-words sm:text-lg lg:text-xl text-black",
};

const REACTIVE_CLASSES: any = {
  "expand": "expand-button !px-0 sm:!px-3",
  "rank": "repo-rank !pl-2 !pr-0 sm:!px-3",
  "name": "break-all text-ellipsis",
  "updatedAt": "hidden md:table-cell",
  "createdAt": "hidden lg:table-cell",
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
      headers: [
        { text: "", value: "", sortable: true },
      ],
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
      const data = await res.json();
      this.items = data.sort(
        (a: any, b: any) => parseInt(a.rank) - parseInt(b.rank)
      );
      this.loading = false;
    },
    expandRow(item: ClickRowArgument) {
      let xpath = `//tr[td[text()='${item["rank"]}'][contains(@class, 'repo-rank')]]/td[contains(@class, 'expand-button')]`;
      let expandButton = document.evaluate(
        xpath,
        document,
        null,
        XPathResult.FIRST_ORDERED_NODE_TYPE,
        null
      ).singleNodeValue as HTMLElement;
      expandButton.click();
    },
    async shrinkAllRows() {
      let expandButtons = document.querySelectorAll(
        ".expanding"
      ) as NodeListOf<HTMLElement>;
      expandButtons.forEach((button) => {
        button.click();
      });
    },
    getHeaderClassNameByIndex(header: Header, index: string) {
      return HEADER_REACTIVE_CLASSES.all + " " + HEADER_REACTIVE_CLASSES[header.value];
    },
    getItemClassNameByIndex(column: string, rowNumber: number) {
      return REACTIVE_CLASSES.all + " " + REACTIVE_CLASSES[column];
    },
    getFlagEmoji(countryCode: string) {
      if (countryCode == undefined) return;
      if (countryCode == "ZH") countryCode = "CN";
      if (countryCode == "JA") countryCode = "JP";
      const codePoints = countryCode
        .toUpperCase()
        .split("")
        .map((char) => 127397 + char.charCodeAt(0));
      return (
        String.fromCodePoint(codePoints[0]) +
        String.fromCodePoint(codePoints[1])
      );
    },
  },
  mounted() {
    this.headers = [
      { text: "RANK", value: "rank", sortable: true },
      { text: "NAME", value: "name", sortable: true },
      { text: "LAST UPDATE", value: "updatedAt", sortable: true },
      { text: "CREATED AT", value: "createdAt", sortable: true },
      { text: "STARGAZERS", value: "stargazers", sortable: true },
      { text: "LINES OF CODE", value: "loc", sortable: true },
      { text: "SCORE", value: "score", sortable: true },
    ];
    this.fetchData();
  },
  watch: {
    language() {
      this.shrinkAllRows();
      this.fetchData();
      (this.$refs["data-table"] as any).updatePage(1);
    },
  },
  updated() {
    Array.from(document.getElementsByClassName("repo-link")).forEach(function (
      el
    ) {
      el.addEventListener("click", (e) => {
        e.stopPropagation();
      });
    });
  },
});
</script>
