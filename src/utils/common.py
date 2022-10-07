from pymongo import MongoClient
from os import getenv
from urllib.parse import quote_plus

LANGS = (
    "javascript",
    "typescript",
    "java",
    "python",
    "c",
    "cpp",
    "csharp",
    "go",
    "kotlin",
    "rust",
    "swift",
    "shell",
    "php",
    "scala",
    "powershell",
    "ruby",
    "dart",
)

mongodb_client = MongoClient(
    f"mongodb+srv://{quote_plus(getenv('MONGO_USER'))}:{quote_plus(getenv('MONGO_SECRET'))}@{getenv('MONGO_CLUSTER')}/?retryWrites=true&w=majority"
)
db = mongodb_client[getenv("MONGO_DATABASE")]


def current_blacklist():
    c = db["blacklist"]
    return set([repo["url"] for repo in c.find({})])


def add_to_blacklist(urls: set, reason: str):
    c = db["blacklist"]
    current = current_blacklist()
    count = 0
    for url in urls:
        if url not in current:
            c.insert_one({"url": url, "reason": reason})
            print(f" - Added {url} to blacklist")
            current.add(url)
            count += 1
        else:
            print(f" ! {url} already in blacklist")
    print(f" -> Added {count} repos to blacklist")
