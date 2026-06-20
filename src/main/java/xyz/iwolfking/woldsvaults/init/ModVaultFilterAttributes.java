package xyz.iwolfking.woldsvaults.init;

import xyz.iwolfking.woldsvaults.integration.vaultfilters.*;

public class ModVaultFilterAttributes {
    public static void initAttributes() {
        new HasUnusualAffixAttribute(true).register(HasUnusualAffixAttribute::new);
        new UnusualPrefixAttribute("Attack Damage").register(UnusualPrefixAttribute::new);
        new UnusualSuffixAttribute("Attack Damage").register(UnusualSuffixAttribute::new);
        new AntiqueAttribute("Acquired taste").register(AntiqueAttribute::new);
        new VaultDollCompletedAttribute(true).register(VaultDollCompletedAttribute::new);
        new SuperCatalystAttribute(true).register(SuperCatalystAttribute::new);
        new GreedCatalystAttribute(true).register(GreedCatalystAttribute::new);
        new SuperInscriptionAttribute(true).register(SuperInscriptionAttribute::new);
        new AncientCompanionRelicModifierCountAttribute("the_vault:plentiful", 1).register(AncientCompanionRelicModifierCountAttribute::new);
        new DeckCoreTypeAttribute("equilibrium").register(DeckCoreTypeAttribute::new);
        new DeckCoreTierAttribute("greater").register(DeckCoreTierAttribute::new);
        new DeckCoreCategoryAttribute("Resource").register(DeckCoreCategoryAttribute::new);
        new DeckCoreTargetAttribute("Stat").register(DeckCoreTargetAttribute::new);
        new DeckCoreStrengthAttribute(50).register(DeckCoreStrengthAttribute::new);
        new DeckCoreSlotCountAttribute(3).register(DeckCoreSlotCountAttribute::new);
    }
}
