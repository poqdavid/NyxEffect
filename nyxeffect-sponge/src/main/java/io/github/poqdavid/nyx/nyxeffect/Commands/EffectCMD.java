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
import io.github.poqdavid.nyx.nyxcore.Utils.CoreTools;
import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.EffectsData;
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;


public class EffectCMD implements CommandExecutor {

    public static String[] getAlias() {
        return new String[]{"effect"};
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final Player player_args = args.<Player>getOne("player").orElse(null);
        final String effect = args.<String>getOne("effect").orElse(null);

        if (src instanceof Player) {
            Player player_cmd_src = CoreTools.getPlayer(src);


            if (effect == null) {
                throw new CommandException(Text.of("Sorry effect can't be empty"));
            }

            EffectsData effectOBJ;
            try {
                effectOBJ = Tools.GetEffect(effect);
            } catch (Exception e) {
                throw new CommandException(Text.of("Sorry effect " + effect + " is not available!"));
            }

            final String effectperm = EffectPermission.EFFECTS + "." + effectOBJ.getId();

            if (player_args == null) {
                if (player_cmd_src.hasPermission(EffectPermission.COMMAND_EFFECT)) {

                    if (!player_cmd_src.hasPermission(effectperm)) {
                        throw new CommandPermissionException(Text.of("You don't have permission to use this effect."));
                    }

                    NyxEffect.getInstance().ParticleToggle(player_cmd_src, effectOBJ);
                } else {
                    throw new CommandPermissionException(Text.of("You don't have permission to use this command."));
                }

                return CommandResult.success();
            }

            if (player_cmd_src.getUniqueId().equals(player_args.getUniqueId())) {
                if (player_cmd_src.hasPermission(EffectPermission.COMMAND_EFFECT)) {

                    if (!player_cmd_src.hasPermission(effectperm)) {
                        throw new CommandPermissionException(Text.of("You don't have permission to use this effect."));
                    }

                    NyxEffect.getInstance().ParticleToggle(player_cmd_src, effectOBJ);
                } else {
                    throw new CommandPermissionException(Text.of("You don't have permission to use this command."));
                }

                return CommandResult.success();
            } else {
                if (src.hasPermission(EffectPermission.COMMAND_EFFECT_OTHER)) {

                    if (!player_args.hasPermission(effectperm)) {
                        src.sendMessage(Text.of(TextColors.GOLD, TextStyles.BOLD, "The user " + player_args.getName() + " doesn't have the permission to use this effect! (" + effectperm + ")"));
                    }

                    NyxEffect.getInstance().ParticleToggle(src, player_args, effectOBJ);
                    return CommandResult.success();
                } else {
                    throw new CommandPermissionException(Text.of("You don't have permission to use this command."));
                }
                // return CommandResult.success();
            }
            //return CommandResult.success();
        } else {
            if (effect == null) {
                throw new CommandException(Text.of("Sorry effect can't be empty"));
            }

            EffectsData effectOBJ;
            try {
                effectOBJ = Tools.GetEffect(effect);
            } catch (Exception e) {
                throw new CommandException(Text.of("Sorry effect " + effect + " is not available!"));
            }

            NyxEffect.getInstance().ParticleToggle(src, player_args, effectOBJ);
            return CommandResult.success();
        }
    }

}


