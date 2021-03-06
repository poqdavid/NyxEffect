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

package io.github.poqdavid.nyx.nyxeffect.Tasks;

import com.flowpowered.math.vector.Vector3d;
import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.UUID;
import java.util.function.Consumer;

public class MovementDetectionTask implements Consumer<Task> {
    private final UUID uuid;
    public Boolean taskStop = false;
    private Player player;
    private Boolean taskran = false;
    private Task task;
    private Vector3d loc1;
    private Vector3d loc2;

    private Vector3d rot1;
    private Vector3d rot2;

    public MovementDetectionTask(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void accept(Task task) {
        this.player = Sponge.getServer().getPlayer(uuid).orElse(player);

        if (!this.taskran) {
            this.task = task;
            this.taskran = true;
            NyxEffect.getInstance().getLogger().info("Starting Task: " + task.getName());
        }

        if (NyxEffect.getInstance().PlayerEvent.containsKey(this.player.getUniqueId()) && player.isOnline() && !this.taskStop) {
            this.Run();
        } else {
            NyxEffect.getInstance().getLogger().info("Stopping Task: " + task.getName());
            this.task.cancel();
        }
    }

    private void Run() {
        try {
            this.loc1 = player.getLocation().getPosition();
            this.rot1 = player.getHeadRotation();

            if ((!this.loc1.equals(this.loc2)) || (!this.rot1.equals(this.rot2))) {
                NyxEffect.getInstance().PlayerEvent.get(player.getUniqueId()).setOnmove(true);
                this.loc2 = this.loc1;
                this.rot2 = this.rot1;
            } else {
                NyxEffect.getInstance().PlayerEvent.get(player.getUniqueId()).setOnmove(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
