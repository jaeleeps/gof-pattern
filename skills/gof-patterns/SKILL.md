---
name: gof-patterns
description: Identify Gang of Four (GoF) design patterns in code and judge whether each one is appropriate. Use when the user asks which design patterns a file, directory, module, or codebase uses; whether code "follows" a pattern such as Factory, Singleton, Strategy, Observer, Adapter, Decorator, Visitor, or any of the 23 GoF patterns; whether a pattern is over-engineered, misapplied, or the right fit; or which pattern would fit a piece of code.
license: MIT (see LICENSE)
metadata:
  author: jaeleeps
  version: "1.2.1"
  repository: https://github.com/jaeleeps/gof-patterns
---

# GoF Design Pattern Analysis

This skill does two jobs:
1. **Detect** which GoF patterns the target code implements, and where.
2. **Evaluate** whether each detected pattern is the right fit for this code.

The catalog in [references/patterns.md](references/patterns.md) lists, for each of the 23 patterns, its intent, the participants you must confirm, search signals, idiomatic forms, when it fits, the smells of a bad fit, and the patterns it is often confused with.

## Workflow

### 0. Read the catalog (required)
Before you classify anything, read `references/patterns.md` in full. It holds the rules that decide the close calls and are not repeated here: what counts as a Singleton versus a static utility class, which idiomatic forms count, and how to tell Proxy from Decorator or Strategy from State. Do this even when the question names a single pattern. You can read it at the same time as the code.

### 1. Scope
Settle on the target: a file, a directory, a module, or the whole repo. If the user named nothing, use the current repo. For a large repo, start with a directory-level survey (entry points, core domain, extension points) and go deep only where candidates show up. Tell the user what you covered and what you skipped.

Note the language and paradigm. They decide which forms of a pattern count as idiomatic (see step 3).

Choose how to read the code based on the size of the target, not of the whole repo:
- **Small target (roughly 40 source files or 3,000 lines or fewer):** read every source file in full before analyzing anything. List the files once, then read them all with your file-reading tool, **issuing the reads in parallel in a single step** if your environment supports parallel tool calls. Prefer the dedicated file-reading tool over dumping the tree through one shell command (`cat` loops, `xargs`, `grep -n ''`). Such commands are often blocked by permission checks or truncated when the output is too large, and then you have to re-read everything anyway. Many patterns only show across several files (Bridge, Mediator, Template Method bypassed by a subclass, Strategy eroded by `instanceof` in the context), so reading everything is cheaper and more accurate than searching first. Then go straight to step 3.
- **Larger target:** survey and search first (step 2), then read the candidate files in parallel batches.

### 2. Gather candidates (large targets)
Run a cheap pass first, and remember that a hit is only a lead:
- Grep for the name signals and structural signals listed in the catalog's lookup table (for example `getInstance`, `accept(`, `Builder`, `subscribe`, `clone`, `handle(`/`setNext`).
- Look at the type structure: interfaces or abstract classes that have several implementations, classes that wrap an object of their own interface, polymorphic dispatch, and registries.
- Read the code itself. A lot of real pattern use has no pattern name attached.

### 3. Confirm each candidate
A pattern is present when its **participants and its intent** are both present. The name alone proves nothing. For each candidate:
- Map every participant from the catalog entry to concrete code (`file:line`).
- Check that the intent matches. Adapter, Decorator, Proxy, and Facade can look structurally identical; they differ in *why* the wrapper exists. Strategy and State differ in *who* triggers the switch.
- Assign a confidence level:
  - **Confirmed**: all participants are present and the intent matches.
  - **Partial**: the core is present but a participant is missing or collapsed (often fine, so say whether it matters).
  - **Idiomatic**: the pattern is expressed through a language feature, such as a function passed as a strategy, a module-level instance acting as a singleton, a generator acting as an iterator, or an event emitter acting as an observer. Count these as real uses. Do not mark them down for skipping class ceremony.
  - **Name-only**: the code is named after a pattern but does not implement it (for example a `UserFactory` that is just a constructor wrapper with no polymorphism). Report these, because the name misleads.

