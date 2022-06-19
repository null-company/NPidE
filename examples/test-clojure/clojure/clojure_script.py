import argparse
import os
import sys
from project_modifier import copy_project_with_bp, parse_bps, change_bps_paths
import subprocess

def generate_project_clj(name, entry_point):
    return f"(defproject {name} \"0.1.0-SNAPSHOT\" \n" \
           "    :dependencies [[org.clojure/clojure \"1.10.3\"], [org.clojure/tools.trace \"0.7.9\"], [debugger \"0.2.1\"]]\n" \
           f"    :main {entry_point}\n" \
           "    :profiles {:uberjar {:aot :all\n" \
           "    :jvm-opts [\"-Dclojure.compiler.direct-linking=true\"]}})"


def check_arguments(project_dir, files, entry_point):
    package = os.path.normpath(entry_point.replace('.', '/'))
    package_flag = False
    if not os.path.isdir(project_dir):
        raise NotADirectoryError(f"{project_dir} folder does not exist!")
    for file in files:
        if not os.path.isfile(file):
            raise FileNotFoundError(f"{file} file does not exist!")
        if package in file:
            package_flag = True
    if not package_flag:
        raise Exception(f"{entry_point} is incorrect")
    return

def check_lein():
    subprocess.run(["lein version"], stdin=sys.stdin, stdout=sys.stdout, stderr=sys.stderr, shell=True)
    if not sys.stdout and sys.stderr:
        raise Exception(f"Leiningen does not exist!")
    else:
        return


def main():
    parser = argparse.ArgumentParser(description='Generate project.clj for clojure projects')
    parser.add_argument('-f', '--flag', type=str, choices=['run', 'build', 'debug'], help='flag of executing', )
    parser.add_argument('-n', '--name', type=str, help='name of clojure project', required=True)
    parser.add_argument('-d', '--project_dir', type=str, help='root directory of clojure project', required=True)
    parser.add_argument('-p', '--file', nargs="+", help='clojure fiels in project', required=True)
    parser.add_argument('-e', '--entry_point', help='entry-point of clojure project', required=True)
    parser.add_argument('-ext', '--change_extension', help='changing extension of project files')
    parser.add_argument('-b', '--breakpoints', action='append', help='breakpoints in project', required=False)

    args = parser.parse_args()
    args.file = [os.path.normpath(path) for path in args.file]
    check_lein()
    check_arguments(args.project_dir, args.file, args.entry_point)
    if args.flag == 'build':
        try:
            with open(f"{args.project_dir}/project.clj", 'w') as f:
                f.write(generate_project_clj(args.name, args.entry_point))
        except FileNotFoundError as err:
            print(str(err), file=sys.stderr)
        os.system(f"cd {args.project_dir} && lein uberjar")
    elif args.flag == 'run':
        os.system(f"java -jar {args.project_dir}/target/{args.name}-0.1.0-SNAPSHOT-standalone.jar")
    elif args.flag == 'debug':
        build_dir = copy_project_with_bp(args.project_dir, change_bps_paths(parse_bps(args.breakpoints[0]), args.project_dir)) # TODO: specify breakpoints format
        subprocess.run(["lein", "repl"], cwd=build_dir, stdin=sys.stdin, stdout=sys.stdout, stderr=sys.stderr)

if __name__ == '__main__':
    main()
