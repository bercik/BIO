#!/usr/bin/python3

import os
from subprocess import Popen, PIPE
from fnmatch import fnmatch

def addIndent(s):
    s = '  ' + s
    s = s.replace('\n', '\n  ')
    return s

if __name__ == "__main__":
    root = './'
    pattern = "*.biot"

    failed = False
    failList = []
    failTxt = "some test passed with errors"

    for path, subdirs, files in os.walk(root):
        for name in files:
            if fnmatch(name, pattern):
                fpath = os.path.join(path, name)

                print("File: " + fpath)

                sub = Popen(['bioc', fpath], stdout=PIPE, stderr=PIPE)
                out, err_out = sub.communicate()
                err_out = err_out.decode('UTF-8')

                if err_out.strip() == "":
                    f = os.popen('bio a.cbio')
                    out = f.read()

                    if failTxt in out.lower():
                        failed = True
                        failList.append(fpath)

                    print(addIndent(out))
                else:
                    print("compilation failed:")
                    print(addIndent(err_out))
                
                print("-----------")


    if failed:
        print("Some tests failed")
        print("List of files with failed tests:")
        print(failList)
    else:
        print("All test passed without errors")
