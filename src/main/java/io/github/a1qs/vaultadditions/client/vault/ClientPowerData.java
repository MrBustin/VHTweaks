package io.github.a1qs.vaultadditions.client.vault;

import io.github.a1qs.vaultadditions.network.KnownPowerMessage;
import io.github.a1qs.vaultadditions.vault.menu.PowerTree;
import iskallia.vault.core.net.ArrayBitBuffer;
import iskallia.vault.skill.base.TieredSkill;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClientPowerData {
    private static final PowerTree POWER_TREE = new PowerTree();


    @Nonnull
    public static List<TieredSkill> getLearnedTalentNodes() {
        List<TieredSkill> talents = new ArrayList<>();
        POWER_TREE.iterate(TieredSkill.class, (talent) -> {
            if (talent.isUnlocked()) {
                talents.add(talent);
            }

        });
        return talents;
    }

    public static PowerTree getPowerTree() {
        return POWER_TREE;
    }

    @Nullable
    public static TieredSkill getLearnedTalentNode(String talentName) {
        Iterator<TieredSkill> var1 = getLearnedTalentNodes().iterator();

        TieredSkill node;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            node = var1.next();
        } while(!node.getId().equals(talentName));

        return node;
    }

    public static void updateTalents(KnownPowerMessage pkt) {
        ArrayBitBuffer buffer = ArrayBitBuffer.empty();
        pkt.getTree().writeBits(buffer);
        buffer.setPosition(0);
        POWER_TREE.readBits(buffer);
    }
}