### 4. Evaluate fit
Every pattern costs indirection. Judge whether the force it addresses **actually exists in this code**, not whether it might exist someday. Use the "Fits when" and "Smells" lists in the catalog. In general:
- Look for evidence of real variation: two or more implementations in use, test doubles that depend on the seam, a plugin or extension point that third parties use, or an external constraint (for example an incompatible third-party API in the case of Adapter).
- Check the cost: added files, indirection hops a reader must follow, testability (Singleton is the usual problem), and hidden control flow (Observer chains, Chain of Responsibility ordering).
- Check direction of change: Visitor makes it cheap to add operations and expensive to add types. Look at the git history or the code to see which one actually changes.

Give one verdict per finding:
- **Appropriate**: the forces are present and the benefit is worth the indirection.
- **Over-engineered**: the pattern solves variation that does not exist, such as a single implementation, a factory that returns one type, or an Abstract Factory with one family. Name the simpler replacement.
- **Misapplied**: the pattern does not match the problem, or a different pattern would fit better (for example State implemented as Strategy, so callers have to manage the transitions). Name the better fit.
- **Degraded**: the pattern was right but has eroded (for example `instanceof` checks that bypass a Strategy, or a Visitor that is missing new node types).

Only flag **Missing pattern** opportunities when there is concrete pain in the code: a repeated `switch` on type across several sites, duplicated algorithm skeletons, or a constructor with many optional parameters. Do not suggest patterns speculatively.

### 5. Report
Use this structure:

```
## Design pattern analysis: <scope>

<1–2 sentences: overall shape, e.g. "Plugin system built on Strategy + Factory Method; one over-engineered Singleton.">

| Pattern | Where | Confidence | Verdict |
|---|---|---|---|
| Strategy | src/pricing/ (PricingRule + 4 impls) | Confirmed | Appropriate |
| Singleton | src/config.ts:12 | Confirmed | Over-engineered |

### <Pattern> — <location>
**Evidence:** participant → `file:line` mapping (short).
**Verdict:** <verdict>. <Why, citing the forces present or absent in this code.>
**Recommendation:** <keep / simplify to X / replace with Y / fix erosion>. Include a short sketch only when it clarifies.

### Not detected / considered and rejected
<Candidates you ruled out and why, especially name-only matches.>

### Opportunities (only with concrete evidence)

### Other issues noticed
<Correctness bugs you saw while reading, one line each with `file:line`.>
```

Keep the report proportional to what you found. A single file with one pattern needs only a short answer, not every section. Reference code as `path:line` so the user can jump to it. When asked about a specific pattern, answer that question directly first, then add any other notable findings.

## Ground rules
- Idiomatic forms are real pattern uses. Put each one in the summary table with confidence **Idiomatic** and give it a verdict. Never move one to "considered and rejected" just because it lacks a class hierarchy. Examples: a Python generator that hides pagination is an Iterator; a `@decorator` that wraps a function to add behavior is a Decorator; a function passed in as `key=` is a Strategy.
- The analysis is about patterns, but don't stay silent about bugs. If you notice a correctness bug while reading, such as an ignored error result, an inverted undo, or a race, list it under "Other issues noticed", even if it is unrelated to any pattern. Don't go hunting for bugs beyond what you read for the pattern analysis.
- Make every claim traceable to code you actually read. Never infer a pattern from file names or directory names alone.
- Framework-provided patterns are worth one line of mention but are not the user's design decision. Examples: Spring beans as Singletons, React context as a form of Observer, Express middleware as Chain of Responsibility. Evaluate how the user's code *uses* them, not the framework itself.
- Non-GoF patterns (Repository, Dependency Injection, MVC, Simple Factory, Null Object) may come up. Name them correctly as non-GoF instead of forcing them into a GoF category.
- Base a verdict on the code as it is. If the right verdict depends on future plans you can't see, state the assumption and ask.
