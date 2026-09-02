package xyz.iwolfking.woldsvaults.datagen;

import iskallia.vault.config.SkillDescriptionsConfig;
import net.minecraft.data.DataGenerator;
import xyz.iwolfking.vhapi.api.datagen.AbstractSkillDescriptionsProvider;
import xyz.iwolfking.vhapi.api.util.builder.description.JsonDescription;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.datagen.util.TalentDescriptionsHelper;

public class ModSkillDescriptionsProvider extends AbstractSkillDescriptionsProvider {
    public ModSkillDescriptionsProvider(DataGenerator generator) {
        super(generator, WoldsVaults.MOD_ID);
    }

    @Override
    public void registerConfigs() {

        //      I'll figure this out another time
//        add("talent_overlevels", builder -> {
//            TalentDescriptionsHelper.appendOverlevelDescription("Haste", builder, "add", "+10% Mining Speed", "#47b8f5");
//        });

        add("wolds_talents", builder -> {
            builder.addDescription("Potent_Elixir", description -> {
                description.add(JsonDescription.text("Increases the "));
                description.add(JsonDescription.text("Potency ", "#5ae09c"));
                description.add(JsonDescription.text("of your "));
                description.add(JsonDescription.simple("Vault Potion ", "aqua"));
                description.add(JsonDescription.text(", boosting its effect."));
                description.add(JsonDescription.text(".\n\n1 "));
                description.add(JsonDescription.text("+2 Absorption Hearts", "gold"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+4% Cooldown Reduction", "aqua"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+1 Casted Ability Levels", "yellow"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+20 Flat Mana gained", "#0353d7"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+5 second effect duration", "#7024ac"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+1 effect level", "#FFB00F"));
                description.add(JsonDescription.text("\n\n2 "));
                description.add(JsonDescription.text("+4 Absorption Hearts", "gold"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+8% Cooldown Reduction", "aqua"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+2 Casted Ability Levels", "yellow"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+40 Flat Mana gained", "#0353d7"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+10 second effect duration", "#7024ac"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+2 effect level", "#FFB00F"));
                description.add(JsonDescription.text("\n\n3 "));
                description.add(JsonDescription.text("+6 Absorption Hearts", "gold"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+12% Cooldown Reduction", "aqua"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+3 Casted Ability Levels", "yellow"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+60 Flat Mana gained", "#0353d7"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+15 second effect duration", "#7024ac"));
                description.add(JsonDescription.text(", "));
                description.add(JsonDescription.text("+3 effect level", "#FFB00F"));
                description.add(JsonDescription.text("\n\n"));
                TalentDescriptionsHelper.appendOverlevelDescription("Potent Elixir", "add", "+2 Absorption Hearts, +2% Cooldown Reduction, +1 casted Ability Level, +20 Flat Mana Restored, +5% max mana restored, +3 second effect duration, or 1 effect amplifier every 3 levels", "gold", description);
            });
            builder.addDescription("Healthy_Elixir", description -> {
                description.add(JsonDescription.text("Enhances the "));
                description.add(JsonDescription.text("Healthiness ", "#7DF587"));
                description.add(JsonDescription.text("of your "));
                description.add(JsonDescription.text("Vault Potion ", "aqua"));
                description.add(JsonDescription.text(", making it also heal an additional percentage of your "));
                description.add(JsonDescription.text("max health", "#8EEDD9"));
                description.add(JsonDescription.text(".\n\nDepending on the tier, the amount restored will be modified: "));
                description.add(JsonDescription.text("\nVial: 50% reduction"));
                description.add(JsonDescription.text("\nPotion: 25% reduction"));
                description.add(JsonDescription.text("\nMixture: 0% reduction"));
                description.add(JsonDescription.text("\nBrew: 50% increase"));
                description.add(JsonDescription.text(".\n\n1 "));
                description.add(JsonDescription.text("+20%", "#7DF587"));
                description.add(JsonDescription.text("\n\n2 "));
                description.add(JsonDescription.text("+30%", "#7DF587"));
                description.add(JsonDescription.text("\n\n3 "));
                description.add(JsonDescription.text("+40%", "#7DF587"));
                description.add(JsonDescription.text("\n\n"));
                TalentDescriptionsHelper.appendOverlevelDescription("Healthy Elixir", "add", "+3% max health restored", "#7DF587", description);
            });
            builder.addDescription("Lunge", description -> {
                description.add(JsonDescription.text("Increases the reach in which you hit with melee weapons with additional "));
                description.add(JsonDescription.text("Attack Range", "#0D7A39"));
                description.add(JsonDescription.text(".\n\n1 "));
                description.add(JsonDescription.text("+0.25 Attack Range", "#0D7A39"));
                description.add(JsonDescription.text("\n\n2 "));
                description.add(JsonDescription.text("+0.5 Attack Range", "#0D7A39"));
                description.add(JsonDescription.text("\n\n3 "));
                description.add(JsonDescription.text("+0.75 Attack Range", "#0D7A39"));
                description.add(JsonDescription.text("\n\n4 "));
                description.add(JsonDescription.text("+1 Attack Range", "#0D7A39"));
                description.add(JsonDescription.text("\n\n"));
                TalentDescriptionsHelper.appendOverlevelDescription("Lunge", "add", "+0.1 Attack Range", "#0D7A39", description);
            });

            builder.addDescription("Medic", description -> {
                description.add(JsonDescription.text("Improve your healing skills and sources with increased "));
                description.add(JsonDescription.text("Healing Effectiveness", "#7DF587"));
                description.add(JsonDescription.text(".\n\n1 "));
                description.add(JsonDescription.text("+10% Healing Effectiveness", "#7DF587"));
                description.add(JsonDescription.text("\n\n2 "));
                description.add(JsonDescription.text("+20% Healing Effectiveness", "#7DF587"));
                description.add(JsonDescription.text("\n\n3 "));
                description.add(JsonDescription.text("+30% Healing Effectiveness", "#7DF587"));
                description.add(JsonDescription.text("\n\n4 "));
                description.add(JsonDescription.text("+40% Healing Effectiveness", "#7DF587"));
                description.add(JsonDescription.text("\n\n5 "));
                description.add(JsonDescription.text("+50% Healing Effectiveness", "#7DF587"));
                description.add(JsonDescription.text("\n\n"));
                TalentDescriptionsHelper.appendOverlevelDescription("Medic", "add", "+10% Healing Effectiveness", "#0D7A39", description);

            });

            builder.addDescription("Arcane_Strike", description -> {
                description.add(JsonDescription.text("Enhances your "));
                description.add(JsonDescription.text("Lucky Hits ", "#6DF5A3"));
                description.add(JsonDescription.text("causing them to reduce all of your abilities "));
                description.add(JsonDescription.text("cooldowns by a percentage of their maximum cooldown time"));
                description.add(JsonDescription.text(".\n\n1 "));
                description.add(JsonDescription.text("+2% reduced cooldown", "#cbe6fe"));
                description.add(JsonDescription.text("\n\n2 "));
                description.add(JsonDescription.text("+3% reduced cooldown", "#cbe6fe"));
                description.add(JsonDescription.text("\n\n3 "));
                description.add(JsonDescription.text("+4% reduced cooldown", "#cbe6fe"));
                description.add(JsonDescription.text("\n\n"));
                TalentDescriptionsHelper.appendOverlevelDescription("Arcane Cascade", "add", "+0.5% reduced cooldown", "#cbe6fe", description);
            });
            builder.addDescription("Fanged_Strike", description -> {
                description.add(JsonDescription.text("Enhances your "));
                description.add(JsonDescription.text("Lucky Hits ", "#6DF5A3"));
                description.add(JsonDescription.text("causing them to launch a Fang striking your target from below dealing a small portion of your attack's "));
                description.add(JsonDescription.text("damage ", "#C23627"));
                description.add(JsonDescription.text("and procs any on-hit effects again"));
                description.add(JsonDescription.text(".\n\n1 "));
                description.add(JsonDescription.text("+5% damage", "#C23627"));
                description.add(JsonDescription.text("\n\n2 "));
                description.add(JsonDescription.text("+7.5% damage", "#C23627"));
                description.add(JsonDescription.text("\n\n3 "));
                description.add(JsonDescription.text("+10% damage", "#C23627"));
                description.add(JsonDescription.text("\n\n"));
                TalentDescriptionsHelper.appendOverlevelDescription("Fanged Strike", "add", "+1% increased damage", "#C23627", description);
            });

            builder.addDescription("Execution_Strike", description -> {
                description.add(JsonDescription.text("Enhances your "));
                description.add(JsonDescription.text("Lucky Hits ", "#6DF5A3"));
                description.add(JsonDescription.text("causing them to deal a portion of the target's "));
                description.add(JsonDescription.text("missing health ", "red"));
                description.add(JsonDescription.text("as bonus "));
                description.add(JsonDescription.text("damage ", "red"));
                description.add(JsonDescription.text(".\n\n1 "));
                description.add(JsonDescription.text("+15% of missing health", "#d80000"));
                description.add(JsonDescription.text("\n\n"));
                TalentDescriptionsHelper.appendOverlevelDescription("Executing Strike", "add", "+1% increased missing health damage", "#d80000", description);
            });


        });



        }
    }
