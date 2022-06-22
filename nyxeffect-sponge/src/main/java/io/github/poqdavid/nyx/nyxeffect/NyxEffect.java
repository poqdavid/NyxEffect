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

package io.github.poqdavid.nyx.nyxeffect;

import com.google.inject.Inject;
import io.github.poqdavid.nyx.nyxcore.NyxCore;
import io.github.poqdavid.nyx.nyxcore.Permissions.EffectPermission;
import io.github.poqdavid.nyx.nyxcore.Utils.CText;
import io.github.poqdavid.nyx.nyxcore.Utils.CoreTools;
import io.github.poqdavid.nyx.nyxcore.Utils.NCLogger;
import io.github.poqdavid.nyx.nyxeffect.Commands.CommandManager;
import io.github.poqdavid.nyx.nyxeffect.Listeners.NyxEffectListener;
import io.github.poqdavid.nyx.nyxeffect.Tasks.EffectTask;
import io.github.poqdavid.nyx.nyxeffect.Tasks.MovementDetectionTask;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.EffectsData;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.ParticlesData;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.PlayerData;
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import net.minecraftforge.fml.common.Loader;
import org.bstats.sponge.Metrics;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Plugin(id = "nyxeffect", name = "@name@", version = "@version@", description = "@description@", url = "https://github.com/poqdavid/NyxEffect", authors = {"@authors@"}, dependencies = {@Dependency(id = "nyxcore", version = "1.+", optional = false), @Dependency(id = "pixelmon", version = "8.3.+", optional = true)})
public class NyxEffect {

    public static boolean HOLDEFFECTS = false;
    public static Map<String, String> EffectCMDs = new HashMap<>();
    private static NyxEffect NyxEffect;
    public final Path particlesdatapath;
    private final NCLogger logger;
    private final Metrics metrics;
    private final Path userparticledatapath;
    private final PluginContainer pluginContainer;
    public Map<UUID, PlayerData> PlayerEvent;
    public Map<String, List<String>> UserParticlesLIST;
    public List<ParticlesData> ParticlesLIST;
    public List<BlockType> restricted_blocks;
    public List<BlockType> allowed_blocks;
    public List<String> blockedPixelmonBlocks;
    public PermissionService permservice;
    public PermissionDescription.Builder permdescbuilder;
    public boolean pixelmon = false;
    private Path configfullpath;
    @Inject
    private Game game;
    private CommandManager cmdManager;

