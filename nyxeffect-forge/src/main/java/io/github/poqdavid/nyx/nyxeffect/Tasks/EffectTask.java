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


import io.github.poqdavid.nyx.nyxcore.shaded.flowpowered.math.imaginary.Quaterniond;
import io.github.poqdavid.nyx.nyxcore.shaded.flowpowered.math.vector.Vector3d;
import com.pixelmonmod.pixelmon.api.world.ParticleArcaneryDispatcher;
import com.pixelmonmod.pixelmon.client.particle.Particles;
import io.github.poqdavid.nyx.nyxcore.Utils.CoreTools;
import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.ParticleDataList;
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.core.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static net.minecraft.block.BlockColored.COLOR;

public class EffectTask extends Thread {
    private List<ParticleDataList> pds_list;
    private UUID uuid;
    public Boolean taskStop = false;
    private Thread thread;
    private EntityPlayerMP player;
    private Vector3d rotation = new Vector3d(0, 0, 0);
    private Boolean taskRan = false;

    public EffectTask(UUID uuid, List<ParticleDataList> pds_list, int intervals) {
        this.uuid = uuid;
        this.pds_list = pds_list;
        this.taskStop = false;
        this.player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid);

    }

    public void run() {
        if (!this.taskRan) {
            this.thread = Thread.currentThread();
            this.taskRan = true;
            NyxEffect.getInstance().getLogger().info("Starting Task: " + this.thread.getName());
        }


        if (NyxEffect.getInstance().PlayerEvent.containsKey(this.player.getUniqueID()) && Tools.isPlayerOnline(this.player.getUniqueID()) && !this.taskStop) {
            this.Run(this.pds_list);
        } else {
            NyxEffect.getInstance().getLogger().info("Stopping Task: " + this.getName());
        }
    }

    private void Run(List<ParticleDataList> pdslist) {
        try {
            for (ParticleDataList pds : pdslist) {
                Vec3d loc;

                Vec3d playerR = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYaw);

                this.rotation = new Vector3d(0, playerR.y, playerR.z);

                if (pds.getParticleEffect().getRelatedlocation()) {
                    Quaterniond Qu = Quaterniond.fromAxesAnglesDeg(this.rotation.getX(), -this.rotation.getY(), this.rotation.getZ());

                    if (pds.getParticleEffect().getRelatedrotation()) {
                        loc = CoreTools.GetLocationV(player, Qu, Qu.rotate(pds.getVector3d()));
                    } else {
                        loc = CoreTools.GetLocationV(player, Qu, pds.getVec3d());
                    }
                } else {
                    if (pds.getParticleEffect().getRelatedrotation()) {
                        final Quaterniond Qu = Quaterniond.fromAxesAnglesDeg(this.rotation.getX(), -this.rotation.getY(), this.rotation.getZ());
                        loc = CoreTools.GetLocationV(player, Qu, Qu.rotate(pds.getVector3d()));
                    } else {
                        loc = player.getPositionVector().add(pds.getVec3d());
                    }
                }

                if (pds.getParticleEffect().getEvent().equalsIgnoreCase("onmove")) {
                    if (NyxEffect.getInstance().PlayerEvent.get(player.getUniqueID()).getOnmove()) {
                        spawnEffects(player, pds.getParticleEffect().getType(), pds.getParticleEffect().getData(), loc, pds.getParticleEffect().getcleartime(), pds.getParticleEffect().getNumberOfParticles(), pds.getParticleEffect().getParticleSpeed());
                    }
                }

                if (pds.getParticleEffect().getEvent().equalsIgnoreCase("onstop")) {
                    if (!NyxEffect.getInstance().PlayerEvent.get(player.getUniqueID()).getOnmove()) {
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

    private void spawnEffects(EntityPlayerMP player, String effecttype, String effectdata, Vec3d loc, long cleartime, int numberofparticles, double particleSpeed) {
        if (!player.isInvisible()) {
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
                    int worldID = player.dimension;
                    String id = "NONE";

                    String[] offsetData = new String[3];
                    String[] pixelmonData = new String[4];
                    String[] elecData = new String[10];

                    offsetData[0] = "NONE";
                    pixelmonData[0] = "NONE";
                    elecData[0] = "NONE";

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

                            if (datax.contains("pixelmon:")) {
                                id = datax;
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
                            player.getServerWorld().playEvent(2003, new BlockPos(loc.x, loc.y, loc.z), 0);
                        } else {
                            EnumParticleTypes enumParticleTypes = EnumParticleTypes.getByName(id.replace("minecraft:", ""));
                            if (enumParticleTypes != null) {
                                player.getServerWorld().spawnParticle(enumParticleTypes, loc.x, loc.y, loc.z, numberofparticles, offset.getX(), offset.getY(), offset.getZ(), particleSpeed);
                            }
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
                                ParticleArcaneryDispatcher.dispatchToDimension(worldID, 50, loc.x, loc.y, loc.z, motion.getX(), motion.getY(), motion.getZ(), size, Particles.Shiny, argList1.toArray());
                            }
                        }

                        if (id.equalsIgnoreCase("pixelmon:bluemagic")) {
                            for (int x = numberofparticles; x > 0; x--) {
                                ParticleArcaneryDispatcher.dispatchToDimension(worldID, 50, loc.x, loc.y, loc.z, motion.getX(), motion.getY(), motion.getZ(), size, Particles.BlueMagic);
                            }
                        }

                        if (id.equalsIgnoreCase("pixelmon:electric")) {
                            for (int x = numberofparticles; x > 0; x--) {
                                ParticleArcaneryDispatcher.dispatchToDimension(worldID, 50, loc.x, loc.y, loc.z, motion.getX(), motion.getY(), motion.getZ(), size, Particles.Electric, elecArgs);
                            }
                        }

                    }
                }
            }

            if (effecttype.equalsIgnoreCase("block")) {
                if (player.onGround) {
                    try {
                        BlockPos locs = player.getPosition().down();
                            //BlockPos blockOn = Tools.getLocBelow(player, player.getServerWorld());
                                IBlockState blockStateBelow = player.world.getBlockState(locs);
                                if (!NyxEffect.getInstance().restricted_blocks.contains(blockStateBelow.getBlock())) {
                                    //Vec3i loci = new Vec3i(blockOn.getX(), blockOn.getY(), blockOn.getZ());
                                    IBlockState iBlockState;
                                    if(effectdata.contains("color=")){

                                        String colorname = effectdata.replace(" ", "").split("\\[")[1]
                                                .replace("color=","")
                                                .replace("]" ,"");

                                        iBlockState = CoreTools.getBlockState(effectdata).withProperty(COLOR, CoreTools.getBlockColor(colorname));
                                    }
                                    else {
                                        iBlockState = CoreTools.getBlockState(effectdata);
                                    }

                                    CoreTools.sendBlockChange(player,locs, iBlockState);
                                    Tools.ClearBlockTask(player, locs, cleartime);

                                }
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
        }
    }
}