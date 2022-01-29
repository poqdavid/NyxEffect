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

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import io.github.poqdavid.nyx.nyxcore.Utils.CoreTools;
import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.ParticleDataList;
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.function.Consumer;

public class EffectTask implements Consumer<Task> {
    private final NyxEffect cf;
    private final Player player;
    private final List<ParticleDataList> pds_list;
    private Boolean taskran = false;
    private Task task;

    public EffectTask(Player player, NyxEffect cf, List<ParticleDataList> pds_list) {
        this.cf = cf;
        this.player = player;
        this.pds_list = pds_list;
    }

    @Override
    public void accept(Task task) {

        if (!this.taskran) {
            this.task = task;
            this.taskran = true;
            this.cf.getLogger().info("Starting Task: " + task.getName());
        }


        if (!this.cf.PlayerEvent.containsKey(this.player.getUniqueId())) {
            this.cf.getLogger().info("Stopping Task: " + task.getName());
            this.task.cancel();
        } else {
            this.Run(this.player, this.pds_list);
        }

    }

    private void Run(Player player, List<ParticleDataList> pdslist) {
        try {
            for (ParticleDataList pds : pdslist) {
                Vector3d loc;

                if (pds.getParticleEffect().getRelatedlocation()) {
                    final Vector3d rotation = player.getRotation();

                    final Quaterniond Qu = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ());

                    if (pds.getParticleEffect().getRelatedrotation()) {
                        loc = CoreTools.GetLocation(player, Qu, Qu.rotate(pds.getVector3d())).getPosition();
                    } else {
                        loc = CoreTools.GetLocation(player, Qu, pds.getVector3d()).getPosition();
                    }
                } else {
                    if (pds.getParticleEffect().getRelatedrotation()) {
                        final Vector3d rotation = player.getRotation();
                        final Quaterniond Qu = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ());
                        loc = CoreTools.GetLocation(player, Qu, Qu.rotate(pds.getVector3d())).getPosition();
                    } else {
                        loc = player.getLocation().getPosition().add(pds.getVector3d());
                    }
                }

                if (pds.getParticleEffect().getEvent().equalsIgnoreCase("onmove")) {
                    if (NyxEffect.getInstance().PlayerEvent.get(player.getUniqueId()).getOnmove()) {
                        spawnEffects(player, pds.getParticleEffect().getType(), pds.getParticleEffect().getData(), loc, pds.getParticleEffect().getcleartime());
                    }
                }

                if (pds.getParticleEffect().getEvent().equalsIgnoreCase("onstop")) {
                    if (!NyxEffect.getInstance().PlayerEvent.get(player.getUniqueId()).getOnmove()) {
                        spawnEffects(player, pds.getParticleEffect().getType(), pds.getParticleEffect().getData(), loc, pds.getParticleEffect().getcleartime());
                    }
                }

                if (pds.getParticleEffect().getEvent().equalsIgnoreCase("always")) {
                    spawnEffects(player, pds.getParticleEffect().getType(), pds.getParticleEffect().getData(), loc, pds.getParticleEffect().getcleartime());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void spawnEffects(Player player, String effecttype, String effectdata, Vector3d loc, long cleartime) {

        if (effecttype.equalsIgnoreCase("timer")) {
            try {
                if (effectdata.contains("delay:")) {
                    final String[] data = effectdata.replace(" ", "").split(":");
                    Thread.sleep(Integer.parseInt(data[1]));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (effecttype.equalsIgnoreCase("particle")) {
            if (loc != null) {
                final String data = effectdata;
                String id = "NONE";
                if (data.contains(";")) {
                    final String[] datas = data.replace(" ", "").split(";");

                    String[] colorData = new String[3];
                    colorData[0] = "NONE";
                    Color colors = Color.BLACK;
                    for (String datax : datas) {
                        if (datax.contains("minecraft:")) {
                            id = datax;
                        }

                        if (datax.contains("Color")) {
                            colorData = datax.replace("Color{", "").replace("}", "").replace("red=", "").replace("green=", "").replace("blue=", "").split(",").clone();

                            final int red = Integer.parseInt(colorData[0]);
                            final int green = Integer.parseInt(colorData[1]);
                            final int blue = Integer.parseInt(colorData[2]);
                            colors = Color.ofRgb(red, green, blue);
                        }

                    }

                    if (!colorData[0].equals("NONE")) {

                        player.getWorld().spawnParticles(ParticleEffect.builder().type(CoreTools.GetParticleType(id)).option(ParticleOptions.COLOR, colors).build(), loc);
                    } else {
                        player.getWorld().spawnParticles(ParticleEffect.builder().type(CoreTools.GetParticleType(id)).build(), loc);
                    }

                } else {
                    id = data;
                    player.getWorld().spawnParticles(ParticleEffect.builder().type(CoreTools.GetParticleType(id)).build(), loc);
                }
            }
        }

        if (effecttype.equalsIgnoreCase("block")) {
            if (player.isOnGround()) {
                try {
                    Location<World> locs = new Location<World>(player.getLocation().getExtent(), new Vector3d(loc.getX(), player.getLocation().getY(), loc.getZ()));
                    if (locs != null) {
                        Location<World> blockOn = Tools.getLocBelow(player, locs);
                        if (blockOn != null) {                //blockOn.getBlockType() != BlockTypes.AIR && blockOn.getBlockType() != BlockTypes.WATER
                            if (!NyxEffect.getInstance().restricted_blocks.contains(blockOn.getBlockType())) {
                                final Vector3i loci = new Vector3i(blockOn.getX(), blockOn.getY(), blockOn.getZ());

                                player.getWorld().sendBlockChange(loci, BlockState.builder().from(CoreTools.GetBlock(effectdata)).build());
                                Tools.ClearBlockTask(player, loci, cleartime);

                            }
                        }
                    }
                } catch (Exception e) {
                }


            }
        }
    }
}