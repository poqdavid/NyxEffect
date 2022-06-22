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
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


public class ReloadCMD extends CommandBase {
    private final List<String> aliases;

    public ReloadCMD() {
        aliases = Lists.newArrayList("reload");
    }

    @Override
    @Nonnull
    public String getName() {
        return "reload";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "reload";
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
            player_cmd_src = (NyxEntityPlayerMP) sender;

            if (player_cmd_src.hasPermission(EffectPermission.COMMAND_RELOAD)) {
                this.Run(sender);
            } else {
                throw new CommandException("NyxEffect.NoPermission");
            }
        } else {
            this.Run(sender);
        }
    }

    private void Run(ICommandSender sender) {
        NyxEffect.HOLDEFFECTS = true;

        sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "Stopping all Tasks!!"));
        Tools.UserTaskStop("all", null, false);
        sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "Stopped all Tasks!!"));

        sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "Loading Effects!!"));
        NyxEffect.getInstance().ParticlesLIST = Tools.loadparticles(NyxEffect.getInstance().particlesdatapath);
        sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "Effects Loaded!!"));

        sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "Registering Effect Permission Nodes!!"));
        NyxEffect.getInstance().RegisterEffectNodes();
        NyxEffect.getInstance().LoadEffectCMDs();
        sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "Registered Effect Permission Nodes!!"));

        NyxEffect.HOLDEFFECTS = false;
        sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "Starting all Tasks!!"));
        Tools.UserTaskStart("all", null, false);
        sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "Started all Tasks!!"));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return Sender.hasPermission(sender, EffectPermission.COMMAND_RELOAD);
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }
}


