<template>
  <vue-final-modal
    v-slot="{ params, close }"
    v-bind="$attrs"
    v-model="showModal"
    classes="flex justify-center items-center"
    content-class="relative flex flex-col max-h-full mx-4 p-4 border dark:border-gray-800 rounded bg-white dark:bg-gray-900"
    @closed="closed"
  >
    <span class="mr-8 text-2xl font-bold">
      <slot name="title">Readme</slot>
    </span>
    <div class="flex-grow overflow-y-auto">
      <slot v-bind:params="params"></slot>
      <div>What is it?</div>

      <div>
        StarryLines retrieves from GitHub the best repositories for each
        language and then ranks them by the ratio between the number of stars
        and the number of lines of code. The idea is to find the lines of code
        that seem to have been proportionally the most interesting for the
        greatest number of developers. Useful for learning or just for
        curiosity.
      </div>

      <div>What should not be in StarryLines</div>
      <div>
        Tutorials, cheatsheets, boilerplates, roadmaps and other directories
        that are not code or not actual projects. Some filters are in place as
        well as a manual exclusion list. It is possible that there are
        irrelevant entries in the tables. If you think you see a repository that
        doesn't belong, please open an issue.
      </div>
      <div>Score calculation</div>
      <div>
        The score is obtained by dividing the number of stars by the number of
        lines of code. Star count is adjusted and several operations are applied
        on the code to obtain a relevant number of lines.
      </div>
      <div>Star count</div>
      <div>
        Although the star count displayed in the table is left unchanged, the
        one taken into account is adjusted. The number of stars is divided by
        the proportion of code written in the main language. For a repository
        whose main language is C, composed of 60% C and 40% Python with 100
        stars in total, the final star count will be 60.
      </div>
      <div>Lines of code</div>
      <div>
        The entire code is analyzed and stripped of comments or documentation.
        The number of lines is calculated by the number of characters divided by
        80 for each file. The goal is to obtain a fair result, without
        penalizing the repositories with the most documentation. Moreover, only
        files written in the main language are retained, the others are ignored.
      </div>
      <div>Technicalities</div>
      <div>Backend</div>
      <div>
        Written in Kotlin as a CLI. The whole thing is orchestrated by crons
        that execute actions (retrieving data from the API, counting lines of
        code and creating tops) periodically. In the dependencies we find Apollo
        for the interactions with the GraphQL database of GitHub, and KMongo for
        the database.
      </div>
      <div>Database</div>
      <div>
        A Mongo database, hosted on Mongo Atlas. Each language has a main
        collection and another one that serves as a top 100
      </div>
      <div>Frontend</div>
      <div>
        A small Vue + Tailwind application, hosted by Cloudflare. The latter
        also hosts workers that act as middleware before the database. The tops
        are stored in KV stores and edge cache as a fallback. This allows to
        relieve the Mongo database and to avoid slow and useless requests.
      </div>
      <div>Contributing</div>
      <div>
        Any contribution is welcome. Apart from pull requests you can open an
        issue to report a bug, report an irrelevant repository, etc. For
        features requests, general ideas or any broader topic, please prefer the
        Discussions section.
      </div>
    </div>
    <button @click="closed" class="absolute top-0 right-0 mt-2 mr-2">X</button>
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
