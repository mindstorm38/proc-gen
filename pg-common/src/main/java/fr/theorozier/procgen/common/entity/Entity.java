package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.entity.controller.EntityController;
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
	
	public static final float GRAVITY_FACTOR = 0.04f;
	public static final float AIR_FRICTION   = 0.01f;
	
	protected final WorldBase world;
	protected final WorldServer serverWorld;
	protected final boolean isInServer;
	protected final long uid;
	protected final Random rand;
	
	protected final AxisAlignedBB boundingBox;
	protected final AxisAlignedBB debugBoundingBox;
	
	protected EntityController controller;
	
	protected double posX;
	protected double posY;
	protected double posZ;
	
	protected float yaw;
	protected float pitch;
	
	protected long lifetime;
	
	protected boolean dead;
	
	public Entity(WorldBase world, long uid) {
		
		this.world = world;
		this.serverWorld = world.isServer() ? world.getAsServer() : null;
		this.isInServer = world.isServer();
		this.uid = uid;
		this.rand = new Random();
		
		this.boundingBox = new AxisAlignedBB();
		this.setPositionInstant(0, 0, 0);
		this.setRotation(0, 0);
		
		this.debugBoundingBox = new AxisAlignedBB();
		
		this.controller = null;
		
		this.lifetime = 0L;
		
		this.dead = false;
		
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
	 * Teleport this entity to a position.
	 * @param x The raw X coordinate.
	 * @param y The raw Y coordinate.
	 * @param z The raw Z coordinate.
	 */
	public void setPositionInstant(double x, double y, double z) {
		
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		
		this.setupBoundingBoxPosition(x, y, z);
		
	}
	
	/**
	 * REMEMBER TO OVERRIDE this method (keep calling super) to initiate the
	 * bounding box coordinates and size.
	 * @param x The raw X coordinate.
	 * @param y The raw Y coordinate.
	 * @param z The raw Z coordinate.
	 */
	public void setupBoundingBoxPosition(double x, double y, double z) {
		this.boundingBox.setPositionUnsafe(x, y, z, x, y, z);
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
	
	public AxisAlignedBB getDebugBoundingBox() {
		this.debugBoundingBox.setPosition(this.boundingBox);
		return this.debugBoundingBox;
	}
	
	public void setRotation(float yaw, float pitch) {
		
		this.yaw = yaw;
		this.pitch = pitch;
		
	}
	
	public double getPosX() {
		return posX;
	}
	
	public double getPosY() {
		return posY;
	}
	
	public double getPosZ() {
		return posZ;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	/**
	 * Set the entity controller.
	 * @param controller Entity controller.
	 */
	public void setController(EntityController controller) {
		this.controller = controller;
	}
	
	/**
	 * Common update method called by worlds on each ticks, only use short
	 * methods calls in this because update can be entirely overrided with
	 * no super call.
	 */
	public void update() {
		
		this.updateLifetime();
		this.updateController();
		
	}
	
	/**
	 * Internal method to increment entity lifetime.
	 */
	protected void updateLifetime() {
		++this.lifetime;
	}
	
	/**
	 * Internal method to update entity controller.
	 */
	protected void updateController() {
		if (this.controller != null) {
			this.controller.control(this);
		}
	}
	
	/**
	 * Immutable method to know if an entity is dead.
	 * @return True if the entity is dead.
	 */
	public final boolean isDead() {
		return this.dead;
	}
	
	/**
	 * Kill the entity.
	 */
	public void setDead() {
		this.dead = true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Entity entity = (Entity) o;
		return entity.uid == this.uid;
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(this.uid);
	}
	
}
