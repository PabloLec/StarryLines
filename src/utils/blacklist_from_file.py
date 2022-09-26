from common import add_to_blacklist

with open("to_blacklist.txt") as f:
    add_to_blacklist(set(f.read().splitlines()))
