#!/usr/bin/python3

from jinja2 import Environment, FileSystemLoader
import os
from os.path import basename

class Obj:
    pass

if __name__ == "__main__":
    dir = os.path.dirname(os.path.realpath(__file__))

    modules = []

    for file in os.listdir(dir):
        if file != "template.html" and file != "index.html" and file != "_header.html":
            if file.endswith(".html"):
                module = Obj()
                # read file and add to modules
                with open(dir + "/" + file) as f:
                    file = basename(file)
                    file = file[:file.rfind(".")]
                    module.name = file
                    module.name_desc = file
                    module.content = f.read()
                modules.append(module)

    modules.sort(key=lambda x: x.name)

    header = Obj()
    header.name = "_header"
    header.header = True
    header.name_desc = "Data types"
    with open(dir + "/_header.html") as f:
        header.content = f.read()
    modules.insert(0, header)

    THIS_DIR = os.path.dirname(os.path.abspath(__file__))
    j2_env = Environment(loader=FileSystemLoader(THIS_DIR), trim_blocks=True)
    html = j2_env.get_template('template.html').render(modules=modules)

    with open(dir + "/index.html", "w") as f:
        f.write(html)
