<template>
  <vue-final-modal
    v-slot="{ params, close }"
    v-bind="$attrs"
    v-model="showModal"
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
      <div class="text-2xl mt-4 font-bold text-mikado">What is it?</div>

      <div class="ml-1 md:ml-2">
        <p class="mt-2">
          StarryLines retrieves from GitHub the best repositories for each
          language and then ranks them by the ratio between the number of stars
          and the number of lines of code.
        </p>
        <p class="mt-4">
          The idea is to find the lines of code that seem to have been
          proportionally the most interesting for the greatest number of
          developers. Useful for learning or just for curiosity.
        </p>
      </div>
      <div class="text-xl mt-6">- What should not be in StarryLines</div>
      <div class="ml-2 md:ml-4">
        <p class="mt-2">
          Tutorials, cheat sheets, lists, roadmaps and other repositories that
          are not code or not actual projects.
        </p>
        <p>
          Some filters are in place as well as a manual exclusion list. It is
          possible that there still are irrelevant entries in the tables.
        </p>
        <p>
          If you think you see a repository that doesn't belong, please
          <a
            href="https://github.com/PabloLec/StarryLines/issues/new?assignees=PabloLec&labels=enhancement&template=repo_removal.md&title=Repository+deletion"
            class="no-underline hover:underline text-mikado"
            >open an issue.</a
          >
        </p>
      </div>
      <div class="text-2xl mt-8 font-bold text-mikado">Score calculation</div>
      <div class="ml-2 md:ml-4">
        <p class="mt-2">
          The score is obtained by dividing the number of stars by the number of
          lines of code.
        </p>
        <p>
          Star count is adjusted and several operations are applied to the code
          to obtain a relevant number of lines.
        </p>
      </div>
      <div class="text-xl mt-6">- Star count</div>
      <div class="ml-2 md:ml-4">
        <p class="mt-2">
          Although the star count displayed in the table is left unchanged, the
          one taken into account is adjusted. The number of stars is divided by
          the proportion of code written in the main language.
        </p>
        <p>
          For a repository whose main language is C, composed of 60% C and 40%
          Python with 100 stars in total, the final star count will be 60.
        </p>
      </div>
      <div class="text-xl mt-6">- Lines of code</div>
      <div class="ml-2 md:ml-4">
        <p class="mt-2">
          The entire code is parsed and stripped of comments or documentation.
          The number of lines is calculated by the number of characters divided
          by 80 for each file.
        </p>
        <p>
          The goal is to obtain a fair result, without penalizing the
          repositories with the most documentation.
        </p>
        <p>
          Moreover, only files written in the main language are retained, others
          are ignored.
        </p>
      </div>
      <div class="text-2xl mt-8 font-bold text-mikado">Contributing</div>
      <div class="ml-2 md:ml-4">
        <p class="mt-2">Any contribution is welcome!</p>
        <p>
          Apart from pull requests you can
          <a
            href="https://github.com/PabloLec/StarryLines/issues/new/choose"
            class="no-underline hover:underline text-mikado"
            >open an issue</a
          >
          to report a bug, report an irrelevant repository, etc.
        </p>
        <p>
          For feature requests, general ideas, or any broader topic, please use
          the
          <a
            href="https://github.com/PabloLec/StarryLines/discussions"
            class="no-underline hover:underline text-mikado"
            >Discussions section</a
          >.
        </p>
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
