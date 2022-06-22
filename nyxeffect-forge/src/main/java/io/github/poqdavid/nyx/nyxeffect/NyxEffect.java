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

import io.github.poqdavid.nyx.nyxcore.NyxCore;
import io.github.poqdavid.nyx.nyxcore.Permissions.EffectPermission;
import io.github.poqdavid.nyx.nyxcore.Utils.CText;
import io.github.poqdavid.nyx.nyxcore.Utils.Commands.Help;
import io.github.poqdavid.nyx.nyxcore.Utils.CoreTools;
import io.github.poqdavid.nyx.nyxcore.Utils.NCLogger;
import io.github.poqdavid.nyx.nyxeffect.Commands.HelpCMD;
import io.github.poqdavid.nyx.nyxeffect.Commands.MainCMD;
import io.github.poqdavid.nyx.nyxeffect.Listeners.NyxEffectListener;
import io.github.poqdavid.nyx.nyxeffect.Tasks.EffectTask;
import io.github.poqdavid.nyx.nyxeffect.Tasks.MovementDetectionTask;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.EffectsData;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.ParticlesData;
import io.github.poqdavid.nyx.nyxeffect.Utils.Data.PlayerData;
import io.github.poqdavid.nyx.nyxeffect.Utils.Tools;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import io.github.poqdavid.nyx.nyxcore.Utils.Forge.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static io.github.poqdavid.nyx.nyxcore.Utils.CoreTools.getForgeMod;

@Mod(
        modid = "@id@",
        name = "@name@",
        version = "@version@",
        serverSideOnly = true,
        acceptableRemoteVersions="*",
        dependencies = "required-after:nyxcore"
)
public class NyxEffect {
    public static final String MODID = "nyxeffect";

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(value = MODID)
    public static NyxEffect instance;

    public static NyxEffect nyxeffect;

    public static boolean HOLDEFFECTS = false;
    public static List<String> EffectCMDs = new ArrayList<>();
    public static Map<String, Thread> NyxEffectThreads = new HashMap<>();
    public Path particlesdatapath;
    public Map<UUID, PlayerData> PlayerEvent;
    public Map<String, List<String>> UserParticlesLIST;
    public List<ParticlesData> ParticlesLIST;
    public List<Block> restricted_blocks;
    public List<Block> allowed_blocks;
    public List<String> blockedPixelmonBlocks;
    public boolean pixelmon = false;
    public NCLogger logger;
    public Path recordsDir;
    private Path userparticledatapath;
    private Path configfullpath;
    private final ModContainer modContainer;

    public static Help help;

    public NyxEffect() {
        nyxeffect = this;
        modContainer = getForgeMod(MODID);

        MinecraftForge.EVENT_BUS.register(new NyxEffectListener());

        this.logger = NyxCore.getInstance().getLogger(CText.get(CText.Colors.BLUE, 1, "Nyx") + CText.get(CText.Colors.MAGENTA, 0, "Effect"));

        this.logger.info(" ");
        this.logger.info(CText.get(CText.Colors.MAGENTA, 0, "@name@") + CText.get(CText.Colors.YELLOW, 0, " v" + this.getVersion()));
        this.logger.info("Starting...");
        this.logger.info(" ");
    }

    @Nonnull
    public static NyxEffect getInstance() {
        return nyxeffect;
    }

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
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


        this.logger.info(" ");
        this.logger.info(CText.get(CText.Colors.MAGENTA, 0, "@name@") + CText.get(CText.Colors.YELLOW, 0, " v" + this.getVersion()));
        this.logger.info("Initializing...");
        this.logger.info(" ");
        nyxeffect = this;
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
    public NCLogger getLogger() {
        return logger;
    }

    @Nonnull
    public ModContainer getModContainer() {
        return this.modContainer;
    }

    @Nonnull
    public String getVersion() {
        if (this.getModContainer().getVersion() != null) {
            return this.getModContainer().getVersion();
        } else {
            return "@version@";
        }
    }

