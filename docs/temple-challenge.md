# Editable temple challenge

The temple implementation is copied from the local `Minecraft Mods/vault_tings` source into the `xyz.iwolfking.woldsvaults` namespace. It includes the controller block and tile entity, challenge manager, spawner, proxy block and tile entity, both block entity renderers, overlay animation, and temple models/textures. The existing Wolds gate animation is integrated directly into the new manager.

## Use the copied implementation

The registered blocks are:

- `woldsvaults:temple_controller`
- `woldsvaults:temple_controller_proxy`

The block entity IDs append `_tile_entity` to those names. The saved challenge-manager type is `woldsvaults:temple`.

Use the Wolds controller in your room templates to run this implementation. Existing `the_vault:temple_controller` blocks continue to run the original manager and the existing temple mixins. This does not migrate active challenges or rewrite world/template files.

When updating a template, replace both halves of the controller and its block entity ID, preserving the existing `zone`, `positivePool`, `negativePool`, `proxyPositions`, `spawnPositions`, `gates`, and renderer data. Use a fresh controller UUID for independently placed copies. Proxies are created automatically by the manager. Controller zones, proxy positions, spawn positions, and gate offsets are relative to the controller; the controller position stored on a spawned proxy is absolute.

## Where to customize

Paths below are relative to `src/main/java/xyz/iwolfking/woldsvaults/`.

| Behavior | Source |
| --- | --- |
| Controller shape, properties, halves, rotation, ticking | `blocks/challenge/TempleControllerBlock.java` |
| Controller NBT and creation of the manager | `blocks/tiles/challenge/TempleControllerTileEntity.java` |
| Phases, infusion delay, action rolls, selection, combat, completion, HUD | `core/vault/challenge/temple/TempleChallengeManager.java` |
| Spawn locations, entity NBT, tracking and confinement | `core/vault/challenge/temple/TempleSpawner.java` |
| Proxy block properties and ticking | `blocks/challenge/TempleControllerProxyBlock.java` |
| Proxy action data, synchronization and cleanup | `blocks/tiles/challenge/TempleControllerProxyBlockEntity.java` |
| Controller cube/glyph effects | `blocks/renderers/challenge/TempleControllerRenderer.java` |
| Floating proxy text | `blocks/renderers/challenge/TempleControllerProxyRenderer.java` |
| Door templates, timing, sounds and placement | `core/vault/challenge/temple/TempleAnimation.java` |
| Optional activation overlay, copied as provided upstream (not wired upstream) | `core/vault/challenge/temple/TempleOverlayAnimation.java` |
| Mob action and both reward placement implementations | `core/vault/challenge/temple/action/` |
| Existing action-pool compatibility and action registration | `core/vault/challenge/temple/TempleActions.java` |

Temple models, blockstates and textures are under `src/main/resources/assets/woldsvaults/`, including `textures/gui/challenge/temple/hud.png`. The copied map icons are available there too; copying them does not register a new map integration.

## Action pools and gates

Existing positive/negative pools work: after flattening a pool, `TempleActions` converts the original add-mobs, tile-reward and floating-item-reward actions into the local implementations, preserving their configuration and populated values. Other shared challenge actions continue through Vault Hunters' action framework.

You can also use the local action type IDs explicitly:

- `woldsvaults:temple/add_mobs`
- `woldsvaults:temple/tile_reward`
- `woldsvaults:temple/floating_item_reward`

The local reward implementations retain the existing cap at the controller's Y coordinate. Tile rewards retain the temple's horizontal inset and vertical offsets. Floating item rewards retain their existing zone behavior with the same height cap.

`gates` keeps the existing list format: each entry contains an adapter-encoded `pos` and an integer `rotation` (0–3, Minecraft `Rotation` ordinal). The manager saves animation state and tick progress alongside the gates, and waits for the opening animation before completing/removing the challenge.

Gate structures still resolve through the Vault template registry as `the_vault:vault/animations/temple/templedoor0`, `templedoor1`, and `templedoor2`. Their existing structure/config files are outside the Java implementation; edit those in your pack or change `TempleAnimation.getTemplate` to select other registered templates.

The shared Vault challenge/controller/spawner base classes, event system, serialization adapters, curses, and world/template APIs remain dependency APIs. The temple-specific implementations and the three action implementations are local source copies.

## Validation

Run `gradlew.bat compileJava processResources --offline --console=plain` to compile the copies and process their assets. In-game validation should cover proxy selection, both reward types, mob completion, gate closing/opening, and saving/reloading during activation and combat.
