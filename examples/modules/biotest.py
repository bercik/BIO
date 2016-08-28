#!/usr/bin/python3

import os
from fnmatch import fnmatch

def addIndent(s):
    s = '  ' + s
    s = s.replace('\n', '\n  ')
    return s

if __name__ == "__main__":
    root = './'
    pattern = "*.biot"

    for path, subdirs, files in os.walk(root):
        for name in files:
            if fnmatch(name, pattern):
                fpath = os.path.join(path, name)

                print("File: " + fpath)

                f = os.popen('bioc ' + fpath)
                out = f.read()

                if out.strip() == "":
                    f = os.popen('bio a.cbio')
                    out = f.read()

                    print(addIndent(out))
                else:
                    print("compilation failed:")
                    print(addIndent(out))
                
                print("-----------")

