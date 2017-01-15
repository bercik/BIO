def process(text):
    text = text.replace("\\\\", "\\")
    if text[1] == "x":
        text = "\\u00" + text[2:]

    return text

f = open("plVar", "r")
s = f.read()

current = ""
current2 = ""
out = ""
compound = False

for ch in s:
    # print("-----------------")
    # print("ch: " + ch)
    # print("curr: " + current)
    # print("curr2: " + current2)
    # print("compound: " + str(compound))

    if compound:
        if current2 == "" or current2 == "\\":
            current2 += ch
            continue
    elif current == "" or current == "\\":
        current += ch
        continue

    if ch == "-":
        compound = True
    elif not compound and (ch == "\\" or ch == "\n"):
        out += "\"" + process(current) + "\"" + ",\n"
        current = ch
        current2 = ""
    elif compound and (ch == "\\" or ch == "\n"):
        out += "\"" + process(current) + "\"-\"" + process(current2) + "\"" + ",\n"
        current = ch
        current2 = ""
        compound = False
    elif compound:
        current2 += ch
    else:
        current += ch

print(out)
