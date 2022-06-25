package com.joelcrosby.fluxpylons.setup;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.FluxPylonsItems;
import com.joelcrosby.fluxpylons.crate.CrateBlock;
import com.joelcrosby.fluxpylons.crate.CrateBlockEntity;
import com.joelcrosby.fluxpylons.crate.CrateContainerMenu;
import com.joelcrosby.fluxpylons.item.WrenchItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.ExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.FluidExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.RetrieverItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.BasicFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.FluidFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.FluidFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.ItemFilterContainerMenu;
import com.joelcrosby.fluxpylons.pipe.*;
import com.joelcrosby.fluxpylons.pylon.PylonBlock;
import com.joelcrosby.fluxpylons.pylon.PylonBlockEntity;
import com.joelcrosby.fluxpylons.util.ClearNbtRecipe;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Common {
    
    public static final CreativeModeTab TAB = new CreativeModeTab(FluxPylons.ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(FluxPylonsItems.WRENCH);
        }
    };
    
    public static Capability<IPipeConnectable> pipeConnectableCapability = CapabilityManager.get(
            new CapabilityToken<>() {});
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new CrateBlock().setRegistryName("crate"),
                new PipeBlock(PipeType.BASIC).setRegistryName("pipe"),
                new PipeBlock(PipeType.ADVANCED).setRegistryName("adv_pipe"),
                new PylonBlock().setRegistryName("pylon")
        );
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        var registry = event.getRegistry();

        registry.register(new WrenchItem().setRegistryName("wrench"));
        registry.register(new ExtractItem().setRegistryName("upgrade_extract"));
        registry.register(new RetrieverItem().setRegistryName("upgrade_retriever"));
        registry.register(new FluidExtractItem().setRegistryName("upgrade_fluid_extract"));
        registry.register(new BasicFilterItem().setRegistryName("upgrade_filter"));
        registry.register(new FluidFilterItem().setRegistryName("upgrade_fluid_filter"));
        registry.register(new TagFilterItem().setRegistryName("upgrade_tag_filter"));

        registry.register(new BlockItem(FluxPylonsBlocks.CRATE, new Item.Properties().rarity(Rarity.COMMON).tab(TAB)).setRegistryName(FluxPylonsBlocks.CRATE.getRegistryName()));
        registry.register(new BlockItem(FluxPylonsBlocks.BASIC_PIPE, new Item.Properties().rarity(Rarity.UNCOMMON).tab(TAB)).setRegistryName(FluxPylonsBlocks.BASIC_PIPE.getRegistryName()));
        registry.register(new BlockItem(FluxPylonsBlocks.ADV_PIPE, new Item.Properties().rarity(Rarity.RARE).tab(TAB)).setRegistryName(FluxPylonsBlocks.ADV_PIPE.getRegistryName()));
        registry.register(new BlockItem(FluxPylonsBlocks.PYLON, new Item.Properties().rarity(Rarity.COMMON).tab(TAB)).setRegistryName(FluxPylonsBlocks.PYLON.getRegistryName()));
    }

    @SubscribeEvent
    public static void registerBlockEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        event.getRegistry().registerAll(
                BlockEntityType.Builder.of(CrateBlockEntity::new, FluxPylonsBlocks.CRATE).build(null).setRegistryName("crate"),
                BlockEntityType.Builder.of((pos, state) -> new PipeBlockEntity(pos, state, PipeType.BASIC), FluxPylonsBlocks.BASIC_PIPE).build(null).setRegistryName("pipe"),
                BlockEntityType.Builder.of((pos, state) -> new PipeBlockEntity(pos, state, PipeType.ADVANCED), FluxPylonsBlocks.ADV_PIPE).build(null).setRegistryName("adv_pipe"),
                BlockEntityType.Builder.of(PylonBlockEntity::new, FluxPylonsBlocks.PYLON).build(null).setRegistryName("pylon")
        );
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new CrateContainerMenu(windowId, inv.player, data.readBlockPos())).setRegistryName("crate"));
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new PipeUpgradeContainerMenu(windowId, inv.player, new PipeUpgradeItemStackHandler(), data)).setRegistryName("upgrade"));
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new ItemFilterContainerMenu(windowId, inv.player, data.readItem())).setRegistryName("filter"));
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new FluidFilterContainerMenu(windowId, inv.player, data.readItem())).setRegistryName("fluid_filter"));
        event.getRegistry().register(IForgeMenuType.create((windowId, inv, data) -> new TagFilterContainerMenu(windowId, inv.player, data.readItem())).setRegistryName("tag_filter"));
    }

    public static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        event.getRegistry().register(new ClearNbtRecipe.Serializer().setRegistryName("fluxpylons:clear_nbt"));
    }
}
