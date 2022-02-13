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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;

@SuppressWarnings("unused")
public class NyxEffectListener {

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        if (!NyxEffect.HOLDEFFECTS) {
            final Player player = CoreTools.getPlayer(event.getCause()).get();
            NyxEffect.getInstance().StartPlayerrEffcts(player);
        }
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event) {
        if (!NyxEffect.HOLDEFFECTS) {
            final Player player = CoreTools.getPlayer(event.getCause()).get();

            for (Task task : Sponge.getScheduler().getScheduledTasks(NyxEffect.getInstance())) {
                if (task.getName().contains(player.getUniqueId().toString())) {
                    NyxEffect.getInstance().getLogger().info("Stopping Task: " + task.getName());
                    task.cancel();
                }
            }
        }
    }
}
