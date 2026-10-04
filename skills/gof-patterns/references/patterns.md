# GoF Pattern Catalog: Detection and Fit

## Contents
- Quick lookup (signals table)
- Creational: Abstract Factory, Builder, Factory Method, Prototype, Singleton
- Structural: Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy
- Behavioral: Chain of Responsibility, Command, Interpreter, Iterator, Mediator, Memento, Observer, State, Strategy, Template Method, Visitor
- Commonly confused groups
- Sources

Every entry uses the same fields:
- **Intent**: what the pattern is for.
- **Canonical examples**: well-known real uses, mostly from the JDK. Tags like [CMU] or [Logica] point to the Sources section at the end.
- **Confirm**: the participants that must be present.
- **Signals**: grep and reading hints. These are leads, not proof.
- **Idiomatic**: language-native forms that count as the pattern.
- **Fits when**: the forces that justify it.
- **Smells**: signs of over-engineering or misuse.
- **Confused with**: lookalike patterns.

## Quick lookup

| Pattern | Name signals | Structural signals |
|---|---|---|
| Abstract Factory | `*Factory` interface with several `create*` methods | Parallel concrete factories, each producing a family of related products |
| Builder | `*Builder`, `.build()`, `with*()` | Step-wise setters that return `this`, then a terminal `build()` |
| Factory Method | `create*`, `make*`, `new*` (abstract/overridable) | Superclass calls an overridable creation hook; subclasses decide the concrete type |
| Prototype | `clone`, `copy`, `deepCopy`, `__copy__` | New objects come from copying a configured instance, often via a registry of prototypes |
| Singleton | `getInstance`, `instance`, `shared`, `default` | Private constructor + static holder; module-level instance; `@lru_cache` getter |
| Adapter | `*Adapter`, `*Wrapper`, `to*` | Class implements Target interface, delegates to an incompatible Adaptee |
| Bridge | `*Impl`, `*Implementor`, `*Driver`, `*Backend` | Abstraction hierarchy holds a reference to a separate implementation hierarchy |
| Composite | `children`, `add/remove`, `Node`, `Component` | Leaf and container share an interface; container iterates children for the same op |
| Decorator | `*Decorator`, `wrap`, `with*`, middleware | Wraps an object of the **same** interface, adds behavior, delegates |
| Facade | `*Facade`, `*Service`, `*Client`, `*Manager` | One class offering a simplified API over several subsystem objects |
| Flyweight | `*Pool`, `*Cache`, `intern`, `getOrCreate` | Shared immutable intrinsic state keyed in a factory; extrinsic state passed in |
| Proxy | `*Proxy`, `lazy`, `Remote*`, `Cached*`, `Guarded*` | Same interface as subject, controls **access** (lazy, remote, auth, cache) |
| Chain of Responsibility | `setNext`, `next`, `handle`, `middleware`, `pipeline` | Handlers linked; each handles or forwards |
| Command | `*Command`, `execute`, `undo`, `Action`, `Job` | Request objects with `execute()`; invoker queues/logs/undoes them |
| Interpreter | `interpret`, `evaluate`, `Expression`, `AST`, `Node` | Grammar classes, each evaluating itself recursively against a context |
| Iterator | `Iterator`, `next`, `hasNext`, `__iter__`, `[Symbol.iterator]`, `yield` | Traversal object separate from the collection |
| Mediator | `*Mediator`, `*Coordinator`, `*Hub`, `*Controller`, `EventBus` | Colleagues talk only to the mediator, not to each other |
| Memento | `*Memento`, `snapshot`, `save/restore`, `getState/setState` | Originator exports opaque state; caretaker stores it without inspecting it |
| Observer | `subscribe`, `addListener`, `on`, `emit`, `notify`, `*Listener` | Subject holds a list of observers and notifies them on change |
| State | `*State`, `transitionTo`, `setState`, `context.state` | Context delegates to a state object; states trigger transitions themselves |
| Strategy | `*Strategy`, `*Policy`, `*Rule`, `*Algorithm` | Context holds an interchangeable algorithm chosen by the client |
| Template Method | `abstract` hooks, `do*`, `on*`, `before*/after*` | Base class defines a fixed algorithm skeleton that calls overridable steps |
| Visitor | `*Visitor`, `accept(visitor)`, `visit*` | Double dispatch: element calls `visitor.visitX(this)` |

---

## Creational

