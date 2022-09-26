from common import db, LANGS, add_to_blacklist

WORDS = [
    "deprecated",
    "obsolete",
    "tutorial",
    "cheatsheet",
    "cheat sheet",
    "boilerplate",
    "exercises",
]


def get_repos_to_blacklist(coll_name: str):
    c = db[coll_name]
    to_blacklist = set()
    for repo in c.find({}):
        if any(word in repo["name"].lower() for word in WORDS) or any(
            word in repo["description"].lower() for word in WORDS
        ):
            to_blacklist.add(repo["url"])
    return to_blacklist


def main():
    for lang in LANGS:
        add_to_blacklist(get_repos_to_blacklist(lang))


main()
