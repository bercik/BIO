#!/usr/bin/python3

import os
from os.path import basename

if __name__ == "__main__":
    dir = os.path.dirname(os.path.realpath(__file__))

    html = "<html>\n"
    html += "<head>\n"
    html += "<link rel=\"stylesheet\" type=\"text/css\" href=\"index.css\">\n"
    html += "<title>BIO DOC</title>\n"
    html += "</head>\n"
    html += "<body>\n"
    html += "<h1>BIO Documentation</h1>\n"
    html += "<a name=\"index\"></a>\n"
    html += "<h3>Table of contents</h3>\n"
    html += "<ul>\n"

    docs = ""

    for file in os.listdir(dir):
        if file != "index.html":
            if file.endswith(".html"):
                # read file and add to whole html
                with open(file) as f:
                    file = basename(file)
                    file = file[:file.rfind(".")]
                    docs += "<a name=\"" + file + "\"></a>"
                    html += "<li><a href=\"#" + file + \
                            "\">" + file + "</a>"
                    docs += f.read()
                    docs += "<a href=\"#index\">table of contents</a>"

    html += "</ul>\n"
    html += docs

    html += "</body>\n"
    html += "</html>\n"

    with open(dir + "/index.html", "w") as f:
        f.write(html)
