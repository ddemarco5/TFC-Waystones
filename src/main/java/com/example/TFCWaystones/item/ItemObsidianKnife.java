package com.example.TFCWaystones.item;

import com.example.TFCWaystones.TFCWaystones;
import com.google.common.collect.Iterables;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.oredict.OreDictionary;

public class ItemObsidianKnife extends ItemTool implements IItemSize {

    public ItemObsidianKnife() {

        // We don't want to hardcode in values in case they change them in tfc, so we just grab the first value
        // in the ROCK_CATEGORIES registry and use its stats
        super(-0.46f * Iterables.get(TFCRegistries.ROCK_CATEGORIES, 0).getToolMaterial().getAttackDamage(),
                -1.5f, Iterables.get(TFCRegistries.ROCK_CATEGORIES, 0).getToolMaterial(), ImmutableSet.of());

        setHarvestLevel("knife", Iterables.get(TFCRegistries.ROCK_CATEGORIES, 0).getToolMaterial().getHarvestLevel());
        setRegistryName(TFCWaystones.MOD_ID, "obsidian_knife");
        setUnlocalizedName(TFCWaystones.MOD_ID.toLowerCase() + "." + "obsidian_knife");

    }

    public void registerOres(){
        OreDictionary.registerOre("damageTypePiercing", (Item) this);
        OreDictionary.registerOre("knife", (Item) this);
        OreDictionary.registerOre("knifeStone", (Item) this);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("A sacrificial dagger");
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.NORMAL; // Stored in large vessels
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.MEDIUM;
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }

}
