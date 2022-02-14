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

package io.github.poqdavid.nyx.nyxeffect.Listeners;

import io.github.poqdavid.nyx.nyxcore.Utils.CoreTools;
import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

@SuppressWarnings("unused")
public class NyxEffectListener {

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        if (!NyxEffect.HOLDEFFECTS) {
            final Player player = CoreTools.getPlayer(event.getCause()).get();
            Tools.UserTaskStart(player, "all", false);
        }
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        if (event.getCause().root() instanceof net.minecraft.entity.player.EntityPlayerMP) {
            if (!NyxEffect.HOLDEFFECTS) {
                final Player player = (Player) event.getCause().root();
                Tools.UserTaskRestart(player, "all", false);
            }
        }
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event) {
        if (!NyxEffect.HOLDEFFECTS) {
            final Player player = CoreTools.getPlayer(event.getCause()).get();
            Tools.UserTaskStop(player, "all", false);
        }
    }

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event) {
        if (!NyxEffect.HOLDEFFECTS) {
            if (event.getTargetEntity() instanceof Player) {
                final Player player = (Player) event.getTargetEntity();
                Tools.UserTaskStop(player, "all", false);
            }
        }
    }

}