    @Nonnull
    public NCLogger getLogger(String name) {
        if (name == null || name.isEmpty()) {
            return this.logger;
        } else {
            return new NCLogger(this.logger.LoggerName + CText.get(CText.Colors.YELLOW, 0, " - ") + CText.get(CText.Colors.BLUE, 1, name));
        }
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        this.ParticlesLIST = Tools.loadparticles(this.particlesdatapath);
        this.UserParticlesLIST = Tools.loaduserparticles(this.userparticledatapath);

        PermissionAPI.registerNode(EffectPermission.COMMAND_MAIN, DefaultPermissionLevel.NONE, "Allows the use of /NyxEffect");

        PermissionAPI.registerNode(EffectPermission.COMMAND_CHECKOTHERS, DefaultPermissionLevel.NONE, "Allows the use of /nyxeffectcheck");

        PermissionAPI.registerNode(EffectPermission.COMMAND_DISABLEOTHERS, DefaultPermissionLevel.NONE, "Allows the use of /nyxeffectdisable");

        PermissionAPI.registerNode(EffectPermission.COMMAND_EFFECT, DefaultPermissionLevel.NONE, "Allows the use of /nyxeffect");

        PermissionAPI.registerNode(EffectPermission.COMMAND_EFFECT_OTHER, DefaultPermissionLevel.NONE, "Allows the use of /nyxeffect <player>");

        PermissionAPI.registerNode(EffectPermission.COMMAND_TASK, DefaultPermissionLevel.NONE, "Allows the use of /nyxeffecttask");

        PermissionAPI.registerNode(EffectPermission.COMMAND_TASK_OTHER, DefaultPermissionLevel.NONE, "Allows the use of /nyxeffecttask <player>");

        PermissionAPI.registerNode(EffectPermission.COMMAND_LIST, DefaultPermissionLevel.NONE, "Allows the use of /nyxeffectlist");

        RegisterEffectNodes();
        LoadEffectCMDs();

        try {
            if (!Files.exists(this.getConfigPath())) {
                Files.createDirectories(this.getConfigPath());
            }
        } catch (final IOException ex) {
            this.logger.error("Error on creating root plugin directory: {}", ex);
        }

        this.restricted_blocks.add(Blocks.ACACIA_DOOR);
        this.restricted_blocks.add(Blocks.ACACIA_FENCE);
        this.restricted_blocks.add(Blocks.ACACIA_FENCE_GATE);
        this.restricted_blocks.add(Blocks.ACACIA_STAIRS);
        this.restricted_blocks.add(Blocks.ACTIVATOR_RAIL);
        this.restricted_blocks.add(Blocks.AIR);
        this.restricted_blocks.add(Blocks.ANVIL);
        this.restricted_blocks.add(Blocks.BARRIER);
        this.restricted_blocks.add(Blocks.BEACON);
        this.restricted_blocks.add(Blocks.BED);
        this.restricted_blocks.add(Blocks.BEETROOTS);
        this.restricted_blocks.add(Blocks.BIRCH_DOOR);
        this.restricted_blocks.add(Blocks.BIRCH_FENCE);
        this.restricted_blocks.add(Blocks.BIRCH_FENCE_GATE);
        this.restricted_blocks.add(Blocks.BIRCH_STAIRS);
        this.restricted_blocks.add(Blocks.BREWING_STAND);
        this.restricted_blocks.add(Blocks.BRICK_STAIRS);
        this.restricted_blocks.add(Blocks.BROWN_MUSHROOM);
        this.restricted_blocks.add(Blocks.CAKE);
        this.restricted_blocks.add(Blocks.CARPET);
        this.restricted_blocks.add(Blocks.CARROTS);
        this.restricted_blocks.add(Blocks.CAULDRON);
        this.restricted_blocks.add(Blocks.CHORUS_FLOWER);
        this.restricted_blocks.add(Blocks.CHORUS_PLANT);
        this.restricted_blocks.add(Blocks.COBBLESTONE_WALL);
        this.restricted_blocks.add(Blocks.COCOA);
        this.restricted_blocks.add(Blocks.DARK_OAK_DOOR);
        this.restricted_blocks.add(Blocks.DARK_OAK_FENCE);
        this.restricted_blocks.add(Blocks.DARK_OAK_FENCE_GATE);
        this.restricted_blocks.add(Blocks.DARK_OAK_STAIRS);
        this.restricted_blocks.add(Blocks.DAYLIGHT_DETECTOR);
        this.restricted_blocks.add(Blocks.DAYLIGHT_DETECTOR_INVERTED);
        this.restricted_blocks.add(Blocks.DEADBUSH);
        this.restricted_blocks.add(Blocks.DETECTOR_RAIL);
        this.restricted_blocks.add(Blocks.DOUBLE_PLANT);
        this.restricted_blocks.add(Blocks.ENCHANTING_TABLE);
        this.restricted_blocks.add(Blocks.END_PORTAL);
        this.restricted_blocks.add(Blocks.END_PORTAL_FRAME);
        this.restricted_blocks.add(Blocks.END_ROD);
        this.restricted_blocks.add(Blocks.FIRE);
        this.restricted_blocks.add(Blocks.FLOWER_POT);
        this.restricted_blocks.add(Blocks.GLASS_PANE);
        this.restricted_blocks.add(Blocks.GOLDEN_RAIL);
        this.restricted_blocks.add(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        this.restricted_blocks.add(Blocks.HOPPER);
        this.restricted_blocks.add(Blocks.IRON_BARS);
        this.restricted_blocks.add(Blocks.IRON_DOOR);
        this.restricted_blocks.add(Blocks.IRON_TRAPDOOR);
        this.restricted_blocks.add(Blocks.JUNGLE_DOOR);
        this.restricted_blocks.add(Blocks.JUNGLE_FENCE);
        this.restricted_blocks.add(Blocks.JUNGLE_FENCE_GATE);
        this.restricted_blocks.add(Blocks.JUNGLE_STAIRS);
        this.restricted_blocks.add(Blocks.LADDER);
        this.restricted_blocks.add(Blocks.LAVA);
        this.restricted_blocks.add(Blocks.LEVER);
        this.restricted_blocks.add(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
        this.restricted_blocks.add(Blocks.MELON_STEM);
        this.restricted_blocks.add(Blocks.NETHER_BRICK_FENCE);
        this.restricted_blocks.add(Blocks.NETHER_BRICK_STAIRS);
        this.restricted_blocks.add(Blocks.NETHER_WART);
        this.restricted_blocks.add(Blocks.OAK_STAIRS);
        this.restricted_blocks.add(Blocks.POTATOES);
        this.restricted_blocks.add(Blocks.POWERED_COMPARATOR);
        this.restricted_blocks.add(Blocks.POWERED_REPEATER);
        this.restricted_blocks.add(Blocks.PUMPKIN_STEM);
        this.restricted_blocks.add(Blocks.PURPUR_STAIRS);
        this.restricted_blocks.add(Blocks.QUARTZ_STAIRS);
        this.restricted_blocks.add(Blocks.RAIL);
        this.restricted_blocks.add(Blocks.REDSTONE_TORCH);
        this.restricted_blocks.add(Blocks.REDSTONE_WIRE);
        this.restricted_blocks.add(Blocks.RED_FLOWER);
        this.restricted_blocks.add(Blocks.RED_MUSHROOM);
        this.restricted_blocks.add(Blocks.RED_SANDSTONE_STAIRS);
        this.restricted_blocks.add(Blocks.REEDS);
        this.restricted_blocks.add(Blocks.SANDSTONE_STAIRS);
        this.restricted_blocks.add(Blocks.SKULL);
        this.restricted_blocks.add(Blocks.SPRUCE_DOOR);
        this.restricted_blocks.add(Blocks.SPRUCE_FENCE);
        this.restricted_blocks.add(Blocks.SPRUCE_FENCE_GATE);
        this.restricted_blocks.add(Blocks.SPRUCE_STAIRS);
        this.restricted_blocks.add(Blocks.STAINED_GLASS_PANE);
        this.restricted_blocks.add(Blocks.STANDING_BANNER);
        this.restricted_blocks.add(Blocks.STANDING_SIGN);
        this.restricted_blocks.add(Blocks.STONE_BRICK_STAIRS);
        this.restricted_blocks.add(Blocks.STONE_BUTTON);
        this.restricted_blocks.add(Blocks.STONE_PRESSURE_PLATE);
        this.restricted_blocks.add(Blocks.STONE_SLAB);
        this.restricted_blocks.add(Blocks.STONE_SLAB2);
        this.restricted_blocks.add(Blocks.STONE_STAIRS);
        this.restricted_blocks.add(Blocks.STRUCTURE_VOID);
        this.restricted_blocks.add(Blocks.TALLGRASS);
        this.restricted_blocks.add(Blocks.TORCH);
        this.restricted_blocks.add(Blocks.TRAPDOOR);
        this.restricted_blocks.add(Blocks.TRIPWIRE);
        this.restricted_blocks.add(Blocks.TRIPWIRE_HOOK);
        this.restricted_blocks.add(Blocks.UNPOWERED_COMPARATOR);
        this.restricted_blocks.add(Blocks.UNPOWERED_REPEATER);
        this.restricted_blocks.add(Blocks.VINE);
        this.restricted_blocks.add(Blocks.WALL_BANNER);
        this.restricted_blocks.add(Blocks.WALL_SIGN);
        this.restricted_blocks.add(Blocks.WATER);
        this.restricted_blocks.add(Blocks.WATERLILY);
        this.restricted_blocks.add(Blocks.WEB);
        this.restricted_blocks.add(Blocks.WHEAT);
        this.restricted_blocks.add(Blocks.WOODEN_BUTTON);
        this.restricted_blocks.add(Blocks.WOODEN_PRESSURE_PLATE);
        this.restricted_blocks.add(Blocks.WOODEN_SLAB);
        this.restricted_blocks.add(Blocks.YELLOW_FLOWER);
        this.restricted_blocks.add(Blocks.SNOW_LAYER);
        this.restricted_blocks.add(Blocks.CHEST);
        this.restricted_blocks.add(Blocks.ENDER_CHEST);
        this.restricted_blocks.add(Blocks.TRAPPED_CHEST);

        //this.restricted_blocks.add(Blocks.FENCE);
        //this.restricted_blocks.add(Blocks.FENCE_GATE);
        //this.restricted_blocks.add(Blocks.WOODEN_DOOR);

        for (String blockid : blockedPixelmonBlocks) {
            try {
                //if (Sponge.getRegistry().getType(BlockType.class, blockid).isPresent()) {
                //this.restricted_blocks.add(Sponge.getRegistry().getType(BlockType.class, blockid).get());
                this.restricted_blocks.add(Block.getBlockFromName(blockid));
                //}
            } catch (Exception e) {

            }
        }

        //NyxEffectListener listener = new NyxEffectListener();
        //Sponge.getEventManager().registerListeners(this, listener);

        if (Loader.isModLoaded("pixelmon")) {
            this.pixelmon = true;
            this.logger.info("Pixelmon detected!");
        }

        this.logger.info("Plugin Initialized successfully!");
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        help = new Help(this.getVersion(), "NyxEffect", "POQDavid");
        help.addLine("");

        help.addLine("§l§aCommands");
        help.addLine("§6- /§7nyxeffect effect §6<§7effect§6> §6<§7player§6>");
        help.addLine("§6- /§7nyxeffect list §6<§7option(more / all)§6>");
        help.addLine("§6- /§7nyxeffect check §6<§7user§6>");
        help.addLine("§6- /§7nyxeffect disable §6<§7user§6>");

        event.registerServerCommand(new MainCMD());
    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

    public void ParticleAdd(String uuid, List<String> pd) {
        this.UserParticlesLIST.putIfAbsent(uuid, pd);
        Tools.saveparticles(this.UserParticlesLIST, this.userparticledatapath);
    }

    public void ParticleRemove(String uuid) {
        this.UserParticlesLIST.remove(uuid);
        Tools.saveparticles(this.UserParticlesLIST, this.userparticledatapath);
    }

    public void ParticleToggle(EntityPlayerMP player_args, EffectsData effect) {
        ParticleToggle(player_args, player_args, effect);
    }

    public void ParticleToggle(EntityPlayerMP player_cmd_src, EntityPlayerMP player_args, EffectsData effect) {
        UUID uuid_args = player_args.getUniqueID();
        String name_args = player_args.getName();

        EntityPlayerMP player_src = null;
        String name_src = "Console";
        UUID uuid_src = UUID.fromString("00000000-0000-0000-0000-000000000000");

        if (player_cmd_src != null) {
            player_src = CoreTools.getPlayer(player_cmd_src);
            name_src = player_src.getName();
            uuid_src = player_src.getUniqueID();
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


                if (uuid_src.equals(player_args.getUniqueID())) {
                    player_args.sendMessage(TextComponentString.from(TextFormatting.GOLD, name_args, " You have disabled your ", effect.getName(), " particles"));
                } else {
                    player_args.sendMessage(TextComponentString.from(TextFormatting.YELLOW, name_src, " has disabled your ", effect.getName(), "!"));
                    if (player_cmd_src != null) {
                        player_cmd_src.sendMessage(TextComponentString.from(TextFormatting.YELLOW, name_args, "'s ", effect.getName(), " has been disabled!"));
                    }
                }
            } else {
                pd.remove("NONE");
                pd.add(effect.getId());
                this.ParticleSet(player_args, pd);

                Tools.AddMovementTask(player_args);
                Tools.AddEffectTask(player_args, effect.getId());

                if (uuid_src.equals(player_args.getUniqueID())) {
                    player_args.sendMessage(TextComponentString.from(TextFormatting.GOLD, name_args, TextFormatting.AQUA, " You have enabled your ", effect.getName(), " particles"));
                } else {
                    player_args.sendMessage(TextComponentString.from(TextFormatting.YELLOW, name_src, " has given you ", effect.getName(), "!"));
                    if (player_cmd_src != null) {
                        player_cmd_src.sendMessage(TextComponentString.from(TextFormatting.YELLOW, name_args, " has been given ", effect.getName(), "!"));
                    }
                }
            }

        } else {
            final List<String> pd = new ArrayList<>();
            pd.add(effect.getId());
            this.ParticleSet(player_args, pd);

            Tools.AddMovementTask(player_args);
            Tools.AddEffectTask(player_args, effect.getId());

            if (uuid_src.equals(player_args.getUniqueID())) {
                player_args.sendMessage(TextComponentString.from(TextFormatting.GOLD, name_args, TextFormatting.AQUA, " You have enabled your ", effect.getName(), " particles"));
            } else {
                player_args.sendMessage(TextComponentString.from(TextFormatting.YELLOW, name_src, " has given you ", effect.getName(), "!"));
                if (player_cmd_src != null) {
                    player_cmd_src.sendMessage(TextComponentString.from(TextFormatting.YELLOW, name_args, " has been given ", effect.getName(), "!"));
                }
            }
        }

        if (player_cmd_src instanceof EntityPlayerMP) {
            if (player_src != null) {
                player_src.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                //player_src.getServerWorld().playSound(player_src, player_src.getPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
        }

    }

    public void ParticleSet(EntityPlayerMP player, List<String> pd) {
        final String uuid = player.getUniqueID().toString();
        this.UserParticlesLIST.put(uuid, pd);
        Tools.saveparticles(this.UserParticlesLIST, this.userparticledatapath);
    }

    public void ParticleClearAll(EntityPlayerMP player) {
        final List<String> temp = new ArrayList<>();
        temp.add("NONE");

        final String uuid = player.getUniqueID().toString();

        this.UserParticlesLIST.put(uuid, temp);

        for (String task : NyxEffectThreads.keySet()) {
            if (task.contains(uuid)) {
                if (NyxEffectThreads.get(task) instanceof EffectTask) {
                    ((EffectTask) NyxEffectThreads.get(task)).taskStop = true;
                }

                if (NyxEffectThreads.get(task) instanceof MovementDetectionTask) {
                    ((MovementDetectionTask) NyxEffectThreads.get(task)).taskStop = true;
                }
            }
        }

        Tools.saveparticles(this.UserParticlesLIST, this.userparticledatapath);
    }

    public void RegisterEffectNodes() {
        if (this.ParticlesLIST != null) {
            for (ParticlesData effect : this.ParticlesLIST) {
                PermissionAPI.registerNode(EffectPermission.EFFECTS + "." + effect.getEffectsData().getId(), DefaultPermissionLevel.OP, "Allows the use of effect with id of " + effect.getEffectsData().getId());
                this.logger.info("Effect node: " + EffectPermission.EFFECTS + "." + effect.getEffectsData().getId());
            }
        }
    }

    public void LoadEffectCMDs() {
        if (this.ParticlesLIST != null) {
            EffectCMDs.clear();
            for (ParticlesData effect : this.ParticlesLIST) {
                EffectCMDs.add(effect.getEffectsData().getId());
            }
        }

    }
}
