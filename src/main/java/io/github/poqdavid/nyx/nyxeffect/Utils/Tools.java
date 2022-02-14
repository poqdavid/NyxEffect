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

import com.flowpowered.math.vector.Vector3i;
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
import org.apache.commons.io.FileUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
        Boolean temp_out = false;
        for (Task task : Sponge.getScheduler().getScheduledTasks(NyxEffect.getInstance())) {
            if (task.getName().contains(uuid)) {
                if (task.getName().contains(lookupst)) {
                    temp_out = true;
                }
            }
        }
        return temp_out;
    }

    public static void RemoveEffectTask(String uuid, String effectname) {
        for (Task task : Sponge.getScheduler().getScheduledTasks(NyxEffect.getInstance())) {
            if (task.getName().contains(uuid)) {
                if (task.getName().contains(effectname)) {
                    NyxEffect.getInstance().getLogger().info("Stopping Task: " + task.getName());
                    task.cancel();
                }
            }
        }
    }

    public static void AddEffectTask(Player player, String effectname) {
        final String uuid = player.getUniqueId().toString();
        if (!Tools.TaskAvilable(uuid, effectname)) {
            for (ParticlesData pdent : NyxEffect.getInstance().ParticlesLIST) {

                if (pdent.getEffectsData().getId().equalsIgnoreCase(effectname)) {
                    Task.builder().execute(new EffectTask(player.getUniqueId(), pdent.getEffectsData().getParticleDataList()))
                            .async()
                            .interval(pdent.getEffectsData().getInterval(), TimeUnit.MILLISECONDS)
                            .name("TaskOwner: " + uuid + " Effect: " + pdent.getEffectsData().getId().toLowerCase()).submit(NyxEffect.getInstance());

                }

            }
        }
    }

    public static void AddMovementTask(Player player) {
        final String uuid = player.getUniqueId().toString();

        if (!Tools.TaskAvilable(uuid, "MovementDetection")) {
            Task.builder().execute(new MovementDetectionTask(player.getUniqueId()))
                    .async()
                    .interval(1000, TimeUnit.MILLISECONDS)
                    .name("TaskOwner: " + uuid + " MovementDetection").submit(NyxEffect.getInstance());
        }
    }

    public static void RemoveMovementTask(Player player) {
        final String uuid = player.getUniqueId().toString();
        final List<String> pd = NyxEffect.getInstance().UserParticlesLIST.get(uuid);

        if (pd.contains("NONE") || pd.isEmpty()) {
            for (Task task : Sponge.getScheduler().getScheduledTasks(NyxEffect.getInstance())) {
                if (task.getName().contains(uuid)) {
                    if (task.getName().contains("MovementDetection")) {
                        NyxEffect.getInstance().getLogger().info("Stopping Task: " + task.getName());
                        task.cancel();
                    }
                }
            }
        }
    }

    public static void ClearBlockTask(Player player, Vector3i loc, long delay) {
        Task.builder()
                .execute(
                        (Task task) -> player.getWorld().resetBlockChange(loc)
                )
                .async()
                .delay(delay, TimeUnit.MILLISECONDS).submit(NyxEffect.getInstance());

    }

    public static boolean TaskDoesNotExist(String name) {
        boolean output = true;
        for (Task taskd : Sponge.getScheduler().getScheduledTasks(NyxEffect.getInstance())) {
            if (taskd.getName().equals(name)) {
                output = false;
            }
        }
        return output;
    }

    public static void UserTaskStop(Player player, String task, Boolean sendmsg) {
        final String uuid = player.getUniqueId().toString();
        if (sendmsg) {
            player.sendMessage(Text.of(TextColors.AQUA, "Stopping task/s"));
        }

        if (task.equals("all")) {
            for (Task taskd : Sponge.getScheduler().getScheduledTasks(NyxEffect.getInstance())) {
                if (taskd.getName().contains(uuid)) {
                    NyxEffect.getInstance().getLogger().info("Stopping Task: " + taskd.getName());
                    taskd.cancel();
                }
            }
            NyxEffect.getInstance().PlayerEvent.remove(player.getUniqueId());
        } else {

            if (task.equals("movement")) {
                Tools.RemoveMovementTask(player);
            } else {
                Tools.RemoveEffectTask(uuid, task);
            }

        }
        if (sendmsg) {
            player.sendMessage(Text.of(TextColors.AQUA, "Stopped task/s"));
        }

    }

    public static void UserTaskStop(String task, Boolean sendmsg) {
        for (String uuid : NyxEffect.getInstance().UserParticlesLIST.keySet()) {
            Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(UUID.fromString(uuid));
            optionalPlayer.ifPresent(player -> UserTaskStop(player, task, sendmsg));
        }
    }

    public static void UserTaskStart(Player player, String task, Boolean sendmsg) {
        if (sendmsg) {
            player.sendMessage(Text.of(TextColors.AQUA, "Starting task/s"));
        }

        NyxEffect.getInstance().PlayerEvent.putIfAbsent(player.getUniqueId(), new PlayerData(player.getName(), false));

        if (task.equals("all")) {
            Tools.AddMovementTask(player);
            if (NyxEffect.getInstance().UserParticlesLIST.containsKey(player.getUniqueId().toString())) {
                final List<String> pd = NyxEffect.getInstance().UserParticlesLIST.get(player.getUniqueId().toString());
                for (String effect : pd) {
                    for (ParticlesData pdent : NyxEffect.getInstance().ParticlesLIST) {
                        if (pdent.getEffectsData().getId().equalsIgnoreCase(effect)) {
                            String name = "TaskOwner: " + player.getUniqueId() + " Effect: " + pdent.getEffectsData().getId().toLowerCase();
                            if (TaskDoesNotExist(name)) {
                                Task.builder().execute(new EffectTask(player.getUniqueId(), pdent.getEffectsData().getParticleDataList()))
                                        .async()
                                        .interval(pdent.getEffectsData().getInterval(), TimeUnit.MILLISECONDS)
                                        .name(name).submit(NyxEffect.getInstance());
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
            player.sendMessage(Text.of(TextColors.AQUA, "Started task/s"));
        }

    }

    public static void UserTaskStart(String task, Boolean sendmsg) {
        for (String uuid : NyxEffect.getInstance().UserParticlesLIST.keySet()) {
            Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(UUID.fromString(uuid));
            if (optionalPlayer.isPresent()) {
                if (optionalPlayer.get().isOnline()) {
                    UserTaskStart(optionalPlayer.get(), task, sendmsg);
                }
            }
        }
    }

    public static void UserTaskRestart(Player player, String task, Boolean sendmsg) {
        UserTaskStop(player, task, sendmsg);

        UserTaskStart(player, task, sendmsg);
    }

    public static Location<World> getLocBelow(Player player, Location<World> loc) {
        Location<World> locBelow = null;
        try {
            if (player.isOnGround()) {
                for (int i = 0; i < 100; i++) {
                    locBelow = loc.sub(0, i, 0);
                    BlockType blockType = locBelow.getBlock().getType();
                    if (blockType != null) {
                        if (!NyxEffect.getInstance().restricted_blocks.contains(blockType)) {
                            return locBelow;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return locBelow;
    }
}
