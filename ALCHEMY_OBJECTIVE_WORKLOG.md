# Alchemy Objective Worklog

Purpose: persistent handoff notes for the alchemy objective port. Append to this file on every user prompt so work can resume after an IDE or session crash.

## 2026-05-06 Prompt 1

User requested adding the Wold's Vaults alchemy objective to the standalone mod on the `alchemy` branch, using the official Wolds mod and pack as references:

- Standalone repo: `E:\Git Repo's\WoldsVaultsStandalone`
- Official Wolds mod: `E:\Git Repo's\Wolds-Vaults-Official-Mod`
- Wolds pack: `E:\Git Repo's\Wolds-Vaults-Pack`
- Required `the_vault` jar when referencing it: `C:\Users\Ethan\AppData\Roaming\PrismLauncher\instances\NewPack Test\minecraft\mods\the_vault-1.18.2-3.21.0.jar`

Requested components to port/reference:

- `AlchemyObjective`
- `AlchemyCrystalObjective`
- `AlchemyObjectiveConfig`
- `AlchemyIngredientItem`
- `CatalystItem`
- `DecoPotionItem`
- `AlchemyTasks`
- `BrewingAltarTileEntity`
- `BrewingAltar`
- `WoldClientEvents` tooltip event
- `TooltipEvent`
- Objective template registration from `ModObjectiveTemplates` and `MixinObjectiveTemplates`
- Item registrations for `AlchemyIngredientItem`, `CatalystItem`, `DecoPotionItem`, and Ingredient Template from `ModItems`
- Block registrations for `BrewingAltarBlock` and `BrewingAltarTileEntity` from `ModBlocks`
- Default config expects vault modifier pools `alchemy_strong_negative`, `alchemy_negative`, `alchemy_positive`, and `alchemy_strong_positive`
- Official Wolds loads modifier pool definitions from the mod via VHAPI; reference `ModVaultModifierPoolsProvider`
- Optional HUD mixin: `MixinVaultObjectivesModule`

Standing instruction: ask questions instead of assuming when details are not defined clearly in the official Wolds mod.

Initial observations:

- Standalone repo is on branch `alchemy`.
- Standalone worktree has a pre-existing untracked folder: `src/main/resources/assets/woldsvaults/beegbackpack/`.
- Official Wolds mod checkout is on `master`.

## 2026-05-06 Implementation Pass

Ported the alchemy objective into the standalone branch using the official Wolds mod as the source of truth, adapted to standalone patterns:

- Added alchemy objective classes, crystal objective, config, and task generation.
- Added alchemy ingredient, catalyst, and decorative potion items.
- Added brewing altar block, tile entity, renderer, particle packet, and brew event.
- Added client tooltip event bridge through a `ClientEvents` accessor.
- Added objective template registration through a standalone `ModObjectiveTemplates` plus objective-template mixin.
- Added alchemy custom objective registry entry and an alchemy crate block/mixin for `VaultCrateBlock.Type.ALCHEMY`.
- Added item/block/resource registrations, assets, recipe, tags, translations, and alchemy HUD texture.
- Added the four expected modifier pools to `src/main/resources/vhapi_configs/wold_modifier_pools.json`: `the_vault:alchemy_strong_negative`, `the_vault:alchemy_negative`, `the_vault:alchemy_positive`, and `the_vault:alchemy_strong_positive`.

Standalone adaptations:

- Did not port official `ModGameRules` or `GameruleHelper`; alchemy overstack allowance uses the objective's random allowance directly.
- Did not port official broad custom-crate map; added only `VAULT_CRATE_ALCHEMY` and a `VaultCrateBlock.getCrateBlock` mixin for `ALCHEMY`.
- Reused the standalone `PacketHandler` instead of the official `ModNetwork`.

Verification completed:

- `vhapi_configs/wold_modifier_pools.json` parses with PowerShell `ConvertFrom-Json`.
- `assets/woldsvaults/lang/en_us.json` parses with PowerShell `ConvertFrom-Json`.
- `.\gradlew.bat compileJava` succeeded.
- `.\gradlew.bat processResources` succeeded.
- `.\gradlew.bat build` succeeded.

