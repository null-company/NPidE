import yaml
import os


def main():
    cwd = os.getcwd()
    mp = {
        "build": [{
            "exec": "python3",
            "beforeFiles": cwd + "/cdm8_asm_build.py",
            "afterFiles": f"-o {cwd}/debug.cdm8dbg.yaml",
            "changeExt": "asm"
        }],
        "run": [{
            "exec": "python3",
            "beforeFiles": cwd + "/cdm8_emu_main.py",
            "afterFiles": "",
            "changeExt": "img"
        }],
        "debug": [{
            "exec": "python3",
            "beforeFiles": cwd + "/cdm8_emu_debug.py",
            "afterFiles": "",
            "changeExt": "img"
        }]
    }
    with open("config.yml", 'w') as file:
        yaml.dump(mp, file, default_style='"')


if __name__ == '__main__':
    main()