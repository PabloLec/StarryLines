from common import db, LANGS, add_to_blacklist

MAX_SIZE = 1_000_000  # 1GB


def get_repos_too_big(coll_name: str):
    c = db[coll_name]
    to_blacklist = set()
    for repo in c.find({}):
        if repo["diskUsage"] > MAX_SIZE:
            to_blacklist.add(repo["url"])
    return to_blacklist


def main():
    for lang in LANGS:
        add_to_blacklist(get_repos_too_big(lang), "REPO TOO BIG")


main()
