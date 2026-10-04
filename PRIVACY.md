# Privacy Policy

_Last updated: 2026-10-04_

**gof-patterns** is an instruction-only skill and plugin. It contains Markdown instructions (`SKILL.md`) and a reference catalog (`references/patterns.md`). It has no executable code, hooks, MCP servers, or scripts that run when you use it.

- **No data collection.** The skill does not collect, store, or transmit any data. It has no telemetry or analytics.
- **No network access.** The skill makes no network requests and points your agent at no external service.
- **Your code stays with your agent.** When the skill is active, your AI agent (Claude Code, Codex, or another) reads files in your project with its own tools and under its own permissions, in order to analyze design patterns. Whatever that agent does with your code is governed by the agent provider's privacy policy, not by this skill.
- **Read-only by design.** The skill tells the agent to analyze and report. It never asks the agent to modify, upload, or delete your files.

The optional `evals/` folder in this repository is a development test suite. It runs only when a maintainer starts it by hand, and nothing invokes it during normal use.

Questions: open an issue at https://github.com/jaeleeps/gof-patterns/issues.
