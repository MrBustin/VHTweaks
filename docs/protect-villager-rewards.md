# Protect Villager rewards

The controller accepts `floatingItemActions`, a list using VH's standard challenge
action format. These actions are added to each of the four initial reward choices,
resolved when the player chooses a reward, and doubled along with that reward by
"Double It". They are paid out on completion or "Forfeit and Loot", not on failure.
The controller and challenge manager both persist this list in NBT. Existing
controllers without the field default to an empty list.

For example, merge this SNBT into a controller before starting the challenge
(replace the coordinates and item as appropriate):

```mcfunction
/data merge block <x> <y> <z> {floatingItemActions:[{type:"floating_item_reward",item:{id:"minecraft:diamond",Count:4b},color1:65535,color2:16777215,textColor:65535}]}
```

This adds four floating diamonds to any initial reward choice. Each "Double It"
doubles that payout. The item is an ordinary item-stack NBT compound and may include
an item `tag`. `color1` and `color2` control the native floating item's colors;
`textColor` controls the reward description color. An optional `name` overrides
the displayed item name.

Inline actions in a generation palette's controller output are read as block-entity
NBT, not through the challenge action JSON reader. Use capital `Count`, an optional
`tag` object for item NBT, and numeric colors there:

```json
"successActions": [
    {
        "type": "floating_item_reward",
        "item": { "id": "minecraft:diamond", "Count": 1 },
        "color1": -16711681,
        "color2": -10092289
    }
]
```

In `challenge/actions.json`, use lowercase `count` and `nbt` instead; that file
uses VH's action JSON reader. A palette can reference a named action from that
file with `{ "type": "reference", "path": "diamond" }`. Palette changes apply
to newly generated controllers after reloading the configuration or restarting.

Category-specific floating rewards can also be placed directly in `gildedActions`,
`ornateActions`, `livingActions`, or `coinPileActions`. Floating rewards in
`successActions` pay out once and do not participate in doubling.

Rewards use VH's native `FloatingItemRewardChallengeAction`, including vault loot
initialization, No Loot modifier handling, air-space checks, and floating item
pickup behavior. Floating items use the configured challenge zone. Ground rewards
use VH's `TileRewardChallengeAction` with the zone capped at the controller's Y
coordinate, including ground rewards in `successActions`.