    @Inject
    public NyxEffect(Metrics.Factory metricsFactory, @ConfigDir(sharedRoot = true) Path path, Logger logger, PluginContainer container) {
        NyxEffect = this;
        this.pluginContainer = container;

        this.logger = NyxCore.getInstance().getLogger(CText.get(CText.Colors.BLUE, 1, "Nyx") + CText.get(CText.Colors.MAGENTA, 0, "Effect"));

        this.logger.info(" ");
        this.logger.info(CText.get(CText.Colors.MAGENTA, 0, "@name@") + CText.get(CText.Colors.YELLOW, 0, " v" + this.getVersion()));
        this.logger.info("Starting...");
        this.logger.info(" ");

        this.userparticledatapath = Paths.get(this.getConfigPath().toString(), "UserParticleData.json");
        this.particlesdatapath = Paths.get(this.getConfigPath().toString(), "ParticlesData.json");

        this.PlayerEvent = new HashMap<>();
        this.UserParticlesLIST = new HashMap<>();
        this.ParticlesLIST = new ArrayList<>();
        this.restricted_blocks = new ArrayList<>();
        this.allowed_blocks = new ArrayList<>();

        this.blockedPixelmonBlocks = new ArrayList<>();

        int pluginId = 13656;
        metrics = metricsFactory.make(pluginId);

        blockedPixelmonBlocks.add("pixelmon:pc");
        blockedPixelmonBlocks.add("pixelmon:ranch");
        blockedPixelmonBlocks.add("pixelmon:anvil");
        blockedPixelmonBlocks.add("pixelmon:mechanical_anvil");
        blockedPixelmonBlocks.add("pixelmon:healer");
        blockedPixelmonBlocks.add("pixelmon:trade_machine");
        blockedPixelmonBlocks.add("pixelmon:fossil_machine");
        blockedPixelmonBlocks.add("pixelmon:cloning_machine");
        blockedPixelmonBlocks.add("pixelmon:articuno_shrine");
        blockedPixelmonBlocks.add("pixelmon:zapdos_shrine");
        blockedPixelmonBlocks.add("pixelmon:moltres_shrine");
        blockedPixelmonBlocks.add("pixelmon:fossil_cleaner");
        blockedPixelmonBlocks.add("pixelmon:poke_chest");
        blockedPixelmonBlocks.add("pixelmon:ultra_chest");
        blockedPixelmonBlocks.add("pixelmon:master_chest");
        blockedPixelmonBlocks.add("pixelmon:hidden_iron_door");
        blockedPixelmonBlocks.add("pixelmon:hidden_pressure_plate");
        blockedPixelmonBlocks.add("pixelmon:hidden_wooden_door");
        blockedPixelmonBlocks.add("pixelmon:gift_box");
        blockedPixelmonBlocks.add("pixelmon:bolder");
        blockedPixelmonBlocks.add("pixelmon:box");
        blockedPixelmonBlocks.add("pixelmon:chair");
        blockedPixelmonBlocks.add("pixelmon:blue_clock");
        blockedPixelmonBlocks.add("pixelmon:pink_clock");
        blockedPixelmonBlocks.add("pixelmon:clothed_table");
        blockedPixelmonBlocks.add("pixelmon:red_cushion_chair");
        blockedPixelmonBlocks.add("pixelmon:yellow_cushion_chair");
        blockedPixelmonBlocks.add("pixelmon:end_table");
        blockedPixelmonBlocks.add("pixelmon:fossil_display");
        blockedPixelmonBlocks.add("pixelmon:fridge");
        blockedPixelmonBlocks.add("pixelmon:green_folding_chair");
        blockedPixelmonBlocks.add("pixelmon:gym_sign");
        blockedPixelmonBlocks.add("pixelmon:picket_fence");
        blockedPixelmonBlocks.add("pixelmon:blue_rug");
        blockedPixelmonBlocks.add("pixelmon:green_rug");
        blockedPixelmonBlocks.add("pixelmon:red_rug");
        blockedPixelmonBlocks.add("pixelmon:yellow_rug");
        blockedPixelmonBlocks.add("pixelmon:pokecenter_sign");
        blockedPixelmonBlocks.add("pixelmon:trash_can");
        blockedPixelmonBlocks.add("pixelmon:tree");
        blockedPixelmonBlocks.add("pixelmon:tree_bottom");
        blockedPixelmonBlocks.add("pixelmon:tree_top");
        blockedPixelmonBlocks.add("pixelmon:tv");
        blockedPixelmonBlocks.add("pixelmon:blue_umbrella");
        blockedPixelmonBlocks.add("pixelmon:green_umbrella");
        blockedPixelmonBlocks.add("pixelmon:red_umbrella");
        blockedPixelmonBlocks.add("pixelmon:yellow_umbrella");
        blockedPixelmonBlocks.add("pixelmon:blue_vending_machine");
        blockedPixelmonBlocks.add("pixelmon:green_vending_machine");
        blockedPixelmonBlocks.add("pixelmon:orange_vending_machine");
        blockedPixelmonBlocks.add("pixelmon:pink_vending_machine");
        blockedPixelmonBlocks.add("pixelmon:red_vending_machine");
        blockedPixelmonBlocks.add("pixelmon:yellow_vending_machine");
        blockedPixelmonBlocks.add("pixelmon:blue_water_float");
        blockedPixelmonBlocks.add("pixelmon:green_water_float");
        blockedPixelmonBlocks.add("pixelmon:orange_water_float");
        blockedPixelmonBlocks.add("pixelmon:pink_water_float");
        blockedPixelmonBlocks.add("pixelmon:purple_water_float");
        blockedPixelmonBlocks.add("pixelmon:red_water_float");
        blockedPixelmonBlocks.add("pixelmon:yellow_water_float");
        blockedPixelmonBlocks.add("pixelmon:window1");
        blockedPixelmonBlocks.add("pixelmon:window2");
        blockedPixelmonBlocks.add("pixelmon:poke_gift");
        blockedPixelmonBlocks.add("pixelmon:poke_gift_event");
        blockedPixelmonBlocks.add("pixelmon:elevator");
        blockedPixelmonBlocks.add("pixelmon:movement_plate");
        blockedPixelmonBlocks.add("pixelmon:timed_fall");
        blockedPixelmonBlocks.add("pixelmon:warp_plate");
        blockedPixelmonBlocks.add("pixelmon:pixelmon_sprite");
        blockedPixelmonBlocks.add("pixelmon:painting");
        blockedPixelmonBlocks.add("pixelmon:stick_plate");
        blockedPixelmonBlocks.add("pixelmon:Black_Apricorn_Tree");
        blockedPixelmonBlocks.add("pixelmon:White_Apricorn_Tree");
        blockedPixelmonBlocks.add("pixelmon:Pink_Apricorn_Tree");
        blockedPixelmonBlocks.add("pixelmon:Green_Apricorn_Tree");
        blockedPixelmonBlocks.add("pixelmon:Blue_Apricorn_Tree");
        blockedPixelmonBlocks.add("pixelmon:Yellow_Apricorn_Tree");
        blockedPixelmonBlocks.add("pixelmon:Red_Apricorn_Tree");
        blockedPixelmonBlocks.add("pixelmon:berrytree_cheri");
        blockedPixelmonBlocks.add("pixelmon:berrytree_pomeg");
        blockedPixelmonBlocks.add("pixelmon:berrytree_kelpsy");
        blockedPixelmonBlocks.add("pixelmon:berrytree_qualot");
        blockedPixelmonBlocks.add("pixelmon:berrytree_hondew");
        blockedPixelmonBlocks.add("pixelmon:berrytree_grepa");
        blockedPixelmonBlocks.add("pixelmon:berrytree_tamato");
        blockedPixelmonBlocks.add("pixelmon:berrytree_occa");
        blockedPixelmonBlocks.add("pixelmon:berrytree_passho");
        blockedPixelmonBlocks.add("pixelmon:berrytree_wacan");
        blockedPixelmonBlocks.add("pixelmon:berrytree_rindo");
        blockedPixelmonBlocks.add("pixelmon:berrytree_yache");
        blockedPixelmonBlocks.add("pixelmon:berrytree_chople");
        blockedPixelmonBlocks.add("pixelmon:berrytree_kebia");
        blockedPixelmonBlocks.add("pixelmon:berrytree_shuca");
        blockedPixelmonBlocks.add("pixelmon:berrytree_coba");
        blockedPixelmonBlocks.add("pixelmon:berrytree_tanga");
        blockedPixelmonBlocks.add("pixelmon:berrytree_charti");
        blockedPixelmonBlocks.add("pixelmon:berrytree_kasib");
        blockedPixelmonBlocks.add("pixelmon:berrytree_haban");
        blockedPixelmonBlocks.add("pixelmon:berrytree_colbur");
        blockedPixelmonBlocks.add("pixelmon:berrytree_babiri");
        blockedPixelmonBlocks.add("pixelmon:berrytree_liechi");
        blockedPixelmonBlocks.add("pixelmon:berrytree_ganlon");
        blockedPixelmonBlocks.add("pixelmon:berrytree_salac");
    }

