package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.phys.AxisAlignedBB;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.WorldServer;

import java.util.Random;

/**
 *
 * Base class for all entities in BaseWorld.
 *
 * @author Theo Rozier
 *
 */
public abstract class Entity {
	
	public static final float GRAVITY_FACTOR = 0.01f;
	public static final float AIR_FRICTION   = 0.01f;
	
	protected final WorldBase world;
	protected final WorldServer serverWorld;
	protected final boolean isInServer;
	protected final long uid;
	protected final Random rand;
	
	protected final AxisAlignedBB boundingBox;
	protected final AxisAlignedBB debugBoundingBox;
	
	protected double posX;
	protected double posY;
	protected double posZ;
	
	protected long lifetime;
	
	public Entity(WorldBase world, WorldServer serverWorld, long uid) {
		
		this.world = world;
		this.serverWorld = serverWorld;
		this.isInServer = serverWorld != null;
		this.uid = uid;
		this.rand = new Random();
		
		this.boundingBox = new AxisAlignedBB();
		this.setPositionRaw(0, 0, 0);
		
		this.debugBoundingBox = new AxisAlignedBB(this.boundingBox);
		
		this.lifetime = 0L;
		
	}
	
	public WorldBase getWorld() {
		return this.world;
	}
	
	public WorldServer getServerWorld() {
		return this.serverWorld;
	}
	
	public boolean isInServer() {
		return this.isInServer;
	}
	
	public long getUid() {
		return this.uid;
	}
	
	/**
	 * Override this method (keep calling super) to initiate the
	 * bounding box coordinates and size.
	 * @param x The raw X coordinate.
	 * @param y The raw Y coordinate.
	 * @param z The raw Z coordinate.
	 */
	protected void setPositionRaw(double x, double y, double z) {
		
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		
	}
	
	public void setPosition(double x, double y, double z) {
		
		this.boundingBox.move(x - this.posX, y - this.posY, z - this.posZ);
		this.resetPositionToBoundingBox();
		
	}
	
	public void move(double dx, double dy, double dz) {
		
		this.boundingBox.move(dx, dy, dz);
		this.resetPositionToBoundingBox();
		
	}
	
	public void resetPositionToBoundingBox() {
		
		this.posX = this.boundingBox.getMiddleX();
		this.posY = this.boundingBox.getMiddleY();
		this.posZ = this.boundingBox.getMiddleZ();
		
	}
	
	public void update() {
		
		++this.lifetime;
		
	}
	
}
