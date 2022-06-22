/*
 *     This file is part of NyxEffect.
 *
 *     NyxEffect is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     NyxEffect is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with NyxEffect.  If not, see <https://www.gnu.org/licenses/>.
 *
 *     Copyright (c) POQDavid <https://github.com/poqdavid/NyxEffect>
 *     Copyright (c) contributors
 */

package io.github.poqdavid.nyx.nyxeffect.Commands;

import com.google.common.collect.Lists;
import io.github.poqdavid.nyx.nyxcore.Permissions.EffectPermission;
import io.github.poqdavid.nyx.nyxcore.Utils.Commands.Sender;
import io.github.poqdavid.nyx.nyxcore.Utils.Forge.NyxEntityPlayerMP;
import io.github.poqdavid.nyx.nyxcore.Utils.Forge.TextComponentString;
import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class DisableCMD extends CommandBase {
    private final List<String> aliases;

    public DisableCMD() {
        aliases = Lists.newArrayList("disable");
    }

    @Override
    @Nonnull
    public String getName() {
        return "disable";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "disable";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        NyxEntityPlayerMP target = null;

        if (args.length != 0) {
            if (args.length == 1) {
                target = (NyxEntityPlayerMP) server.getPlayerList().getPlayerByUsername(args[0]);
            }
        }

        if (target != null) {
            if (Sender.hasPermission(sender, EffectPermission.COMMAND_DISABLEOTHERS)) {
                NyxEffect.getInstance().ParticleClearAll(target);

                target.sendMessage(TextComponentString.from(TextFormatting.YELLOW, "All of your NyxEffect modes have been cleared by ", sender.getName(), "!"));
                sender.sendMessage(TextComponentString.from(TextFormatting.YELLOW, "All of ", TextFormatting.AQUA, target.getName(), "'s ", TextFormatting.YELLOW, "NyxEffect modes have been cleared!"));
            } else {
                NyxEntityPlayerMP player = Sender.toNyxEntityPlayerMP(sender);

                if (player.hasPermission(EffectPermission.COMMAND_EFFECT)) {
                    NyxEffect.getInstance().ParticleClearAll(player);

                    player.sendMessage(TextComponentString.from(TextFormatting.AQUA, "All of your NyxEffect modes have been cleared!"));
                }
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return Sender.hasPermission(sender, EffectPermission.COMMAND_EFFECT);
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }
}