    @Nonnull
    public static NyxEffect getInstance() {
        return NyxEffect;
    }

    @Nonnull
    public Path getConfigPath() {
        return NyxCore.getInstance().getEffectPath();
    }

    @Nonnull
    public Path getUserParticleDataPath() {
        return this.userparticledatapath;
    }

    @Nonnull
    public PluginContainer getPluginContainer() {
        return this.pluginContainer;
    }

    @Nonnull
    public String getVersion() {
        if (this.getPluginContainer().getVersion().isPresent()) {
            return this.getPluginContainer().getVersion().get();
        } else {
            return "@version@";
        }
    }

    @Nonnull
    public NCLogger getLogger() {
        return logger;
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    @Inject
    public void setGame(Game game) {
        this.game = game;
    }

    @Listener
    public void onGamePreInit(@Nullable final GamePreInitializationEvent event) {
        this.logger.info(" ");
        this.logger.info(CText.get(CText.Colors.MAGENTA, 0, "@name@") + CText.get(CText.Colors.YELLOW, 0, " v" + this.getVersion()));
        this.logger.info("Initializing...");
        this.logger.info(" ");
    }

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if (event.getService().equals(PermissionService.class)) {
            this.permservice = (PermissionService) event.getNewProviderRegistration().getProvider();
        }
    }

