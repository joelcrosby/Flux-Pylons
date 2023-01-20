package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.crate.CrateBlock;
import com.joelcrosby.fluxpylons.machine.ChamberBlock;
import com.joelcrosby.fluxpylons.machine.SmelterBlock;
import com.joelcrosby.fluxpylons.machine.WasherBlock;
import com.joelcrosby.fluxpylons.pipe.PipeBlock;
import com.joelcrosby.fluxpylons.pipe.PipeType;
import com.joelcrosby.fluxpylons.pylon.PylonBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(FluxPylons.ID)
public class FluxPylonsBlocks
{
    public static final DeferredRegister<Block> BLOCKS_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, FluxPylons.ID);
    
    public static RegistryObject<PipeBlock> BASIC_PIPE = BLOCKS_REGISTRY.register("pipe", () -> new PipeBlock(PipeType.BASIC));
    public static RegistryObject<PipeBlock> ADV_PIPE = BLOCKS_REGISTRY.register("adv_pipe", () -> new PipeBlock(PipeType.ADVANCED));
    public static RegistryObject<CrateBlock> CRATE = BLOCKS_REGISTRY.register("crate", CrateBlock::new);
    public static RegistryObject<PylonBlock> PYLON = BLOCKS_REGISTRY.register("pylon", PylonBlock::new);
    public static RegistryObject<ChamberBlock> CHAMBER = BLOCKS_REGISTRY.register("chamber", ChamberBlock::new);
    public static RegistryObject<SmelterBlock> SMELTER = BLOCKS_REGISTRY.register("smelter", SmelterBlock::new);
    public static RegistryObject<WasherBlock> WASHER = BLOCKS_REGISTRY.register("washer", WasherBlock::new);
}