### Abstract Factory
- **Intent:** Create families of related objects without naming their concrete classes.
- **Canonical examples:** Look-and-feel families in a GUI toolkit [CMU]. Insurance product families [Logica]. Rare in the JDK [CMU].
- **Confirm:** An AbstractFactory interface with several creation methods. Two or more ConcreteFactories, one per family. AbstractProduct interfaces. Clients that depend only on the abstractions.
- **Signals:** `UIFactory.createButton()/createCheckbox()`, `DbFactory.createConnection()/createCommand()`. One factory is selected at startup by config or platform.
- **Idiomatic:** A dict or module of constructors per family, for example `themes["dark"].Button`. DI container profiles.
- **Fits when:** Several product families really exist and their products must be used together (platform themes, DB vendors, cloud providers).
- **Smells:** Only one concrete factory exists, so the pattern is over-engineered. Factories that create unrelated products are just a grab bag. Adding a new product type forces edits to every factory; if that happens often, the family boundary is wrong.
- **Confused with:** Factory Method (a single product, chosen by subclassing). Builder (one complex product, assembled step by step).

### Builder
- **Intent:** Separate how a complex object is constructed from how it is represented, so the same construction process can produce different results.
- **Canonical examples:** `StringBuilder` (one representation) [CMU]. Effective Java's `NutritionFacts.Builder` [CMU]. A director that builds wood or brick houses [Logica]. Parsers in compilers [Logica].
- **Confirm:** A Builder with step methods and a terminal `build()`/`getResult()`. Optionally a Director that sequences the steps. The full GoF form has several ConcreteBuilders that produce different representations.
- **Signals:** Fluent `with*()`/`set*()` methods that return `this`, then `build()`. Validation in `build()`.
- **Idiomatic:** The fluent "Effective Java" builder, which has no Director and one representation. That is a common, legitimate variant, so classify it as **Partial/Idiomatic**. Kotlin `apply {}`, named or default arguments, and Python kwargs or dataclasses often remove the need for a builder.
- **Fits when:** There are many optional parameters, invariants must be checked before the object exists, an immutable object is assembled incrementally, or one construction process yields several representations (SQL, HTML, documents). Bloch's view: Builder emulates named parameters in languages that lack them, and turns an O(2ⁿ) explosion of constructors into O(n) combinable setters, at the cost of one intermediate object [CMU]. So in a language that *has* named or default parameters, most of the justification disappears.
- **Smells:** A builder for an object with 2–3 required fields, or in a language with named or default arguments: over-engineered. A builder that leaves the object half-built or mutable after `build()`. A builder that duplicates every field of the target with no validation.
- **Confused with:** Abstract Factory. Fluent interfaces in general, because method chaining alone is not Builder.

### Factory Method
- **Intent:** Define an interface for creating an object and let subclasses decide which class to instantiate.
- **Canonical examples:** `Iterable.iterator()`: each collection decides which `Iterator` to return [CMU]. `java.sql.Connection.createStatement()` returns a statement specific to the vendor's driver [Logica].
- **Confirm:** A Creator with an overridable creation method that the Creator's **own logic** calls. ConcreteCreators override it. Products share an interface.
- **Signals:** An `abstract createX()` in a base class whose other methods use the result. Framework hooks such as `createView()` or `newThread()`.
- **Idiomatic:** Passing a factory function or callback (`threadFactory`, `default_factory`) acts as Factory Method by composition.
- **Fits when:** A framework or base class must create objects whose concrete type subclasses or plugins supply.
- **Smells:** A static `create()` with a `switch` on a string is a **Simple Factory** or Static Factory, which is not GoF but "close enough" and very common [CMU]. Name it as such. That is often fine, but it is not Factory Method. Sources disagree here: Logica presents a parameterized `createProduct(type)` as "the Factory pattern". GoF itself lists *parameterized factory methods* as a Factory Method variant, but only when subclasses can override them. A non-overridable static switch stays a Simple Factory. A factory method that has only one override, or that returns `new X()` with no variation: over-engineered.
- **Confused with:** Simple Factory (not GoF), Abstract Factory, Template Method (Factory Method is a specialized Template Method step).

### Prototype
- **Intent:** Create new objects by copying a prototypical instance.
- **Canonical examples:** `clone()` on arrays [CMU]. Copying an account object, modifying the copy in a transaction, then swapping it in [Logica].
- **Confirm:** A `clone()` operation on the prototype interface. Clients create objects by cloning configured instances instead of calling constructors, often through a prototype registry.
- **Signals:** `clone()`, `copy.deepcopy`, `structuredClone`, spread-copy of templates, a `prototypes[key].clone()` registry.
- **Idiomatic:** JS prototypal inheritance (`Object.create`) is a language mechanism and is not automatically the pattern. Records or data classes with `copy(...)`/`with`-expressions.
- **Fits when:** Building an object is expensive, or its configuration is complex or comes from runtime data, and you need many variants. Also when classes are loaded dynamically.
- **Smells:** Shallow clones that share mutable state by accident. Objects with circular references, or classes that can't support copying [Logica]. Java's `Cloneable`/`clone()` in particular is a poor fit, so prefer copy constructors or static copy factories [CMU]. A `clone()` method that nothing calls. Cloning used where a plain constructor or factory would be clearer.
- **Confused with:** Memento (copies state for restore, not for creation). Flyweight (shares instances instead of copying them).

