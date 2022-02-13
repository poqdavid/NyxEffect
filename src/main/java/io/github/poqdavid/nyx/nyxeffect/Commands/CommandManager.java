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

import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {

    //public static CommandSpec NyxEffectFeetCmd;
/*    public static CommandSpec NyxEffectFireCmd;
    public static CommandSpec NyxEffectNoteCmd;
    public static CommandSpec NyxEffectMagicCmd;
    public static CommandSpec NyxEffectHeartCmd;
    public static CommandSpec NyxEffectSmokeCmd;
    public static CommandSpec NyxEffectPearlCmd;
    public static CommandSpec NyxEffectWitchCmd;*/

    public static CommandSpec helpCmd;
    public static CommandSpec checkCmd;
    public static CommandSpec disableCmd;

    public static CommandSpec mainCmd;
    public static CommandSpec taskCmd;
    public static CommandSpec reloadCmd;
    public static CommandSpec listCmd;
    private final Game game;
    private final NyxEffect ne;

    public CommandManager(Game game, NyxEffect ne) {
        this.game = game;
        this.ne = ne;
        registerCommands();
    }

    public void registerCommands() {
        helpCmd = CommandSpec.builder()
                .description(Text.of("/nyxeffect help"))
                .executor(new HelpCMD())
                .build();

        reloadCmd = CommandSpec.builder()
                .description(Text.of("/nyxeffect reload"))
                .executor(new ReloadCMD())
                .build();

        mainCmd = CommandSpec.builder()
                .description(Text.of("To enable/disable magic particles"))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("effect"))), GenericArguments.optional(GenericArguments.playerOrSource(Text.of("player"))))
                .executor(new MainCMD())
                .child(helpCmd, HelpCMD.getAlias())
                .child(reloadCmd, ReloadCMD.getAlias())
                .build();

        checkCmd = CommandSpec.builder()
                .description(Text.of("nyxeffectcheck your particles status - helpful to see what enabled"))
                .arguments(GenericArguments.firstParsing(GenericArguments.flags().buildWith(GenericArguments.firstParsing(GenericArguments.optional(GenericArguments.player(Text.of("target"))), GenericArguments.optional(GenericArguments.string(Text.of("targets")))))))
                .executor(new CheckCMD()).build();

        disableCmd = CommandSpec.builder()
                .description(Text.of("nyxeffectdisable disable your/other particles all of them"))
                .arguments(GenericArguments.firstParsing(GenericArguments.flags().buildWith(GenericArguments.firstParsing(GenericArguments.optional(GenericArguments.player(Text.of("target"))), GenericArguments.optional(GenericArguments.string(Text.of("targets")))))))
                .executor(new DisableCMD()).build();


        taskCmd = CommandSpec.builder()
                .description(Text.of("To restart/stop/start effect tasks"))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("task"))), GenericArguments.optional(GenericArguments.string(Text.of("action"))), GenericArguments.optional(GenericArguments.playerOrSource(Text.of("player"))))
                .executor(new TaskCMD()).build();

        listCmd = CommandSpec.builder()
                .description(Text.of("Shows you a list of effects"))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("option"))))
                .executor(new ListCMD()).build();

        Sponge.getCommandManager().register(ne, mainCmd, "nyxeffect");

        Sponge.getCommandManager().register(ne, checkCmd, "nyxeffectcheck");
        Sponge.getCommandManager().register(ne, disableCmd, "nyxeffectdisable");
        Sponge.getCommandManager().register(ne, taskCmd, "nyxeffecttask");
        Sponge.getCommandManager().register(ne, listCmd, "nyxeffectlist");
    }
}
