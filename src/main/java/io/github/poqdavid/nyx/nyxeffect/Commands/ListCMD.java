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
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.ParticlesData;
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
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;

public class ListCMD implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        final String option = args.<String>getOne("option").orElse("*");

        final Player player = CoreTools.getPlayer(src);
        if (src.hasPermission(EffectPermission.COMMAND_LIST)) {
            final List<ParticlesData> pd = NyxEffect.getInstance().ParticlesLIST;
            final List<String> pd_player = NyxEffect.getInstance().UserParticlesLIST.get(player.getUniqueId().toString());


            if (option.equalsIgnoreCase("*")) {
                src.sendMessage(Text.of(TextColors.AQUA, "List of available ", TextColors.YELLOW, "NyxEffect modes", TextColors.GRAY, ":"));
                for (ParticlesData effect : pd) {
                    final String effectperm = EffectPermission.EFFECTS + "." + effect.getEffectsData().getId();
                    if (src.hasPermission(effectperm)) {
                        Text texts;

                        try {
                            if (pd_player.contains(effect.getEffectsData().getId())) {
                                texts = Text.builder(effect.getEffectsData().getName() + " (" + effect.getEffectsData().getId() + ")").style(TextStyles.BOLD).color(TextColors.GREEN).onHover(TextActions.showText(Text.of("/nyxeffect " + effect.getEffectsData().getId()))).onClick(TextActions.runCommand("/nyxeffect " + effect.getEffectsData().getId())).build();
                            } else {
                                texts = Text.builder(effect.getEffectsData().getName() + " (" + effect.getEffectsData().getId() + ")").style(TextStyles.BOLD).color(TextColors.GRAY).onHover(TextActions.showText(Text.of("/nyxeffect " + effect.getEffectsData().getId()))).onClick(TextActions.runCommand("/nyxeffect " + effect.getEffectsData().getId())).build();
                            }
                        } catch (Exception ex) {
                            texts = Text.builder(effect.getEffectsData().getName() + " (" + effect.getEffectsData().getId() + ")").style(TextStyles.BOLD).color(TextColors.GRAY).onHover(TextActions.showText(Text.of("/nyxeffect " + effect.getEffectsData().getId()))).onClick(TextActions.runCommand("/nyxeffect " + effect.getEffectsData().getId())).build();

                        }


                        src.sendMessage(Text.of(TextColors.GOLD, "- ", texts));
                    }
                }
                final Text texts = Text.builder("More...").style(TextStyles.BOLD).color(TextColors.GRAY).onClick(TextActions.runCommand("/nyxeffectlist more")).build();
                src.sendMessage(Text.of(texts));
            }

            if (option.equalsIgnoreCase("more")) {
                src.sendMessage(Text.of(TextColors.AQUA, "List of available but inactive ", TextColors.YELLOW, "NyxEffect modes", TextColors.GRAY, ":"));
                for (ParticlesData effect : pd) {
                    final String effectperm = EffectPermission.EFFECTS + "." + effect.getEffectsData().getId();
                    if (!src.hasPermission(effectperm)) {
                        src.sendMessage(Text.of(TextColors.GOLD, "- ", TextColors.GRAY, effect.getEffectsData().getName() + " (" + effect.getEffectsData().getId() + ")"));
                    }
                }
            }

            if (option.equalsIgnoreCase("all")) {
                src.sendMessage(Text.of(TextColors.AQUA, "List of available ", TextColors.YELLOW, "NyxEffect modes", TextColors.GRAY, ":"));
                for (ParticlesData effect : pd) {
                    final String effectperm = EffectPermission.EFFECTS + "." + effect.getEffectsData().getId();
                    if (src.hasPermission(effectperm)) {
                        Text texts;
                        if (pd_player.contains(effect.getEffectsData().getId())) {
                            texts = Text.builder(effect.getEffectsData().getName() + " (" + effect.getEffectsData().getId() + ")").style(TextStyles.BOLD).color(TextColors.GREEN).onHover(TextActions.showText(Text.of("/nyxeffect " + effect.getEffectsData().getId()))).onClick(TextActions.runCommand("/nyxeffect " + effect.getEffectsData().getId())).build();
                        } else {
                            texts = Text.builder(effect.getEffectsData().getName() + " (" + effect.getEffectsData().getId() + ")").style(TextStyles.BOLD).color(TextColors.GRAY).onHover(TextActions.showText(Text.of("/nyxeffect " + effect.getEffectsData().getId()))).onClick(TextActions.runCommand("/nyxeffect " + effect.getEffectsData().getId())).build();
                        }

                        src.sendMessage(Text.of(TextColors.GOLD, "- ", texts));
                    } else {
                        src.sendMessage(Text.of(TextColors.GOLD, "- ", TextColors.GRAY, effect.getEffectsData().getName() + " (" + effect.getEffectsData().getId() + ")"));
                    }

                }
            }

        } else {
            throw new CommandPermissionException(Text.of("You don't have permission to use this command."));
        }
        return CommandResult.success();
    }
}