### Singleton
- **Intent:** Ensure a class has exactly one instance and provide a global access point to it.
- **Canonical examples:** `java.lang.Runtime` [CMU]. An enum singleton (`enum Elvis { ELVIS }`) [CMU].
- **Confirm:** Instantiation is restricted (private constructor, a guard, or a module scope) **and** there is a global accessor **that returns an instance**. A class with only static members whose private constructor is never called (for example `final class Ids { private static final AtomicLong NEXT; … }`) is a **static utility class**, not a Singleton, not even a "Partial" one, even when it holds shared static state. List it under "considered and rejected".
- **Signals:** `getInstance()`, `static instance`, `INSTANCE`, `shared`, `@Singleton`, module-level `instance = X()`, a memoized `get_x()`.
- **Idiomatic:** A module-level instance in Python, JS, or Go. Kotlin `object`. A Java enum singleton. A DI container singleton scope, which is the healthier variant because consumers receive it through injection rather than a global lookup.
- **Fits when:** The thing is truly unique in the process (a hardware handle, process-wide registry, or logger) **and** a global access point is acceptable. "Compelling uses are rare but they do exist" [CMU]. Some consider Singleton an anti-pattern because it is overused where a single instance isn't actually required [Logica]. Singleton is one member of a family of *instance-controlled* classes. The others are a static utility class (no instances), an enum (one instance per value, known at compile time), and an interned class (one canonical instance per value, created at runtime) [CMU]. Before calling something a Singleton, check whether it is really one of these.
- **Smells:** Used as a convenient global for mutable state, which hides dependencies and makes tests hard to isolate. Code that calls `getInstance()` deep inside business logic. Non-thread-safe lazy initialization in concurrent code. Several "singletons" that need resetting in tests: prefer DI with a single instance. This is the most frequently **over-engineered or misused** pattern, so evaluate it critically.
- **Confused with:** Monostate. Static utility classes, which have no instance at all. Flyweight factory.

---

## Structural

### Adapter
- **Intent:** Convert the interface of a class into the interface that clients expect.
- **Canonical examples:** `Arrays.asList(T[])` [CMU].
- **Confirm:** A Target interface the client uses, an Adaptee with an **incompatible** interface, and an Adapter that implements Target by translating calls to the Adaptee. The object adapter delegates; the class adapter inherits.
- **Signals:** `*Adapter`, a wrapper around a third-party SDK that implements an internal interface, and conversions such as `toDomain()`/`fromDto()` inside the wrapper.
- **Idiomatic:** A function that adapts a callback signature. Ports-and-adapters (hexagonal) architecture adapters.
- **Fits when:** You must use an existing or third-party class whose interface does not match, or you are isolating a vendor dependency behind your own interface.
- **Smells:** Name trap: Java AWT's `WindowAdapter`, `MouseAdapter`, `KeyAdapter`, and similar classes are convenience base classes with empty listener methods. They are *not* GoF Adapters, because they convert no interface, even though some sources list them as such [Logica]. Treat similar `*Adapter` classes as name-only unless they translate between two interfaces. An "adapter" whose interface matches the adaptee one-to-one and adds no translation. That is a pass-through, so it is over-engineered unless it exists only as a seam for test doubles, in which case say so. An adapter that accumulates business logic.
- **Confused with:** Decorator (same interface, adds behavior). Proxy (same interface, controls access). Facade (simplifies many objects instead of converting one). Bridge (designed in up front, not retrofitted).

### Bridge
- **Intent:** Decouple an abstraction from its implementation so that the two can vary independently.
- **Canonical examples:** JDBC and JCE. In Service Provider Interface (SPI) frameworks, the SPI is the Bridge's Implementor [CMU]. AWT peers [Logica].
- **Confirm:** An Abstraction hierarchy (often refined by subclasses) that holds a reference to an Implementor interface, which has its own ConcreteImplementor hierarchy. **Both** sides vary.
- **Signals:** `Shape` → `Renderer`, `RemoteControl` → `Device`, `Logger` → `Sink/Transport`, JDBC-style drivers.
- **Idiomatic:** Composition with an injected backend, when the front side also has variants.
- **Fits when:** Without it you would get M×N subclasses (for example shapes × renderers), and both dimensions change independently.
- **Smells:** Only one dimension varies. In that case it is plain composition or Strategy, and naming it Bridge overstates it. The Abstraction has a single class with no refinements. Every call pays for double indirection, from the abstraction to the implementor [Logica]. That cost is usually negligible but real on hot paths.
- **Confused with:** Adapter (applied after the fact to make things fit; Bridge is designed up front). Strategy (an algorithm swap, not two parallel hierarchies).

