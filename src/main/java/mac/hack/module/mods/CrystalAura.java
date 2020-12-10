package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.event.events.EventWorldRender;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.setting.other.SettingRotate;
import mac.hack.utils.EntityUtils;
import mac.hack.utils.RenderUtils;
import mac.hack.utils.WorldUtils;
import com.google.common.collect.Streams;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.explosion.Explosion;
import java.util.*;
import java.util.stream.Collectors;
public class CrystalAura extends Module {
	private HashMap<Entity, Float> damageCache = new HashMap<>();
	private BlockPos render;
	private boolean togglePitch = false;
	private boolean switchCooldown = false;
	private boolean isAttacking = false;
	private int oldSlot = -1;
	private int newSlot;
	private int breaks;
	private boolean isSpoofingAngles;
	private HashMap<BlockPos, Integer> blackList = new HashMap<>();
	public CrystalAura() {
		super("CrystalAura", KEY_UNBOUND, Category.COMBAT, "Automatically attacks crystals for you.",
				new SettingToggle("Players", true).withDesc("Target players"),
				new SettingToggle("Mobs", false).withDesc("Target mobs"),
				new SettingToggle("Animals", false).withDesc("Target animals"),
				new SettingToggle("Explode", true).withDesc("Hit/explode crystals").withChildren(
						new SettingToggle("Anti Weakness", true).withDesc("Hit with sword when you have weakness"),
						new SettingToggle("Slow", false).withDesc("Hits crystals slower")),
				new SettingToggle("Place", true).withDesc("Place crystals").withChildren(
						new SettingToggle("AutoSwitch", true).withDesc("Automatically switches to crystal when in combat"),
						new SettingToggle("raycast", true).withDesc("Click on the most \"legit\" side of a block when possible"),
						new SettingToggle("Blacklist", true).withDesc("Blacklists a crystal when it can't place so it doesn't spam packets"),
						new SettingSlider("R: ", 0.0D, 255.0D, 255.0D, 0),//3
						new SettingSlider("G: ", 0.0D, 255.0D, 255.0D, 0),//4
						new SettingSlider("B: ", 0.0D, 255.0D, 255.0D, 0)),//5
				new SettingRotate(false).withDesc("Rotates to crystals"),
				new SettingSlider("Range", 0, 6, 4.25, 2).withDesc("Range to place and attack crystals"),
				new SettingToggle("Old Calcs", true).withDesc("Uses the old damage caclulations"));
	}
	@Subscribe
	@SuppressWarnings("null")
	public void onTick(EventTick event) {
		damageCache.clear();
		EndCrystalEntity crystal = Streams.stream(mc.world.getEntities()).filter(entityx -> (entityx instanceof EndCrystalEntity)).map(entityx -> {
			BlockPos p = entityx.getBlockPos().down();
			if (blackList.containsKey(p)) {
				if (blackList.get(p) > 0)
					blackList.replace(p, blackList.get(p) - 1);
				else
					blackList.remove(p);
			}
			return (EndCrystalEntity) entityx;
		}).min(Comparator.comparing(c -> mc.player.distanceTo(c))).orElse(null);
		int crystalSlot;
		if (getSetting(3).asToggle().state && crystal != null && mc.player.distanceTo(crystal) <= getSetting(6).asSlider().getValue()) {
			if (getSetting(3).asToggle().getChild(0).asToggle().state && mc.player.hasStatusEffect(StatusEffects.WEAKNESS)) {
				if (!this.isAttacking) {
					this.oldSlot = mc.player.inventory.selectedSlot;
					this.isAttacking = true;
				}
				this.newSlot = -1;
				for (crystalSlot = 0; crystalSlot < 9; ++crystalSlot) {
					ItemStack stack = mc.player.inventory.getStack(crystalSlot);
					if (stack != ItemStack.EMPTY) {
						if (stack.getItem() instanceof SwordItem) {
							this.newSlot = crystalSlot;
							break;
						}
						if (stack.getItem() instanceof ToolItem) {
							this.newSlot = crystalSlot;
							break;
						}
					}
				}
				if (this.newSlot != -1) {
					mc.player.inventory.selectedSlot = this.newSlot;
					this.switchCooldown = true;
				}
			}
			if (getSetting(5).asRotate().state) {
				WorldUtils.facePosAuto(crystal.getX(), crystal.getY(), crystal.getZ(), getSetting(5).asRotate());
			}
			mc.interactionManager.attackEntity(mc.player, crystal);
			mc.player.swingHand(Hand.MAIN_HAND);
			++this.breaks;
			if (this.breaks == 2 && !getSetting(3).asToggle().getChild(1).asToggle().state) {
				if (getSetting(5).asRotate().state) {
					isSpoofingAngles = false;
				}
				this.breaks = 0;
				return;
			}
			if (getSetting(3).asToggle().getChild(1).asToggle().state && this.breaks == 1) {
				if (getSetting(5).asRotate().state) {
					isSpoofingAngles = false;
				}
				this.breaks = 0;
				return;
			}
		} else {
			if (getSetting(5).asRotate().state) {
				isSpoofingAngles = false;
			}
			if (this.oldSlot != -1) {
				mc.player.inventory.selectedSlot = this.oldSlot;
				this.oldSlot = -1;
			}
			this.isAttacking = false;
		}
		crystalSlot = mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL ? mc.player.inventory.selectedSlot : -1;
		if (crystalSlot == -1) {
			for (int l = 0; l < 9; ++l) {
				if (mc.player.inventory.getStack(l).getItem() == Items.END_CRYSTAL) {
					crystalSlot = l;
					break;
				}
			}
		}
		boolean offhand = false;
		if (mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL) {
			offhand = true;
		} else if (crystalSlot == -1) {
			return;
		}
		Set<BlockPos> blocks = getCrystalPoses();
		List<Entity> entities = new ArrayList<>();
		entities.addAll(Streams.stream(mc.world.getEntities()).filter(e -> ((e instanceof PlayerEntity && getSetting(0).asToggle().state)
				|| (e instanceof MobEntity && getSetting(1).asToggle().state)
				|| (EntityUtils.isAnimal(e) && getSetting(2).asToggle().state))).collect(Collectors.toList()));
// TODO: not this
		BlockPos q = null;
		double damage = 0.5D;
		Iterator<Entity> var9 = entities.iterator();
		main_loop: while (true) {
			Entity entity;
			do {
				do {
					if (!var9.hasNext()) {
						if (damage == 0.5D) {
							this.render = null;
							if (getSetting(5).asRotate().state) {
								isSpoofingAngles = false;
							}
							return;
						}
						this.render = q;
						if (getSetting(4).asToggle().state) {
							if (!offhand && mc.player.inventory.selectedSlot != crystalSlot) {
								if (getSetting(4).asToggle().getChild(0).asToggle().state) {
									mc.player.inventory.selectedSlot = crystalSlot;
									if (getSetting(5).asRotate().state) {
										isSpoofingAngles = false;
									}
									this.switchCooldown = true;
								}
								return;
							}
							if (getSetting(5).asRotate().state) {
								WorldUtils.facePosAuto(q.getX() + 0.5D, q.getY() - 0.5D, q.getZ() + 0.5D, getSetting(5).asRotate());
							}
							Direction f;
							if (!getSetting(4).asToggle().getChild(1).asToggle().state) {
								f = Direction.UP;
							} else {
								BlockHitResult result = mc.world.raycast(new RaycastContext(
										new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ()),
										new Vec3d(q.getX() + 0.5D, q.getY() - 0.5D, q.getZ() + 0.5D),
										RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
								if (result != null && result.getSide() != null) {
									f = result.getSide();
								} else {
									f = Direction.UP;
								}
								if (this.switchCooldown) {
									this.switchCooldown = false;
									return;
								}
							}
							mc.interactionManager.interactBlock(mc.player, mc.world, offhand ? Hand.OFF_HAND : Hand.MAIN_HAND,
									new BlockHitResult(Vec3d.of(q), f, q, false));
							blackList.put(q, 5);
						}
						if (isSpoofingAngles) {
							if (togglePitch) {
								mc.player.pitch += 4.0E-4D;
								togglePitch = false;
							} else {
								mc.player.pitch -= 4.0E-4D;
								togglePitch = true;
							}
						}
						return;
					}
					entity = var9.next();
				} while (entity == mc.player);
			} while (((LivingEntity) entity).getHealth() <= 0.0F);
			Iterator<BlockPos> var11 = blocks.iterator();
			while (true) {
				BlockPos blockPos;
				double d;
				double self;
				do {
					do {
						double b;
						do {
							if (!var11.hasNext()) {
								continue main_loop;
							}
							blockPos = var11.next();
							b = entity.getBlockPos().getSquaredDistance(blockPos);
						} while (b >= 169.0D);
						d = getSetting(7).asToggle().state
								? getExplosionDamage_old(blockPos, (LivingEntity) entity)
								: getExplosionDamage(blockPos, (LivingEntity) entity);
					} while (d <= damage);
					self = getSetting(7).asToggle().state
							? getExplosionDamage_old(blockPos, (LivingEntity) entity)
							: getExplosionDamage(blockPos, mc.player);
				} while (self > d && d >= ((LivingEntity) entity).getHealth());
				if (self - 0.5D <= mc.player.getHealth()) {
					damage = d;
					q = blockPos;
				}
			}
		}
	}
	@Subscribe
	public void onRenderWorld(EventWorldRender event) {
		if (this.render != null) {
			float r =  (float) (this.getSetting(4).asToggle().getChild(3).asSlider().getValue() / 255.0D);
			float g =  (float) (this.getSetting(4).asToggle().getChild(4).asSlider().getValue() / 255.0D);
			float b =  (float) (this.getSetting(4).asToggle().getChild(5).asSlider().getValue() / 255.0D);
			RenderUtils.drawFilledBox(render, r, g, b, 0.4f);
		}
	}
	public Set<BlockPos> getCrystalPoses() {
		Set<BlockPos> poses = new HashSet<>();
		int range = (int) Math.ceil(getSetting(6).asSlider().getValue());
		for (int x = -range; x < range + 1; x++) {
			for (int y = -range; y < range; y++) {
				for (int z = -range; z < range + 1; z++) {
					BlockPos basePos = mc.player.getBlockPos().add(x, y, z);
					if (!canPlace(basePos) || (blackList.containsKey(basePos) && getSetting(4).asToggle().getChild(2).asToggle().state))
						continue;
					if (mc.player.getPos().distanceTo(Vec3d.of(basePos).add(0.5, 1, 0.5)) <= getSetting(6).asSlider().getValue() + 0.25)
						poses.add(basePos);
				}
			}
		}
		return poses;
	}
	private boolean canPlace(BlockPos basePos) {
		BlockState baseState = mc.world.getBlockState(basePos);
		if (baseState.getBlock() != Blocks.BEDROCK && baseState.getBlock() != Blocks.OBSIDIAN)
			return false;
		BlockPos placePos = basePos.up();
		if (!mc.world.isAir(placePos))
			return false;
		return mc.world.getOtherEntities((Entity) null, new Box(placePos).stretch(0, 1, 0)).isEmpty();
	}
	private float getExplosionDamage(BlockPos basePos, LivingEntity target) {
		if (mc.world.getDifficulty() == Difficulty.PEACEFUL)
			return 0;
		if (damageCache.containsKey(target))
			return damageCache.get(target);
		Vec3d crystalPos = Vec3d.of(basePos).add(0.5, 1, 0.5);
		Explosion explosion = new Explosion(mc.world, null, crystalPos.x, crystalPos.y, crystalPos.z, 6f, false, Explosion.DestructionType.DESTROY);
		double power = 12;
		if (!mc.world.getOtherEntities((Entity) null, new Box(
				MathHelper.floor(crystalPos.x - power - 1.0),
				MathHelper.floor(crystalPos.y - power - 1.0),
				MathHelper.floor(crystalPos.z - power - 1.0),
				MathHelper.floor(crystalPos.x + power + 1.0),
				MathHelper.floor(crystalPos.y + power + 1.0),
				MathHelper.floor(crystalPos.z + power + 1.0))).contains(target)) {
			damageCache.put(target, 0f);
			return 0f;
		}
		if (!target.isImmuneToExplosion()) {
			double double_8 = MathHelper.sqrt(target.squaredDistanceTo(crystalPos)) / power;
			if (double_8 <= 1.0D) {
				double double_9 = target.getX() - crystalPos.x;
				double double_10 = target.getY() + target.getStandingEyeHeight() - crystalPos.y;
				double double_11 = target.getZ() - crystalPos.z;
				double double_12 = MathHelper.sqrt(double_9 * double_9 + double_10 * double_10 + double_11 * double_11);
				if (double_12 != 0.0D) {
					double_9 /= double_12;
					double_10 /= double_12;
					double_11 /= double_12;
					double double_13 = Explosion.getExposure(crystalPos, target);
					double double_14 = (1.0D - double_8) * double_13;
// entity_1.damage(explosion.getDamageSource(), (float)((int)((double_14 *
// double_14 + double_14) / 2.0D * 7.0D * power + 1.0D)));
					float toDamage = (float) Math.floor((double_14 * double_14 + double_14) / 2.0D * 7.0D * power + 1.0D);
					if (target instanceof PlayerEntity) {
						if (mc.world.getDifficulty() == Difficulty.EASY)
							toDamage = Math.min(toDamage / 2.0F + 1.0F, toDamage);
						else if (mc.world.getDifficulty() == Difficulty.HARD)
							toDamage = toDamage * 3.0F / 2.0F;
					}
// Armor
					toDamage = DamageUtil.getDamageLeft(toDamage, target.getArmor(),
							(float) target.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());
// Enchantments
					if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
						int resistance = (target.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
						int int_2 = 25 - resistance;
						float resistance_1 = toDamage * int_2;
						toDamage = Math.max(resistance_1 / 25.0F, 0.0F);
					}
					if (toDamage <= 0.0F) {
						toDamage = 0.0F;
					} else {
						int protAmount = EnchantmentHelper.getProtectionAmount(target.getArmorItems(), explosion.getDamageSource());
						if (protAmount > 0) {
							toDamage = DamageUtil.getInflictedDamage(toDamage, protAmount);
						}
					}
					damageCache.put(target, toDamage);
					return toDamage;
				}
			}
		}
		damageCache.put(target, 0f);
		return 0;
	}
	private float getExplosionDamage_old(BlockPos basePos, LivingEntity target) {
		if (mc.world.getDifficulty() == Difficulty.PEACEFUL)
			return 0f;
		Vec3d crystalVec = new Vec3d(basePos.getX() + 0.5, basePos.getY() + 1.0, basePos.getZ() + 0.5);
		float doubleExplosionSize = 12.0F;
		double distancedsize = target.getPos().distanceTo(crystalVec) / doubleExplosionSize;
		double blockDensity = Explosion.getExposure(crystalVec, target);
		double v = (1.0D - distancedsize) * blockDensity;
		float damage = ((int) ((v * v + v) / 2.0D * 9.0D * doubleExplosionSize + 1.0D));
		double finald = 1.0D;
		damage *= (mc.world.getDifficulty() == Difficulty.HARD ? 1.5f : mc.world.getDifficulty() == Difficulty.EASY ? 0.5f : 1f);
		finald = this.getBlastReduction(target, damage,
				new Explosion(this.mc.world, null, crystalVec.x, crystalVec.y, crystalVec.z, 6.0F, false, Explosion.DestructionType.DESTROY));
		return (float) finald;
	}
	private float getBlastReduction(LivingEntity entity, float damage, Explosion explosion) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity ep = (PlayerEntity) entity;
			DamageSource ds = DamageSource.explosion(explosion);
			damage = DamageUtil.getDamageLeft(damage, entity.getArmor(), (float) entity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());
			int k = EnchantmentHelper.getProtectionAmount(ep.getArmorItems(), ds);
			float f = MathHelper.clamp(k, 0.0F, 20.0F);
			damage *= 1.0F - f / 25.0F;
			if (entity.hasStatusEffect(StatusEffects.RESISTANCE)) {
				damage -= damage / 4.0F;
			}
			damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0F);
			return damage;
		} else {
			damage = DamageUtil.getDamageLeft(damage, entity.getArmor(), (float) entity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());
			return damage;
		}
	}
}