# Codex Handoff: WoldsVaults Namespace Normalization

Repo:
`E:\Git Repo's\WoldsVaultsStandalone`

## What you are taking over

This repo has a split identity problem:

- Forge/mod loader identity is mostly `woldsvaultsstandalone`
- Actual content/resource namespace is mostly `woldsvaults`

The user wants to move **away from `woldsvaultsstandalone`** and standardize on **`woldsvaults`** instead.

Important constraint:

- The **jar filename can still contain `Standalone`**
- The user only wants the **mod/content identity** normalized to `woldsvaults`

That distinction matters. Do not assume jar filename must match mod id.

## Recommendation

The safest direction is:

1. Change loader-facing metadata from `woldsvaultsstandalone` to `woldsvaults`
2. Keep the actual content namespace as `woldsvaults`
3. Do **not** migrate content IDs to `woldsvaultsstandalone`

Reason:

- Most content is already registered under `woldsvaults:*`
- Resources already live under `assets/woldsvaults` and `data/woldsvaults`
- Existing saves/configs are much more likely to remain compatible
- This should remove Forge warnings about `woldsvaults` being an alternative prefix when `woldsvaultsstandalone` is expected

## Core findings already established

### 1. Main split point

File:
`src/main/java/xyz/iwolfking/woldsvaults/WoldsVaults.java`

Relevant lines:

- `@Mod("woldsvaultsstandalone")`
- `public static final String MODID = "woldsvaults";`

Meaning:

- Forge thinks the mod id is `woldsvaultsstandalone`
- `WoldsVaults.id(...)` still generates `woldsvaults:*`

### 2. Build metadata also says standalone

File:
`gradle.properties`

Relevant line:

- `mod_id=woldsvaultsstandalone`

### 3. Actual registrations are mostly old namespace already

Examples:

- `src/main/java/xyz/iwolfking/woldsvaults/init/ModItems.java`
- `src/main/java/xyz/iwolfking/woldsvaults/init/ModBlocks.java`
- `src/main/java/xyz/iwolfking/woldsvaults/init/ModEffects.java`
- `src/main/java/xyz/iwolfking/woldsvaults/init/ModEntities.java`

These use `WoldsVaults.id(...)` or `WoldsVaults.MODID`, which currently resolve to `woldsvaults`.

### 4. Resources are also old namespace

Resource directories found:

- `src/main/resources/assets/woldsvaults`
- `src/main/resources/data/woldsvaults`

Approx counts from prior scan:

- `assets/woldsvaults`: about 790 files
- `data/woldsvaults`: about 101 files

### 5. Existing warning source

The mismatch explains startup warnings of the form:

- alternative prefix `woldsvaults` ... expected `woldsvaultsstandalone`

### 6. At least one stray old/new mismatch in the opposite direction

File:
`src/main/java/xyz/iwolfking/woldsvaults/abilities/ColossusAbility.java`

Contains:

- `@Mod.EventBusSubscriber(modid = "woldsvaults", ...)`

If the mod id is switched back to `woldsvaults`, this becomes consistent again.

## Important conclusion

There do **not** appear to be actual game objects registered under `woldsvaultsstandalone`.

The split is mostly:

- loader-facing metadata: `woldsvaultsstandalone`
- game content IDs: `woldsvaults`

That is why reverting the loader-facing side to `woldsvaults` is the low-risk path.

## Git history already checked

The split was introduced on **2025-01-23**.

Blame results:

- `@Mod("woldsvaultsstandalone")` in `WoldsVaults.java` came from commit `39d8427b` on `2025-01-23 10:30:58 -0600`
- `mod_id=woldsvaultsstandalone` in `gradle.properties` also came from commit `39d8427b`
- `public static final String MODID = "woldsvaults";` predates that and came from older history (`bc271a3`, `2024-10-29`)

Meaning:

- the standalone loader/build identity was added later
- the content namespace was not migrated
- later work kept building on the old content namespace

## Prior server log context

Separate from this namespace issue, there was a log investigation involving:

- `UnhingedScavengerObjective`
- invalid enum ordinals during deserialization
- Vault reporting corrupted vault snapshots

Prior conclusion:

- the immediate error looked like stale/corrupted persisted vault data
- the namespace split may contribute to compatibility confusion, but did not look like the sole direct runtime cause

Do not conflate the two issues without more evidence.

## What to change

Target the loader-facing `woldsvaultsstandalone` references and convert them back to `woldsvaults`.

Likely classes/files to inspect:

- `src/main/java/xyz/iwolfking/woldsvaults/WoldsVaults.java`
- `gradle.properties`
- every `@Mod.EventBusSubscriber(modid = "woldsvaultsstandalone")`
- any other loader-facing annotations or hardcoded standalone mod ids
- `mods.toml` flow via `${mod_id}`
- build files referencing the mod id where needed

## What not to change unless there is a deliberate migration plan

Do **not** casually rename:

- `assets/woldsvaults`
- `data/woldsvaults`
- content IDs from `woldsvaults:*` to `woldsvaultsstandalone:*`
- saved-data-facing registry names

That would be the high-risk path for compatibility.

## Suggested execution plan

1. Audit all `woldsvaultsstandalone` references in Java/build/resource metadata.
2. Classify each one as:
   - loader-facing and should become `woldsvaults`
   - unrelated and should remain unchanged
3. Patch only the loader-facing ones.
4. Build and verify.
5. Check for any external assumptions in the repo that still expect `woldsvaultsstandalone`.

## Useful search commands

Use these from repo root:

```powershell
rg -n "woldsvaultsstandalone|woldsvaults" src/main/java src/main/resources gradle.properties build.gradle settings.gradle -S
rg -n "@Mod\(|modid = " src/main/java -S
rg -n "WoldsVaults\.MODID|WoldsVaults\.id\(" src/main/java -S
```

## Success criteria

Desired end state:

- Forge/mod id uses `woldsvaults`
- content namespace remains `woldsvaults`
- no more warning about alternative prefix `woldsvaults` when `woldsvaultsstandalone` is expected
- jar filename may still include `Standalone`

## Practical note

The user explicitly said:

- they ideally want everything switched away from the standalone name
- they want to use `woldsvaults`
- the jar name can still include standalone

So optimize for compatibility and consistency, not for matching jar filename to mod id.
