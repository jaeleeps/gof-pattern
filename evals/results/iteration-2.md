# Iteration 2 results

- Date: 2026-10-03
- Model: claude-opus-5-5 (Claude Code 2.1.289, headless `claude -p`)
- Skill: catalog checked against the CMU and Logica sources (PR #3)
- Configs: with_skill only (a regression check after the catalog changes)

| Eval | Skill triggered | Expectations | Time | Cost |
|---|---|---|---|---|
| 1 full-scan-ts | yes | 11/11 | 60s | $0.45 |
| 2 targeted-misapplied-state | yes | 5/5 | 28s | $0.27 |
| 3 name-trap | yes | 4/4 | 23s | $0.25 |
| 4 python-idiomatic-and-overengineering | yes | 7/7 | 41s | $0.29 |
| **Total** | 4/4 | **27/27 (100%)** | | $1.26 |

## Observations
- No regression from the catalog changes.
- Eval 1 reported the incidental `ChargeResult.ok` bug this time; iteration 1 did not. The "skill narrows focus" observation from iteration 1 varies between runs and does not happen every time. Adding an explicit ground rule would still make it reliable.
- Eval 4 now notes that the `EXPORTERS` dict is a registry and not a GoF Factory, matching the clarified Simple Factory guidance.
