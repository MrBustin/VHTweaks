# Ballistic Bingo Official Diff Log

This file records the known differences between this standalone repo and the official Wolds Vaults mod for Ballistic Bingo. It is intentionally a log only; the objective code was not changed during the objective parity pass because this objective has had fragile local fixes.

## Compared Sources

- Standalone: `src/main/java/xyz/iwolfking/woldsvaults/objectives/BallisticBingoObjective.java`
- Standalone crystal: `src/main/java/xyz/iwolfking/woldsvaults/objectives/BallisticBingoCrystalObjective.java`
- Standalone scaling crystal: `src/main/java/xyz/iwolfking/woldsvaults/objectives/ScalingBallisticBingoCrystalObjective.java`
- Official repo: `E:\Git Repo's\Wolds-Vaults-Official-Mod`

## Official Changes Not Ported

- Official calls `ObjectiveHelper.handleAddingNormalizedToVault(vault, world)` at the start of `BallisticBingoObjective.initServer`.
- Official removed the initial non-PvP runner prefill / `JOINED` initialization block from `initServer`.
- Official increments `JOINED` on listener join without the standalone duplicate-join guard.
- Official marks blackout runners as `Completion.BAILED` when they leave before completion.
- Official ticks child objectives once `getBingos() > 0`; standalone uses `isGatewayReady()`.
- Official uses `VaultModifierUtils.getModifiersOfType(vault, ObjectiveShuffleModifier.class)` instead of the standalone local `getBingoShuffleModifier` helper.
- Official rewrote target scaling inline and removed `scaleTargetWithCondition`.
- Official removed `markTaskProgressDirty()`, which standalone currently uses to force task sync/dirty state.
- Official `BallisticBingoCrystalObjective` creates blackout bingo with `BallisticBingoObjective.of(task, 7, 7, blackout)`; standalone uses `BallisticBingoObjective.of(task, blackout)`.
- Official `ScalingBallisticBingoCrystalObjective` scales board size from `sealCount` with default `6x6`, exposes `getHeight()` / `getWidth()`, and shows the dimensions in tooltip/name. Standalone creates the default board without seal-count sizing.

## Risk Notes

- The standalone dirty-state handling may be part of why this objective currently works locally. Removing it should be tested carefully before any port.
- The official join-count changes can affect target scaling in multiplayer vaults.
- The official blackout leave behavior changes completion stats and should be tested with disconnect, bail, and death paths.
- The crystal board-size changes affect generated objective shape and may interact with existing configured bingo task pools.

## Suggested Test Matrix Before Porting

- Solo non-blackout bingo start, complete one bingo, exit.
- Solo blackout bingo start, leave before completion, confirm completion stat and crate behavior.
- Multiplayer non-PvP bingo with players joining late.
- Multiplayer player disconnect and reconnect during active bingo.
- Objective shuffle modifier with both regenerate and shuffle modes.
- Scaling seal counts from 1 through the highest expected count.
- Client HUD rendering and scroll handling after task regeneration.
