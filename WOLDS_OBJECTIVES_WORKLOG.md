# Wolds Objectives Worklog

Purpose: persistent handoff notes for non-alchemy Wolds objective work. Append to this file on every related prompt so work can resume after an IDE or session crash.

## 2026-05-06 Prompt 1

User said alchemy looks good for now and requested a new log for other Wolds objectives. First target is Doomsayer.

Request:

- Compare Doomsayer piece by piece against the official Wolds main mod.
- Fix anything broken in the standalone mod.
- Bring over any Doomsayer-related changes that were made to the main mod.
- Note that `woldsvaults:crystal_seal_doomsayer` was deliberately removed from `crystal_seals.json` before because the objective was not functional in the standalone mod; it should not just be exposed unless the objective actually works.

Reference paths:

- Standalone repo: `E:\Git Repo's\WoldsVaultsStandalone`
- Official Wolds mod: `E:\Git Repo's\Wolds-Vaults-Official-Mod`
- Wolds pack: `E:\Git Repo's\Wolds-Vaults-Pack`
- Required `the_vault` jar if needed: `C:\Users\Ethan\AppData\Roaming\PrismLauncher\instances\NewPack Test\minecraft\mods\the_vault-1.18.2-3.21.0.jar`

Initial state:

- Current standalone branch: `alchemy`.
- Existing alchemy persistence log remains `ALCHEMY_OBJECTIVE_WORKLOG.md`; this new file is for other objectives.

## 2026-05-06 Doomsayer Pass

Compared standalone Doomsayer/Ballistic Bingo against official Wolds main mod and Wolds pack.

Findings:

- Standalone had `woldsvaults:crystal_seal_doomsayer` back in `data/the_vault/tags/items/crystal_seals.json`, but the seal config still pointed at plain `ballistic_bingo`.
- Official Wolds now uses `scaling_ballistic_bingo` for the Doomsayer seal, with `objective_probability: 0.25` and `sealCount: 0`.
- Official Wolds supports repeat-applying the Doomsayer seal by incrementing `sealCount`, scaling the board from 6x6 upward.
- Official Wolds uses a distinct `BALLISTIC_BINGO` crate type and `vault_crate_ballistic_bingo` block/resources.
- Official Wolds added blackout support to `BallisticBingoCrystalObjective` and `BallisticBingoObjective`.
- Standalone Ballistic Bingo data had a namespace mismatch: the bundled `default_configs/ballistic_bingo.json` adds `the_vault:*` bingo modifiers, while several VHAPI modifier definitions/pools were under `woldsvaults:*`.

Completed:

- Added `ScalingBallisticBingoCrystalObjective` adapted for standalone without the external `scalingbingoseals` dependency.
- Added `ModCrystalObjectives` and call `ModCrystalObjectives.init()` from `WoldsVaults` to register `scaling_ballistic_bingo` with `CrystalData.OBJECTIVE`.
- Added `MixinVaultCrystalConfig` to increment `sealCount` when applying another scaling Ballistic Bingo seal to a crystal that already has that objective.
- Updated `BallisticBingoCrystalObjective` with official blackout serialization/text support and switched its crate type from `BINGO` to `BALLISTIC_BINGO`.
- Updated `BallisticBingoObjective` with the official `BLACKOUT` field and overload used by blackout challenge crystals.
- Added/register `ModBlocks.VAULT_CRATE_BALLISTIC_BINGO`, block item, crate blockstate/model/item model/loot table, lang entry, and texture copied from official Wolds.
- Updated `MixinVaultCrateBlock` to return the custom Ballistic Bingo crate for `VaultCrateBlock.Type.BALLISTIC_BINGO`.
- Updated `ModCustomVaultObjectiveEntries.BALLISTIC_BINGO` to expose the custom Ballistic Bingo crate item.
- Updated `objective_seals.json` so `woldsvaults:crystal_seal_doomsayer` uses `type: scaling_ballistic_bingo`, `objective_probability: 0.25`, and `sealCount: 0`.
- Replaced the old standalone Doomsayer recipe path `data/woldsvaults/recipes/seals/seal_of_the_doomsayer.json` with `data/woldsvaults/recipes/crystal_seal_doomsayer.json`, matching official naming while keeping standalone's existing `woldsvaults:compressed_vault_essence` ingredient.
- Aligned Ballistic Bingo VHAPI modifier ids and modifier-pool references from `woldsvaults:*` to official `the_vault:*` for the bingo modifier family so the bundled Ballistic Bingo config resolves the modifiers it adds.

Verification:

- Validated edited JSON files with PowerShell `ConvertFrom-Json`.
- `.\gradlew.bat compileJava` succeeded.
- `.\gradlew.bat build` succeeded.

Notes:

- The repo still contains earlier alchemy work and untracked `src/main/resources/assets/woldsvaults/beegbackpack/`; those were not reverted or cleaned.
- Runtime testing should specifically verify Crystal Workbench application, repeat Doomsayer seal scaling, vault entry, bingo completion, and Ballistic Bingo crate award/rendering.

