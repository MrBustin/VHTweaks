package xyz.iwolfking.woldsvaults.core.vault.challenge.temple;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.vault.challenge.action.ChallengeAction;
import xyz.iwolfking.woldsvaults.core.vault.challenge.temple.action.AddMobsChallengeAction;
import xyz.iwolfking.woldsvaults.core.vault.challenge.temple.action.FloatingItemRewardChallengeAction;
import xyz.iwolfking.woldsvaults.core.vault.challenge.temple.action.TileRewardChallengeAction;

/** Keeps existing action pools usable while executing the editable temple implementations. */
public final class TempleActions {
    private TempleActions() {}

    public static void registerAdapters() {
        Adapters.CHALLENGE_ACTION.register("woldsvaults:temple/add_mobs", AddMobsChallengeAction.class, AddMobsChallengeAction::new);
        Adapters.CHALLENGE_ACTION.register("woldsvaults:temple/tile_reward", TileRewardChallengeAction.class, TileRewardChallengeAction::new);
        Adapters.CHALLENGE_ACTION.register("woldsvaults:temple/floating_item_reward", FloatingItemRewardChallengeAction.class, FloatingItemRewardChallengeAction::new);
    }

    public static ChallengeAction<?> localize(ChallengeAction<?> action) {
        ChallengeAction<?> replacement;
        if (action != null && action.getClass() == iskallia.vault.core.vault.challenge.action.AddMobsChallengeAction.class) {
            replacement = new AddMobsChallengeAction();
        } else if (action != null && action.getClass() == iskallia.vault.core.vault.challenge.action.TileRewardChallengeAction.class) {
            replacement = new TileRewardChallengeAction();
        } else if (action != null && action.getClass() == iskallia.vault.core.vault.challenge.action.FloatingItemRewardChallengeAction.class) {
            replacement = new FloatingItemRewardChallengeAction();
        } else {
            return action;
        }
        replacement.readNbt(action.writeNbt().orElseThrow());
        return replacement;
    }
}
