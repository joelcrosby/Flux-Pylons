package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.crate.CrateBlock;
import com.joelcrosby.fluxpylons.machine.SmelterBlock;
import com.joelcrosby.fluxpylons.machine.WasherBlock;
import com.joelcrosby.fluxpylons.pipe.PipeBlock;
import com.joelcrosby.fluxpylons.pipe.PipeType;
import com.joelcrosby.fluxpylons.pylon.PylonBlock;
import com.joelcrosby.fluxpylons.setup.Common;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FluxPylonsBlocks
{
    public static final DeferredRegister<Block> BLOCKS_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, FluxPylons.ID);

    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(Common.TAB);
    
    public static RegistryObject<PipeBlock> BASIC_PIPE = BLOCKS_REGISTRY.register("pipe", () -> new PipeBlock(PipeType.BASIC));
    public static RegistryObject<Item> BASIC_PIPE_ITEM = fromBlock(BASIC_PIPE);
    
    public static RegistryObject<PipeBlock> ADV_PIPE = BLOCKS_REGISTRY.register("adv_pipe", () -> new PipeBlock(PipeType.ADVANCED));
    public static RegistryObject<Item> ADV_PIPE_ITEM = fromBlock(ADV_PIPE);
    
    public static RegistryObject<CrateBlock> CRATE = BLOCKS_REGISTRY.register("crate", CrateBlock::new);
    public static RegistryObject<Item> CRATE_ITEM = fromBlock(CRATE);
    
    public static RegistryObject<PylonBlock> PYLON = BLOCKS_REGISTRY.register("pylon", PylonBlock::new);
    public static RegistryObject<Item> PYLON_ITEM = fromBlock(PYLON);
    
    public static RegistryObject<SmelterBlock> SMELTER = BLOCKS_REGISTRY.register("smelter", SmelterBlock::new);
    public static RegistryObject<Item> SMELTER_ITEM = fromBlock(SMELTER);
    
    public static RegistryObject<WasherBlock> WASHER = BLOCKS_REGISTRY.register("washer", WasherBlock::new);
    public static RegistryObject<Item> WASHER_ITEM = fromBlock(WASHER);

    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return FluxPylonsItems.ITEM_REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }
}
