from common import db, LANGS, add_to_blacklist

MAX_SIZE = 1_000_000  # 1GB


def get_repos_to_blacklist(coll_name: str):
    c = db[coll_name]
    too_big = set()
    too_small = set()
    for repo in c.find({}):
        if repo["diskUsage"] > MAX_SIZE:
            too_big.add(repo["url"])
        elif repo["loc"] < 15:
            too_small.add(repo["url"])
    return too_big, too_small


def main():
    for lang in LANGS:
        too_big, too_small = get_repos_to_blacklist(lang)
        add_to_blacklist(too_big, "REPO TOO BIG")
        add_to_blacklist(too_small, "REPO TOO SMALL")


main()
