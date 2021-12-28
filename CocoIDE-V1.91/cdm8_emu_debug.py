import functools

from cdm8_emu import CDM8Emu
import argparse as ap


def main():
    main_parser = ap.ArgumentParser(description='CDM8 emulator')
    main_parser.add_argument('-f', dest='filename', type=str, const=None, default="", help='memory_image_file[.img]')
    main_parser.add_argument('-b', dest='breakpoints', type=str, const=None, default='', help='list of breakpoints (ex. "0, 6, 15, 140")')
    args = main_parser.parse_args()
    print('CDM8 Emulator, running: ', args.filename)
    cdm8 = CDM8Emu()
    with open(args.filename) as file:
        filelines = file.readlines()
        if not filelines[0] == 'v2.0 raw\n':
            raise ValueError('File is not in a standard .img format')
        filelines.pop(0)
        bytelist = []
        for line in filelines:
            bytelist.append(int.from_bytes(bytes.fromhex(line), byteorder='little', signed=False))
    for i in range(256):
        cdm8.memory[0][0][i] = bytelist[i]
    breakpoints = [int(i) for i in args.breakpoints.split(',')]
    do_step = False
    while not cdm8.HALT:
        if cdm8.PC in breakpoints or do_step:
            print(f"PC: {cdm8.PC}")
            print("Flags(CVZN): " + "0b{:04b}".format(cdm8.CVZN))
            print('Registers: ', ["0x{:02x}".format(i) for i in cdm8.regs])
            print('Memory:')
            for i in range(16):
                print(functools.reduce(lambda x, y: x + ' ' + y, ["{:02x}".format(i) for i in cdm8.memory[0][0][i * 16: (i + 1) * 16]]))
            a = input('Enter S to step, anything else to continue\n')
            do_step = a == 'S' or a == 's'

        cdm8.step()


if __name__ == "__main__":
    main()




