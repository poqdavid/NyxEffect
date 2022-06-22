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

import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@SuppressWarnings("unused")
public class NyxEffectListener {

    @SubscribeEvent
    public static void PlayerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (!NyxEffect.HOLDEFFECTS) {
            Tools.UserTaskStart(event.player, "all", null, false);
        }
    }

    public static void PlayerLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!NyxEffect.HOLDEFFECTS) {
            Tools.UserTaskStop(event.player, "all", null, false);
        }

    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (!NyxEffect.HOLDEFFECTS) {
                Tools.UserTaskStop(player, "all", null, false);
            }
        }

    }

    @SubscribeEvent
    public void livingSpawn(LivingSpawnEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            if (!NyxEffect.HOLDEFFECTS) {
                EntityPlayer player = (EntityPlayer) event.getEntity();
                Tools.UserTaskRestart(player, "all", null, false);
            }
        }
    }

}
