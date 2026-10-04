# Iteration 5: cross-agent portability

- Date: 2026-10-04
- Model: claude-opus-5-5 (Claude Code 2.1.289, headless `claude -p`)
- Goal: make the skill usable from Codex, Gemini CLI, Cursor, GitHub Copilot, OpenCode, and other agents, not only Claude.

## Changes
- **Layout:** the skill moved to `skills/gof-patterns/`. Cross-agent installers copy only this folder: `SKILL.md`, `references/patterns.md`, and `LICENSE`. Before the move, `npx skills add` copied all 73 repo files into the user's project, eval fixtures included, so a pattern scan of the user's repo could pick up the planted fixture code.
- **Wording:** the one Claude-specific instruction ("List the files once (Glob), then issue all the `Read` calls…") now names no particular tool: list the files, read them with your file-reading tool, in parallel if supported, and avoid shell dumps.
- **Frontmatter:** added the optional spec fields `license` and `metadata` (author, version, repository).
- **`run_evals.py`:** separates `REPO_ROOT` (fixtures and evals) from `SKILL_DIR` (what gets installed).

## Verification

| Check | Result |
|---|---|
| `agentskills validate skills/gof-patterns` (the spec's reference validator, skills-ref 0.1.1) | Valid |
| `claude plugin validate . --strict` | Passed; the plugin loads 1 skill (v1.1.0) from the default `skills/` folder |
| `npx skills add <repo> -a codex cursor gemini-cli github-copilot opencode claude-code --copy` | Installed to `.agents/skills/gof-patterns` (shared by Codex, Cursor, Gemini CLI, Copilot, OpenCode) and to `.claude/skills/gof-patterns`. 3 files each; both copies pass `agentskills validate` |

### Claude regression (with_skill)

| Eval | Expectations | Model turns | Cost | Notes |
|---|---|---|---|---|
| 3 name-trap | 4/4 | 3 | $0.24 | |
| 5 full-scan-java-subtle (run a) | 10/10 | 3 | $0.79 | cold prompt cache: cache write 66.6k |
| 5 full-scan-java-subtle (run b) | 10/10 | 3 | $0.77 | cold prompt cache: cache write 66.6k |
| 5 full-scan-java-subtle (run c) | 10/10 | 4 | **$0.54** | warm cache: cache write 34.0k, the same as iteration 4 |
| 7 no-false-positives | 3/3 | 3 | $0.23 | |

Output tokens are unchanged from iteration 4 (10–11k on eval 5). The higher cost of runs a and b comes entirely from rebuilding the prompt cache after a session limit reset, not from the skill. Two earlier eval-5 attempts hit the account's session limit mid-run and were discarded.

## Not verified
- None of the other agents (Codex, Gemini CLI, Cursor, Copilot, OpenCode) is installed on this machine, so the skill was **not executed** under them. What *was* verified: spec validity, the installer layout for each agent, and that the instructions use no tool names specific to Claude. A next step is to run the eval prompts under `codex exec` once Codex is available.
