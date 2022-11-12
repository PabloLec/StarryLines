<template>
  <vue-final-modal
    v-slot="{ params, close }"
    v-bind="$attrs"
    v-model="showModalModel"
    classes="flex justify-center items-center"
    content-class="relative flex flex-col max-h-full mx-4 p-4 border rounded bg-night-blue text-white"
    @closed="closed"
    :transition="{
      'enter-active-class': 'transition duration-350 ease-in-out transform',
      'enter-class': 'translate-y-full',
      'enter-to-class': 'translate-y-0',
      'leave-active-class': 'transition duration-350 ease-in-out transform',
      'leave-to-class': 'translate-y-full',
      'leave-class': 'translate-y-0',
    }"
  >
    <div
      class="flex-grow overflow-y-auto text-left sm: px-6 xl:px-32 max-w-lg lg:max-w-2xl xl:max-w-3xl 2xl:max-w-7xl"
    >
      <slot v-bind:params="params"></slot>
      <h1 v-if="isPrerender" class="text-3xl">
        Most starred lines of code on GitHub
      </h1>
      <h2 class="text-2xl mt-4 font-bold text-mikado">What is it?</h2>

      <div class="ml-1 md:ml-2">
        <h3 class="mt-2">
          StarryLines retrieves from GitHub the best repositories for each
          language and then ranks them by the ratio between the number of stars
          and the number of lines of code.
        </h3>
        <h3 class="mt-4">
          The idea is to find the lines of code that seem to have been
          proportionally the most interesting for the greatest number of
          developers. Useful for learning or just for curiosity.
        </h3>
      </div>
      <h2 class="text-xl mt-6">- What should not be in StarryLines</h2>
      <div class="ml-2 md:ml-4">
        <h3 class="mt-2">
          Tutorials, cheat sheets, lists, roadmaps and other repositories that
          are not code or not actual projects.
        </h3>
        <h3>
          Some filters are in place as well as a manual exclusion list. It is
          possible that there still are irrelevant entries in the tables.
        </h3>
        <h3>
          If you think you see a repository that doesn't belong, please
          <a
            href="https://github.com/PabloLec/StarryLines/issues/new?assignees=PabloLec&labels=enhancement&template=repo_removal.md&title=Repository+deletion"
            class="no-underline hover:underline text-mikado"
            >open an issue.</a
          >
        </h3>
      </div>
      <h2 class="text-2xl mt-8 font-bold text-mikado">Score calculation</h2>
      <div class="ml-2 md:ml-4">
        <h3 class="mt-2">
          The score is obtained by dividing the number of stars by the number of
          lines of code.
        </h3>
        <h3>
          Star count is adjusted and several operations are applied to the code
          to obtain a relevant number of lines.
        </h3>
      </div>
      <h2 class="text-xl mt-6">- Star count</h2>
      <div class="ml-2 md:ml-4">
        <h3 class="mt-2">
          Although the star count displayed in the table is left unchanged, the
          one taken into account is adjusted. The number of stars is divided by
          the proportion of code written in the main language.
        </h3>
        <h3>
          For a repository whose main language is C, composed of 60% C and 40%
          Python with 100 stars in total, the final star count will be 60.
        </h3>
      </div>
      <h2 class="text-xl mt-6">- Lines of code</h2>
      <div class="ml-2 md:ml-4">
        <h3 class="mt-2">
          The entire code is parsed and stripped of comments or documentation.
          The number of lines is calculated by the number of characters divided
          by 80 for each file.
        </h3>
        <h3>
          The goal is to obtain a fair result, without penalizing the
          repositories with the most documentation.
        </h3>
        <h3>
          Moreover, only files written in the main language are retained, others
          are ignored.
        </h3>
      </div>
      <h2 class="text-2xl mt-8 font-bold text-mikado">Contributing</h2>
      <div class="ml-2 md:ml-4">
        <h3 class="mt-2">Any contribution is welcome!</h3>
        <h3>
          Apart from pull requests you can
          <a
            href="https://github.com/PabloLec/StarryLines/issues/new/choose"
            class="no-underline hover:underline text-mikado"
            >open an issue</a
          >
          to report a bug, report an irrelevant repository, etc.
        </h3>
        <h3>
          For feature requests, general ideas, or any broader topic, please use
          the
          <a
            href="https://github.com/PabloLec/StarryLines/discussions"
            class="no-underline hover:underline text-mikado"
            >Discussions section</a
          >.
        </h3>
      </div>
    </div>
    <button
      @click="closed"
      class="absolute top-0 right-2 mt-2 mr-2 text-2xl sm:text-base"
    >
      X
    </button>
  </vue-final-modal>
</template>

<script>
import { VueFinalModal, ModalsContainer } from "vue-final-modal";

export default {
  props: {
    showModal: {
      type: Boolean,
      required: true,
    },
    isPrerender: {
      type: Boolean,
      required: true,
    },
  },
  computed: {
    showModalModel: {
      get () {
        return this.showModal
      },
      set (value) {
        if (value === false) {
          this.closed()
        }
      }
    },
  },
  name: "Readme",
  components: {
    VueFinalModal,
    ModalsContainer,
  },
  methods: {
    closed() {
      this.$emit("close");
    },
  },
};
</script>
