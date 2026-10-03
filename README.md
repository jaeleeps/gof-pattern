# gof-patterns

A [Claude skill](https://docs.claude.com/en/docs/claude-code/skills) that finds **Gang of Four design patterns** in a codebase and judges whether each one is a good fit.

- **Detect**: reports which files and directories implement which of the 23 GoF patterns, mapping each pattern participant to `file:line`. Every finding gets a confidence level: Confirmed, Partial, Idiomatic, or Name-only.
- **Evaluate**: gives each pattern a verdict of Appropriate, Over-engineered, Misapplied, or Degraded, based on whether the problem the pattern solves is actually present in the code.

## Install

```bash
git clone https://github.com/jaeleeps/gof-pattern.git ~/.claude/skills/gof-patterns
```

Then ask Claude things like *"What design patterns does `src/payments` use?"* or *"Is this Singleton appropriate?"*

## Layout

- `SKILL.md`: the workflow and report format.
- `references/patterns.md`: a catalog of all 23 patterns. Each entry covers intent, the participants to confirm, search signals, idiomatic forms, when the pattern fits, smells, and lookalike patterns.

## Sources

The pattern catalog is based on the GoF book, and was checked against these sources:
- Bloch & Garrod, [*23 Patterns in 80 Minutes*](https://www.cs.cmu.edu/~charlie/courses/15-214/2016-spring/slides/24%20-%20All%20the%20GoF%20Patterns.pdf), CMU 15-214 (2016)
- Christiansson (ed.) et al., [*GoF Design Patterns – with examples using Java and UML2*](https://edeleastar.github.io/design-patterns/topic00/pdf/c-logica-gof-catalogue.pdf), Logica (2008), CC BY-SA 3.0

See the Sources section of [references/patterns.md](references/patterns.md) for how these sources are used and where they disagree.
