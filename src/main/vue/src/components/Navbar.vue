<template>
  <nav
    id="navbar"
    class="bg-white border-gray-200 px-2 sm:px-4 dark:bg-gray-900"
  >
    <div
      class="container flex flex-wrap justify-between items-center mx-auto flex items-center"
    >
      <span
        id="nav-title"
        class="leading-none self-center text-2xl sm:text-4xl font-semibold whitespace-nowrap text-mikado md:text-5xl mt-1 mb-2"
        >StarryLines</span
      >
      <div class="w-auto text-white py-2.5" id="navbar-buttons">
        <button
          @click="showReadme"
          class="nav-button text-sm sm:text-lg bg-mikado hover:bg-black hover:text-mikado text-black font-bold py-1 px-2 md:px-4 rounded inline-flex items-center mr-2.5"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="mr-1.5 nav-icon w-4 h-4 lg:w-6 lg:h-6"
            viewBox="0 0 24 24"
          >
            <path
              d="M12 0c-6.627 0-12 5.373-12 12s5.373 12 12 12 12-5.373 12-12-5.373-12-12-12zm0 18.25c-.69 0-1.25-.56-1.25-1.25s.56-1.25 1.25-1.25c.691 0 1.25.56 1.25 1.25s-.559 1.25-1.25 1.25zm1.961-5.928c-.904.975-.947 1.514-.935 2.178h-2.005c-.007-1.475.02-2.125 1.431-3.468.573-.544 1.025-.975.962-1.821-.058-.805-.73-1.226-1.365-1.226-.709 0-1.538.527-1.538 2.013h-2.01c0-2.4 1.409-3.95 3.59-3.95 1.036 0 1.942.339 2.55.955.57.578.865 1.372.854 2.298-.016 1.383-.857 2.291-1.534 3.021z"
            />
          </svg>
          <span class="mt-1">Readme</span>
        </button>
        <a
          href="https://github.com/PabloLec/StarryLines"
          target="_blank"
          rel="noopener noreferrer"
        >
          <button
            class="nav-button text-sm sm:text-lg bg-mikado hover:bg-black hover:text-mikado text-black font-bold py-1 px-2 md:px-4 rounded inline-flex items-center"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="mr-1.5 nav-icon w-4 h-4 lg:w-6 lg:h-6"
              viewBox="0 0 24 24"
            >
              <path
                d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"
              />
            </svg>
            <span class="mt-1">View on GitHub</span>
          </button>
        </a>
      </div>
    </div>
  </nav>
</template>

<style>
@font-face {
  font-display: swap;
}
#nav-title {
  text-shadow: 1px 1px 0 #000, -1px -1px 0 #000, 1px -1px 0 #000,
    -1px 1px 0 #000, 1px 1px 0 #000;
  font-family: "DancingScript", serif;
}
#navbar {
  background: url("/navbar.svg");
}
.nav-button:hover .nav-icon {
  fill: #ffc40c;
}
</style>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  methods: {
    showReadme() {
      this.$emit("showReadme", true);
    },
  },
});
</script>
