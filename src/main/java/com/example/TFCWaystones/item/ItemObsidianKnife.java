package com.example.TFCWaystones.item;

import com.example.TFCWaystones.TFCWaystones;
import com.google.common.collect.Iterables;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.oredict.OreDictionary;

public class ItemObsidianKnife extends ItemTool implements IItemSize {

    public static final double SACRIFICE_RANGE_BLOCKS = 2;
    public static final int SACRIFICE_DURATION_SECONDS = 3;

    private EntityAnimal TARGET_ANIMAL;

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
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return SACRIFICE_DURATION_SECONDS * 20; // in ticks
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        return EnumAction.BOW;
    }

    private List<EntityAnimal> getAnimalsAround(EntityPlayer playerIn) {

        // A length and width of SACRIFICE_RANGE_BLOCKS, but a hight of the player's eyes.
        // This is because we don't want people being able to sacrifice animals in a pit that won't run from them
        AxisAlignedBB playerBB = new AxisAlignedBB(playerIn.posX + SACRIFICE_RANGE_BLOCKS,
                //playerIn.posY + SACRIFICE_RANGE_BLOCKS,
                playerIn.posY + playerIn.getDefaultEyeHeight(), // Not Y, we don't want people cheesing and keeping an animal in a hole
                playerIn.posZ + SACRIFICE_RANGE_BLOCKS ,
                playerIn.posX - SACRIFICE_RANGE_BLOCKS ,
                //playerIn.posY - SACRIFICE_RANGE_BLOCKS,
                playerIn.posY, // Not Y, we don't want people cheesing and keeping an animal in a hole
                playerIn.posZ - SACRIFICE_RANGE_BLOCKS);

        List<Entity> entityList = playerIn.getEntityWorld().getEntitiesWithinAABBExcludingEntity(playerIn, playerBB);

        List<EntityAnimal> animalList = new ArrayList<>();
        for (Entity e : entityList) {
            if (e instanceof EntityAnimal) {
                animalList.add((EntityAnimal) e);
            }
        }
        return animalList;
    }

    private boolean isAnimalClickable(EntityPlayer playerIn, EntityAnimal animal) {

        Minecraft mc = Minecraft.getMinecraft();
        Vec3d posVec = playerIn.getPositionEyes(1f);
        Vec3d lookVec = playerIn.getLookVec();
        Vec3d magLookVec = lookVec.scale(SACRIFICE_RANGE_BLOCKS);
        Vec3d endVec = posVec.add(magLookVec);

        //TFCWaystones.logger.info("checking entity: {}", e.getName());
        RayTraceResult result = animal.getEntityBoundingBox().calculateIntercept(posVec, endVec);
        //TFCWaystones.logger.info("BB: {}", e.getEntityBoundingBox().toString());
        if (result != null) {
            //TFCWaystones.logger.info("Result: {}", result.toString());
            //TFCWaystones.logger.info("looking");

            boolean blockFound = false;
            double blockDistance = SACRIFICE_RANGE_BLOCKS + 1; // Bigger than is possible
            double animalDistance = playerIn.getDistanceSq(animal);

            // We see an animal, let's check if there are any blocks in front of it
            RayTraceResult blockTrace = mc.world.rayTraceBlocks(posVec, endVec);
            if (blockTrace != null) {
                Block blockDetected = playerIn.getEntityWorld().getBlockState(blockTrace.getBlockPos()).getBlock();
                Material blockMaterial = blockDetected.getDefaultState().getMaterial();
                if (!(blockMaterial == Material.PLANTS || blockMaterial == Material.VINE || blockMaterial == Material.LEAVES)) {
                    //TFCWaystones.logger.info("block material {}", blockDetected.getDefaultState().getMaterial().toString());
                    blockFound = true;
                    blockDistance = playerIn.getDistanceSq(blockTrace.getBlockPos());
                    TFCWaystones.logger.info("block distance {}", blockDistance);
                    TFCWaystones.logger.info("animal distance {}", animalDistance);
                }
                else {
                    TFCWaystones.logger.info("The block we've detected is a plant, ignoring");
                }
            }

            // If our raytrace didn't hit any block, or the animal is closer
            if (!blockFound || (blockDistance > animalDistance)) {
                return true;
            }
            else {
                TFCWaystones.logger.info("We've got a block in front of our animal: {}", blockTrace.toString());
            }

        }
        return false;
    }

    @Nullable
    private EntityAnimal getAnimalAimedAt(EntityPlayer playerIn) {


        List<EntityAnimal> animalList = getAnimalsAround(playerIn);

        if (!animalList.isEmpty()) {
            TFCWaystones.logger.info("got {} entities", animalList.size());
            TFCWaystones.logger.info("{}}", animalList.toString());

            for (EntityAnimal e : animalList) {
                if (isAnimalClickable(playerIn, e)) {
                    return e;
                }
            }

        }

        TFCWaystones.logger.info("not looking at any animals");
        return null;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {

        if (!worldIn.isRemote) {

            EntityAnimal animal;
            if ((animal = getAnimalAimedAt(playerIn)) != null) {
                TFCWaystones.logger.info("success");
                // setting the hurtTime of an animal causes it to panic
                animal.hurtTime = 600;
                playerIn.setActiveHand(handIn); // This is the secret sauce that actually starts the long usage animation

                // Set an animal entity as our target for onItemUseFinish
                TARGET_ANIMAL = animal;
                return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));

            }

        }

        // The client side always fails. This doesn't really seem to matter
        TFCWaystones.logger.info("fail");
        playerIn.resetActiveHand();
        return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entityLiving) {
        if (!world.isRemote && entityLiving instanceof EntityPlayer) {
            TFCWaystones.logger.info("in finish");
            TFCWaystones.logger.info("saved animal is: {}", TARGET_ANIMAL);
            EntityPlayer player = (EntityPlayer) entityLiving;
            if (isAnimalClickable(player, TARGET_ANIMAL)) {
                float damageToDo = TARGET_ANIMAL.getMaxHealth();
                TFCWaystones.logger.info("Doing {} points of damage to {}", damageToDo, TARGET_ANIMAL.getName());
                TARGET_ANIMAL.setFire(10);
                TARGET_ANIMAL.setDropItemsWhenDead(false);
                TARGET_ANIMAL.attackEntityFrom(DamageSource.ON_FIRE, TARGET_ANIMAL.getMaxHealth());
                itemStack.damageItem(1, entityLiving);
            } else {
                TFCWaystones.logger.info("No longer pointing at the animal");
            }
        }

        return itemStack;
    }

    // Check every half second to make sure we're still tracking the target with the mouse
    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if ((count % 10) == 0) { // Every half second, 10 ticks
            TFCWaystones.logger.info("checking...");
            if (!player.getEntityWorld().isRemote && player instanceof EntityPlayer) {
                if (!isAnimalClickable((EntityPlayer) player, TARGET_ANIMAL)) {
                    player.resetActiveHand();
                }
            }
        }
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
