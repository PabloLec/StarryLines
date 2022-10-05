<div align="center">
    <h1>:star2: StarryLines :star2:</h1>
</div>

<p align="center">
<a href="https://github.com/PabloLec/StarryLines/blob/main/LICENSE" target="_blank">
    <img src="https://img.shields.io/github/license/pablolec/StarryLines" alt="License">
</a>
<a href="#" target="_blank">
    <img src="https://sonarcloud.io/api/project_badges/measure?project=PabloLec_StarryLines&metric=coverage" alt="Coverage">
</a>
<a href="#" target="_blank">
    <img src="https://github.com/PabloLec/StarryLines/actions/workflows/tests.yml/badge.svg" alt="Tests">
</a>
</p>

---

<!--ts-->
   * [What is it?](#what-is-it)
      * [What should not be in StarryLines](#what-should-not-be-in-starrylines)
   * [Score calculation](#score-calculation)
      * [Star count](#star-count)
      * [Lines of code](#lines-of-code)
   * [Technicalities](#technicalities)
   * [Contributing](#contributing)
<!--te-->

---

# What is it?

StarryLines retrieves from GitHub the best repositories for each language and then ranks them by the ratio between the number of stars and the number of lines of code.
The idea is to find the lines of code that seem to have been proportionally the most interesting for the greatest number of developers. Useful for learning or just for curiosity.


#### What should not be in StarryLines

Tutorials, cheatsheets, boilerplates, roadmaps and other directories that are not code or not actual projects.
Some filters are in place as well as a manual exclusion list. It is possible that there are irrelevant entries in the tables.
If you think you see a repository that doesn't belong, please [open an issue](https://github.com/PabloLec/StarryLines/issues/new?assignees=PabloLec&labels=enhancement&template=repo_removal.md&title=Repository+deletion).


# Score calculation

The score is obtained by dividing the number of stars by the number of lines of code.
Star count is adjusted and several operations are applied on the code to obtain a relevant number of lines.

### Star count

Although the star count displayed in the table is left unchanged, the one taken into account is adjusted. The number of stars is divided by the proportion of code written in the main language.
For a repository whose main language is C, composed of 60% C and 40% Python with 100 stars in total, the final star count will be 60.

### Lines of code

The entire code is analyzed and stripped of comments or documentation. The number of lines is calculated by the number of characters divided by 80 for each file.
The goal is to obtain a fair result, without penalizing the repositories with the most documentation.
Moreover, only files written in the main language are retained, the others are ignored.

# Technicalities

#### Backend

Written in Kotlin as a CLI. The whole thing is orchestrated by crons that execute actions (retrieving data from the API, counting lines of code and creating tops) periodically.
In the dependencies we find Apollo for the interactions with the GraphQL database of GitHub, and KMongo for the database.

#### Database

A Mongo database, hosted on Mongo Atlas. Each language has a main collection and another one that serves as a top 100

#### Frontend

A small Vue + Tailwind application, hosted by Cloudflare. The latter also hosts workers that act as middleware before the database. The tops are stored in KV stores and edge cache as a fallback. This allows to relieve the Mongo database and to avoid slow and useless requests.

# Contributing

Any contribution is welcome.
Apart from pull requests you can [open an issue](https://github.com/PabloLec/StarryLines/issues/new/choose) to report a bug, report an irrelevant repository, etc.
For features requests, general ideas or any broader topic, please prefer the [Discussions section](https://github.com/PabloLec/StarryLines/discussions).
