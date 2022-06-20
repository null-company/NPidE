import shutil
import os.path
import clojure_bp_modifier
import json

def parse_bps(bps_str):
    return json.loads(bps_str)


def change_bps_paths(bps, project_dir):
    return {os.path.relpath(k, project_dir): v for k, v in bps.items()}


build_dir = ".build1"


def ignore_dir(dir, files):
    files = list(filter(lambda dir: dir == build_dir, files))
    return files


def copy_project_with_bp(project_dir: str, break_points):
    shutil.copytree(project_dir, os.path.join(project_dir, build_dir), dirs_exist_ok=True,
                    ignore=ignore_dir)
    for filename, bps in break_points.items():
        with open(os.path.join(project_dir, build_dir, filename), "r") as f:
            program_with_bps = clojure_bp_modifier.add_breakpoints(f.read(), bps)
        with open(os.path.join(project_dir, build_dir, filename), "w") as f:
            f.write(program_with_bps)
    return os.path.join(project_dir, build_dir)
