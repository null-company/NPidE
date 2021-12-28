import os
import re
import subprocess
import cocas
import argparse
import yaml
from pathlib import Path


def hex_to_int(s: str):
    return int.from_bytes(bytes.fromhex(s), 'little')


def main():
    parser = argparse.ArgumentParser(description='CdM-8 Build 0.1dev')
    parser.add_argument('filenames', type=str, nargs='+', help='source_file[.asm]')
    parser.add_argument('-b', dest='breakpoints', type=str, default='',
                        help='map of breakpoints (ex. "file1.asm: 0, 5, 13- file2.asm: 2, 3, 5")'
                        )
    parser.add_argument('-o', dest='output_file', type=str, default='debug.cdm8dbg.yaml',
                        help='output file path')
    parser.add_argument('-m', dest='mlb', type=str,
                        default=str(Path(os.path.dirname(os.path.abspath(__file__)))) + '/' + 'standard.mlb',
                        help='macro lib path')

    args = parser.parse_args()
    breakpoints_str: str = args.breakpoints
    breakpoint_files = breakpoints_str.split('-')
    breakpoint_map = {}
    for i in breakpoint_files:
        if i == '':
            continue
        bp_file, bp_list_str = i.split('::')
        bp_file: str
        breakpoint_map[bp_file.rstrip()] = [int(j) for j in bp_list_str.split(',')]

    line_byte_map = {}
    for filename in args.filenames:
        # compile the file
        cocas.filename = filename
        if cocas.filename[-4:] == ".asm":
            cocas.filename = cocas.filename[:-4]
        try:
            file = open(cocas.filename + '.asm', 'r')
        except IOError:
            raise IOError(cocas.filename + ".asm: file not found")

        text = []
        for line in file.readlines():
            line = line.rstrip()
            text += [line.expandtabs()]

        skipfile = False
        mlb_path = '.'
        mlb_name = args.mlb
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
                raise IOError("WARNING: no " + mlb_name + " found")

        if not skipfile:
            cocas.takemdefs(mlibfile, "standard.mlb")
            mlibfile.close()

        cocas.text = text

        result = cocas.asm()
        cocas.genoc(result)
        cocas.lst_me = True
        listing = cocas.pretty_print(result, cocas.text, False)
        # get the listing by lines to produce line_byte_map[filename]
        lines = listing.split('\n')
        my_dict = {}
        idx = 0
        while idx < len(lines):
            while ':' not in lines[idx] or re.match("^[0-9abcdef]{2}$", lines[idx].split(':')[0]) is None:
                idx += 1
                if idx == len(lines):
                    break
            if idx == len(lines):
                break
            if re.match("^[0-9abcdef]{2}$", lines[idx].split(':')[0]) is not None:
                my_dict[idx] = hex_to_int(lines[idx].split(':')[0])
            idx += 1

        line_byte_map[filename.rstrip()] = my_dict
        print(f"ASM listing of {filename}:")
        print(listing)
        # check if breakpoints are relevant to the files

    obj_filenames = [i[:-4] + '.obj' for i in args.filenames]
    # link files
    print('Linker output:')
    link_result = subprocess.check_output(
        ['python', str(Path(os.path.dirname(os.path.abspath(__file__)) + '/cocol.py'))] + obj_filenames + ['-l']).decode('utf-8')
    print(link_result)
    sects = {}
    # parse the listing to get info about sections
    rel_sects = re.findall(
        r'SECTION \'(.*)\' from file:([^\s]*)\s*ALLOCATION start: ([0-9abcdef]{2}) size: ([0-9abcdef]{2})', link_result)
    abs_sects = re.findall(
        r'From file: (.*\s*)\s*start:\s*([0-9abcdef]{2})\s*size:([0-9abcdef]{2}\s*)', link_result)
    for i in rel_sects:
        sects_filename = i[1].rstrip()
        if i[1] not in sects.keys():
            sects[sects_filename] = []
        sects[sects_filename].append({'start': hex_to_int(i[2].rstrip()), 'size': hex_to_int(i[3].rstrip()), 'name': i[0]})
    for i in abs_sects:
        sects_filename = i[0].rstrip()
        if i[0] not in sects.keys():
            sects[sects_filename] = []
        sects[sects_filename].append({'start': hex_to_int(i[1].rstrip()), 'size': hex_to_int(i[2].rstrip()), 'name': 'abs'})

    for i in line_byte_map.keys():
        for j in line_byte_map[i].keys():
            for k in range(len(sects[i[:-4] + '.obj'])):
                line_byte_map[i][j] += sects[i[:-4] + '.obj'][k]['start']
    finaldict = {
        'exec': args.filenames[0][:-4] + '.img',
        'sects': sects,
        'line_byte_map': line_byte_map,
        'breakpoints': breakpoint_map
    }
    print(sects)
    print(line_byte_map)
    print(breakpoint_map)
    with open(args.output_file, 'wt') as file:
        yaml.dump(finaldict, file)


if __name__ == "__main__":
    main()