## 2026-05-06 Prompt 2

User reported that bingo objectives do not update correctly while inside the Doomsayer/Ballistic Bingo vault.

Initial focus:

- Compare standalone and official Wolds task update hooks.
- Check whether the Ballistic Bingo objective uses its copied `BingoTask` correctly after board sizing/scaling.
- Check whether mixins that patch vanilla `BingoTask` or `BingoObjective` are missing from standalone.
- Verify whether config ids or task filters differ from official Wolds pack data.

Completed:

- Replaced standalone `src/main/resources/default_configs/ballistic_bingo.json` with the current Wolds pack `config/the_vault/ballistic_bingo.json`.
  - Standalone's bundled config was stale and started with an old 5x5/default task set.
  - Current Wolds config includes current board sizes and task definitions across level ranges.
- Registered existing standalone `MixinBingoTask` in `woldsvaultsstandalone.mixins.json`.
  - The class existed but was not listed, so its `BingoTask.onComplete` hook did not run.
  - This hook adds the Ballistic Bingo task-completion modifier rolls.
- Added standalone `the_vault.fixes.MixinBingoObjective`, ported from official Wolds.
  - Overwrites vanilla `BingoObjective` listener-specific `getContext` to use a UUID-specific `EntityTaskSource`.
  - Registered it in `woldsvaultsstandalone.mixins.json`.
- Updated `BallisticBingoObjective` to include the official `BLACKOUT` field and `of(BingoTask, int, int, boolean)` factory overload used by `BallisticBingoCrystalObjective`.

Verification:

- Validated the updated `default_configs/ballistic_bingo.json` with PowerShell `ConvertFrom-Json`.
- `.\gradlew.bat compileJava` succeeded.
- `.\gradlew.bat build` succeeded.

Next runtime checks:

- Enter a fresh Doomsayer vault generated after this build.
- Verify visible board task counters update for normal chest/mob/block tasks.
- Complete a line and verify BINGO completion effects/modifier messages happen.
- If counters still fail, capture one specific task name that does not update and the action used to progress it; likely next target would be task filter/event compatibility for that specific task type.

## 2026-05-06 Prompt 3

User clarified that the bingo tasks were tracking and completing correctly; the issue is that the task board does not graphically update.

Revised focus:

- Server-side task event tracking is likely working.
- Investigate client-side objective sync, task NBT field sync, board render, HUD/module integration, and whether the custom `BallisticBingoObjective` fields are compatible with vanilla `BingoObjective` client rendering.

Findings:

- Official Wolds includes a `VaultObjectivesModule` mixin that standalone was missing.
- That mixin explicitly maps `BallisticBingoObjective` to the vanilla HUD objective key `"bingo"`.
- It also overrides the objective HUD module size for Ballistic Bingo using the active `BingoTask` board width/height, covering both compact and expanded board views.
- Since tasks were tracking and completing, the likely failure path was the client HUD module not treating the custom objective as the vanilla bingo renderer target.

Completed:

- Added standalone `MixinVaultObjectivesModule`.
- Registered the mixin in `woldsvaultsstandalone.mixins.json`.
- Ported the relevant official Wolds behavior for standalone objectives:
  - `BallisticBingoObjective` now resolves to objective type key `"bingo"` for the HUD renderer.
  - Ballistic Bingo HUD size now derives from the rendered task board width/height.
  - Existing standalone custom objective keys for alchemy, zealot, scavenger, monolith, elixir, and brutal bosses are mapped for HUD compatibility.
  - Added HUD settings pages for alchemy, zealot, and brutal bosses, matching the subset of official Wolds objectives that exist in standalone.

Verification:

- `.\gradlew.bat compileJava` succeeded.
- `.\gradlew.bat build` succeeded.

Next runtime check:

- Test with the rebuilt jar in a fresh Doomsayer vault and verify the visible bingo task board updates immediately as tasks progress.

## 2026-05-06 Prompt 4

User confirmed the `VaultObjectivesModule` HUD mixin did not solve the graphical update issue and asked to check directly against the specified `the_vault` jar to verify the bingo board render/sync path.

Focus:

- Inspect `C:\Users\Ethan\AppData\Roaming\PrismLauncher\instances\NewPack Test\minecraft\mods\the_vault-1.18.2-3.21.0.jar`.
- Confirm which classes/methods render the bingo board and which objective/task fields they read on the client.
- Compare that path against standalone `BallisticBingoObjective`, its field keys, and the current mixins.

Findings:

- The Vault Hunters jar confirms `VaultObjectivesModule.renderVault` only positions/delegates rendering through `Listener.renderObjectives`.
- The actual board draw path is `BingoObjective.render(...) -> Task.onRender(...) -> BingoRenderer$Root`.
- `BingoRenderer$Root` reads `BingoTask.isCompleted(...)`, `getState(...)`, `getChild(...)`, and task progress values from the client-side synced `BingoTask`.
- Vanilla `BingoObjective.tickServer(...)` ends by calling private `markTaskProgressDirty()`.
- That vanilla method marks `TASK` dirty for normal bingo and re-puts each entry into `TASKS` for PvP bingo, forcing nested bingo board state to sync to clients.
- Standalone `BallisticBingoObjective` had the copied Wolds objective logic but did not include the current Vault Hunters dirty-marking path from the jar.

