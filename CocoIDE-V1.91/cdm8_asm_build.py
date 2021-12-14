import cocas
import argparse

def main():
    parser = argparse.ArgumentParser(description='CdM-8 Build 0.1dev')
    parser.add_argument('filename', type=str, help='source_file[.asm]')

    args = parser.parse_args()
    cocas.filename = args.filename
    if cocas.filename[-4:] == ".asm":
        filename = cocas.filename[:-4]
    try:
        file = open(cocas.filename + '.asm', 'r')
    except IOError:
        raise cocas.filename + ".asm: file not found"

    text = []
    for line in file:
        line = line.rstrip()
        text += [line.expandtabs()]

    skipfile = False
    mlb_path = './'
    mlb_name = 'standard.mlb'
    try:
        mlibfile = open(mlb_path, 'r')
    except IOError:
        skipfile = True

    if skipfile:
        skipfile = False
        try:
            mlibfile = open(mlb_name, 'r')
        except IOError:
            skipfile = True
            raise ("WARNING: no " + mlb_name + " found")

    if not skipfile:
        cocas.takemdefs(mlibfile, "standard.mlb")
        mlibfile.close()

    """
    if args.mlibs != None:
        for x in args.mlibs:
            if x[-4:] == ".mlb":
                x = x[:-4]
            try:
                mlibfile = open(x + ".mlb", 'r')
            except IOError:
                raise (x + ".mlb not found")
            cocas.takemdefs(mlibfile, x)
            mlibfile.close()
    """
    cocas.lst_me = True
    result = cocas.asm(text)
    cocas.genoc(result)
    asdf = cocas.pretty_print(result, text, False)
    cocas.pretty_print(result, text, True)

    print("FUCK THE POLICE")
    print(asdf)


if __name__ == "__main__":
    main()
