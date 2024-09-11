package com.greenbean.clackers;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

import net.minecraft.SharedConstants;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import java.util.List;
import java.util.ArrayList;

// 這個值應該和 META-INF/mods.toml 文件中的一個條目對應
@Mod(ClackersMod.MODID)
public class ClackersMod {
    
    // 定義模組ID，方便各處引用
    public static final String MODID = "clackers";
    // 直接引用slf4j的日誌記錄器
    private static final Logger LOGGER = LogUtils.getLogger();
    // 創建一個延遲註冊器來保存Block，這些Block都會註冊在"clackers"命名空間下
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // 創建一個延遲註冊器來保存Item，這些Item都會註冊在"clackers"命名空間下
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // 創建一個延遲註冊器來保存CreativeModeTab，這些Tab都會註冊在"clackers"命名空間下
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 創建一個新的Block，ID是"clackers:greenbean_block"，結合命名空間和路徑
    public static final RegistryObject<Block> GREENBEAN_BLOCK = BLOCKS.register("greenbean_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // 創建一個新的BlockItem，ID是"clackers:greenbean_block"，結合命名空間和路徑
    public static final RegistryObject<Item> GREENBEAN_BLOCK_ITEM = ITEMS.register("greenbean_block", () -> new BlockItem(GREENBEAN_BLOCK.get(), new Item.Properties()));

    // 創建一個新的食物Item，ID是"clackers:greenbean_item"，營養1，飽和度2
    public static final RegistryObject<Item> GREENBEAN_ITEM = ITEMS.register("greenbean_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    // 創建一個名為 clackers 的物品並將其添加到戰鬥分類中
    public static final RegistryObject<Item> CLACKERS_ITEM = ITEMS.register("clackers", () -> new Item(new Item.Properties().stacksTo(1).fireResistant()));

    // 創建一個創造模式的分類，ID是"clackers:greenbean_tab"，放置在戰鬥分類之後，包含greenbean_item
    public static final RegistryObject<CreativeModeTab> GREENBEAN_TAB = CREATIVE_MODE_TABS.register("greenbean_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.translatable("itemGroup.clackers.greenbean_tab"))
            .icon(() -> GREENBEAN_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(GREENBEAN_ITEM.get()); // 把greenbean_item加到這個分類。對於自定義的分類，這種方法比事件更好
            }).build());

    public ClackersMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 註冊模組加載的commonSetup方法
        modEventBus.addListener(this::commonSetup);

        // 註冊延遲註冊器到模組事件總線，這樣Block就會被註冊
        BLOCKS.register(modEventBus);
        // 註冊延遲註冊器到模組事件總線，這樣Item就會被註冊
        ITEMS.register(modEventBus);
        // 註冊延遲註冊器到模組事件總線，這樣創造模式分類就會被註冊
        CREATIVE_MODE_TABS.register(modEventBus);

        // 註冊自己到伺服器和其他我們感興趣的遊戲事件
        MinecraftForge.EVENT_BUS.register(this);

        // 把Item註冊到創造模式分類
        modEventBus.addListener(this::addCreative);

        // 註冊模組的ForgeConfigSpec，這樣Forge可以為我們創建和加載配置文件
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // 一些通用的設置代碼
        LOGGER.info("從通用設置打招呼");

        if (Config.logDirtBlock)
            LOGGER.info("泥土方塊 >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("Item >> {}", item.toString()));
    }

    // 把clackers_item加到戰鬥分類
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(CLACKERS_ITEM.get());
        }
        if (event.getTabKey().equals(GREENBEAN_TAB.getKey())) {
            event.accept(GREENBEAN_BLOCK_ITEM.get());
            event.accept(GREENBEAN_ITEM.get());
        }
    }
    
    

    // 你可以使用@SubscribeEvent，讓事件總線自動發現並調用方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // 當伺服器啟動時做一些事情
        LOGGER.info("伺服器啟動時打招呼");
    }

    // 你可以使用EventBusSubscriber，自動註冊類中所有用@SubscribeEvent標註的靜態方法
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            

            // 一些客戶端設置代碼
            LOGGER.info("從客戶端設置打招呼");
            LOGGER.info("Minecraft名字 >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