    @Listener
    public void onGameInit(@Nullable final GameInitializationEvent event) {
        this.ParticlesLIST = Tools.loadparticles(this.particlesdatapath);
        this.UserParticlesLIST = Tools.loaduserparticles(this.userparticledatapath);

        if (Sponge.getServiceManager().getRegistration(PermissionService.class).get().getPlugin().getId().equalsIgnoreCase("sponge")) {
            this.logger.error("Unable to initialize plugin. NyxBackpack requires a PermissionService like  LuckPerms, PEX, PermissionsManager.");
            return;
        }

        this.permdescbuilder = this.permservice.newDescriptionBuilder(this.getPluginContainer());
        if (this.permdescbuilder != null) {

            this.permdescbuilder
                    .id(EffectPermission.COMMAND_MAIN)
                    .description(Text.of("Allows the use of /NyxEffect"))
                    .assign(PermissionDescription.ROLE_USER, true)
                    .assign(PermissionDescription.ROLE_STAFF, true)
                    .assign(PermissionDescription.ROLE_ADMIN, true)
                    .register();

            this.permdescbuilder
                    .id(EffectPermission.COMMAND_CHECKOTHERS)
                    .description(Text.of("Allows the use of /nyxeffectcheck"))
                    .assign(PermissionDescription.ROLE_USER, false)
                    .assign(PermissionDescription.ROLE_STAFF, true)
                    .assign(PermissionDescription.ROLE_ADMIN, true)
                    .register();
            this.permdescbuilder
                    .id(EffectPermission.COMMAND_DISABLEOTHERS)
                    .description(Text.of("Allows the use of /nyxeffectdisable"))
                    .assign(PermissionDescription.ROLE_USER, false)
                    .assign(PermissionDescription.ROLE_STAFF, true)
                    .assign(PermissionDescription.ROLE_ADMIN, true)
                    .register();

            this.permdescbuilder
                    .id(EffectPermission.COMMAND_EFFECT)
                    .description(Text.of("Allows the use of /nyxeffect"))
                    .assign(PermissionDescription.ROLE_USER, true)
                    .assign(PermissionDescription.ROLE_STAFF, true)
                    .assign(PermissionDescription.ROLE_ADMIN, true)
                    .register();

            this.permdescbuilder
                    .id(EffectPermission.COMMAND_EFFECT_OTHER)
                    .description(Text.of("Allows the use of /nyxeffect <player>"))
                    .assign(PermissionDescription.ROLE_USER, false)
                    .assign(PermissionDescription.ROLE_STAFF, true)
                    .assign(PermissionDescription.ROLE_ADMIN, true)
                    .register();


            this.permdescbuilder
                    .id(EffectPermission.COMMAND_TASK)
                    .description(Text.of("Allows the use of /nyxeffecttask"))
                    .assign(PermissionDescription.ROLE_USER, true)
                    .assign(PermissionDescription.ROLE_STAFF, true)
                    .assign(PermissionDescription.ROLE_ADMIN, true)
                    .register();

            this.permdescbuilder
                    .id(EffectPermission.COMMAND_TASK_OTHER)
                    .description(Text.of("Allows the use of /nyxeffecttask <player>"))
                    .assign(PermissionDescription.ROLE_USER, false)
                    .assign(PermissionDescription.ROLE_STAFF, true)
                    .assign(PermissionDescription.ROLE_ADMIN, true)
                    .register();

            this.permdescbuilder
                    .id(EffectPermission.COMMAND_LIST)
                    .description(Text.of("Allows the use of /nyxeffectlist"))
                    .assign(PermissionDescription.ROLE_USER, true)
                    .assign(PermissionDescription.ROLE_STAFF, true)
                    .assign(PermissionDescription.ROLE_ADMIN, true)
                    .register();

            RegisterEffectNodes();
            LoadEffectCMDs();
        }

        try {
            if (!Files.exists(this.getConfigPath())) {
                Files.createDirectories(this.getConfigPath());
            }
        } catch (final IOException ex) {
            this.logger.error("Error on creating root plugin directory: {}", ex);
        }

        this.restricted_blocks.add(BlockTypes.ACACIA_DOOR);
        this.restricted_blocks.add(BlockTypes.ACACIA_FENCE);
        this.restricted_blocks.add(BlockTypes.ACACIA_FENCE_GATE);
        this.restricted_blocks.add(BlockTypes.ACACIA_STAIRS);
        this.restricted_blocks.add(BlockTypes.ACTIVATOR_RAIL);
        this.restricted_blocks.add(BlockTypes.AIR);
        this.restricted_blocks.add(BlockTypes.ANVIL);
        this.restricted_blocks.add(BlockTypes.BARRIER);
        this.restricted_blocks.add(BlockTypes.BEACON);
        this.restricted_blocks.add(BlockTypes.BED);
        this.restricted_blocks.add(BlockTypes.BEETROOTS);
        this.restricted_blocks.add(BlockTypes.BIRCH_DOOR);
        this.restricted_blocks.add(BlockTypes.BIRCH_FENCE);
        this.restricted_blocks.add(BlockTypes.BIRCH_FENCE_GATE);
        this.restricted_blocks.add(BlockTypes.BIRCH_STAIRS);
        this.restricted_blocks.add(BlockTypes.BREWING_STAND);
        this.restricted_blocks.add(BlockTypes.BRICK_STAIRS);
        this.restricted_blocks.add(BlockTypes.BROWN_MUSHROOM);
        this.restricted_blocks.add(BlockTypes.CAKE);
        this.restricted_blocks.add(BlockTypes.CARPET);
        this.restricted_blocks.add(BlockTypes.CARROTS);
        this.restricted_blocks.add(BlockTypes.CAULDRON);
        this.restricted_blocks.add(BlockTypes.CHORUS_FLOWER);
        this.restricted_blocks.add(BlockTypes.CHORUS_PLANT);
        this.restricted_blocks.add(BlockTypes.COBBLESTONE_WALL);
        this.restricted_blocks.add(BlockTypes.COCOA);
        this.restricted_blocks.add(BlockTypes.DARK_OAK_DOOR);
        this.restricted_blocks.add(BlockTypes.DARK_OAK_FENCE);
        this.restricted_blocks.add(BlockTypes.DARK_OAK_FENCE_GATE);
        this.restricted_blocks.add(BlockTypes.DARK_OAK_STAIRS);
        this.restricted_blocks.add(BlockTypes.DAYLIGHT_DETECTOR);
        this.restricted_blocks.add(BlockTypes.DAYLIGHT_DETECTOR_INVERTED);
        this.restricted_blocks.add(BlockTypes.DEADBUSH);
        this.restricted_blocks.add(BlockTypes.DETECTOR_RAIL);
        this.restricted_blocks.add(BlockTypes.DOUBLE_PLANT);
        this.restricted_blocks.add(BlockTypes.ENCHANTING_TABLE);
        this.restricted_blocks.add(BlockTypes.END_PORTAL);
        this.restricted_blocks.add(BlockTypes.END_PORTAL_FRAME);
        this.restricted_blocks.add(BlockTypes.END_ROD);
        this.restricted_blocks.add(BlockTypes.FENCE);
        this.restricted_blocks.add(BlockTypes.FENCE_GATE);
        this.restricted_blocks.add(BlockTypes.FIRE);
        this.restricted_blocks.add(BlockTypes.FLOWER_POT);
        this.restricted_blocks.add(BlockTypes.GLASS_PANE);
        this.restricted_blocks.add(BlockTypes.GOLDEN_RAIL);
        this.restricted_blocks.add(BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE);
        this.restricted_blocks.add(BlockTypes.HOPPER);
        this.restricted_blocks.add(BlockTypes.IRON_BARS);
        this.restricted_blocks.add(BlockTypes.IRON_DOOR);
        this.restricted_blocks.add(BlockTypes.IRON_TRAPDOOR);
        this.restricted_blocks.add(BlockTypes.JUNGLE_DOOR);
        this.restricted_blocks.add(BlockTypes.JUNGLE_FENCE);
        this.restricted_blocks.add(BlockTypes.JUNGLE_FENCE_GATE);
        this.restricted_blocks.add(BlockTypes.JUNGLE_STAIRS);
        this.restricted_blocks.add(BlockTypes.LADDER);
        this.restricted_blocks.add(BlockTypes.LAVA);
        this.restricted_blocks.add(BlockTypes.LEVER);
        this.restricted_blocks.add(BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE);
        this.restricted_blocks.add(BlockTypes.MELON_STEM);
        this.restricted_blocks.add(BlockTypes.NETHER_BRICK_FENCE);
        this.restricted_blocks.add(BlockTypes.NETHER_BRICK_STAIRS);
        this.restricted_blocks.add(BlockTypes.NETHER_WART);
        this.restricted_blocks.add(BlockTypes.OAK_STAIRS);
        this.restricted_blocks.add(BlockTypes.POTATOES);
        this.restricted_blocks.add(BlockTypes.POWERED_COMPARATOR);
        this.restricted_blocks.add(BlockTypes.POWERED_REPEATER);
        this.restricted_blocks.add(BlockTypes.PUMPKIN_STEM);
        this.restricted_blocks.add(BlockTypes.PURPUR_STAIRS);
        this.restricted_blocks.add(BlockTypes.QUARTZ_STAIRS);
        this.restricted_blocks.add(BlockTypes.RAIL);
        this.restricted_blocks.add(BlockTypes.REDSTONE_TORCH);
        this.restricted_blocks.add(BlockTypes.REDSTONE_WIRE);
        this.restricted_blocks.add(BlockTypes.RED_FLOWER);
        this.restricted_blocks.add(BlockTypes.RED_MUSHROOM);
        this.restricted_blocks.add(BlockTypes.RED_SANDSTONE_STAIRS);
        this.restricted_blocks.add(BlockTypes.REEDS);
        this.restricted_blocks.add(BlockTypes.SANDSTONE_STAIRS);
        this.restricted_blocks.add(BlockTypes.SKULL);
        this.restricted_blocks.add(BlockTypes.SPRUCE_DOOR);
        this.restricted_blocks.add(BlockTypes.SPRUCE_FENCE);
        this.restricted_blocks.add(BlockTypes.SPRUCE_FENCE_GATE);
        this.restricted_blocks.add(BlockTypes.SPRUCE_STAIRS);
        this.restricted_blocks.add(BlockTypes.STAINED_GLASS_PANE);
        this.restricted_blocks.add(BlockTypes.STANDING_BANNER);
        this.restricted_blocks.add(BlockTypes.STANDING_SIGN);
        this.restricted_blocks.add(BlockTypes.STONE_BRICK_STAIRS);
        this.restricted_blocks.add(BlockTypes.STONE_BUTTON);
        this.restricted_blocks.add(BlockTypes.STONE_PRESSURE_PLATE);
        this.restricted_blocks.add(BlockTypes.STONE_SLAB);
        this.restricted_blocks.add(BlockTypes.STONE_SLAB2);
        this.restricted_blocks.add(BlockTypes.STONE_STAIRS);
        this.restricted_blocks.add(BlockTypes.STRUCTURE_VOID);
        this.restricted_blocks.add(BlockTypes.TALLGRASS);
        this.restricted_blocks.add(BlockTypes.TORCH);
        this.restricted_blocks.add(BlockTypes.TRAPDOOR);
        this.restricted_blocks.add(BlockTypes.TRIPWIRE);
        this.restricted_blocks.add(BlockTypes.TRIPWIRE_HOOK);
        this.restricted_blocks.add(BlockTypes.UNPOWERED_COMPARATOR);
        this.restricted_blocks.add(BlockTypes.UNPOWERED_REPEATER);
        this.restricted_blocks.add(BlockTypes.VINE);
        this.restricted_blocks.add(BlockTypes.WALL_BANNER);
        this.restricted_blocks.add(BlockTypes.WALL_SIGN);
        this.restricted_blocks.add(BlockTypes.WATER);
        this.restricted_blocks.add(BlockTypes.WATERLILY);
        this.restricted_blocks.add(BlockTypes.WEB);
        this.restricted_blocks.add(BlockTypes.WHEAT);
        this.restricted_blocks.add(BlockTypes.WOODEN_BUTTON);
        this.restricted_blocks.add(BlockTypes.WOODEN_DOOR);
        this.restricted_blocks.add(BlockTypes.WOODEN_PRESSURE_PLATE);
        this.restricted_blocks.add(BlockTypes.WOODEN_SLAB);
        this.restricted_blocks.add(BlockTypes.YELLOW_FLOWER);
        this.restricted_blocks.add(BlockTypes.SNOW_LAYER);
        this.restricted_blocks.add(BlockTypes.CHEST);
        this.restricted_blocks.add(BlockTypes.ENDER_CHEST);
        this.restricted_blocks.add(BlockTypes.TRAPPED_CHEST);

        for (String blockid : blockedPixelmonBlocks) {
            try {
                if (Sponge.getRegistry().getType(BlockType.class, blockid).isPresent()) {
                    this.restricted_blocks.add(Sponge.getRegistry().getType(BlockType.class, blockid).get());
                }
            } catch (Exception e) {

            }
        }

        NyxEffectListener listener = new NyxEffectListener();
        Sponge.getEventManager().registerListeners(this, listener);

        if (Loader.isModLoaded("pixelmon")) {
            this.pixelmon = true;
            this.logger.info("Pixelmon detected!");
        }

        this.logger.info("Plugin Initialized successfully!");
    }

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        this.logger.info("Loading...");
        this.cmdManager = new CommandManager(game, this);
        this.logger.info("Loaded!");