### Composite
- **Intent:** Compose objects into tree structures and treat individual objects and compositions uniformly.
- **Canonical examples:** `javax.swing.JComponent` [CMU]. Expression trees [CMU].
- **Confirm:** A Component interface. Leaf objects. A Composite that holds child Components and implements operations by delegating to its children, usually recursively.
- **Signals:** `children: Component[]`, `add/remove/getChild`, recursive `render()`/`size()`/`evaluate()` over a tree.
- **Idiomatic:** Recursive data types and algebraic data types (ADTs) with recursive functions. UI component trees, file-system nodes, AST nodes.
- **Fits when:** The domain is genuinely part–whole hierarchical and clients benefit from not distinguishing leaves from containers.
- **Smells:** Leaves forced to implement meaningless `add()` methods that throw, a "transparency" cost that is often acceptable but worth noting. Clients still checking `instanceof Composite`, which is degraded. Used for flat lists. The design becomes overly general: it is hard to restrict which component types a container may hold, so look for runtime type checks that try to enforce this [Logica].
- **Confused with:** Decorator (one wrapped child, adds behavior). Interpreter (which is built on top of Composite).

### Decorator
- **Intent:** Attach responsibilities to an object dynamically by wrapping it in an object with the **same interface**.
- **Canonical examples:** `java.io` streams, `Collections.synchronized*` / `unmodifiable*` wrappers, Swing borders [CMU].
- **Confirm:** A Component interface. A ConcreteComponent. A Decorator that implements Component, holds a Component, delegates to it, and adds behavior before or after. Decorators can be stacked.
- **Signals:** `new Buffered(new Compressed(new FileStream()))`, `wrap()`, middleware that wraps a handler and returns a handler, `with*` HOCs.
- **Idiomatic:** Higher-order functions and Python `@decorators` (Decorator for functions). React HOCs. Note that Java annotations or TS decorators used only as metadata are **not** this pattern.
- **Fits when:** Optional, combinable cross-cutting behaviors (logging, caching, retry, compression, auth) that would otherwise cause a subclass explosion.
- **Smells:** A wide component interface: every decorator must forward every method, so a complex interface makes each decorator error-prone [Logica]. Long chains add overhead to every call [Logica]. Deep stacks where the order matters but is not documented. A decorator that changes the interface (that is an Adapter). Decorators that need to unwrap or downcast to reach the inner object. Only one decorator ever applied, always: inline it.
- **Confused with:** Proxy (Proxy controls access and usually manages the subject's lifecycle; Decorator adds behavior and is composed by the client). Even authoritative sources overlap here: CMU lists `Collections.synchronized*` wrappers as an example of *both* Decorator and Proxy (a smart reference). When the code supports both readings, say so, and decide by the dominant intent. Adapter. Composite. Chain of Responsibility (decorators always delegate; handlers may stop the chain).

### Facade
- **Intent:** Provide a unified, simplified interface to a set of interfaces in a subsystem.
- **Canonical examples:** `java.util.concurrent.Executors` [CMU]. The compiler subsystem example from GoF [CMU].
- **Confirm:** One class or module that coordinates **several** subsystem objects behind a smaller API. The subsystem stays accessible for advanced use.
- **Signals:** `*Facade`, `*Service`, `*Client`, or `*Manager` exposing high-level methods that call several lower-level components. SDK top-level clients.
- **Idiomatic:** A package's `index` or `__init__` that re-exports a curated API, but only if it simplifies something.
- **Fits when:** A complex subsystem has common use cases that callers would otherwise reimplement, or you want to layer and decouple clients from subsystem churn.
- **Smells:** A "god facade" that grows unrelated methods. Variant parameters or near-duplicate methods added for callers that each need a small variation; the facade "ends up a mess" [Logica]. A facade that wraps a single class one-to-one: that is an Adapter or a needless pass-through. Clients that bypass the facade anyway: degraded.
- **Confused with:** Adapter (converts one interface). Mediator (Facade is one-directional; with Mediator, colleagues know the mediator and it coordinates them in both directions).

### Flyweight
- **Intent:** Use sharing to support large numbers of fine-grained objects efficiently.
- **Canonical examples:** All enums, `TimeUnit` (the number of units is extrinsic state) [CMU]. Glyphs in a word processor [Logica].
- **Confirm:** Intrinsic state (immutable, shared) is separated from extrinsic state (passed in by the client). A FlyweightFactory caches and returns shared instances by key.
- **Signals:** `getOrCreate(key)`, `intern`, glyph or tile or sprite caches, `Integer.valueOf` caching, object pools of immutable values.
- **Idiomatic:** String interning, enum instances, memoized immutable value objects.
- **Fits when:** There is a very large number of objects, most of their state can be made extrinsic, and memory is a measured problem.
- **Smells:** No evidence of memory pressure (premature optimization). Shared instances that are mutable, which is a correctness bug. Confused with a plain cache of expensive results: that is a cache, not Flyweight. An object pool of *mutable, reused* objects is also not Flyweight.
- **Confused with:** Singleton (one instance versus many shared keyed instances). Prototype. Object Pool (not GoF).

### Proxy
- **Intent:** Provide a surrogate for another object to control access to it.
- **Canonical examples:** RMI stubs (remote proxy), collection wrappers (smart reference) [CMU]. Lazily loaded images (virtual proxy) [CMU, Logica].
- **Confirm:** A Proxy implementing the same interface as the RealSubject, holding or creating the RealSubject and **controlling access**. The four GoF variants are: virtual proxy (lazy creation), remote proxy (a local stand-in for a remote object), protection proxy (denies some operations to some callers), and smart reference (locking, reference counting, or loading on first access) [CMU]. Caching proxies are a common modern addition.
- **Signals:** `Lazy*`, `Remote*`, `*Stub`, `Cached*`, `Guarded*`, the JS `Proxy` object, ORM lazy-loading entities, RPC stubs.
- **Idiomatic:** JS `Proxy`, Python `__getattr__` forwarding, generated RPC client stubs, Spring AOP proxies.
- **Fits when:** Access needs to be deferred, remote, guarded, or cached transparently to the caller.
- **Smells:** A proxy that hides expensive remote calls behind what look like cheap local calls, creating N+1 or latency surprises. Transparency here can harm the caller: note it. **Mixed access:** some code uses the proxy and other code reaches the real subject directly, so the two paths behave differently (for example, one skips the authorization check) [Logica]. Search for direct construction or injection of the RealSubject. A proxy that adds no control and only forwards.
- **Confused with:** Decorator (see above). Adapter (which has a different interface).

---

## Behavioral

### Chain of Responsibility
- **Intent:** Pass a request along a chain of handlers. Each one either handles the request or forwards it to the next.
- **Canonical examples:** `ClassLoader` delegation, `Properties` defaults. Exception propagation is a form of it [CMU]. A chain of error-logging handlers [Logica].
- **Confirm:** A Handler interface with `handle(request)` and a link to the next handler (or a list the dispatcher walks). Handlers decide independently whether to process, forward, or stop.
- **Signals:** `setNext`, `next()`, middleware pipelines, interceptor chains, a `handlers.find(h => h.canHandle(x))` loop.
- **Idiomatic:** Express/Koa middleware, servlet filters, event bubbling in the DOM. A list of predicate→handler pairs walked in order.
- **Fits when:** More than one handler might handle a request, the handler set or order is configured at runtime, and the sender should not know the receiver.
- **Smells:** Order dependence that is hidden or undocumented. Requests that silently fall off the end of the chain with no default. A chain where exactly one handler always matches by type: a map lookup would be simpler.
- **Confused with:** Decorator (always delegates). Command. Observer (all observers get the notification; in a chain, typically one handler handles the request).

### Command
- **Intent:** Encapsulate a request as an object, so you can parameterize, queue, log, or undo operations.
- **Canonical examples:** `Runnable`, the executor framework [CMU]. Menu, button, and popup actions that share one command; undo and redo [Logica].
- **Confirm:** A Command interface with `execute()` (optionally `undo()`). ConcreteCommands that bind a Receiver plus arguments. An Invoker that triggers them without knowing what they do. A Client that creates them.
- **Signals:** `*Command`, `execute`, `undo/redo` stacks, job and task queues, `Action` objects in editors, CQRS command handlers.
- **Idiomatic:** Closures or lambdas stored for later execution. Redux actions plus reducers (a Command-like data object). Serialized jobs (Sidekiq, Celery).
- **Fits when:** You need undo or redo, queuing or scheduling, macro recording, audit logging, or retry of operations, or you are decoupling UI triggers from operations.
- **Smells:** A Command class hierarchy whose `execute()` is called immediately and never stored or queued: that is just indirection, so use a method or function. A proliferation of small classes [Logica], which is a fair trade only when the reification buys undo, queuing, logging, or the ability to bind one action to many triggers. The boundary is fuzzy: does a reusable `Runnable` that takes an argument and returns a value still count? [CMU] Judge by whether the request is *treated as data*. Commands that hold large mutable state.
- **Confused with:** Strategy (a Strategy is *how* to do something and is swapped into a context; a Command is *what* to do and is stored or executed by an invoker).

### Interpreter
- **Intent:** Represent a grammar as classes and interpret sentences by evaluating a tree of those classes.
- **Canonical examples:** Regular expression matching, expression evaluators [CMU].
- **Confirm:** An AbstractExpression with `interpret(context)`. TerminalExpressions and NonterminalExpressions (the latter composing others, so it is a Composite). A Context.
- **Signals:** `Expression`, `interpret`/`evaluate`, `AST`, rule engines, filter or query DSLs, `And/Or/Not` spec classes.
- **Idiomatic:** ADTs plus a recursive `eval` function. The Specification pattern (a domain-level interpreter).
- **Fits when:** A small, stable grammar, where evaluation performance is not critical and grammar changes should be local.
- **Smells:** A large or growing grammar: use a parser generator plus Visitor-based passes instead. Poor performance on hot paths. Hand-rolled string parsing scattered across code that should be a small grammar: that is the missing-pattern signal.
- **Confused with:** Composite (Interpreter uses it). Visitor (an alternative way to add evaluation operations over an AST).

### Iterator
- **Intent:** Access the elements of an aggregate sequentially without exposing its representation.
- **Canonical examples:** `Iterable`/`Iterator`, the for-each statement [CMU, Logica].
- **Confirm:** A separate traversal object or protocol (`next`/`hasNext`, `__iter__`/`__next__`, `Symbol.iterator`) supplied by the collection.
- **Signals:** A custom `Iterator` class, generators (`yield`), `Iterable` implementations, cursor or paginator classes.
- **Idiomatic:** GoF also recognizes *internal* iteration, where the collection drives the loop and calls your function (`forEach`, `each`, `map`), as Iterator [CMU]. Almost always language-native. Generators and iterables count as **Idiomatic**. Do not report every `for` loop. Report only *custom* iterators the user wrote.
- **Fits when:** A custom collection or data source (tree traversal, paginated API, stream) needs uniform or lazy traversal.
- **Smells:** Not safe under concurrent modification unless designed for it; one remedy is to snapshot the iteration state with a Memento [Logica]. A custom iterator that duplicates a built-in. An iterator that exposes internal indices. Modification during iteration with no fail-fast behavior.
- **Confused with:** Visitor (operations over heterogeneous elements versus sequential access).

### Mediator
- **Intent:** Define an object that encapsulates how a set of objects interact, so they don't refer to each other directly.
- **Canonical examples:** A dialog box whose widgets affect each other [CMU, Logica]. Chat rooms and list servers [Logica].
- **Confirm:** Colleagues hold a reference to the Mediator only and call it. The Mediator knows the colleagues and coordinates them. Colleague-to-colleague coupling is absent.
- **Signals:** `*Mediator`, `*Coordinator`, dialog or form controllers that coordinate widgets, chat rooms, air-traffic-control style classes. MediatR-style request dispatch, which is mostly Command dispatch; note the difference.
- **Idiomatic:** An event bus can act as a mediator, but is often closer to Observer or pub-sub; decide based on whether there is central coordination logic.
- **Fits when:** Many-to-many interactions among a fixed set of components have become tangled. A mediator turns O(n²) pairwise couplings into O(n) [CMU].
- **Smells:** A god mediator that holds all the business logic: it trades interaction complexity for mediator complexity [Logica]. All traffic flows through one object, so it can become a performance bottleneck [Logica]. Colleagues that still reference each other directly (degraded). Used for only two objects.
- **Confused with:** Facade (one-directional). Observer (distributed and with no coordination logic). Event Aggregator or Pub-Sub (not GoF).

### Memento
- **Intent:** Capture and externalize an object's internal state without violating encapsulation, so the object can be restored to it later.
- **Canonical examples:** An undo stack for line-art edits [CMU]. Database transactions [Logica]. Serialization alone is *not* Memento [CMU].
- **Confirm:** An Originator that creates a Memento of its state and can restore from one. A Caretaker that stores mementos **without inspecting them**.
- **Signals:** `snapshot()/restore()`, `createMemento/setMemento`, undo history holding state blobs, `saveState/loadState`.
- **Idiomatic:** Immutable state snapshots (Redux history, persistent data structures). Serialization used for checkpoints.
- **Fits when:** Undo or rollback, checkpoints, and transactional "try then revert".
- **Smells:** The Caretaker reads or mutates the internals of a memento, breaking encapsulation. The Originator also changes *other* objects or resources that the memento doesn't capture, so a restore is only partial [Logica]. Full snapshots of huge state on every change: consider Command-based undo or diffs instead.
- **Confused with:** Command undo (inverse operations versus state snapshots). Prototype.

### Observer
- **Intent:** Define a one-to-many dependency so that when one object changes, all its dependents are notified.
- **Canonical examples:** Swing listeners throughout [CMU]. Publish/subscribe [Logica].
- **Confirm:** A Subject with subscribe and unsubscribe methods and a list of observers. Observers with an update or callback method. The Subject notifies them on state change without knowing their concrete types.
- **Signals:** `subscribe`, `addListener/removeListener`, `on/off/emit`, `notify`, `*Listener`, `EventEmitter`, signals, observables.
- **Idiomatic:** EventEmitter, RxJS, signals and stores, C# events, Kotlin Flow, and reactive frameworks. These count as Idiomatic. Evaluate how the user's code uses them. GoF is agnostic on many details, such as push versus pull updates, who triggers the notification, and per-aspect subscriptions [CMU]. Do not mark down an implementation for choosing one of these variants.
- **Fits when:** Changes in one object must propagate to a variable, unknown set of dependents, and loose coupling matters.
- **Smells:** Every subscriber is notified of every change even when most don't care, which wastes communication [Logica]. Memory leaks from subscriptions that are never removed. Cascading or cyclic updates. Notification order that is relied on but not guaranteed. Hard-to-trace control flow ("who changed this?"). A single, permanent observer: a direct call would be simpler.
- **Confused with:** Mediator, Chain of Responsibility, Pub-Sub (decoupled through a broker, not GoF).

### State
- **Intent:** Let an object alter its behavior when its internal state changes. The object appears to change its class.
- **Canonical examples:** A TCP connection [CMU]. Drawing-tool modes in an editor [Logica]. In Java: enum constants as states, held in an `AtomicReference<State>` [CMU].
- **Confirm:** A Context delegating behavior to a current State object. ConcreteStates implement the behavior for that state **and usually trigger the transitions themselves** (or the context does so based on state results).
- **Signals:** `*State` classes, `context.setState(new X())`, `transitionTo`, state-machine libraries (XState) or enum-plus-transition tables.
- **Idiomatic:** In Java, enum constants with constant-specific method bodies are per-state objects, so they count as State [CMU]. An enum plus a transition table, or a sealed class with `when`/`match` and no per-state behavior, is a state machine that may or may not be the State pattern. Use the State pattern label only when behavior is delegated to per-state objects; otherwise call it a "state machine (non-GoF form)".
- **Fits when:** The behavior differs substantially per state, there are many states and transitions, and the alternative is a large `switch (state)` repeated across several methods.
- **Smells:** Only 2–3 trivial states: an enum plus `if` is simpler. States that need deep access to the Context's internals. Transitions scattered between the Context and the States with no single source of truth.
- **Confused with:** Strategy (structurally identical; with Strategy the *client* chooses and the strategies don't know each other, whereas States know and trigger the transitions).

### Strategy
- **Intent:** Define a family of interchangeable algorithms and make them selectable independently of the clients that use them.
- **Canonical examples:** `Comparator` [CMU]. Line-breaking algorithms in text layout [CMU].
- **Confirm:** A Strategy interface. Two or more ConcreteStrategies. A Context that holds a strategy and delegates to it. The **client or config selects** the strategy.
- **Signals:** `*Strategy`, `*Policy`, `*Rule`, `*Comparator`, `*Algorithm`, a `strategies[key]` map, dependency-injected algorithm interfaces.
- **Idiomatic:** Passing a function or lambda (`sort(key=...)`, `Comparator`), or a dict of functions. Count these as real Strategy usage.
- **Fits when:** Several algorithm variants are actually used, selected at runtime or by configuration, or a seam is needed for testing.
- **Smells:** Clients must understand how the strategies differ in order to choose one [Logica]. If choosing is itself complex, centralize it in a factory or config instead of in every caller. A single implementation with no test double: over-engineered. Context code that checks `instanceof` on the strategy (degraded). Strategies that need to know about each other or switch the context's strategy, which means it is really State.
- **Confused with:** State, Command, Template Method (inheritance-based variation of steps versus composition-based swap of the whole algorithm), Bridge.

### Template Method
- **Intent:** Define the skeleton of an algorithm in a base class and let subclasses redefine certain steps without changing the structure.
- **Canonical examples:** Skeletal collection implementations (`AbstractList`, etc.) [CMU].
- **Confirm:** A base-class method (ideally non-overridable or `final`) that calls abstract or hook methods in a fixed order. Subclasses override only the steps.
- **Signals:** An abstract class with one public driver method plus `protected abstract` steps. `beforeX/afterX/onX` hooks. Framework lifecycle methods.
- **Idiomatic:** A higher-order function that takes step functions (which is closer to Strategy by composition). Framework lifecycles (`setUp/tearDown`, React class lifecycle).
- **Fits when:** Several classes share an invariant algorithm skeleton and differ only in specific steps.
- **Smells:** Deep inheritance chains. Subclasses that override the template method itself, which defeats the purpose. Many hooks that most subclasses leave empty. Only one subclass exists. Consider composition (Strategy) when the steps vary independently.
- **Confused with:** Strategy, which is its dual: you can mechanically convert one into the other, with inheritance swapped for composition [CMU]. Factory Method.

### Visitor
- **Intent:** Represent an operation to be performed on the elements of an object structure, so new operations can be added without changing the element classes.
- **Canonical examples:** Type checking and pretty-printing over a parse tree [CMU].
- **Confirm:** Elements with `accept(visitor)` that call `visitor.visitConcreteX(this)`. The essence is **double dispatch**: first dynamically on the visitor, then on the visited element. Walking a structure and applying a function is *not* Visitor; that is Iterator [CMU]. A Visitor interface with one `visit` method per element type. Two or more ConcreteVisitors (operations).
- **Signals:** `accept(`, `visit*(`, `*Visitor`, AST or compiler passes, document exporters.
- **Idiomatic:** Pattern matching over sealed types or ADTs (`match`, `when`, `switch` with exhaustiveness). This is the modern replacement; count it as an Idiomatic Visitor if it is used as "operations over a closed hierarchy".
- **Fits when:** The element hierarchy is **stable** and new operations are added often (compilers, linters, serializers).
- **Smells:** The element hierarchy changes often: every new type forces edits to every visitor. Look at the git history. A single visitor: just use a method. Visitors that need private element state, which breaks encapsulation. A language with exhaustive pattern matching, where Visitor ceremony may be unnecessary.
- **Confused with:** Iterator, Composite (Visitor often walks a Composite), Interpreter.

---

## Commonly confused groups

**Wrappers: Adapter, Decorator, Proxy, Facade, Bridge.** Ask these questions:
- Does it have the same interface as the wrapped object? If no and it converts one interface, it is an **Adapter**. If no and it simplifies several objects, it is a **Facade**.
- If the interface is the same, why does the wrapper exist? To add behavior that can be composed: **Decorator**. To control access, lifetime, location, or permission: **Proxy**.
- Was it designed up front so that two hierarchies can vary independently? **Bridge**.

**Polymorphic swap: Strategy, State, Command, Template Method.**
- The client picks the algorithm and keeps it: **Strategy**.
- The object switches its own behavior as its state changes: **State**.
- A request is reified so it can be stored, queued, or undone: **Command**.
- Steps vary through subclass overrides inside a fixed skeleton: **Template Method**.

**Creation: Factory Method, Abstract Factory, Builder, Prototype, Simple Factory.**
- Subclasses decide one product type: **Factory Method**.
- A family of products from one swappable factory: **Abstract Factory**.
- One complex object assembled step by step: **Builder**.
- Copy a configured instance: **Prototype**.
- A static method with a `switch`: **Simple Factory**, which is not GoF.

**Communication: Observer, Mediator, Chain of Responsibility.**
- One-to-many broadcast, with the subject unaware of who listens: **Observer**.
- A central coordinator for many-to-many interactions: **Mediator**.
- A request passed along until something handles it: **Chain of Responsibility**.

---

## Sources

This catalog is synthesized from the following references. It paraphrases them and does not reproduce their text. The bracketed tags in entries point to them.

- **[GoF]** Gamma, Helm, Johnson, Vlissides. *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley, 1994. This is the primary definition of the 23 patterns.
- **[CMU]** Josh Bloch and Charlie Garrod. "23 Patterns in 80 Minutes: a Whirlwind Java-centric Tour of the Gang-of-Four Design Patterns". CMU 15-214, Spring 2016. https://www.cs.cmu.edu/~charlie/courses/15-214/2016-spring/slides/24%20-%20All%20the%20GoF%20Patterns.pdf. It gives intent, use case, key types, and JDK examples for all 23 patterns, plus Bloch's views on Builder and Singleton.
- **[Logica]** Benneth Christiansson (ed.) et al. *GoF Design Patterns – with examples using Java and UML2*. Logica Java Architects Training Crew, 2008. CC BY-SA 3.0. https://edeleastar.github.io/design-patterns/topic00/pdf/c-logica-gof-catalogue.pdf. It gives where to use, benefits, drawbacks, and usage examples for 20 patterns; it does not cover Interpreter, Template Method, or Visitor.

Where the sources disagree, the catalog follows GoF and records the disagreement inline. Examples are Logica's "Factory pattern" (a parameterized or simple factory) and AWT's `*Adapter` listener classes.

As CMU notes, "definitions can be vague, coverage is incomplete". That is why this skill reports a confidence level and relies on intent rather than on names.
