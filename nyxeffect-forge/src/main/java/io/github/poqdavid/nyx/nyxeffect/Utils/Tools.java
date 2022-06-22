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

package io.github.poqdavid.nyx.nyxeffect.Utils;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.poqdavid.nyx.nyxcore.Utils.CoreTools;
import io.github.poqdavid.nyx.nyxeffect.NyxEffect;
import io.github.poqdavid.nyx.nyxeffect.Tasks.EffectTask;
import io.github.poqdavid.nyx.nyxeffect.Tasks.MovementDetectionTask;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.EffectsData;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.ParticlesData;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.PlayerData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.server.FMLServerHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;

import static io.github.poqdavid.nyx.nyxeffect.NyxEffect.NyxEffectThreads;

public class Tools {
    public static Map<String, List<String>> loaduserparticles(Path filePath) {
        File file = filePath.toFile();
        if (!file.exists()) {
            CoreTools.WriteFile(file, "{}");
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, List<String>>>() {
        }.getType();

        Map<String, List<String>> data = new HashMap<>();
        try {
            data = gson.fromJson(FileUtils.readFileToString(file, Charsets.UTF_8), type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static List<ParticlesData> loadparticles(Path filePath) {
        File file = filePath.toFile();
        if (!file.exists()) {
            CoreTools.WriteFile(file, "[]");
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<ParticlesData>>() {
        }.getType();

        List<ParticlesData> data = new ArrayList<>();
        try {
            data = gson.fromJson(FileUtils.readFileToString(file, Charsets.UTF_8), type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static void saveparticles(Map<String, List<String>> items, Path filePath) {
        File file = filePath.toFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        if (items == null || items.isEmpty()) {
            CoreTools.WriteFile(file, "{}");
        } else {
            CoreTools.WriteFile(file, gson.toJson(items));
        }
    }

    public static void save(List<ParticlesData> items, Path filePath) {
        File file = filePath.toFile();

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.setPrettyPrinting().disableHtmlEscaping();

        //gsonBuilder.registerTypeAdapter(ParticleDataList.class, new InterfaceAdapter<ParticleDataList>());

        Gson gson = gsonBuilder.create();
        Type type = new com.google.common.reflect.TypeToken<List<ParticlesData>>() {
        }.getType();
        if (items == null || items.isEmpty()) {
            CoreTools.WriteFile(file, "{}");
        } else {

            CoreTools.WriteFile(file, gson.toJson(items, type));
        }
    }

    public static EffectsData GetEffect(String nameorid) throws Exception {
        for (ParticlesData pdent : NyxEffect.getInstance().ParticlesLIST) {

            if (pdent.getEffectsData().getId().equalsIgnoreCase(nameorid.toLowerCase())) {
                return pdent.getEffectsData();
            }

            if (pdent.getEffectsData().getName().equalsIgnoreCase(nameorid.toLowerCase())) {
                return pdent.getEffectsData();
            }
        }

        throw new Exception("Didn't find the effect.");
    }

    public static Boolean TaskAvilable(String uuid, String lookupst) {
        boolean temp_out = false;
        for (String task : NyxEffectThreads.keySet()) {
            if (task.contains(uuid)) {
                if (task.contains(lookupst)) {
                    temp_out = true;
                }
            }
        }

        return temp_out;
    }

    public static void RemoveEffectTask(String uuid, String effectname) {
        for (String task : NyxEffectThreads.keySet()) {
            if (task.contains(uuid)) {
                if (task.contains(effectname)) {
                    ((EffectTask) NyxEffectThreads.get(task)).taskStop = true;
                    NyxEffectThreads.remove(task);
                }
            }
        }
    }

    public static void AddEffectTask(EntityPlayer player, String effectname) {
        String uuid = player.getUniqueID().toString();
        if (!Tools.TaskAvilable(uuid, effectname)) {
            for (ParticlesData pdent : NyxEffect.getInstance().ParticlesLIST) {
                if (pdent.getEffectsData().getId().equalsIgnoreCase(effectname)) {
                    String taskname = "TaskOwner: " + uuid + " Effect: " + pdent.getEffectsData().getId().toLowerCase();
                    NyxEffectThreads.put(taskname, new EffectTask(player.getUniqueID(), pdent.getEffectsData().getParticleDataList(), pdent.getEffectsData().getInterval()));
                    NyxEffectThreads.get(taskname).start();
                }
            }
        }
    }

    public static void AddMovementTask(EntityPlayer player) {
        final String uuid = player.getUniqueID().toString();
        if (!Tools.TaskAvilable(uuid, "MovementDetection")) {
            String taskname = "TaskOwner: " + uuid + " MovementDetection";
            NyxEffectThreads.put(taskname, new MovementDetectionTask(player.getUniqueID(), 1000));
            NyxEffectThreads.get(taskname).start();
        }
    }

    public static void RemoveMovementTask(EntityPlayer player) {
        String uuid = player.getUniqueID().toString();
        List<String> pd = NyxEffect.getInstance().UserParticlesLIST.get(uuid);

        if (pd.contains("NONE") || pd.isEmpty()) {
            for (String task : NyxEffectThreads.keySet()) {
                if (task.contains(uuid)) {
                    if (task.contains("MovementDetection")) {
                        ((MovementDetectionTask) NyxEffectThreads.get(task)).taskStop = true;
                        NyxEffectThreads.remove(task);
                    }
                }
            }
        }
    }

    public static void ClearBlockTask(EntityPlayerMP player, BlockPos loc, long delay) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(delay);
                CoreTools.resetBlockChange(player, loc);
                Thread.currentThread().interrupt();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
    }

    public static boolean TaskDoesNotExist(String name) {
        return !NyxEffectThreads.containsKey(name);
    }

    public static void UserTaskStop(EntityPlayer player, String task, Entity et, Boolean sendmsg) {
        final String uuid = player.getUniqueID().toString();
        if (sendmsg) {
            et.sendMessage(new TextComponentString("&b" + "Stopping task/s"));
        }

        if (task.equals("all")) {
            for (String taskd : NyxEffectThreads.keySet()) {
                if (taskd.contains(uuid)) {

                    if (NyxEffectThreads.get(taskd) instanceof EffectTask) {
                        ((EffectTask) NyxEffectThreads.get(taskd)).taskStop = true;
                    }

                    if (NyxEffectThreads.get(taskd) instanceof MovementDetectionTask) {
                        ((MovementDetectionTask) NyxEffectThreads.get(taskd)).taskStop = true;
                    }
                }
            }
            NyxEffect.getInstance().PlayerEvent.remove(player.getUniqueID());
        } else {

            if (task.equals("movement")) {
                Tools.RemoveMovementTask(player);
            } else {
                Tools.RemoveEffectTask(uuid, task);
            }

        }

        if (sendmsg) {
            et.sendMessage(new TextComponentString("&b" + "Stopped task/s"));
        }

    }

    public static void UserTaskStop(String task, Entity et, Boolean sendmsg) {
        for (String uuid : NyxEffect.getInstance().UserParticlesLIST.keySet()) {
            EntityPlayer player = FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(UUID.fromString(uuid));
            if (player != null) {
                UserTaskStop(player, task, et,  sendmsg);
            }
        }
    }

    public static void UserTaskStart(EntityPlayer player, String task, Entity et, Boolean sendmsg) {
        if (sendmsg) {
            et.sendMessage(new TextComponentString("&b" + "Starting task/s"));
        }

        NyxEffect.getInstance().PlayerEvent.putIfAbsent(player.getUniqueID(), new PlayerData(player.getName(), false));

        if (task.equals("all")) {
            Tools.AddMovementTask(player);
            if (NyxEffect.getInstance().UserParticlesLIST.containsKey(player.getUniqueID().toString())) {
                final List<String> pd = NyxEffect.getInstance().UserParticlesLIST.get(player.getUniqueID().toString());
                for (String effect : pd) {
                    for (ParticlesData pdent : NyxEffect.getInstance().ParticlesLIST) {
                        if (pdent.getEffectsData().getId().equalsIgnoreCase(effect)) {
                            String taskname = "TaskOwner: " + player.getUniqueID() + " Effect: " + pdent.getEffectsData().getId().toLowerCase();
                            if (TaskDoesNotExist(taskname)) {
                                NyxEffectThreads.put(taskname, new EffectTask(player.getUniqueID(), pdent.getEffectsData().getParticleDataList(), pdent.getEffectsData().getInterval()));
                                NyxEffectThreads.get(taskname).start();
                            }
                        }

                    }

                }

            }

        } else {
            if (task.equals("movement")) {
                Tools.AddMovementTask(player);
            } else {
                Tools.AddEffectTask(player, task);
            }
        }
        if (sendmsg) {
            et.sendMessage(new TextComponentString("&b" + "Started task/s"));
        }

    }

    public static void UserTaskStart(String task, Entity et, Boolean sendmsg) {
        for (String uuid : NyxEffect.getInstance().UserParticlesLIST.keySet()) {
            EntityPlayerMP player = FMLServerHandler.instance().getServer().getPlayerList().getPlayerByUUID(UUID.fromString(uuid));
            if (player != null) {
                UserTaskStart(player, task,et, sendmsg);
            }
        }
    }

    public static void UserTaskRestart(EntityPlayer player, String task, Entity et, Boolean sendmsg) {
        UserTaskStop(player, task, et, sendmsg);

        UserTaskStart(player, task, et, sendmsg);
    }

    public static BlockPos getLocBelow(EntityPlayerMP player, BlockPos loc) {
        try {
            if (player.onGround) {
                for (int i = 0; i < 100; i++) {
                    BlockPos locBelow = loc.subtract(new Vec3i(0, i, 0));

                    IBlockState blockState = player.world.getBlockState(locBelow);
                    Block blockType = blockState.getBlock();

                    if (blockType != null) {
                        if (!NyxEffect.getInstance().restricted_blocks.contains(blockType)) {
                            return locBelow;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            return null;
        }

        return null;
    }

    public static Boolean isPlayerOnline(UUID uuid) {
        for (EntityPlayerMP p : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            if (p.getUniqueID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }
}
