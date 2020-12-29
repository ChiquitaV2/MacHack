package mac.hack.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityUtils {

	private static final MinecraftClient mc = MinecraftClient.getInstance();

	public static boolean isAnimal(Entity e) {
		return e instanceof PassiveEntity || e instanceof AmbientEntity || e instanceof WaterCreatureEntity || e instanceof GolemEntity;
	}

	public static void setGlowing(Entity entity, Formatting color, String teamName) {
		Team team = (mc.world.getScoreboard().getTeamNames().contains(teamName) ?
				mc.world.getScoreboard().getTeam(teamName) :
				mc.world.getScoreboard().addTeam(teamName));

		mc.world.getScoreboard().addPlayerToTeam(
				entity instanceof PlayerEntity ? entity.getEntityName() : entity.getUuidAsString(), team);
		mc.world.getScoreboard().getTeam(teamName).setColor(color);

		entity.setGlowing(true);
	}

	public static boolean isPassive(Entity entity) {
		return !(entity instanceof HostileEntity);
	}

	public static double[] calculateLookAt(double px, double py, double pz, PlayerEntity me)
	{
		double dirx = me.getX() - px;
		double diry = me.getY() - py;
		double dirz = me.getZ() - pz;

		double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

		dirx /= len;
		diry /= len;
		dirz /= len;

		double pitch = Math.asin(diry);
		double yaw = Math.atan2(dirz, dirx);

		// to degree
		pitch = pitch * 180.0d / Math.PI;
		yaw = yaw * 180.0d / Math.PI;

		yaw += 90f;

		return new double[]
				{ yaw, pitch };
	}
	public enum FacingDirection
	{
		North,
		South,
		East,
		West,
		SouthEast,
		SouthWest,
		NorthWest,
		NorthEast,
	}

	public static FacingDirection GetFacing()
	{
		switch (MathHelper.floor((double) (mc.player.yaw * 8.0F / 360.0F) + 0.5D) & 7)
		{
			case 0:
			case 1:
				return FacingDirection.South;
			case 2:
			case 3:
				return FacingDirection.West;
			case 4:
			case 5:
				return FacingDirection.North;
			case 6:
			case 7:
				return FacingDirection.East;
			case 8:
		}
		return FacingDirection.North;
	}

	public static double GetDistance(double p_X, double p_Y, double p_Z, double x, double y, double z)
	{
		double d0 = p_X - x;
		double d1 = p_Y - y;
		double d2 = p_Z - z;
		return (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}

	public static float getDamageAfterAbsorb(float damage, float totalArmor, float toughnessAttribute) {
		float f = 2.0F + toughnessAttribute / 4.0F;
		float f1 = MathHelper.clamp(totalArmor - damage / f, totalArmor * 0.2F, 20.0F);
		return damage * (1.0F - f1 / 25.0F);
	}

	public static boolean IsEating()
	{
		return mc.player != null &&  mc.player.getActiveItem().getItem() == Items.GOLDEN_APPLE;
	}

	public static float GetRotationYawForCalc()
	{
		float rotationYaw = mc.player.yaw;
		if (mc.player.forwardSpeed < 0.0f)
		{
			rotationYaw += 180.0f;
		}
		float n = 1.0f;
		if (mc.player.forwardSpeed < 0.0f)
		{
			n = -0.5f;
		}
		else if (mc.player.forwardSpeed > 0.0f)
		{
			n = 0.5f;
		}
		if (mc.player.sidewaysSpeed > 0.0f)
		{
			rotationYaw -= 90.0f * n;
		}
		if (mc.player.sidewaysSpeed < 0.0f)
		{
			rotationYaw += 90.0f * n;
		}
		return rotationYaw * 0.017453292f;
	}

	public static int determineHighway() {
		MinecraftClient mc = MinecraftClient.getInstance();
		PlayerEntity player = mc.player;
		int highwayNum = 0;
		if (player.getX() >= 100) {
			if (player.getZ() >= -5 && player.getZ() <= 5) {
				//+X highway
				highwayNum = 1;
			}
			else if (player.getZ() - player.getX() >= -50 && player.getZ() - player.getX() <= 50) {
				//+X+Z highway
				highwayNum = 2;
			}
			else if (player.getZ() + player.getX() >= -50 && player.getZ() + player.getX() <= 50) {
				//+X-Z highway
				highwayNum = 3;
			}
			else {
				MacLogger.errorMessage("I have no idea where you are, but it probably isn't a highway.");
			}
		}
		else if (player.getX() <= -100) {
			if (player.getZ() >= -5 && player.getZ() <= 5) {
				//-X highway
				highwayNum = 4;
			}
			else if (player.getX() + player.getZ() >= -50 && player.getX() + player.getZ() <= 50) {
				//-X+Z highway
				highwayNum = 5;
			}
			else if (player.getZ() <= player.getX() + 100 && player.getZ() >= player.getX() - 100) {
				//-X-Z highway
				highwayNum = 6;
			}
			else {
				MacLogger.errorMessage("I have no idea where you are, but it probably isn't a highway.");
			}
		}
		else if (player.getZ() >= 100) {
			if (player.getX() >= -5 && player.getX() <= 5) {
				//+Z highway
				highwayNum = 7;
			}
			else {
				MacLogger.errorMessage("I have no idea where you are, but it probably isn't a highway.");
			}
		}
		else if (player.getZ() <= -100) {
			if (player.getX() >= -5 && player.getX() <= 5) {
				//-Z highway
				highwayNum = 8;
			}
			else {
				MacLogger.errorMessage("I have no idea where you are, but it probably isn't a highway.");
			}
		}
		return highwayNum;
	}

}