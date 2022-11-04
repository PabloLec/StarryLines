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
   * [Contributing](#contributing)
   * [Technicalities](#technicalities)
<!--te-->

---

# What is it?

StarryLines retrieves from GitHub the best repositories for each language and then ranks them by the ratio between the number of stars and the number of lines of code.
The idea is to find the lines of code that seem to have been proportionally the most interesting for the greatest number of developers. Useful for learning or just for curiosity.


#### What should not be in StarryLines

Tutorials, cheat sheets, lists, roadmaps and other repositories that are not code or not actual projects.
Some filters are in place as well as a manual exclusion list. It is possible that there still are irrelevant entries in the tables.
If you think you see a repository that doesn't belong, please [open an issue](https://github.com/PabloLec/StarryLines/issues/new?assignees=PabloLec&labels=enhancement&template=repo_removal.md&title=Repository+deletion).


# Score calculation

The score is obtained by dividing the number of stars by the number of lines of code.
Star count is adjusted and several operations are applied to the code to obtain a relevant number of lines.

### Star count

Although the star count displayed in the table is left unchanged, the one taken into account is adjusted. The number of stars is divided by the proportion of code written in the main language.
For a repository whose main language is C, composed of 60% C and 40% Python with 100 stars in total, the final star count will be 60.

### Lines of code

The entire code is parsed and stripped of comments or documentation. The number of lines is calculated by the number of characters divided by 80 for each file.
The goal is to obtain a fair result, without penalizing the repositories with the most documentation.  
Moreover, only files written in the main language are retained, others are ignored.

# Contributing

Any contribution is welcome!  
Apart from pull requests you can [open an issue](https://github.com/PabloLec/StarryLines/issues/new/choose) to report a bug, report an irrelevant repository, etc.
For feature requests, general ideas, or any broader topic, please use the [Discussions section](https://github.com/PabloLec/StarryLines/discussions).

# Technicalities

```mermaid
sequenceDiagram
participant B as Backend<br>(Kotlin)
participant D as Database<br>(MongoDB Atlas)
participant C as Cloudflare Edge
participant F as Frontend<br>(Vue)
loop cron
B-->B: Fetch Github API
B->>D: Update main collections
D->>B: Get outdated repos
B-->B: Parse repos lines of code
B->>D: Update LoC counts
D->>B: Get sorted repos by language
B-->B: Create top 100
B->>D: Update top 100
end
D->>C: Node workers consuming DB
Note over C: Top lists are stored inside a KV data store<br>and edge cache for redundancy
F->>C: Request top list
C->>F: Node worker consume KV<br>or cache to serve data
```
