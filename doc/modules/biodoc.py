#!/usr/bin/python3

from jinja2 import Environment, FileSystemLoader
import re
import sys
import os

class Obj:
    pass

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

    temp_funs = []
    # create template_functions (temp_funs)
    for f in functions:
        temp_fun = Obj()
        temp_fun.name = f.name
        if f.alias != "":
            temp_fun.alias = f.alias

        params_header = ""
        for i in range(0, len(f.params)):
            p = f.params[i]
            if i == f.repeatedFrom:
                params_header += "&lt"
            params_header += p.name
            if i == f.repeatedTo:
                    params_header += "&gt*"
            if i != len(f.params) - 1:
                params_header += ", "
        
        if params_header != "":
            temp_fun.params_header = params_header

        temp_fun.desc = f.desc

        if len(f.params) > 0:
            temp_fun.params = []
            # add only unique parameters
            for p in unique(f.params):
                temp_par = Obj()
                temp_par.name = p.name
                temp_par.types = p.types
                if p.desc.strip() != "":
                    temp_par.desc = p.desc
                temp_fun.params.append(temp_par)

        if f.returned != None:
            temp_fun.ret = Obj()
            temp_fun.ret.types = f.returned.types
            if f.returned.desc.strip() != "":
                temp_fun.ret.desc = f.returned.desc 

        if len(f.errors) > 0:
            temp_fun.errors = []
            for e in f.errors:
                temp_err = Obj()
                temp_err.name = e.name
                if e.desc.strip() != "":
                    temp_err.desc = e.desc
                temp_fun.errors.append(temp_err)
        
        temp_funs.append(temp_fun)

    THIS_DIR = os.path.dirname(os.path.abspath(__file__))
    j2_env = Environment(loader=FileSystemLoader(THIS_DIR), trim_blocks=True)
    html = j2_env.get_template('template.html').render(mod_name=fileName, functions=temp_funs)

    indx = fname.rfind("/")
    dir = "."
    if indx != -1:
        dir = fname[:indx]
    fname = fname[indx+1:fname.rfind(".")]
    dir += "/html"
    outFile = dir + "/" + fname + ".html"
    with open(outFile, "w") as f:
        f.write(html)
