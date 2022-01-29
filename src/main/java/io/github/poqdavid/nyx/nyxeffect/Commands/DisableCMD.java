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
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class DisableCMD implements CommandExecutor {

    public CommandResult execute(CommandSource sender, CommandContext args) {
        final Optional<Player> target = args.getOne("target");

        if (!target.isPresent()) {

            final Player player = (Player) sender;

            if (player.hasPermission(EffectPermission.COMMAND_EFFECT)) {
                NyxEffect.getInstance().ParticleClearAll(player);

                player.sendMessage(Text.of(TextColors.AQUA, "All of your NyxEffect modes have been cleared!"));
            }
        } else if (target.isPresent() && sender.hasPermission(EffectPermission.COMMAND_DISABLEOTHERS)) {
            final Player targ = target.get();

            NyxEffect.getInstance().ParticleClearAll(targ);

            targ.sendMessage(Text.of(TextColors.YELLOW, "All of your NyxEffect modes have been cleared by ",
                    sender.getName(), "!"));
            sender.sendMessage(Text.of(TextColors.YELLOW, "All of ", TextColors.AQUA, targ.getName(), "'s ",
                    TextColors.YELLOW, "NyxEffect modes have been cleared!"));
        } else {
            // return CommandResult.success();
        }
        return CommandResult.success();

    }
    // return CommandResult.success();
}
