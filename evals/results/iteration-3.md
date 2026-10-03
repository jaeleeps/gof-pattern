# Iteration 3 results

- Date: 2026-10-03
- Model: claude-opus-5-5 (Claude Code 2.1.289, headless `claude -p`)
- Skill changes: a ground rule plus an "Other issues noticed" report section, so correctness bugs seen while reading are always listed.
- New fixture: `editor-java`, a Java diagram editor with subtler cases:
  - Bridge (Shape × Canvas)
  - Composite
  - Command with undo history, next to a name-only `AutosaveCommand`
  - Strategy eroded by `instanceof`
  - Template Method bypassed by a subclass override, with a Factory Method hook inside it
  - Mediator named `*Facade`
  - a `util` package with no patterns
  - a planted bug: an inverted undo
- New evals: 5 (full Java scan), 6 (targeted degraded Strategy), 7 (no false positives).

## Benchmark

| Eval | Config | Triggered | Expectations | Time | Turns | Cost |
|---|---|---|---|---|---|---|
| 1 full-scan-ts | with_skill | yes | 11/11 | 84s | 30 | $0.49 |
| 2 targeted-misapplied-state | with_skill | yes | 5/5 | 25s | 7 | $0.26 |
| 3 name-trap | with_skill | yes | 4/4 | 26s | 10 | $0.27 |
| 4 python-idiomatic-and-overengineering | with_skill | yes | 7/7 | 41s | 13 | $0.29 |
| 5 full-scan-java-subtle | with_skill | yes | **10/10** | 97s | 38 | $0.61 |
| 5 full-scan-java-subtle | without_skill | — | 9/10 | 64s | 5 | $0.43 |
| 6 targeted-degraded-strategy | with_skill | yes | 4/4 | 32s | 11 | $0.29 |
| 6 targeted-degraded-strategy | without_skill | — | 4/4 | 29s | 9 | $0.25 |
| 7 no-false-positives | with_skill | yes | 3/3 | 18s | 9 | $0.23 |
| 7 no-false-positives | without_skill | — | 3/3 | 12s | 5 | $0.17 |

- With skill, all evals: **44/44**, and the skill triggered in 7 of 7 runs.
- On the new evals 5–7: with skill **17/17**, without skill **16/17**.
- Baseline miss: in eval 5 it did not identify `Exporter.createCanvas()` as a Factory Method hook inside the Template Method.

## Observations

1. **The bug-reporting rule works.** All 4 regression runs now list incidental bugs. Eval 1 reports the ignored `ChargeResult.ok`, which iteration 1 omitted. Eval 5 reports the inverted `MoveCommand.undo`.
2. **Still close to the ceiling.** Even the subtler Java cases (Bridge, the Mediator posing as a Facade, the degraded Template Method) are mostly found without the skill. The skill adds:
   - consistent structure: confidence labels, a fixed verdict vocabulary, and a "considered and rejected" section;
   - finer distinctions, such as the Factory Method nested in a Template Method, and Bridge flagged as an idiomatic, parameter-passed form;
   - verdicts that don't hedge.
3. **Efficiency regression on large scans.** In eval 5 the skill used 38 turns against the baseline's 5: it searched first and then read files one at a time, while the baseline read all the files in a few batches. That made it 1.5× slower and 1.4× more expensive. A next iteration should tell the skill to batch-read small scopes up front and save searching for large repos.
4. **The no-false-positive check passes in both configs.** Neither run invented patterns in `util`, and both explicitly ruled out Singleton for `Ids`.
