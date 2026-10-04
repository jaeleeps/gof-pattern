# Iteration 6: running the skill under OpenAI Codex

- Date: 2026-10-04
- Agents: OpenAI Codex CLI 0.160.0 (`codex exec`, read-only sandbox, ChatGPT login, default model) and, for the regression check, Claude Code 2.1.289 (claude-opus-5-5)
- Runner: `python3 evals/run_evals.py <ws> --agent codex`. With the skill, it is installed at `.agents/skills/gof-patterns`. "Triggered" means Codex read the skill's `SKILL.md`, since Codex has no dedicated skill tool.

## 1. First Codex run (skill v1.1.0): with vs without the skill

| Eval | with_skill | without_skill | Baseline failure |
|---|---|---|---|
| 1 full-scan-ts | 11/11 | 9/11 | kept calling order status a Strategy instead of State; missed the repeated `switch` |
| 2 targeted-misapplied-state | 5/5 | 5/5 | |
| 3 name-trap | 4/4 | **0/4** | called `ShippingProxy` a real "logging proxy"; never said `UserFactory` isn't a GoF factory |
| 4 python-idiomatic | 6/7 | 4/7 | hedged on Template Method; dismissed `@retry` as non-GoF; missed the Iterator |
| 5 full-scan-java-subtle | 9/10 | 6/10 | missed Bridge and Factory Method; called the Mediator a Facade |
| 6 targeted-degraded-strategy | 4/4 | 4/4 | |
| 7 no-false-positives | 3/3 | **0/3** | answered without reading any files |
| **Total** | **42/44 (95%)** | **28/44 (64%)** | |

The skill triggered in 7 of 7 runs. **It helps much more on Codex (+14 expectations) than on Claude (+1 in iteration 1).** The Codex baseline is weaker exactly where the catalog adds knowledge: telling lookalike patterns apart, idiomatic forms, and name traps.

## 2. Diagnosis: Codex skipped the catalog

Across the 13 with-skill Codex runs in sections 1 and 3 (re-runs included), Codex read `references/patterns.md` in only 8. **Every with-skill failure came from a run that skipped it:**

- `Ids` labeled a "Partial Singleton": twice, in eval 5 (iteration 6) and eval 7 (6c/a).
- `@retry` put under "rejected" because it lacked GoF class structure (6b/c).
- A near-empty eval-4 report that listed only 2 patterns (6c/a).

Claude always followed `SKILL.md`'s suggestion to read the catalog. Codex treated it as optional.

## 3. Fixes (skill v1.2.0)

- **`SKILL.md`, new step 0 "Read the catalog (required)":** read `references/patterns.md` in full before classifying anything, even for single-pattern questions.
- **`SKILL.md`, new ground rule:** idiomatic forms (generator → Iterator, `@decorator` → Decorator, a `key=` function → Strategy) go in the summary table as **Idiomatic**, never under "rejected".
- **`patterns.md`, Singleton "Confirm":** the global accessor must *return an instance*. A class with only static members is a static utility class, not even a "Partial" Singleton.

Intermediate re-runs (6b, 6c) applied the Singleton and idiom rules without the required catalog step. Results stayed inconsistent until step 0 was added.

## 4. Codex after the fixes (two full runs, with_skill)

| Eval | Run a | Run b |
|---|---|---|
| 1 | 11/11 | 11/11 |
| 2 | 5/5 | 5/5 |
| 3 | 4/4 | 4/4 |
| 4 | 7/7 | 6/7 (`read_records` called "ordinary language-native iteration", not an idiomatic Iterator) |
| 5 | 10/10 | 10/10 |
| 6 | 4/4 | 4/4 |
| 7 | 3/3 | 3/3 |
| **Total** | **44/44** | **43/44** |

The catalog was read in **14 of 14** runs. Grading: keyword checks flagged candidate misses, and every flagged output was then read by hand. All flags except the one above were correct answers worded differently.

Token use (Codex reports tokens, not dollars, on a ChatGPT login): with the skill, about 45k–130k input tokens per run (mostly cached) and 0.4k–3.8k output tokens.

## 5. Claude regression (skill v1.2.0, with_skill)

| Eval | Result | Cost |
|---|---|---|
| 4 python-idiomatic | 7/7 | $0.42 |
| 5 full-scan-java-subtle | 9/10 under the original expectation, **10/10 under the revised one** | $0.65 |
| 7 no-false-positives | 3/3 | $0.35 |

**Expectation revised (eval 5, #2).** Claude classified `Canvas` + `SvgCanvas`/`RasterCanvas` as **Builder**: the build steps (`drawRect`, `beginGroup`, …) plus a `toBytes()` result, with two concrete builders, which matches GoF's own Builder example. It explicitly rejected Bridge because `Shape` receives the canvas as an argument instead of holding a reference, which is the catalog's own Bridge "Confirm" test. The fixture supports both readings, so the expectation now accepts Bridge **or** Builder with reasoning. All earlier runs had answered Bridge, so their scores don't change.

Costs here are higher than in iteration 4 ($0.52 → $0.65 on eval 5) because these runs started on a cold prompt cache after the Codex runs. Output tokens are in line with earlier iterations.
