# gof-patterns

An [Agent Skill](https://agentskills.io) that finds **Gang of Four design patterns** in a codebase and judges whether each one is a good fit.

- **Detect**: reports which files and directories implement which of the 23 GoF patterns, mapping each pattern participant to `file:line`. Every finding gets a confidence level: Confirmed, Partial, Idiomatic, or Name-only.
- **Evaluate**: gives each pattern a verdict of Appropriate, Over-engineered, Misapplied, or Degraded, based on whether the problem the pattern solves is actually present in the code.

It follows the open [Agent Skills](https://agentskills.io/specification) format, so it works with Claude Code, OpenAI Codex, Gemini CLI, Cursor, GitHub Copilot, OpenCode, and any other agent that reads `SKILL.md`.

## Install

**Any agent (Codex, Gemini CLI, Cursor, GitHub Copilot, OpenCode, Claude Code, …)**, using the [`skills`](https://github.com/vercel-labs/skills) installer:

```bash
npx skills add jaeleeps/gof-pattern              # this project: .agents/skills/gof-patterns
npx skills add jaeleeps/gof-pattern -g           # every project (user scope)
npx skills add jaeleeps/gof-pattern -a codex     # a specific agent only
```

**Claude Code plugin:**

```
/plugin marketplace add jaeleeps/gof-pattern
/plugin install gof-patterns@gof-patterns
```

**Manual:** copy `skills/gof-patterns/` into your agent's skills directory. Keep the folder name `gof-patterns`, because the spec requires it to match the skill name.

| Agent | Project scope | User scope |
|---|---|---|
| Codex, Gemini CLI, Cursor, GitHub Copilot, OpenCode, Amp | `.agents/skills/gof-patterns/` | `~/.agents/skills/gof-patterns/` |
| Claude Code | `.claude/skills/gof-patterns/` | `~/.claude/skills/gof-patterns/` |

## Usage

Ask in plain language, for example *"What design patterns does `src/payments` use?"* or *"Is this Singleton appropriate?"* The agent loads the skill when the request matches. You can also invoke it explicitly: `$gof-patterns` in Codex, or `/gof-patterns:gof-patterns` with the Claude Code plugin.

**Agents without skill support** (chat UIs, the raw API): paste `skills/gof-patterns/SKILL.md` as instructions and attach `skills/gof-patterns/references/patterns.md` together with the code.

## Layout

- `skills/gof-patterns/`: the skill. Installers copy only this folder.
  - `SKILL.md`: the workflow and report format.
  - `references/patterns.md`: a catalog of all 23 patterns. Each entry covers intent, canonical examples, the participants to confirm, search signals, idiomatic forms, when the pattern fits, smells, and lookalike patterns.
- `.claude-plugin/`: the Claude Code plugin and marketplace manifests.
- `evals/`: test fixtures, the runner, and results. These aren't installed.

## Evals

`evals/` holds test prompts with checkable expectations (`evals.json`) and fixture codebases with planted patterns (`files/`: TypeScript, Python, Java). It also includes a runner that runs each prompt headlessly, with and without the skill, under Claude Code (`claude -p`) or OpenAI Codex (`codex exec`, run through `npx`; needs `codex login`):

```bash
python3 evals/run_evals.py /tmp/gof-evals                    # Claude Code, all evals, both configs
python3 evals/run_evals.py /tmp/gof-evals --agent codex      # the same under Codex
python3 evals/run_evals.py /tmp/gof-evals --ids 5,6          # a subset
```

Graded results for each iteration are in `evals/results/`.

## Sources

The pattern catalog is based on the GoF book, and was checked against these sources:
- Bloch & Garrod, [*23 Patterns in 80 Minutes*](https://www.cs.cmu.edu/~charlie/courses/15-214/2016-spring/slides/24%20-%20All%20the%20GoF%20Patterns.pdf), CMU 15-214 (2016)
- Christiansson (ed.) et al., [*GoF Design Patterns – with examples using Java and UML2*](https://edeleastar.github.io/design-patterns/topic00/pdf/c-logica-gof-catalogue.pdf), Logica (2008), CC BY-SA 3.0

See the Sources section of [references/patterns.md](skills/gof-patterns/references/patterns.md) for how these sources are used and where they disagree.

## License

[MIT](LICENSE)
