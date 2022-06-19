import re
import sys


def prepocess_clojure_program(s: str):
    """
    Process clojure program: replace comments and string literals with #
    :param s: clojure program
    :return:
    """
    string_literal = re.compile(r"\"([^\"\\\\]|\\\\.)*\"")
    comment = re.compile(r";;(.*?)(?=\n)")

    repl = lambda m: '#' * len(m.group())
    return comment.sub(repl, string_literal.sub(repl, s))


def get_open_bracket_pos(s: str, line_number: int):
    line_n = re.compile(f"(.*?\n){'{'}{line_number}{'}'}")
    start_idx = line_n.search(s).end()

    counter = 0
    max_counter = 0
    max_pos = -1
    for i in range(start_idx, len(s)):
        if s[i] == '(':
            counter += 1
        elif s[i] == ')':
            counter -= 1
        elif s[i] == '\n':
            break

        if counter > max_counter:
            max_counter = counter
            max_pos = i

    return max_pos


def get_closed_bracket(s: str, open_bracket_pos):
    counter = 1
    for i in range(open_bracket_pos+1, len(s)):
        if s[i] == '(':
            counter += 1
        elif s[i] == ')':
            counter -= 1
        if counter == 0:
            return i

    return None


def add_breakpoints(s, breakpoints):
    if len(breakpoints) == 0:
        return s
    prepocessed_str = prepocess_clojure_program(s)
    bp = breakpoints[0]
    open_bracket_pos = get_open_bracket_pos(prepocessed_str, line_number=bp)
    if open_bracket_pos == -1:
        print(f"No \"(\" found in {bp} line. Skip...", file=sys.stderr)
        return add_breakpoints(s, breakpoints[1:])
    closed_bracket_pos = get_closed_bracket(prepocessed_str, open_bracket_pos)
    if closed_bracket_pos is None:
        print(f"No \")\" found for open bracket in position: {open_bracket_pos}", file=sys.stderr)
        return add_breakpoints(s, breakpoints[1:])
    splitted_programm = [s[:open_bracket_pos], "(break ", s[open_bracket_pos:closed_bracket_pos], ")",
                         s[closed_bracket_pos:]]
    return add_breakpoints("".join(splitted_programm), breakpoints[1:])


if __name__ == "__main__":
    with open("test.clj", "r") as f:
        s = f.read()
    with open("output.clj", "w") as f:
        f.write(add_breakpoints(s, [1,4,5,10,11,13,16,17,20,24,25,29,44, 122, 190]))
