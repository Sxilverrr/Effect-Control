<img width="768" height="546" alt="effect_control_banner" src="https://github.com/user-attachments/assets/9e2c5bf1-eb83-49e9-9d13-a586a2574556" />


Highly customizable potion effect applier & remover.

Effect Control is a compliment for [In Control!](https://www.curseforge.com/minecraft/mc-mods/in-control) that adds more control over potion effects.

## Config

`config/effectcontrol-common.toml`

Each entry in `effects` is one rule: comma separated `key=value` pairs.

```toml
effects = [
    "effect=minecraft:strength, duration=30, trigger=kill, entity=minecraft:zombie",
    "effect=minecraft:regeneration, duration=20, trigger=consume, item=minecraft:golden_apple",
    "effect=minecraft:fire_resistance, duration=infinite, trigger=enter, dimension=minecraft:the_nether",
    "effect=minecraft:fire_resistance, trigger=leave, dimension=minecraft:the_nether, action=remove"
]
```

## Effect Attributes

| Key | Meaning | Default |
| --- | --- | --- |
| `trigger` | What makes the rule activate. | `respawn` |
| `effect` | Potion effect id. Required unless the rule only writes flags. | |
| `duration` | Seconds, or `infinite`. | `30` |
| `level` | `1` is level I, `2` is level II. | `1` |
| `action` | `give` or `remove`. | `give` |
| `ambient` | Softer beacon particles. | `false` |
| `particles` | Show the particles. | `true` |
| `icon` | Show the HUD icon. | `true` |
| `hidden` | Shorthand for `particles=false, icon=false`. | `false` |

## Triggers

Activated by something the player does:

| Trigger | Activates when | Useful filters |
| --- | --- | --- |
| `join` | Player logs in | |
| `first_join` | First login ever, once per player | |
| `respawn` | Player respawns after dying | `dimension` |
| `end_return` | Player leaves the End through the exit portal | |
| `any` | `join`, `respawn` or `end_return` | |
| `enter` / `leave` | Dimension entered / left | `dimension` (required) |
| `kill` | Player kills something | `entity`, `source` |
| `hurt` | Player damages something | `entity`, `source` |
| `hurt_by` | Player is damaged | `entity`, `source` |
| `use` | Player right-clicks an item in the air, e.g. an ender pearl or a bucket | `item` |
| `consume` | Player finishes eating or drinking | `item` |
| `shoot` | Player fires a bow | `item` |
| `release` | Player releases a held item, e.g. a trident | `item` |
| `pickup` | Player picks an item up | `item` |
| `container` | Player opens a container or their inventory | |
| `advancement` | Player earns an advancement | `advancement` |
| `craft` / `smelt` | Item crafted / smelted | `item` |
| `fish` | Player reels something in | `item` |
| `tame` / `breed` | Animal tamed / bred | `entity` |
| `trade` | Villager trade completed | `item` |
| `level_up` | Player gains XP levels | |
| `tool_broken` | Held item breaks | `item` |

Continuous player state, activated when entering or leaving it. They come in pairs, so you pair the
`give` with the matching `remove`:

| Trigger | Becomes true when | Needs |
| --- | --- | --- |
| `near` / `away` | Something is within `radius` | `radius` and `block` or `entity` |
| `looking` / `not_looking` | Player is looking at it | `block` or `entity`, `radius` optional (default 5) |
| `sneaking` / `not_sneaking` | Player is crouching | |
| `sprinting` / `not_sprinting` | Player is sprinting | |
| `on_fire` / `not_on_fire` | Player is burning | |
| `in_fluid` / `not_in_fluid` | Player is in a fluid | `fluid` |
| `riding` / `not_riding` | Player is on a mount | `entity` optional |
| `sleeping` / `not_sleeping` | Player is in a bed | |
| `underwater` / `not_underwater` | Player's head is submerged | |

Most triggers accept alternative spellings, so write whichever you prefer. `damage` or `attack`
for `hurt`; `takedamagefrom` or `damaged_by` for `hurt_by`; `eat` or `drink` for `consume`;
`use_item` or `interact` for `use`; `inventory` or `open` for `container`; `achievement` for `advancement`; `always` for `any`;
`every_join` or `login` for `join`; `every_respawn` or `death` for `respawn`; `end` for
`end_return`; `enter_dimension` for `enter`; `leave_dimension` or `exit` for `leave`; `fire` for
`shoot`; `when_near` for `near`; `not_near` or `leave_near` for `away`; `once` for `first_join`;
`look` or `looking_at` for `looking`; `look_away` for `not_looking`; `sneak` or `crouching` for `sneaking`;
`sprint` for `sprinting`; `burning` for `on_fire`; `mounted` for `riding`; `dismounted` for
`not_riding`; `asleep` for `sleeping`; `awake` for `not_sleeping`; `submerged` for `underwater`;
`in_liquid` / `not_in_liquid` for `in_fluid` / `not_in_fluid`; `levelup` for `level_up`;
`item_broken` for `tool_broken`.

Filter keys have aliases too: `mob` for `entity`, `armor` for `wearing`, `damage_type` for
`source`, `liquid` for `fluid`, `xp` for `xp_level`, `tag` for `nbt`, `has`/`without` for
`requires`/`missing`, and `set_flag`/`clear_flag`/`flagged`/`unflagged` for
`set`/`unset`/`if_set`/`if_unset`.

## Filters

Any of these narrow a rule. Repeat a key to accept more than one value; the rule needs one match
from each key you used.

| Key | Matches | Example |
| --- | --- | --- |
| `dimension` | Dimension the rule is about | `dimension=minecraft:the_nether` |
| `block` | Block id | `block=minecraft:diamond_ore` |
| `entity` | Entity type id | `entity=minecraft:creeper` |
| `item` | Item id | `item=minecraft:golden_apple` |
| `source` | Damage type, as vanilla names it | `source=lava` |
| `advancement` | Advancement id | `advancement=minecraft:story/mine_diamond` |
| `radius` | Blocks, `1` to `64` | `radius=8` |
| `x` `y` `z` | Coordinate, exact or a range | `y=50..100`, `y=..0`, `x=64..` |
| `time` | Day tick `0..24000`, or a name | `time=night`, `time=0..6000` |
| `weather` | `clear`, `rain` or `thunder` | `weather=thunder` |
| `days` | In-world days elapsed | `days=100..` |
| `stat` + `count` | A player statistic and its value | `stat=killed:minecraft:zombie, count=100..` |
| `nbt` | Item NBT, matched as a subset | `nbt={Damage:0}` |
| `health` | Player health in points | `health=..6` |
| `xp_level` | Experience level | `xp_level=30..` |
| `light` | Light level where the player stands | `light=..7` |
| `biome` | Biome id | `biome=minecraft:desert` |
| `tag` | Scoreboard tag on the player | `tag=vip` |
| `gamemode` | `survival` `creative` `adventure` `spectator` | `gamemode=survival` |
| `difficulty` | `peaceful` `easy` `normal` `hard` | `difficulty=hard` |
| `wearing` | Item worn in an armor slot | `wearing=minecraft:diamond_helmet` |
| `fluid` | `water` or `lava` | `fluid=lava` |
| `chance` | Percent chance the rule fires, `1` to `100` | `chance=25` |
| `cooldown` | Seconds before the rule may fire again | `cooldown=60` |

Time names: `day`, `night`, `noon`, `midnight`, `sunrise` (or `dawn`), `sunset` (or `dusk`).

Stat types: `mined`, `killed`, `killed_by`, `used`, `crafted`, `broken`, `picked_up`, `dropped`,
`custom`. `count` defaults to `1..`, meaning at least once.

Everything from `x` downwards works on every trigger:

```toml
effects = [
    "effect=minecraft:poison, duration=10, trigger=pickup, item=minecraft:diamond, y=..0, time=night",
    "effect=minecraft:strength, duration=60, trigger=join, stat=killed:minecraft:zombie, count=100.."
]
```

## Conditions

`requires` and `missing` gate a rule on effects the player already has. Both repeat, and all must
pass. `has` and `without` are aliases.

```toml
effects = [
    "effect=minecraft:regeneration, duration=15, trigger=join, requires=minecraft:poison",
    "effect=minecraft:strength, duration=30, trigger=respawn, missing=minecraft:weakness"
]
```

Conditions read the player's state from **before** the rule set runs, so rules in one event cannot
see each other's work. A rule blocked by a condition does not count against its `limit`.

## Flags

Flags are permanent per-player marks, saved in the world. You can use them to make something like "once this
happens, never again".

| Key | Does |
| --- | --- |
| `set` | Set the flag when the rule fires |
| `unset` | Clear the flag when the rule fires |
| `if_set` | Only fire if the flag is set |
| `if_unset` | Only fire if the flag is not set |

```toml
effects = [
    "trigger=advancement, advancement=minecraft:end/kill_dragon, set=dragon_slain",
    "effect=minecraft:strength, duration=60, trigger=join, if_unset=dragon_slain",
    "trigger=consume, item=minecraft:milk_bucket, unset=dragon_slain"
]
```

Kill the dragon and the strength stops forever. Drink milk and it comes back. A rule that only
writes flags needs no `effect`.

## Curios

The `wearing` filter checks armour slots and, when
[Curios](https://www.curseforge.com/minecraft/mc-mods/curios) is installed, curio slots as well:

```toml
effects = [
    "effect=minecraft:speed, duration=30, trigger=consume, item=minecraft:bread, wearing=curios:ring"
]
```

## Limits

`limit` stops a rule after that many grants per player, `0` never stops. `id` names the counter,
defaulting to the effect, trigger and filters combined, so give two rules the same `id` when they
should share a counter.

## Timing

`apply_delay_ticks` (`0` to `200`) waits before applying. Use it if another mod clears effects when
a player spawns.

`scan_interval_ticks` (`1` to `200`, default `20`) is how often the continuous triggers re-check.
Block scans for `near` sweep a sphere of `radius`.

## Command

This clears saved `limit` counters and flags.

| Command | Clears |
| --- | --- |
| `/effectcontrol reset` | Whoever ran it |
| `/effectcontrol reset <targets>` | The players a selector picks out |
| `/effectcontrol reset all` | Every stored player, online or not |

## Notes

Effects, blocks, items, entities and dimensions from other mods all work, just use their ids. A
namespace-less id is assumed to be `minecraft:`. Entries that cannot be read are skipped and
reported in the log, as is any id that is not installed.
