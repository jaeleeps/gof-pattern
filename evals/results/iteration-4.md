# Iteration 4: file-reading efficiency

- Date: 2026-10-03
- Model: claude-opus-5-5 (Claude Code 2.1.289, headless `claude -p`)
- Goal: fix the cost and time regression on full scans that iteration 3 reported.

## Diagnosis

Iteration 3's "38 turns vs 5" compared the wrong things. `num_turns` in the result event counts tool calls. Parallel `Read` calls from a single model turn show up as separate stream events, so each one was counted. Counting distinct assistant message ids shows the actual pattern:

| Run (eval 5) | Model turns | Tool calls | What happened | Cost | Time |
|---|---|---|---|---|---|
| it-3 with_skill | 10 | 36 | 2 shell dumps blocked by the permission check, 1 dump too large (46.9 KB, saved to a file), then a parallel re-read of all files | $0.61 | 96s |
| it-3 without_skill | 4 | 4 | one shell dump that came back inline (short relative paths, 29.5 KB) | $0.43 | 63s |

The waste came from **trying to dump the source tree through a shell command**. Whether that works depends on luck: permission checks, and the inline output limit, which path length alone can push over. The same thing happens without the skill (see baseline r1 below).

## Change

- `SKILL.md`: for small targets (up to roughly 40 files or 3,000 lines), list the files with Glob, read them all in **one parallel batch of `Read` calls**, and *don't* dump the tree through the shell. Step 2 (search first) now applies only to large targets.
- `run_evals.py`: reports `model_turns` (distinct assistant messages that used tools) and `tool_calls`, instead of the misleading `num_turns`.

## Results

| Eval | Config | Runs | Expectations | Model turns | Cost | Time |
|---|---|---|---|---|---|---|
| 5 full-scan-java-subtle | with_skill (before, it-3 + it-4 pre-fix) | 3 | 10/10 each | 10 / 5 / 7 | $0.61 / $0.43 / $0.62 | 96 / 62 / 110s |
| 5 full-scan-java-subtle | **with_skill (after)** | 3 | **10/10 each** | 3 / 4 / 3 | **$0.52 / $0.52 / $0.52** | 90 / 92 / 95s |
| 5 full-scan-java-subtle | without_skill | 2 | 9/10 each | 6 / 4 | $0.55 / $0.43 | 97 / 60s |
| 1 full-scan-ts | with_skill (after) | 1 | 11/11 | 4 | $0.56 | 81s |
| 1 full-scan-ts | without_skill | 1 | — (not graded) | 5 | $0.32 | 55s |
| 7 no-false-positives | with_skill (after) | 1 | 3/3 | 3 | $0.23 | 17s |
| 7 no-false-positives | without_skill | 1 | 3/3 | 2 | $0.17 | 12s |

Every baseline eval-5 run, here and in iteration 3, misses `Exporter.createCanvas()` as a Factory Method inside the Template Method.

## Observations

1. **Reading is now deterministic.** With the skill, reading takes 3–4 model turns, with no blocked or oversized shell attempts. Eval-5 cost is consistently $0.52, compared with $0.43–$0.62 before. The worst case is gone, and the mean drops from $0.55 to $0.52. The mean time doesn't change (89s → 92s): the occasional lucky fast run is gone along with the slow ones.
2. **The remaining overhead is the skill itself, not file reading.** In token terms:
   - With the skill: about 10k output tokens (reasoning plus a structured report), and it loads the 41 KB catalog (about 10k tokens, visible as a cache write of 42.7k vs 20.6k in eval 1).
   - Without the skill: 5–10k output tokens, and nothing extra loaded.
   - In return, the skill gives a consistent structure, catches the nested Factory Method that every baseline run missed, and produces 10/10 vs 9/10.
3. **A possible next lever:** split the catalog so that targeted questions (evals 2, 3, 6) load only the entries they need. Full scans need most of it anyway, so this would help only targeted questions.
