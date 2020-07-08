package com.example.TFCWaystones.gui;

import com.example.TFCWaystones.TFCWaystones;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.client.gui.GuiKnapping;
import net.dries007.tfc.objects.container.ContainerKnapping;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    public static final KnappingType OBSIDIAN_KNAPPING = new KnappingType(1, false);

    private static final ResourceLocation OBSIDIAN_TEXTURE = new ResourceLocation(TFCWaystones.MOD_ID, "textures/gui/obsidian_button.png");


    public static void openGui(World world, EntityPlayer player, Type type)
    {
        player.openGui(TFCWaystones.getInstance(), type.ordinal(), world, 0, 0, 0);
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        BlockPos pos = new BlockPos(x, y, z);
        ItemStack stack = player.getHeldItemMainhand();
        Type type = Type.valueOf(ID);
        switch (type)
        {
            case KNAPPING_OBSIDIAN:
                return new ContainerKnapping(OBSIDIAN_KNAPPING, player.inventory, OreDictionaryHelper.doesStackMatchOre(stack, "rockObsidian") ? stack : player.getHeldItemOffhand());
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        Container container = (Container) getServerGuiElement(ID, player, world, x, y, z);
        Type type = Type.valueOf(ID);
        BlockPos pos = new BlockPos(x, y, z);

        switch (type)
        {
            case KNAPPING_OBSIDIAN:
                return new GuiKnapping(container, player, OBSIDIAN_KNAPPING, OBSIDIAN_TEXTURE);
            default:
                return null;
        }
    }

    public enum Type {
        KNAPPING_OBSIDIAN,
        NULL;


        private static final Type[] values = values();

        @Nonnull
        public static Type valueOf(int id) {
            return id < 0 || id >= values.length ? NULL : values[id];
        }
    }
}
