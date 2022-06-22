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
import io.github.poqdavid.nyx.nyxcore.Utils.Forge.ClickText;
import io.github.poqdavid.nyx.nyxcore.Utils.Forge.HoverText;
import io.github.poqdavid.nyx.nyxcore.Utils.Forge.NyxEntityPlayerMP;
import io.github.poqdavid.nyx.nyxcore.Utils.Forge.TextComponentString;
import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.EffectsData;
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
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


public class CheckCMD extends CommandBase {

    private final List<String> aliases;

    public CheckCMD() {
        aliases = Lists.newArrayList("check");
    }

    @Override
    @Nonnull
    public String getName() {
        return "check";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "check";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        NyxEntityPlayerMP target = null;
        String targets = null;

        if (args.length != 0) {
            if (args.length == 1) {
                target = (NyxEntityPlayerMP) server.getPlayerList().getPlayerByUsername(args[0]);
            }

            if (args.length == 2) {
                targets = args[1];
            }
        }

        if (target == null && targets == null) {
            NyxEntityPlayerMP player = Sender.toNyxEntityPlayerMP(sender);
            if (player.hasPermission(EffectPermission.COMMAND_CHECK)) {
                final List<String> pd = NyxEffect.getInstance().UserParticlesLIST.get(player.getUniqueId().toString());
                player.sendMessage(TextComponentString.from(TextFormatting.AQUA, "Your ", TextFormatting.YELLOW, "Active NyxEffect modes", TextFormatting.GRAY, ":"));

                if (pd == null) {
                    player.sendMessage(TextComponentString.from(TextFormatting.GOLD, "NONE"));
                } else {
                    for (String effect : pd) {
                        try {
                            final EffectsData ed = Tools.GetEffect(effect);
                            final TextComponentString texts = TextComponentString.text(ed.getName() + " (" + ed.getId() + ")").style(TextFormatting.GRAY).hover(HoverText.from(TextComponentString.from("/nyxeffect effect " + ed.getId()))).click(ClickText.CMDfrom("/nyxeffect effect " + ed.getId())).create();
                            player.sendMessage(TextComponentString.from(TextFormatting.GOLD, "- ", texts));
                        } catch (Exception ignored) {

                        }
                    }
                }
            } else {
                throw new CommandException("NyxEffect.NoPermission");
            }
        } else if (target != null) {
            if (Sender.hasPermission(sender, EffectPermission.COMMAND_CHECKOTHERS)) {
                final List<String> pd = NyxEffect.getInstance().UserParticlesLIST.get(target.getUniqueId().toString());
                sender.sendMessage(TextComponentString.from(TextFormatting.YELLOW, target.getName(), "'s", TextFormatting.AQUA, " active NyxEffect modes:"));
                if (pd == null) {
                    sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "NONE"));
                } else {
                    for (String effect : pd) {
                        try {
                            final EffectsData ed = Tools.GetEffect(effect);
                            final TextComponentString texts = TextComponentString.text(ed.getName() + " (" + ed.getId() + ")").style(TextFormatting.GRAY).hover(HoverText.from(TextComponentString.from("/nyxeffect effect " + ed.getId() + " " + target.getName()))).click(ClickText.CMDfrom("/nyxeffect effect " + ed.getId() + " " + target.getName())).create();
                            sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "- ", texts));
                        } catch (Exception ignored) {

                        }
                    }
                }
            } else {
                throw new CommandException("NyxEffect.NoPermission");
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return Sender.hasPermission(sender, EffectPermission.COMMAND_CHECK);
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }
}
