#!/usr/bin/env python3
"""Run the gof-patterns evals with headless `claude -p`, with and without the skill.

Usage: python3 evals/run_evals.py <workspace-dir> [--ids 1,2] [--configs with_skill,without_skill]

Each eval runs on a fresh copy of its fixture. The with_skill copy gets the skill
installed as a project skill at .claude/skills/gof-patterns. Runs are read-only.
Grading is done separately against the `expectations` in evals.json.
"""
import argparse
import json
import shutil
import subprocess
import sys
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
SKILL_DIR = REPO_ROOT / "skills" / "gof-patterns"
ALLOWED = "Read,Grep,Glob,Skill,Bash(grep:*),Bash(find:*),Bash(ls:*),Bash(cat:*),Bash(wc:*),Bash(git log:*)"
DISALLOWED = "Write,Edit,NotebookEdit,WebFetch,WebSearch,Agent"


def run_one(ev, config, workspace):
    run_dir = workspace / f"eval-{ev['id']}-{ev['name']}" / config
    project = run_dir / "project"
    if run_dir.exists():
        shutil.rmtree(run_dir)
    shutil.copytree(REPO_ROOT / ev["cwd"], project)
    if config == "with_skill":
        shutil.copytree(SKILL_DIR, project / ".claude" / "skills" / "gof-patterns")

    proc = subprocess.run(
        ["claude", "-p", ev["prompt"], "--output-format", "stream-json", "--verbose",
         "--allowedTools", ALLOWED, "--disallowedTools", DISALLOWED],
        cwd=project, capture_output=True, text=True, timeout=1800,
    )
    (run_dir / "transcript.jsonl").write_text(proc.stdout)

    events = [json.loads(line) for line in proc.stdout.splitlines() if line.strip().startswith("{")]
    result = next((e for e in reversed(events) if e.get("type") == "result"), {})
    # stream-json emits one event per content block, so parallel tool calls from a
    # single model turn arrive as separate events sharing a message id.
    tool_uses = [
        (e["message"]["id"], block)
        for e in events if e.get("type") == "assistant"
        for block in e.get("message", {}).get("content", []) if block.get("type") == "tool_use"
    ]
    triggered = any(
        b.get("name") == "Skill" and "gof-patterns" in json.dumps(b.get("input", {}))
        for _, b in tool_uses
    )
    summary = {
        "eval_id": ev["id"], "eval_name": ev["name"], "config": config,
        "exit_code": proc.returncode, "skill_triggered": triggered,
        "duration_ms": result.get("duration_ms"), "cost_usd": result.get("total_cost_usd"),
        "model_turns": len({mid for mid, _ in tool_uses}), "tool_calls": len(tool_uses),
    }
    (run_dir / "output.md").write_text(result.get("result", "") or proc.stderr)
    (run_dir / "run.json").write_text(json.dumps(summary, indent=2))
    return summary


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("workspace", type=Path)
    ap.add_argument("--ids", default="")
    ap.add_argument("--configs", default="with_skill,without_skill")
    args = ap.parse_args()

    evals = json.loads((REPO_ROOT / "evals" / "evals.json").read_text())["evals"]
    if args.ids:
        wanted = {int(i) for i in args.ids.split(",")}
        evals = [e for e in evals if e["id"] in wanted]
    jobs = [(e, c) for e in evals for c in args.configs.split(",")]

    args.workspace.mkdir(parents=True, exist_ok=True)
    with ThreadPoolExecutor(max_workers=len(jobs)) as pool:
        for s in pool.map(lambda j: run_one(j[0], j[1], args.workspace), jobs):
            print(json.dumps(s), flush=True)


if __name__ == "__main__":
    sys.exit(main())
