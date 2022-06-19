import functools
import os

from cdm8_emu import CDM8Emu
import argparse as ap
import yaml


def in_breakpoint(d: dict, name: str, n: int):
    return d is not None and name is not None and n is not None and name[:-4] + '.asm' in d.keys() and str(n) in d[name[:-4] + '.asm']


def get_filename_by_byte(sects: dict, byte: int):
    for i in sects.keys():
        for j in sects[i]:
            if int(j['start']) <= byte < int(j['start']) + int(j['size']):
                return i
    return None


def get_line_by_byte(line_byte_map, curr_file, pc):
    if curr_file not in line_byte_map.keys():
        return None

    for i in line_byte_map[curr_file].keys():
        if pc == int(line_byte_map[curr_file][i]):
            return int(i)

    return None


def main():
    main_parser = ap.ArgumentParser(description='CDM8 emulator')
    main_parser.add_argument('filename')
    main_parser.add_argument('-y', dest='yaml', type=str,
                             default=os.path.dirname(os.path.abspath(__file__)) + '/' + 'debug.cdm8dbg.yaml',
                             help='path to build yaml file')

    args = main_parser.parse_args()
    yamlname = args.yaml
    with open(yamlname) as file:
        dbg_dict = yaml.load(file, yaml.BaseLoader)
    filename = dbg_dict['exec']
    breakpoints = dbg_dict['breakpoints']
    line_byte_map = dbg_dict['line_byte_map']
    sects = dbg_dict['sects']
    print('CDM8 Emulator, debugging: ', filename)
    cdm8 = CDM8Emu()

    with open(filename) as file:
        filelines = file.readlines()
        if not filelines[0] == 'v2.0 raw\n':
            raise ValueError('File is not in a standard .img format')
        filelines.pop(0)
        bytelist = []
        for line in filelines:
            bytelist.append(int.from_bytes(bytes.fromhex(line), byteorder='little', signed=False))
    for i in range(256):
        cdm8.memory[0][0][i] = bytelist[i]
    do_step = False
    while not cdm8.HALT:
        curr_file = get_filename_by_byte(sects, cdm8.PC)
        curr_file = curr_file if curr_file is None else curr_file[:-4] + '.asm'
        curr_line = get_line_by_byte(line_byte_map, curr_file, cdm8.PC)
        if in_breakpoint(breakpoints, curr_file, curr_line) or do_step:
            print(f'In file: {curr_file}')
            print(f'On line: {curr_line}')
            print(f"PC: {cdm8.PC}")
            print("Flags(CVZN): " + "0b{:04b}".format(cdm8.CVZN))
            print('Registers: ', ["0x{:02x}".format(i) for i in cdm8.regs])
            print('Memory:')
            for i in range(16):
                print(functools.reduce(lambda x, y: x + ' ' + y, ["{:02x}".format(i) for i in cdm8.memory[0][0][i * 16: (i + 1) * 16]]))
            print('Enter S to step, anything else to continue')
            a = input()
            with open('asdf.txt', 'wt')as xD:
                xD.write(a)
            do_step = 'S' in a or 's' in a

        cdm8.step()

    print(f"PC: {cdm8.PC}")
    print("Flags(CVZN): " + "0b{:04b}".format(cdm8.CVZN))
    print('Registers: ', ["0x{:02x}".format(i) for i in cdm8.regs])
    print('Memory:')
    for i in range(16):
        print(functools.reduce(lambda x, y: x + ' ' + y,
                               ["{:02x}".format(i) for i in cdm8.memory[0][0][i * 16: (i + 1) * 16]]))


if __name__ == "__main__":
    main()
