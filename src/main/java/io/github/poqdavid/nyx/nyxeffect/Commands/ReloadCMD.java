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

import io.github.poqdavid.nyx.nyxcore.Permissions.EffectPermission;
import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;


public class ReloadCMD implements CommandExecutor {

    public static String[] getAlias() {
        return new String[]{"reload"};
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src.hasPermission(EffectPermission.COMMAND_RELOAD)) {
            NyxEffect.HOLDEFFECTS = true;

            src.sendMessage(Text.of(TextColors.GOLD, "Stopping all Tasks!!"));
            Tools.UserTaskStop("all", false);
            src.sendMessage(Text.of(TextColors.GOLD, "Stopped all Tasks!!"));

            src.sendMessage(Text.of(TextColors.GOLD, "Loading Effects!!"));
            NyxEffect.getInstance().ParticlesLIST = Tools.loadparticles(NyxEffect.getInstance().particlesdatapath);
            src.sendMessage(Text.of(TextColors.GOLD, "Effects Loaded!!"));

            src.sendMessage(Text.of(TextColors.GOLD, "Registering Effect Permission Nodes!!"));
            NyxEffect.getInstance().RegisterEffectNodes();
            src.sendMessage(Text.of(TextColors.GOLD, "Registered Effect Permission Nodes!!"));

            NyxEffect.HOLDEFFECTS = false;
            src.sendMessage(Text.of(TextColors.GOLD, "Starting all Tasks!!"));
            Tools.UserTaskStart("all", false);
            src.sendMessage(Text.of(TextColors.GOLD, "Started all Tasks!!"));

        } else {
            throw new CommandPermissionException(Text.of("You don't have permission to use this command."));
        }

        return CommandResult.success();
    }

}


