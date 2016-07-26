#!/usr/bin/python3

import os
from os.path import basename

if __name__ == "__main__":
    dir = os.path.dirname(os.path.realpath(__file__))

    html = "<html>\n"
    html += "<head>\n"
    html += "<link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\" rel=\"stylesheet\">"
    html += "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>"
    html += "<link rel=\"stylesheet\" type=\"text/css\" href=\"index.css\">\n"
    html += "<title>BIO DOC</title>\n"
    html += "</head>\n"
    html += "<body>\n"
    html += "<h1 id=\"title\">BIO Documentation</h1>\n"


    html += "<a name=\"index\"></a>\n"
    html += "<h3>Table of contents</h3>\n"
    html += "<ul class=\"nav nav-pills nav-stacked\">\n"
    html += "<li><a href=\"#_header\">data types</a>"

    docs = ""

    for file in os.listdir(dir):
        if file != "index.html" and file != "_header.html":
            if file.endswith(".html"):
                # read file and add to whole html
                with open(dir + "/" + file) as f:
                    file = basename(file)
                    file = file[:file.rfind(".")]
                    docs += "<a name=\"" + file + "\"></a>"
                    html += "<li><a href=\"#" + file + \
                            "\">" + file + "</a>"
                    docs += f.read()
                    docs += "<a href=\"#index\">table of contents</a>"

    html += "</ul>\n"

    html += "<a name=\"_header\"></a>"
    html += "<hr/>"
    with open(dir + "/_header.html") as f:
        html += f.read()

    html += docs

    html += "</body>\n"
    html += "</html>\n"

    with open(dir + "/index.html", "w") as f:
        f.write(html)
