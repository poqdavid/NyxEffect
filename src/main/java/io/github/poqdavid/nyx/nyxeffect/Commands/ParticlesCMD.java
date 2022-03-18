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
import net.minecraft.util.EnumParticleTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.lang.reflect.Field;


public class ParticlesCMD implements CommandExecutor {

    public static String[] getAlias() {
        return new String[]{"particles"};
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src.hasPermission(EffectPermission.COMMAND_PARTICLES)) {
            src.sendMessage(Text.of(TextColors.RED, "====================== ", TextColors.GOLD, "Sponge", TextColors.RED, " ========================"));
            for (Field pt : ParticleTypes.class.getDeclaredFields()) {
                ParticleType pts = null;
                try {
                    pts = (ParticleType) pt.get(ParticleType.class);
                    src.sendMessage(Text.of(TextColors.GOLD, "sponge", TextColors.RED, ":", TextColors.GOLD, pts.getId().replace("minecraft:", "")));
                } catch (IllegalAccessException e) {
                    // ignore
                }
            }

            src.sendMessage(Text.of(TextColors.RED, " "));
            src.sendMessage(Text.of(TextColors.RED, "======================= ", TextColors.GOLD, "Forge", TextColors.RED, " ========================"));
            for (String pt : EnumParticleTypes.getParticleNames()) {
                src.sendMessage(Text.of(TextColors.GOLD, "minecraft", TextColors.RED, ":", TextColors.GOLD, pt));
            }

            src.sendMessage(Text.of(TextColors.RED, " "));
            src.sendMessage(Text.of(TextColors.RED, "===================== ", TextColors.GOLD, "Pixelmon", TextColors.RED, " ======================="));
            src.sendMessage(Text.of(TextColors.GOLD, "pixelmon", TextColors.RED, ":", TextColors.GOLD, "shiny"));
            src.sendMessage(Text.of(TextColors.GOLD, "pixelmon", TextColors.RED, ":", TextColors.GOLD, "bluemagic"));
            src.sendMessage(Text.of(TextColors.GOLD, "pixelmon", TextColors.RED, ":", TextColors.GOLD, "electric"));
        } else {
            throw new CommandPermissionException(Text.of("You don't have permission to use this command."));
        }

        return CommandResult.success();
    }
}


