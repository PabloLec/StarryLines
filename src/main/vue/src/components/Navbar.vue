<template>
  <nav
    id="navbar"
    class="bg-white border-gray-200 px-2 sm:px-4 py-2.5 dark:bg-gray-900"
  >
    <div class="container flex flex-wrap justify-between items-center mx-auto">
      <a class="flex items-center">
        <span
          class="self-center text-xl font-semibold whitespace-nowrap dark:text-white"
          >StarryLines</span
        >
      </a>
      <div class="w-auto" id="navbar-default"></div>
    </div>
  </nav>
</template>

<style>
#navbar {
  background: url("navbar.svg");
}
</style>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({});
</script>
