package com.ddemarco5.TFCWaystones.registry;

import com.ddemarco5.TFCWaystones.TFCWaystones;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;


import java.util.List;


@Mod.EventBusSubscriber
public class EventListeners {

    private static Logger logger = TFCWaystones.logger;

    // Change vanilla obsidian blocks to drop our rocks instead of blocks
    @SubscribeEvent
    public static void harvestDropsEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() != null) {
            if (event.getState() == Blocks.OBSIDIAN.getDefaultState()) {
                List<ItemStack> dropList = event.getDrops();
                dropList.clear();
                dropList.add(new ItemStack(TFCWaystones.OBSIDIAN_ROCK, 4));
            }
        }
    }

}
