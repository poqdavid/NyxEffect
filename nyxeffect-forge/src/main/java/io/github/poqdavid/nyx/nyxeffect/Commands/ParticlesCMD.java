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

import akka.io.Udp;
import com.google.common.collect.Lists;
import io.github.poqdavid.nyx.nyxcore.Permissions.EffectPermission;
import io.github.poqdavid.nyx.nyxcore.Utils.Commands.Sender;
import io.github.poqdavid.nyx.nyxcore.Utils.Forge.TextComponentString;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ParticlesCMD extends CommandBase {
    private final List<String> aliases;

    public ParticlesCMD() {
        aliases = Lists.newArrayList("particles");
    }

    @Override
    @Nonnull
    public String getName() {
        return "particles";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "particles";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        if (Sender.hasPermission(sender, EffectPermission.COMMAND_PARTICLES)) {
            sender.sendMessage(TextComponentString.from(TextFormatting.RED, "======================= ", TextFormatting.GOLD, "Forge", TextFormatting.RED, " ========================"));
            for (String pt : EnumParticleTypes.getParticleNames()) {
                sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "minecraft", TextFormatting.RED, ":", TextFormatting.GOLD, pt));
            }

            sender.sendMessage(TextComponentString.from(TextFormatting.RED, " "));
            sender.sendMessage(TextComponentString.from(TextFormatting.RED, "===================== ", TextFormatting.GOLD, "Pixelmon", TextFormatting.RED, " ======================="));
            sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "pixelmon", TextFormatting.RED, ":", TextFormatting.GOLD, "shiny"));
            sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "pixelmon", TextFormatting.RED, ":", TextFormatting.GOLD, "bluemagic"));
            sender.sendMessage(TextComponentString.from(TextFormatting.GOLD, "pixelmon", TextFormatting.RED, ":", TextFormatting.GOLD, "electric"));
        } else {
            throw new CommandException("NyxEffect.NoPermission");
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return Sender.hasPermission(sender, EffectPermission.COMMAND_PARTICLES);
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (targetPos != null) {
            sender.sendMessage(TextComponentString.from("ArgLength: " + args.length, " BlockPos: ", targetPos.toString()));
        }
        else {

            sender.sendMessage(TextComponentString.from("ArgLength: " + args.length, " BlockPos: N/A"));
        }

        List<String> list = new ArrayList<>();
        list.add("test1");
        list.add("test2");
        return list;
    }
}


