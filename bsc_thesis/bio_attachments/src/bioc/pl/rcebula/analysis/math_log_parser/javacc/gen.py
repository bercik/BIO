import sys

class Obj:
    pass

def indent(ind):
    return " " * ind

if __name__ == "__main__":
    if len(sys.argv) < 5 or len(sys.argv) % 2 == 0:
        print("Usage: " + sys.argv[0] + " prod_name next_prod_name (token_name token_symbol)+")
        sys.exit(0)

    prod_name = sys.argv[1]
    prod_name_prim = prod_name + "_1"
    next_prod_name = sys.argv[2]
    tokens = []

    for i in range(3, len(sys.argv), 2):
        token = Obj()
        token.name = sys.argv[i]
        token.symbol = sys.argv[i + 1]
        tokens.append(token)
        
    gen_code = "\nvoid " + prod_name + "() #" + prod_name.upper() + " : \n"
    gen_code += "{\n}\n{\n"
    gen_code += indent(4) + next_prod_name + "()" + "\n"
    gen_code += indent(4) + prod_name_prim + "()" + "\n"
    gen_code += "}\n\n"

    gen_code += "void " + prod_name_prim + "() #" + prod_name_prim.upper() + " : \n"
    gen_code += "{\n"
    gen_code += indent(4) + "Token t;\n"
    gen_code += "}\n"

    gen_code += "{\n"
    gen_code += indent(4) + "(\n"

    for t in tokens:
        gen_code += indent(8) + "t = <" + t.name + ">\n"
        gen_code += indent(8) + next_prod_name + "()\n"
        gen_code += indent(8) + prod_name_prim + "()\n"
        gen_code += indent(8) + "{\n"
        if t == tokens[0]:
            gen_code += indent(12) + "int newLine = t.beginLine + ei.getLineNum() - 1;\n"
            gen_code += indent(12) + "int newCh;\n"
        else:
            gen_code += indent(12) + "newLine = t.beginLine + ei.getLineNum() - 1;\n"
        gen_code += indent(12) + "if (t.beginLine == 1)\n"
        gen_code += indent(12) + "{\n"
        gen_code += indent(16) + "newCh = t.beginColumn + ei.getChNum() - 1;\n"
        gen_code += indent(12) + "}\n"
        gen_code += indent(12) + "else\n"
        gen_code += indent(12) + "{\n"
        gen_code += indent(16) + "newCh = t.beginColumn;\n"
        gen_code += indent(12) + "}\n"
        gen_code += indent(12) + "jjtThis.value = new ValueErrorInfo(\"" + \
            t.symbol + "\", new ErrorInfo(newLine, newCh, ei.getFile()));\n"
        gen_code += indent(8) + "}\n"

        if t != tokens[len(tokens) - 1]:
            gen_code += indent(4) + "|\n"

    gen_code += indent(4) + ")?\n"
    gen_code += "}\n"

    print(gen_code)
