package com.ddemarco5.TFCWaystones.item;

import com.ddemarco5.TFCWaystones.TFCWaystones;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.CreativeTabsTFC;
import net.dries007.tfc.objects.items.ItemTFC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

public class ItemObsidianKnifeHead extends ItemTFC {

    public ItemObsidianKnifeHead() {
        setRegistryName(TFCWaystones.MOD_ID, "obsidian_knife_head");
        setUnlocalizedName(TFCWaystones.MOD_ID.toLowerCase() + "." + "obsidian_knife_head");
        setCreativeTab(CreativeTabsTFC.CT_ROCK_ITEMS);
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL; // Stored in large vessels
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.LIGHT;
    }

    public void registerOres(){
        OreDictionary.registerOre("knifeHead", this);
        OreDictionary.registerOre("knifeHeadObsidian", this);
    }

}
