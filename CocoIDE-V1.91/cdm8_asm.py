# CDM8 Assembly language syntax/highlighter definitions
# M L Walters, V1 Sept 2016
# CocoIDE V 0.93
# CocoIDE V1.0+  Unpdate for CDM8 V4 Assembly Language Version + additional standard macro defs

language = "CDM8 Assembly Language"
fileext = ".asm"
version = "V1.2"
screenScaleMode = False # = "Presenter mode" scales main window to fill
                        # screen, also good for smaller screens.
                        # False = "Lab PC mode" smaller fixed window size.
                        # Note, -p option overides this setting to scale display.


helpFile="CocoIDE-SoftwareManual.pdf"
basefont=None#"monospace 6" # None = system default font. Try "courier 10 bold", "monospace 12", "arial 11" etc.
watchtrigs = ["dc", "ds"]
labelspec = [":", ">"]
entrySpec = "_" # If label starts with this, add to RunFrom menu
labelcolour = "brown"
commentprefix = "#"
commentcolour="slate gray"
PCcolour = "orange"
SPcolour = "medium orchid"
membgColour = "white"
memColour = "black"
chMemColour = "red"
bpColour = "grey"
errColour = "pink"
indent=4
iportColour = "royal blue"
oportColour = "green3"

highlights ={"blue":["r0", "r1", "r2", "r3",
                "ext", "ld", "st", "ldi", "ldc", "move",
                "add", "addc", "sub", "cmp", "and", "or", "xor", "not",
                "neg", "dec", "inc", "shr", "shra", "shla", "rol",
                "push", "pop",
                "jsr", "rts", "osi", "osix", "rti", "crc",
                "br", "beq", "bz", "bne", "bnz", "bhs", "bcs", "blo",
                "bcc", "bmi", "bpl", "bvs", "bvc", "bhi", "bls",
                "bge", "blt", "bgt", "ble", "ret", "nop", "wait", "halt",
                "pushall", "popall", "setsp", "addsp", "ldsa",
                "ioi","osix", "rti"],

        "green":["asect", "rsect", "end", "dc", "ds", "tplate",
                    "p0", "p1", "p2", "p3", "p4", "p5", "p6", "p7"],

        "purple":[  "run", "else", "if", "fi", "is",
                    "gt", "lt", "le", "ge", "mi", "pl", "eq", "ne", "z", "nz",
                    "cs", "cc", "vs", "vc","hi", "lo", "hs", "ls",
                    "macro", "mpop", "mpush", "mend",
                    "continue", "wend", "until", "while",
                    "save", "restore", "define", "stsp", "ldsp",
                    "stays", "true", "break", "tst", "clr", "do",
                    "then", "unique", "first_item", "item", "last_item",
                    "jmp", "jsrr", "shl", "banything", "bngt", "bnge", "bneq",
                    "bnne", "bnlt", "bnle", "bnhi", "bnhs", "bncs", "bnlo",
                    "bnls", "bncc", "bnmi", "bnpl", "bnfalse", "bntrue",
                    "bnvs", "bnvc", "bnvs", "define", "ldv", "stv",
                    "ei", "di" ]
        }


# Not implemented
delim = ","
hexprefix = "0x"
binprefix = "0b"
numcolour = "black"
srtdelim = '"'
alphachrs = "abcdefghijklmnopqrstuvwABCDEFGHIJKLMNOPQRSTUVW_"
numchrs = "01234567890abcdefABCDEF-+"
bracketleft = ["[", "{", "("] #Not used - too slow
bracketright = ["]", "}", ")"]# Not used - to slow