Completed:

- Added `BallisticBingoObjective.markTaskProgressDirty()` matching the jar behavior:
  - non-PvP: `markDirty(TASK)`
  - PvP: re-put each existing `TASKS` entry to dirty/sync the nested task map
- Called `markTaskProgressDirty()` at the end of `BallisticBingoObjective.tickServer(...)`.
- Marked `TASK_SOURCE` dirty when runner UUIDs are added/removed from the objective source.
- Updated `MixinVaultObjectivesModule` to read `BallisticBingoObjective.TASK` / `TASKS` directly for board sizing instead of the vanilla static field keys.

Verification:

- `.\gradlew.bat compileJava` succeeded.
- `.\gradlew.bat build` succeeded.

Next runtime check:

- Test a fresh Doomsayer vault and verify that task progress numbers, completed-cell highlights, and checkmarks update live without leaving/re-entering the vault.

## 2026-05-23 Prompt 1

User requested execution of the Codex handoff for WoldsVaults namespace normalization.

Findings:

- No standalone handoff markdown matching that exact title was present in this repo, and the earlier sibling repo paths referenced by older logs were not available on this machine, so the normalization scope was derived from the current standalone sources.
- A subset of Wolds-added vault modifiers was still defined under `woldsvaults:*` in `src/main/resources/vhapi_configs/wold_modifiers.json`, while other standalone configs and Java already referenced the same logical modifiers under `the_vault:*`.
- The mixed namespace set included `sparkling`, `wooden`, `spooky`, `poor`, `critical_mobs`, `chunky_mobs4`, `baby_mobs2`, `brutal_mobs`, `stunning`, `haunting`, `corrosive`, `jumpy_deluxe`, `super_stronk`, `rigged`, `rending`, `piercing`, `orematic`, `blessed`, `sweet_retro`, `haunted_mansion`, `phantasmal_mobs`, and `fleet_footed_mobs`.
- This mismatch had already leaked into runtime-facing paths:
  - `HauntedBraziersObjective` applies `VaultMod.id("haunting")`.
  - `MixinWildSpawnerTileEntity` checks for `the_vault:spooky`.
  - `UnusualModifierLib` already targets `the_vault:*` ids for several unusual affixes.

Completed:

- Renamed the affected modifier definition ids in `src/main/resources/vhapi_configs/wold_modifiers.json` from `woldsvaults:*` to `the_vault:*`.
- Updated grouped modifier child references in `wold_modifiers.json` to the normalized ids.
- Updated all remaining stale pool entries for that modifier subset in `src/main/resources/vhapi_configs/wold_modifier_pools.json`.
- Left item/block/resource namespaces unchanged; this pass only normalized the mixed vault modifier ids.

Verification:

- Confirmed there are no remaining `woldsvaults:*` references for that normalized modifier subset in `src/main/resources/vhapi_configs` or `src/main/java`.
- Build/test still pending after the normalization patch at the time of this note.

## 2026-05-23 Prompt 2

User added the actual handoff file `CODEX_HANDOFF_WOLDSVAULTS_NAMESPACE.md` and asked for that specific namespace-normalization pass to be executed.

Findings:

- The handoff scope is loader/build identity normalization, not a content namespace migration.
- The repo was split between loader-facing `woldsvaultsstandalone` and content-facing `woldsvaults`.
- Core mismatches confirmed by the handoff were present locally:
  - `@Mod("woldsvaultsstandalone")` while `WoldsVaults.MODID` remained `woldsvaults`
  - `gradle.properties` still had `mod_id=woldsvaultsstandalone`
  - multiple `@Mod.EventBusSubscriber` annotations still targeted `woldsvaultsstandalone`
  - `AddonLoader` still queried `ModList.get().getModFileById("woldsvaultsstandalone")`
  - objective lang keys still only existed under `objective.woldsvaultsstandalone.*` / `vault_objective.woldsvaultsstandalone.*`

Completed:

- Switched loader-facing mod identity from `woldsvaultsstandalone` to `woldsvaults` in Java annotations and `gradle.properties`.
- Updated `AddonLoader` to resolve the mod file by `WoldsVaults.MODID`.
- Renamed the mixin config resource from `woldsvaultsstandalone.mixins.json` to `woldsvaults.mixins.json` and aligned the configured refmap name to `woldsvaults.refmap.json`.
- Added `objective.woldsvaults.*` and `vault_objective.woldsvaults.*` lang aliases for the custom objectives while keeping the old `woldsvaultsstandalone` keys for compatibility.
- Intentionally kept the jar basename on `woldsvaultsstandalone` in `build.gradle` so artifact filenames do not change as a side effect of the loader-id normalization.

Verification:

- Pending build/test after this namespace-normalization patch at the time of writing this note.
