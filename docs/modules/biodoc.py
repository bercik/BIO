#!/usr/bin/python3

import re
import sys

def unique(seq): 
   # order preserving
   checked = []
   for e in seq:
       if e not in checked:
           checked.append(e)
   return checked

class Function:
    def __init__(self, name, alias, params, errors,
            returned, desc, repeatedFrom, repeatedTo):
        self.name = name
        self.alias = alias
        self.params = params
        self.errors = errors
        self.returned = returned
        self.desc = desc
        self.repeatedFrom = repeatedFrom
        self.repeatedTo = repeatedTo

class Returned:
    def __init__(self, types, desc):
        self.types = types
        self.desc = desc

class Error:
    def __init__(self, name, desc):
        self.name = name
        self.desc = desc

class Param:
    def __init__(self, name, types, desc):
        self.name = name
        self.types = types
        self.desc = desc
    def __eq__(self, other):
        return (isinstance(other, self.__class__)
            and self.name == other.name)

    def __ne__(self, other):
        return not self.__eq__(other)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("usage: " + sys.argv[0] + " input_file")
        sys.exit(0)

    fname = sys.argv[1]

    with open(fname) as f:
        lines = f.read().splitlines()

    nameRe = re.compile("^!name\s+([^\s]+)$")
    aliasRe = re.compile("^!alias\s+([^\s]+)$")
    paramRe = re.compile("^!param\s+([^\s]+)\s+\(([^\)]+)\)\s+(.*)$")
    repeatedRe = re.compile("^!repeat\s+([^\s]+)\s+\(([^\)]+)\)\s+(.*)$")
    returnRe = re.compile("^!return\s+\(([^\)]+)\)\s+(.*)$")
    errorRe = re.compile("^!error\s+\(([^\)]+)\)\s+(.*)$")
    descRe = re.compile("^!desc\s+(.*)$")

    functions = []

    name = ""
    alias = ""
    params = []
    errors = []
    returned = None
    desc = ""
    repeatedFrom = None
    repeatedTo = None
    
    for i in range(0, len(lines)):
        line = lines[i]

        if line == "":
            fun = Function(name, alias, params, errors, 
                    returned, desc, repeatedFrom, repeatedTo)
            functions.append(fun)
            params = []
            errors = []
            name = ""
            alias = ""
            returned = None
            desc = ""
            repeatedFrom = None
            repeatedTo = None

        m = nameRe.match(line)
        if m != None:
            name = m.group(1)
            continue

        m = aliasRe.match(line)
        if m != None:
            alias = m.group(1)
            continue

        m = paramRe.match(line)
        if m != None:
            param = Param(m.group(1), m.group(2), m.group(3))
            params.append(param)
            continue

        m = repeatedRe.match(line)
        if m != None:
            if repeatedFrom == None:
                repeatedFrom = len(params)
                repeatedTo = len(params)
            else:
                repeatedTo = len(params)
            param = Param(m.group(1), m.group(2), m.group(3))
            params.append(param)
            continue

        m = returnRe.match(line)
        if m != None:
            returned = Returned(m.group(1), m.group(2))
            continue

        m = errorRe.match(line)
        if m != None:
            error = Error(m.group(1), m.group(2))
            errors.append(error)
            continue

        m = returnRe.match(line)
        if m != None:
            returned = Returned(m.group(1), m.group(2))
            continue
        m = descRe.match(line)
        if m != None:
            desc = m.group(1)
            continue

    s = fname
    fileName = s[s.rfind("/")+1:s.rfind(".")]

    html = "<h1>" + fileName + "</h1>\n"
    html += "<hr/>\n"
    # create html
    for f in functions:
        html += "<p class=\"fun_header\"><b>" + f.name + "</b>"
        if f.alias != "":
            html += "/<i>" + f.alias + "</i>"

        html += " (<i>"

        for i in range(0, len(f.params)):
            p = f.params[i]
            if i == f.repeatedFrom:
                html += "&lt"
            if i == len(f.params) - 1:
                html += p.name
            else:
                html += p.name + ", "
            if i == f.repeatedTo:
                html += "&gt"

        html += "</i>)</p>"
        html += "<p>" + f.desc + "</p>"

        if len(f.params) > 0:
            html += "<p><b>Parameters:</b></p>\n"
            html += "<div class=\"params\">\n"
            # add only unique parameters
            for p in unique(f.params):
                html += "<p><i>" + p.name + "</i> (<u>" + p.types + "</u>)" 
                if p.desc.strip() != "":
                    html += " - " + p.desc
                html += "</p>\n"
            html += "</div>\n"

        if f.returned != None:
            html += "<p><b>Return value:</b></p>\n"
            html += "<div class=\"returned\">\n"
            html += "<p>(<u>" + f.returned.types + "</u>)"
            if f.returned.desc.strip() != "":
                html += " - " + f.returned.desc 
            html += "</p>\n"
            html += "</div>\n"

        if len(f.errors) > 0:
            html += "<p><b>Errors:</b></p>\n"
            html += "<div class=\"errors\">\n"
            for e in f.errors:
                html += "<p><i>" + e.name + "</i> - " + \
                    e.desc + "</p>\n"
            html += "</div>\n"

        html += "<hr/>\n\n"

    indx = fname.rfind("/")
    dir = "."
    if indx != -1:
        dir = fname[:indx]
    fname = fname[indx+1:fname.rfind(".")]
    dir += "/html"
    outFile = dir + "/" + fname + ".html"
    with open(outFile, "w") as f:
        f.write(html)
