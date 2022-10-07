from common import db, LANGS, add_to_blacklist

MAX_SIZE = 1_000_000  # 1GB


def get_repos_to_blacklist(coll_name: str):
    c = db[coll_name]
    too_big = set()
    for repo in list(c.find({})):
        if repo["diskUsage"] > MAX_SIZE:
            too_big.add(repo["url"])
    return too_big


def main():
    for lang in LANGS:
        too_big = get_repos_to_blacklist(lang)
        add_to_blacklist(too_big, "REPO TOO BIG")


main()
