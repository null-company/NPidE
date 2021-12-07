from cdm8_emu import CDM8Emu
import argparse as ap


def main():
    main_parser = ap.ArgumentParser(description='CDM8 emulator')
    main_parser.add_argument('filename', type=str, const=None, default="", help='memory_image_file[.img]')
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
    while not cdm8.HALT:
        cdm8.step()
    print('Registers: ', cdm8.regs)
    print('Memory:')
    for i in range(16):
        print(cdm8.memory[0][0][i * 16: (i + 1) * 16])


if __name__ == "__main__":
    main()