## 2026-05-06 Prompt 2

User requested bumping the mod version, adding `alchemy` to the build artifact filename temporarily for this branch, and rebuilding.

Planned edits:

- Bump `mod_version` from `0.22.5` to `0.22.6`.
- Set Gradle `archivesName` to `${mod_id}-alchemy` so output jars are distinguishable while preserving the actual mod id.

Completed:

- Updated `gradle.properties` to `mod_version=0.22.6`.
- Updated `build.gradle` archive base name to `${mod_id}-alchemy`.
- Ran `.\gradlew.bat build` successfully.
- Built artifacts:
  - `build/libs/woldsvaultsstandalone-alchemy-0.22.6.jar`
  - `build/libs/woldsvaultsstandalone-alchemy-0.22.6-all.jar`

## 2026-05-06 Prompt 3

User began testing and found that the Seal of the Alchemist cannot be applied to a vault crystal in the Crystal Workbench. User asked whether this is the intended application method or if Wolds applies it differently.

Findings:

- Official Wolds registers `woldsvaults:crystal_seal_alchemy` as an `ItemVaultCrystalSeal`.
- Official generated data includes `woldsvaults:crystal_seal_alchemy` in `data/the_vault/tags/items/crystal_seals.json`.
- The Wolds pack config wires `woldsvaults:crystal_seal_alchemy` in `config/the_vault/vault_crystal.json`, with level-scaled alchemy crystal objectives at levels 0, 20, 50, and 80.
- Therefore the intended application method is the normal Crystal Workbench seal flow. The standalone branch was missing the alchemy seal tag/config data.

Completed:

- Added `"replace": false` and `woldsvaults:crystal_seal_alchemy` to `src/main/resources/data/the_vault/tags/items/crystal_seals.json`.
- Added `woldsvaults:crystal_seal_alchemy` entries to `src/main/resources/vhapi_configs/objective_seals.json`, copied from the Wolds pack values:
  - level 0: `objective_probability` 1.0, `required_progress` 1.0
  - level 20: `objective_probability` 0.8, `required_progress` 1.5
  - level 50: `objective_probability` 0.6, `required_progress` 2.0
  - level 80: `objective_probability` 0.4, `required_progress` 2.5
- Validated both edited JSON files with PowerShell `ConvertFrom-Json`.
- Ran `.\gradlew.bat build` successfully.

## 2026-05-06 Prompt 4

User asked where the several new alchemy `Catalyst` items are located: loot pools or craftables.

Findings:

- The five alchemy catalyst items are registered directly as standalone items:
  - `woldsvaults:catalyst_stability`
  - `woldsvaults:catalyst_amplifying`
  - `woldsvaults:catalyst_focusing`
  - `woldsvaults:catalyst_temporal`
  - `woldsvaults:catalyst_unstable`
- In both standalone and official Wolds code, they are grouped by `data/woldsvaults/tags/items/alchemy_catalyst.json`.
- No normal recipe JSON exists for these five catalyst items in the standalone port.
- No normal loot table directly references these five catalyst item ids in the standalone port.
- The source in `AlchemyObjective.handleChampionDeath` drops a random catalyst during alchemy vaults from champion deaths:
  - entity must be a champion
  - entity must not have `ChampionLogic.NO_DROPS`
  - kill source entity must be a `ServerPlayer`
  - random check is `entity.level.random.nextFloat() >= 0.5F`, effectively a 50% chance
  - dropped catalyst is created by `CatalystItem.createRandomCatalyst(vault, world.getRandom())`
- `CatalystItem.createRandomCatalyst` randomly chooses one of the five catalyst items and writes the current vault id into NBT as `VaultId`.
- `BrewingAltar` only accepts alchemy catalyst items whose `VaultId` matches the current vault, so catalysts are intended to be used inside the same alchemy vault where they drop.