        // final Collection<BlockState> d = Sponge.getRegistry().getAllOf(BlockState.class);
        // for (BlockState datax : d) {
        //  this.logger.info("BlockState: "+datax.toString());
        // }
    }


    public void ParticleAdd(String uuid, List<String> pd) {
        this.UserParticlesLIST.putIfAbsent(uuid, pd);
        Tools.saveparticles(this.UserParticlesLIST, this.userparticledatapath);
    }

    public void ParticleRemove(String uuid) {
        this.UserParticlesLIST.remove(uuid);
        Tools.saveparticles(this.UserParticlesLIST, this.userparticledatapath);
    }

    public void ParticleToggle(Player player_args, EffectsData effect) {
        ParticleToggle(player_args, player_args, effect);
    }

    public void ParticleToggle(CommandSource player_cmd_src, Player player_args, EffectsData effect) {
        final UUID uuid_args = player_args.getUniqueId();
        final String name_args = player_args.getName();

        Player player_src = null;
        String name_src = "Console";
        UUID uuid_src = UUID.fromString("00000000-0000-0000-0000-000000000000");

        if (player_cmd_src instanceof Player) {
            player_src = CoreTools.getPlayer(player_cmd_src);
            name_src = player_src.getName();
            uuid_src = player_src.getUniqueId();
        }

        if (this.UserParticlesLIST.containsKey(uuid_args.toString())) {
            final List<String> pd = this.UserParticlesLIST.get(uuid_args.toString());
            if (pd.contains(effect.getId())) {
                pd.remove(effect.getId());
                if (pd.isEmpty()) {
                    pd.add("NONE");
                }

                Tools.RemoveEffectTask(uuid_args.toString(), effect.getId());
                this.ParticleSet(player_args, pd);
                Tools.RemoveMovementTask(player_args);

                if (uuid_src.equals(player_args.getUniqueId())) {
                    player_args.sendMessage(Text.of(TextColors.GOLD, name_args, " You have disabled your " + effect.getName() + " particles"));
                } else {
                    player_args.sendMessage(Text.of(TextColors.YELLOW, name_src, " has disabled your " + effect.getName() + "!"));
                    player_cmd_src.sendMessage(Text.of(TextColors.YELLOW, name_args + "'s " + effect.getName() + " has been disabled!"));
                }
            } else {
                pd.remove("NONE");
                pd.add(effect.getId());
                this.ParticleSet(player_args, pd);

                Tools.AddMovementTask(player_args);
                Tools.AddEffectTask(player_args, effect.getId());

                if (uuid_src.equals(player_args.getUniqueId())) {
                    player_args.sendMessage(Text.of(TextColors.GOLD, name_args, TextColors.AQUA, " You have enabled your " + effect.getName() + " particles"));
                } else {
                    player_args.sendMessage(Text.of(TextColors.YELLOW, name_src + " has given you " + effect.getName() + "!"));
                    player_cmd_src.sendMessage(Text.of(TextColors.YELLOW, name_args + " has been given " + effect.getName() + "!"));
                }
            }

        } else {
            final List<String> pd = new ArrayList<>();
            pd.add(effect.getId());
            this.ParticleSet(player_args, pd);

            Tools.AddMovementTask(player_args);
            Tools.AddEffectTask(player_args, effect.getId());

            if (uuid_src.equals(player_args.getUniqueId())) {
                player_args.sendMessage(Text.of(TextColors.GOLD, name_args, TextColors.AQUA, " You have enabled your " + effect.getName() + " particles"));
            } else {
                player_args.sendMessage(Text.of(TextColors.YELLOW, name_src + " has given you " + effect.getName() + "!"));
                player_cmd_src.sendMessage(Text.of(TextColors.YELLOW, name_args + " has been given " + effect.getName() + "!"));
            }
        }

        if (player_cmd_src instanceof Player) {
            player_src.playSound(SoundTypes.UI_BUTTON_CLICK, player_src.getLocation().getPosition(), 0.25);
        }

    }

    public void ParticleSet(Player player, List<String> pd) {
        final String uuid = player.getUniqueId().toString();
        this.UserParticlesLIST.put(uuid, pd);
        Tools.saveparticles(this.UserParticlesLIST, this.userparticledatapath);
    }

    public void ParticleClearAll(Player player) {
        final List<String> temp = new ArrayList<>();
        temp.add("NONE");

        final String uuid = player.getUniqueId().toString();

        this.UserParticlesLIST.put(uuid, temp);

        for (Task task : Sponge.getScheduler().getScheduledTasks(this)) {
            if (task.getName().contains(uuid)) {
                if (task.getConsumer() instanceof EffectTask) {
                    ((EffectTask) task.getConsumer()).taskStop = true;
                }

                if (task.getConsumer() instanceof MovementDetectionTask) {
                    ((MovementDetectionTask) task.getConsumer()).taskStop = true;
                }
            }
        }

        Tools.saveparticles(this.UserParticlesLIST, this.userparticledatapath);
    }

    public void RegisterEffectNodes() {
        if (this.ParticlesLIST != null) {
            for (ParticlesData effect : this.ParticlesLIST) {
                this.permdescbuilder
                        .id(EffectPermission.EFFECTS + "." + effect.getEffectsData().getId())
                        .description(Text.of("Allows the use of effect with id of " + effect.getEffectsData().getId()))
                        .assign(PermissionDescription.ROLE_USER, false)
                        .assign(PermissionDescription.ROLE_STAFF, false)
                        .assign(PermissionDescription.ROLE_ADMIN, true)
                        .register();

                this.logger.info("Effect node: " + EffectPermission.EFFECTS + "." + effect.getEffectsData().getId());
            }
        }
    }

    public void LoadEffectCMDs() {
        if (this.ParticlesLIST != null) {
            EffectCMDs.clear();
            for (ParticlesData effect : this.ParticlesLIST) {
                EffectCMDs.put(effect.getEffectsData().getId(), effect.getEffectsData().getId());
            }
        }

    }
}
