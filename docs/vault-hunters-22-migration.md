# Vault Hunters 22 migration

Target: `libs/the_vault-1.18.2-22.0.0-remastered.7152.jar`, resolved through the existing Gradle `flatDir` repository.

## Integration changes

- Tooler prestige now wraps the server's Tool Station jewel application plan. Client previews do not roll prestige RNG. The apply button permits Tooler attempts when capacity alone prevents application; normal restrictions on sealed tools and duplicate jewels still apply. Capacity-granting geodes retain their benefit.
- Fire Nova implements the new Nova burst API, preserving suppression, etching damage and extra casts. Extra casts do not invalidate an existing damage-over-time effect. Fire damage numbers use the surviving shared damage-type hook.
- Fireball's repeat-cast modifier uses the server scheduler and the new projectile helpers, preserving Trinity and PvP rules. Glacial Blast modifies the new direct-hit prison chance without replacing projectile damage, targeting or etching behavior.
- Chain Thorns supports Quillstorm's extra targets and damage falloff. The old duplicate call to normal thorns was removed because the new jar already makes that call.
- Smite sound hooks follow the new strike and extra-bolt methods. Booster packs pass player context through the new roll method. Shocking Hit and legacy effect-cloud hooks target the updated methods; the new gear-cloud system also receives the custom active cloud chance bonus.
- Greed reset uses the new refund API, including effect refresh and synchronization. Crystal Trader retains its trades and pools; unused fields referring to deleted Greed quest/challenge types were removed.
- Powers use the new skill-panel constructor. Bonus talent ranks are added to the new rank display, preserving native tooltips and build previews.
- Treasure Affinity reads cached gear data. Barrel elixir and Crystal Workbench injection signatures were corrected. Expertise refunds retain purchased points with the new method signature. Temporal modifiers respect the new exclusion rule, and custom blocking respects unreducible vessel damage.
- The old skill altar template null-map fix was retired with `SkillAltarData`; saved builds use a different persistence model.

## Upstream startup blocker

On a fresh run directory, this Vault Hunters jar's `VaultStatsConfig.reset()` calls `barrels.put(...)` without initializing `barrels`. This aborts common setup; the later null `VAULT_MAP_ICONS` error is a consequence. This dependency bug is left untouched, as requested. Other dependency versions are unchanged.

Migration diagnostics and decompiled reference sources are under ignored `build/vh22-migration/`. The temporary startup diagnostic is excluded from normal builds. Existing worlds and configs were not used for startup checks.
