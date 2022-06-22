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
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class TaskCMD extends CommandBase {
    private final List<String> aliases;

    public TaskCMD() {
        aliases = Lists.newArrayList("task");
    }

    @Override
    @Nonnull
    public String getName() {
        return "task";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "task";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        NyxEntityPlayerMP player_cmd_src;

        NyxEntityPlayerMP player_args;
        String task;
        String action;

        if (args.length == 0 || args.length > 2) {
            NyxEffect.help.sendHelp(sender.getCommandSenderEntity());
            return;
        }

        task = args[0];
        action = args[1];

        if (args.length >= 3) {
            player_args = (NyxEntityPlayerMP) server.getPlayerList().getPlayerByUsername(args[2]);
        } else {
            if (sender instanceof EntityPlayerMP) {
                player_args = (NyxEntityPlayerMP) sender;
            } else {
                NyxEffect.help.sendHelp(sender.getCommandSenderEntity());
                return;
            }
        }

        if (sender instanceof EntityPlayerMP) {
            player_cmd_src = (NyxEntityPlayerMP) sender;

            if (player_cmd_src.getUniqueId().equals(player_args.getUniqueId())) {

                if (player_cmd_src.hasPermission(EffectPermission.COMMAND_TASK)) {

                    if (action.equals("restart")) {
                        Tools.UserTaskRestart(player_cmd_src, task, player_cmd_src, true);
                    }
                    if (action.equals("stop")) {
                        Tools.UserTaskStop(player_cmd_src, task, player_cmd_src, true);
                    }
                    if (action.equals("start")) {
                        Tools.UserTaskStart(player_cmd_src, task, player_cmd_src, true);
                    }
                } else {
                    throw new CommandException("NyxEffect.NoPermission");
                }
            } else {

                if (player_cmd_src.hasPermission(EffectPermission.COMMAND_TASK_OTHER)) {
                    if (action.equals("restart")) {
                        Tools.UserTaskRestart(player_args, task, player_cmd_src, true);
                    }
                    if (action.equals("stop")) {
                        Tools.UserTaskStop(player_args, task, player_cmd_src, true);
                    }
                    if (action.equals("start")) {
                        Tools.UserTaskStart(player_args, task, player_cmd_src, true);
                    }
                } else {
                    throw new CommandException("NyxEffect.NoPermission");
                }

            }
        } else if (sender instanceof MinecraftServer) {

            if (action.equals("restart")) {
                Tools.UserTaskRestart(player_args, task, sender.getCommandSenderEntity(), true);
            }
            if (action.equals("stop")) {
                Tools.UserTaskStop(player_args, task, sender.getCommandSenderEntity(), true);
            }
            if (action.equals("start")) {
                Tools.UserTaskStart(player_args, task, sender.getCommandSenderEntity(), true);
            }

        } else {

            if (action.equals("restart")) {
                Tools.UserTaskRestart(player_args, task, sender.getCommandSenderEntity(), true);
            }
            if (action.equals("stop")) {
                Tools.UserTaskStop(player_args, task, sender.getCommandSenderEntity(), true);
            }
            if (action.equals("start")) {
                Tools.UserTaskStart(player_args, task, sender.getCommandSenderEntity(), true);
            }

        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return Sender.hasPermission(sender, EffectPermission.COMMAND_TASK);
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }


}
