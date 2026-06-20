package io.github.a1qs.vaultadditions.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class LuckPermsHelper {
    private LuckPermsHelper() {
    }

    public static Set<String> getRoleNames(ServerPlayer player) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(player.getUUID());
        if (user == null) {
            return Set.of();
        }

        LinkedHashSet<String> roleNames = new LinkedHashSet<>();
        roleNames.add(user.getPrimaryGroup().toLowerCase(Locale.ROOT));
        for (Group group : user.getInheritedGroups(user.getQueryOptions())) {
            roleNames.add(group.getName().toLowerCase(Locale.ROOT));
        }
        return roleNames;
    }
}
