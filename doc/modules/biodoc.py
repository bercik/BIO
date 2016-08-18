#!/usr/bin/python3

from lib.bd_parser import *
from jinja2 import Environment, FileSystemLoader
import os

class Obj:
    pass

if __name__ == "__main__":
    mods = []
    for file in os.listdir("."):
        if file.endswith(".bd"):
            mods.append(parse_bd(file))

    mods.sort(key=lambda x: x.mod_name)
    
    THIS_DIR = os.path.dirname(os.path.abspath(__file__))
    j2_env = Environment(loader=FileSystemLoader(THIS_DIR), trim_blocks=True)
    html = j2_env.get_template('template.html').render(modules=mods)

    with open(THIS_DIR + "/html/index.html", "w") as f:
        f.write(html)
