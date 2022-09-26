from common import db, LANGS


def repo_to_html(repo: dict):
    return (
        f'<b><a href="{repo["url"]}">{repo["name"]}</a></b> - {repo["description"]}<br>'
    )


def get_all():
    output = ""
    for lang in LANGS:
        for repo in db[lang].find({}):
            output += repo_to_html(repo)

    return output


def get_tops():
    output = ""
    for lang in LANGS:
        output += f"<h1>{lang}</h1>"
        for repo in db[f"{lang}_top"].find({}):
            output += repo_to_html(repo)

    return output


with open("all.html", "w") as f:
    f.write("<html><body>")
    f.write(get_all())
    f.write("</body></html>")

with open("tops.html", "w") as f:
    f.write("<html><body>")
    f.write(get_tops())
    f.write("</body></html>")
