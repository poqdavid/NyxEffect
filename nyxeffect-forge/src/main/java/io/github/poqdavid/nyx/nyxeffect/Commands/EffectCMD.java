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
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.EffectsData;
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


public class EffectCMD extends CommandBase {
    private final List<String> aliases;

    public EffectCMD() {
        aliases = Lists.newArrayList("effect");
    }

    @Override
    @Nonnull
    public String getName() {
        return "effect";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "effect";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        NyxEntityPlayerMP player_args = null;

        if (args.length == 0) {
            NyxEffect.help.sendHelp(sender.getCommandSenderEntity());
            throw new CommandException("NyxEffect.EffectCMD.Effect.Null");
        }

        String effect = args[0];

        if (args.length == 2){
            player_args = NyxEntityPlayerMP.from(args[1]);
        }

        if (sender instanceof EntityPlayerMP) {
            NyxEntityPlayerMP player_cmd_src = Sender.toNyxEntityPlayerMP(sender);

            EffectsData effectOBJ;
            try {
                effectOBJ = Tools.GetEffect(effect);
            } catch (Exception e) {
                throw new CommandException("NyxEffect.EffectCMD.Effect.NotAvailable", effect);
            }

            final String effectperm = EffectPermission.EFFECTS + "." + effectOBJ.getId();

            if (player_args == null) {
                if (player_cmd_src.hasPermission(EffectPermission.COMMAND_EFFECT)) {

                    if (!player_cmd_src.hasPermission(effectperm)) {
                        throw new CommandException("NyxEffect.NoPermission");
                    }

                    NyxEffect.getInstance().ParticleToggle(player_cmd_src, effectOBJ);
                } else {
                    throw new CommandException("NyxEffect.NoPermission");
                }
            }

            if (player_cmd_src.getUniqueId().equals(player_args.getUniqueId())) {
                if (player_cmd_src.hasPermission(EffectPermission.COMMAND_EFFECT)) {

                    if (!player_cmd_src.hasPermission(effectperm)) {
                        throw new CommandException("NyxEffect.NoPermission");
                    }

                    NyxEffect.getInstance().ParticleToggle(player_cmd_src, effectOBJ);
                } else {
                    throw new CommandException("NyxEffect.NoPermission");
                }
            } else {
                if (Sender.hasPermission(sender, EffectPermission.COMMAND_EFFECT_OTHER)) {

                    if (!player_args.hasPermission(effectperm)) {
                        sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, TextFormatting.BOLD, "The user " + player_args.getName() + " doesn't have the permission to use this effect! (" + effectperm + ")"));
                    }

                    NyxEffect.getInstance().ParticleToggle(Sender.toNyxEntityPlayerMP(sender), player_args, effectOBJ);
                } else {
                    throw new CommandException("NyxEffect.NoPermission");
                }
            }
        } else {
            if (effect == null) {
                throw new CommandException("NyxEffect.EffectCMD.Effect.Null");
            }

            EffectsData effectOBJ;
            try {
                effectOBJ = Tools.GetEffect(effect);
            } catch (Exception e) {
                throw new CommandException("NyxEffect.EffectCMD.Effect.NotAvailable", effect);
            }

            NyxEffect.getInstance().ParticleToggle(Sender.toNyxEntityPlayerMP(sender), player_args, effectOBJ);
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return Sender.hasPermission(sender, EffectPermission.COMMAND_EFFECT);
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1){
            return NyxEffect.EffectCMDs;
        } else if (args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}


