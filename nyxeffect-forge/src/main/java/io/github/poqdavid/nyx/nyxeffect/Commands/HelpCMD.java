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
import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import java.util.List;


public class HelpCMD extends CommandBase {
    private final List<String> aliases;

    public HelpCMD() {
        aliases = Lists.newArrayList("help", "?");
    }

    @Override
    @Nonnull
    public String getName() {
        return "help";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "help";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        NyxEntityPlayerMP player_cmd_src;
        if (sender instanceof EntityPlayerMP) {
            player_cmd_src = NyxEntityPlayerMP.from((EntityPlayerMP) sender);
            if (player_cmd_src.hasPermission(EffectPermission.COMMAND_HELP)) {
                NyxEffect.help.sendHelp(player_cmd_src);
            } else {
                throw new CommandException("NyxEffect.NoPermission");
            }

        } else {
            NyxEffect.help.sendHelp(sender);
        }
    }


    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return Sender.hasPermission(sender, EffectPermission.COMMAND_HELP);
    }
}


