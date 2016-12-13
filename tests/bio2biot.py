
import re 
import sys

comment_sign = "@"

def change_one_line(line):
    if line.strip() == "def onSTART()":
        return ""

    if line == "\n":
        return "end\n"

    reg_exp = '(\s*)PRINT\((.*),\s*"\\\\n"\)\s*' + comment_sign + '\s*(.*)'
    m = re.search(reg_exp, line)
    if m != None:
        indent = m.group(1)
        actual = m.group(2)
        expected = m.group(3)
        expected = expected.strip()

        if expected == "true":
            result = indent + "bt_assert_true(" + actual + ")"
        elif expected == "false":
            result = indent + "bt_assert_false(" + actual + ")"
        elif expected == "error":
            result = indent + "bt_assert_error(" + actual + ")"
        else:
            reg_exp = '\[(.*)\]'
            m = re.search(reg_exp, expected)
            if m != None:
                inside = m.group(1)
                expected = "CR_TUP(" + inside + ")"

            result = indent + "bt_assert_eq(" + actual + ", " + expected + ")"

        return result
    else:
        reg_exp = '\s*PRINT\("(.*)\\\\n"\)'
        m = re.search(reg_exp, line)
        if m != None:
            name = m.group(1)

            return "def test_" + name.lower() + "()"
        else:
            return line[0:-1]

if __name__ == "__main__":

    if len(sys.argv) < 2:
        print("usage: " + sys.argv[0] + " input_file")
        exit(-1)

    with open(sys.argv[1]) as f:
        lines = f.readlines()

    output = '#INCLUDE("<stdlib/test.biom>")\n'
    
    for l in lines:
        output += change_one_line(l) + "\n"

    print(output)
