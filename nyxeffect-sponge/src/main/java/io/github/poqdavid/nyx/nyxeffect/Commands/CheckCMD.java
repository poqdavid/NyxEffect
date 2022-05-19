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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;

public class CheckCMD implements CommandExecutor {

    public static String[] getAlias() {
        return new String[]{"check"};
    }

    public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {
        final Optional<Player> target = args.getOne("target");
        final Optional<String> targets = args.getOne("targets");

        if (!target.isPresent() && !targets.isPresent()) {
            final Player player = (Player) sender;
            if (player.hasPermission(EffectPermission.COMMAND_CHECK)) {
                final List<String> pd = NyxEffect.getInstance().UserParticlesLIST.get(player.getUniqueId().toString());
                player.sendMessage(Text.of(TextColors.AQUA, "Your ", TextColors.YELLOW, "Active NyxEffect modes", TextColors.GRAY, ":"));

                if (pd == null) {
                    player.sendMessage(Text.of(TextColors.GOLD, "NONE"));
                } else {
                    for (String effect : pd) {
                        try {
                            final EffectsData ed = Tools.GetEffect(effect);
                            final Text texts = Text.builder(ed.getName() + " (" + ed.getId() + ")").color(TextColors.GRAY).onHover(TextActions.showText(Text.of("/nyxeffect effect " + ed.getId()))).onClick(TextActions.runCommand("/nyxeffect effect " + ed.getId())).build();
                            player.sendMessage(Text.of(TextColors.GOLD, "- ", texts));
                        } catch (Exception e) {

                        }
                    }
                }
            } else {
                throw new CommandPermissionException(Text.of("You don't have permission to use this command."));
            }
        } else if (target.isPresent()) {
            final Player targ = target.get();
            if (sender.hasPermission(EffectPermission.COMMAND_CHECKOTHERS)) {
                final List<String> pd = NyxEffect.getInstance().UserParticlesLIST.get(targ.getUniqueId().toString());
                sender.sendMessage(Text.of(TextColors.YELLOW, targ.getName(), "'s", TextColors.AQUA, " active NyxEffect modes:"));
                if (pd == null) {
                    sender.sendMessage(Text.of(TextColors.GOLD, "NONE"));
                } else {
                    for (String effect : pd) {
                        try {
                            final EffectsData ed = Tools.GetEffect(effect);
                            final Text texts = Text.builder(ed.getName() + " (" + ed.getId() + ")").color(TextColors.GRAY).onHover(TextActions.showText(Text.of("/nyxeffect effect " + ed.getId() + " " + targ.getName()))).onClick(TextActions.runCommand("/nyxeffect effect " + ed.getId() + " " + targ.getName())).build();
                            sender.sendMessage(Text.of(TextColors.GOLD, "- ", texts));
                        } catch (Exception e) {

                        }
                    }
                }
            } else {
                throw new CommandPermissionException(Text.of("You don't have permission to use this command."));
            }
        }
        return CommandResult.success();
    }
}
