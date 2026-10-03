# Iteration 1 results

- Date: 2026-10-03
- Model: claude-opus-5-5 (Claude Code 2.1.289, headless `claude -p`)
- Skill: initial version (PR #1)
- Runner: `python3 evals/run_evals.py <workspace>`

## Benchmark

| Eval | Config | Skill triggered | Expectations | Time | Cost |
|---|---|---|---|---|---|
| 1 full-scan-ts | with_skill | yes | 11/11 | 52s | $0.34 |
| 1 full-scan-ts | without_skill | — | 11/11 | 63s | $0.35 |
| 2 targeted-misapplied-state | with_skill | yes | 5/5 | 36s | $0.27 |
| 2 targeted-misapplied-state | without_skill | — | 5/5 | 35s | $0.54 |
| 3 name-trap | with_skill | yes | 4/4 | 28s | $0.23 |
| 3 name-trap | without_skill | — | 4/4 | 26s | $0.21 |
| 4 python-idiomatic-and-overengineering | with_skill | yes | 7/7 | 39s | $0.29 |
| 4 python-idiomatic-and-overengineering | without_skill | — | 6/7 | 31s | $0.24 |
| **Total** | with_skill | 4/4 | **27/27 (100%)** | | $1.13 |
| | without_skill | | **26/27 (96%)** | | $1.34 |

The only expectation that failed without the skill: in eval 4, the baseline called `BaseExporter` (Template Method) "borderline" and suggested replacing it with plain functions. The expected verdict is appropriate: there are two real subclasses that share a real skeleton.

## Observations

1. **The skill triggered in all 4 of 4 with-skill runs**, both for broad prompts ("what design patterns…") and narrow ones ("is Strategy right for…").
2. **These evals barely discriminate.** The base model already identifies GoF patterns well, including the State-vs-Strategy and Decorator-vs-Proxy traps. What the skill adds is mostly consistency:
   - Every finding gets a confidence label: Confirmed, Partial, Idiomatic, or Name-only.
   - Verdicts use a fixed vocabulary.
   - Participants are mapped to code at `file:line`.
   - Reports include a "Considered and rejected" section. For example, the skill run ruled out Chain of Responsibility in `RetryingHttpClient` and Template Method in `MemberPricing`.
   - The skill run commits to a verdict on idiomatic patterns instead of hedging.
3. **Regression to address: the skill narrows focus.** Baseline eval 1 also reported real correctness bugs it noticed while reading: `pay()` ignores `ChargeResult.ok`, a double-charge race, and a refund that is never recorded. The with-skill run did not mention them. The skill should surface bugs it notices, briefly.

## Next iteration
- Harder fixtures: lookalikes that the base model is more likely to miss (Bridge vs Strategy, Composite vs Decorator, Command vs Strategy, a degraded pattern eroded by `instanceof`, a Mediator posing as a Facade), framework-provided patterns, and a larger Java codebase.
- Add a ground rule to report incidental correctness bugs in a short section.
