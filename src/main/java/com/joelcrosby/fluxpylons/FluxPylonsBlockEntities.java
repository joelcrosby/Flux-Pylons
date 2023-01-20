package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.crate.CrateBlockEntity;
import com.joelcrosby.fluxpylons.machine.ChamberBlockEntity;
import com.joelcrosby.fluxpylons.machine.SmelterBlockEntity;
import com.joelcrosby.fluxpylons.machine.WasherBlockEntity;
import com.joelcrosby.fluxpylons.pipe.PipeBlockEntity;
import com.joelcrosby.fluxpylons.pipe.PipeType;
import com.joelcrosby.fluxpylons.pylon.PylonBlockEntity;
import com.joelcrosby.fluxpylons.pylon.network.graph.PylonGraphNodeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(FluxPylons.ID)
public class FluxPylonsBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FluxPylons.ID);
    
    public static final RegistryObject<BlockEntityType<PipeBlockEntity>> BASIC_PIPE = BLOCK_ENTITIES_REGISTRY
            .register("pipe", () -> BlockEntityType.Builder.of((pos, state) -> new PipeBlockEntity(pos, state, PipeType.BASIC), FluxPylonsBlocks.BASIC_PIPE.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<PipeBlockEntity>> ADV_PIPE = BLOCK_ENTITIES_REGISTRY
            .register("adv_pipe", () -> BlockEntityType.Builder.of((pos, state) -> new PipeBlockEntity(pos, state, PipeType.BASIC), FluxPylonsBlocks.ADV_PIPE.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<CrateBlockEntity>> CRATE = BLOCK_ENTITIES_REGISTRY
            .register("crate", () -> BlockEntityType.Builder.of(CrateBlockEntity::new, FluxPylonsBlocks.CRATE.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<PylonBlockEntity>> PYLON = BLOCK_ENTITIES_REGISTRY
            .register("pylon", () -> BlockEntityType.Builder.of((pos, state) -> new PylonBlockEntity(pos, state, PylonGraphNodeType.BASIC), FluxPylonsBlocks.PYLON.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<ChamberBlockEntity>> CHAMBER = BLOCK_ENTITIES_REGISTRY
            .register("chamber", () -> BlockEntityType.Builder.of(ChamberBlockEntity::new, FluxPylonsBlocks.CHAMBER.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<SmelterBlockEntity>> SMELTER = BLOCK_ENTITIES_REGISTRY
            .register("smelter", () -> BlockEntityType.Builder.of(SmelterBlockEntity::new, FluxPylonsBlocks.SMELTER.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<WasherBlockEntity>> WASHER = BLOCK_ENTITIES_REGISTRY
            .register("washer", () -> BlockEntityType.Builder.of(WasherBlockEntity::new, FluxPylonsBlocks.WASHER.get()).build(null));
    
}
