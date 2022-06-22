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

import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;
import java.util.function.Consumer;

public class MovementDetectionTask extends Thread {
    private final UUID uuid;
    public Boolean taskStop = false;
    private EntityPlayerMP player;
    private Boolean taskran = false;

    private Thread thread;

    private BlockPos loc1;
    private BlockPos loc2;

    private Vec2f rot1;
    private Vec2f rot2;

    public MovementDetectionTask(UUID uuid, int intervals) {
        this.uuid = uuid;
        this.player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid);
    }

    public void run() {
        if (!this.taskran) {
            this.taskran = true;
            this.thread = Thread.currentThread();
            NyxEffect.getInstance().getLogger().info("Starting Task: " + this.thread.getName());
        }

        if (NyxEffect.getInstance().PlayerEvent.containsKey(this.player.getUniqueID()) && Tools.isPlayerOnline(this.player.getUniqueID()) && !this.taskStop) {
            this.Run();
        } else {
           NyxEffect.getInstance().getLogger().info("Stopping Task: " + this.thread.getName());
            this.thread.interrupt();
            //this.task.cancel();
        }
    }

    private void Run() {
        try {
            this.loc1 = player.getPosition();
            this.rot1 = player.getPitchYaw();

            if ((!this.loc1.equals(this.loc2)) || (!this.rot1.equals(this.rot2))) {
                NyxEffect.getInstance().PlayerEvent.get(player.getUniqueID()).setOnmove(true);
                this.loc2 = this.loc1;
                this.rot2 = this.rot1;
            } else {
                NyxEffect.getInstance().PlayerEvent.get(player.getUniqueID()).setOnmove(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
