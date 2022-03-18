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
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.net.MalformedURLException;
import java.net.URL;


public class HelpCMD implements CommandExecutor {

    public static String[] getAlias() {
        return new String[]{"help", "?"};
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src.hasPermission(EffectPermission.COMMAND_HELP)) {
            PaginationList.Builder builder = PaginationList.builder();
            URL url1 = null;
            try {
                url1 = new URL("https://github.com/poqdavid/NyxEffect/");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Text h1 = Text.builder("Author: ").color(TextColors.BLUE).style(TextStyles.BOLD).build();
            Text h2 = Text.builder("POQDavid").color(TextColors.GRAY).style(TextStyles.BOLD).onHover(TextActions.showText(Text.of(url1.toString()))).onClick(TextActions.openUrl(url1)).build();

            builder.title(Text.of("§9Nyx§5Effect §7- §6V" + NyxEffect.getInstance().getVersion()))
                    .header(Text.of(h1, h2))
                    .contents(
                            Text.of(TextColors.BLUE, TextStyles.ITALIC, ""),
                            Text.of(TextColors.GREEN, TextStyles.BOLD, "Commands"),
                            Text.of("§6- /§7nyxeffect effect §6<§7effect§6> §6<§7player§6>"),
                            Text.of("§6- /§7nyxeffect list §6<§7option(more / all)§6>"),
                            Text.of("§6- /§7nyxeffect check §6<§7user§6>"),
                            Text.of("§6- /§7nyxeffect disable §6<§7user§6>")
                    )
                    .padding(Text.of("="))
                    .sendTo(src);
        } else {
            throw new CommandPermissionException(Text.of("You don't have permission to use this command."));
        }

        return CommandResult.success();
    }

}


