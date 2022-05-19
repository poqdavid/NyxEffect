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
import com.pixelmonmod.pixelmon.api.world.ParticleArcaneryDispatcher;
import com.pixelmonmod.pixelmon.client.particle.Particles;
import io.github.poqdavid.nyx.nyxcore.Utils.CoreTools;
import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.ParticleDataList;
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class EffectTask implements Consumer<Task> {
    private final List<ParticleDataList> pds_list;
    private final UUID uuid;
    public Boolean taskStop = false;
    private Task task;
    private Player player;
    private Vector3d rotation = new Vector3d(0, 0, 0);
    private Boolean taskRan = false;

    public EffectTask(UUID uuid, List<ParticleDataList> pds_list) {
        this.uuid = uuid;
        this.pds_list = pds_list;
        this.taskStop = false;
    }

    @Override
    public void accept(Task task) {
        this.player = Sponge.getServer().getPlayer(uuid).orElse(player);

        if (!this.taskRan) {
            this.task = task;
            this.taskRan = true;
            NyxEffect.getInstance().getLogger().info("Starting Task: " + task.getName());
        }

        if (NyxEffect.getInstance().PlayerEvent.containsKey(this.player.getUniqueId()) && player.isOnline() && !this.taskStop) {
            this.Run(this.pds_list);
        } else {
            NyxEffect.getInstance().getLogger().info("Stopping Task: " + task.getName());
            this.task.cancel();
        }
    }

    private void Run(List<ParticleDataList> pdslist) {
        try {
            for (ParticleDataList pds : pdslist) {
                Vector3d loc;
                Vector3d playerR = player.getRotation();

                this.rotation = new Vector3d(0, playerR.getY(), playerR.getZ());

                if (pds.getParticleEffect().getRelatedlocation()) {
                    final Quaterniond Qu = Quaterniond.fromAxesAnglesDeg(this.rotation.getX(), -this.rotation.getY(), this.rotation.getZ());

                    if (pds.getParticleEffect().getRelatedrotation()) {
                        loc = CoreTools.GetLocation(player, Qu, Qu.rotate(pds.getVector3d())).getPosition();
                    } else {
                        loc = CoreTools.GetLocation(player, Qu, pds.getVector3d()).getPosition();
                    }
                } else {
                    if (pds.getParticleEffect().getRelatedrotation()) {
                        final Quaterniond Qu = Quaterniond.fromAxesAnglesDeg(this.rotation.getX(), -this.rotation.getY(), this.rotation.getZ());
                        loc = CoreTools.GetLocation(player, Qu, Qu.rotate(pds.getVector3d())).getPosition();
                    } else {
                        loc = player.getLocation().getPosition().add(pds.getVector3d());
                    }
                }

                if (pds.getParticleEffect().getEvent().equalsIgnoreCase("onmove")) {
                    if (NyxEffect.getInstance().PlayerEvent.get(player.getUniqueId()).getOnmove()) {
                        spawnEffects(player, pds.getParticleEffect().getType(), pds.getParticleEffect().getData(), loc, pds.getParticleEffect().getcleartime(), pds.getParticleEffect().getNumberOfParticles(), pds.getParticleEffect().getParticleSpeed());
                    }
                }

                if (pds.getParticleEffect().getEvent().equalsIgnoreCase("onstop")) {
                    if (!NyxEffect.getInstance().PlayerEvent.get(player.getUniqueId()).getOnmove()) {
                        spawnEffects(player, pds.getParticleEffect().getType(), pds.getParticleEffect().getData(), loc, pds.getParticleEffect().getcleartime(), pds.getParticleEffect().getNumberOfParticles(), pds.getParticleEffect().getParticleSpeed());
                    }
                }

                if (pds.getParticleEffect().getEvent().equalsIgnoreCase("always")) {
                    spawnEffects(player, pds.getParticleEffect().getType(), pds.getParticleEffect().getData(), loc, pds.getParticleEffect().getcleartime(), pds.getParticleEffect().getNumberOfParticles(), pds.getParticleEffect().getParticleSpeed());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void spawnEffects(Player player, String effecttype, String effectdata, Vector3d loc, long cleartime, int numberofparticles, double particleSpeed) {
        EntityPlayerMP ePlayer = (EntityPlayerMP) player;
        if (!player.get(Keys.VANISH).filter(value -> value).isPresent()) {
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
                    int worldID = ePlayer.dimension;
                    String id = "NONE";

                    String[] colorData = new String[3];
                    String[] offsetData = new String[3];
                    String[] pixelmonData = new String[4];
                    String[] elecData = new String[10];
                    colorData[0] = "NONE";
                    offsetData[0] = "NONE";
                    pixelmonData[0] = "NONE";
                    elecData[0] = "NONE";

                    Color colors = Color.BLACK;
                    Vector3d offset = new Vector3d(0.0, 0.0, 0.0);
                    Vector3d motion = new Vector3d(0.0, 0.0, 0.0);
                    Object[] elecArgs = new Object[]{1, true, 1.0f, 1.0f, 1.0f, 1.0f, 238.0f, 75.0f, 43.0f, 1.0f};
                    float size = 1;

                    if (data.contains(";")) {
                        final String[] datas = data.replace(" ", "").split(";");

                        for (String datax : datas) {
                            if (datax.contains("minecraft:")) {
                                id = datax;
                            }

                            if (datax.contains("sponge:")) {
                                id = datax;
                            }

                            if (datax.contains("pixelmon:")) {
                                id = datax;
                            }

                            if (datax.toLowerCase().contains("color")) {
                                colorData = datax.toLowerCase().replace("color{", "").replace("}", "").replace("red=", "").replace("green=", "").replace("blue=", "").split(",").clone();

                                final int red = Integer.parseInt(colorData[0]);
                                final int green = Integer.parseInt(colorData[1]);
                                final int blue = Integer.parseInt(colorData[2]);
                                colors = Color.ofRgb(red, green, blue);
                            }

                            if (datax.toLowerCase().contains("offset")) {
                                offsetData = datax.toLowerCase().replace("offset{", "").replace("}", "").replace("x=", "").replace("y=", "").replace("z=", "").split(",").clone();

                                offset = new Vector3d(Double.parseDouble(offsetData[0]), Double.parseDouble(offsetData[1]), Double.parseDouble(offsetData[2]));
                            }

                            if (datax.toLowerCase().contains("pixelmonoptions")) {
                                pixelmonData = datax.toLowerCase().replace("pixelmonoptions{", "").replace("}", "").replace("motionx=", "").replace("motiony=", "").replace("motionz=", "").replace("floatsize=", "").split(",").clone();

                                motion = new Vector3d(Double.parseDouble(pixelmonData[0]), Double.parseDouble(pixelmonData[1]), Double.parseDouble(pixelmonData[2]));
                                size = Float.parseFloat(pixelmonData[3]);
                            }

                            if (datax.toLowerCase().contains("electricoptions")) {
                                elecData = datax.toLowerCase().replace("electricoptions{", "").replace("}", "").replace("age=", "").replace("parent=", "").replace("pitch=", "").replace("yaw=", "")
                                        .replace("velocity=", "").replace("innaccuracy=", "").replace("r=", "").replace("g=", "").replace("b=", "").replace("a=", "").split(",").clone();

                                elecArgs[0] = Integer.parseInt(elecData[0]); // int age
                                elecArgs[1] = Boolean.parseBoolean(elecData[1]); // boolean parent
                                elecArgs[2] = Float.parseFloat(elecData[2]); // float pitch
                                elecArgs[3] = Float.parseFloat(elecData[3]); // float yaw
                                elecArgs[4] = Float.parseFloat(elecData[4]); // float velocity
                                elecArgs[5] = Float.parseFloat(elecData[5]); // float innaccuracy
                                elecArgs[6] = Float.parseFloat(elecData[6]); // float r
                                elecArgs[7] = Float.parseFloat(elecData[7]); // float g
                                elecArgs[8] = Float.parseFloat(elecData[8]); // float b
                                elecArgs[9] = Float.parseFloat(elecData[9]); // float a / 1.0f
                            }
                        }
                    } else {
                        id = data;
                    }

                    if (id.contains("minecraft:")) {
                        if (id.equals("minecraft:ender_teleport")) {
                            ePlayer.getServerWorld().playEvent(2003, new BlockPos(loc.getX(), loc.getY(), loc.getZ()), 0);
                        } else {
                            EnumParticleTypes enumParticleTypes = EnumParticleTypes.getByName(id.replace("minecraft:", ""));
                            if (enumParticleTypes != null) {
                                ePlayer.getServerWorld().spawnParticle(enumParticleTypes, loc.getX(), loc.getY(), loc.getZ(), numberofparticles, offset.getX(), offset.getY(), offset.getZ(), particleSpeed);
                            }
                        }
                    }

                    if (id.contains("sponge:")) {
                        if (!colorData[0].equals("NONE")) {
                            player.getWorld().spawnParticles(ParticleEffect.builder().type(CoreTools.GetParticleType(id.replace("sponge:", "minecraft:"))).option(ParticleOptions.COLOR, colors).build(), loc);
                        } else {
                            player.getWorld().spawnParticles(ParticleEffect.builder().type(CoreTools.GetParticleType(id.replace("sponge:", "minecraft:"))).build(), loc);
                        }
                    }

                    if (id.contains("pixelmon:") && NyxEffect.getInstance().pixelmon) {

                        //Thanks to Varijon @ GitHub https://github.com/Varijon for helping me with spawning particles using Forge and Pixelmon also allowing me to use part of her code.
                        if (id.equalsIgnoreCase("pixelmon:shiny")) {
                            ArrayList<Double> argList1 = new ArrayList<Double>();
                            argList1.add(offset.getX());
                            argList1.add(offset.getY());
                            argList1.add(offset.getZ());
                            for (int x = numberofparticles; x > 0; x--) {
                                ParticleArcaneryDispatcher.dispatchToDimension(worldID, 50, loc.getX(), loc.getY(), loc.getZ(), motion.getX(), motion.getY(), motion.getZ(), size, Particles.Shiny, argList1.toArray());
                            }
                        }

                        if (id.equalsIgnoreCase("pixelmon:bluemagic")) {
                            for (int x = numberofparticles; x > 0; x--) {
                                ParticleArcaneryDispatcher.dispatchToDimension(worldID, 50, loc.getX(), loc.getY(), loc.getZ(), motion.getX(), motion.getY(), motion.getZ(), size, Particles.BlueMagic);
                            }
                        }

                        if (id.equalsIgnoreCase("pixelmon:electric")) {
                            for (int x = numberofparticles; x > 0; x--) {
                                ParticleArcaneryDispatcher.dispatchToDimension(worldID, 50, loc.getX(), loc.getY(), loc.getZ(), motion.getX(), motion.getY(), motion.getZ(), size, Particles.Electric, elecArgs);
                            }
                        }

                    }
                }
            }

            if (effecttype.equalsIgnoreCase("block")) {
                if (player.isOnGround() && player.isLoaded()) {
                    try {
                        Location<World> locs = new Location<World>(player.getLocation().getExtent(), new Vector3d(loc.getX(), player.getLocation().getY(), loc.getZ()));
                        if (locs != null) {
                            Location<World> blockOn = Tools.getLocBelow(player, locs);
                            if (blockOn != null) {
                                if (!NyxEffect.getInstance().restricted_blocks.contains(blockOn.getBlockType())) {
                                    final Vector3i loci = new Vector3i(blockOn.getX(), blockOn.getY(), blockOn.getZ());

                                    player.getWorld().sendBlockChange(loci, BlockState.builder().from(CoreTools.GetBlock(effectdata)).build());
                                    Tools.ClearBlockTask(player, loci, cleartime);

                                }
                            }
                        }
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
        }
    }
}