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
import io.github.poqdavid.nyx.nyxeffect.Tasks.EffectTask;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.ParticlesData;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.PlayerData;
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class NyxEffectListener {

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        final Player player = CoreTools.getPlayer(event.getCause()).get();

        NyxEffect.getInstance().PlayerEvent.putIfAbsent(player.getUniqueId(), new PlayerData(player.getName(), false));

        if (NyxEffect.getInstance().UserParticlesLIST != null) {
            if (NyxEffect.getInstance().UserParticlesLIST.get(player.getUniqueId().toString()) != null) {
                if (!NyxEffect.getInstance().UserParticlesLIST.get(player.getUniqueId().toString()).contains("NONE")) {
                    Tools.AddMovementTask(player);
                }
            }
        }


        if (NyxEffect.getInstance().UserParticlesLIST.containsKey(player.getUniqueId().toString())) {
            final List<String> pd = NyxEffect.getInstance().UserParticlesLIST.get(player.getUniqueId().toString());
            for (String effect : pd) {
                for (ParticlesData pdent : NyxEffect.getInstance().ParticlesLIST) {

                    if (pdent.getEffectsData().getId().equalsIgnoreCase(effect)) {
                        Task.builder().execute(new EffectTask(player, NyxEffect.getInstance(), pdent.getEffectsData().getParticleDataList()))
                                .async()
                                .interval(pdent.getEffectsData().getInterval(), TimeUnit.MILLISECONDS)
                                .name("TaskOwner: " + player.getUniqueId() + " Effect: " + pdent.getEffectsData().getId().toLowerCase()).submit(NyxEffect.getInstance());

                    }

                }

            }

        }

    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event) {
        final Player player = CoreTools.getPlayer(event.getCause()).get();

        for (Task task : Sponge.getScheduler().getScheduledTasks(NyxEffect.getInstance())) {
            if (task.getName().contains(player.getUniqueId().toString())) {
                NyxEffect.getInstance().getLogger().info("Stopping Task: " + task.getName());
                task.cancel();
            }
        }
    }
}
