package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.entity.controller.EntityController;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.WorldDimension;
import io.sutil.math.MathHelper;

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
	
	private static final Random UID_RANDOM = new Random();
	
	public static long getNewUid() {
		return UID_RANDOM.nextLong();
	}
	
	protected final WorldBase world;
	protected final WorldDimension serverWorld;
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
	
	protected boolean inChunk;
	protected int chunkPosX;
	protected int chunkPosY;
	protected int chunkPosZ;
	
	protected long lifetime;
	
	protected boolean dead;
	
	public Entity(WorldBase world, long uid) {
		
		this.world = world;
		this.serverWorld = world.isServer() ? world.getAsServer() : null;
		this.isInServer = world.isServer();
		this.uid = uid;
		this.rand = new Random();
		
		this.boundingBox = new AxisAlignedBB();
		this.debugBoundingBox = new AxisAlignedBB();
		
		this.controller = null;
		
		this.setPositionInstant(0, 0, 0);
		this.setRotation(0, 0);
		
		this.inChunk = false;
		this.chunkPosX = 0;
		this.chunkPosY = 0;
		this.chunkPosZ = 0;
		
		this.lifetime = 0L;
		
		this.dead = false;
		
	}
	
	// MAIN PROPERTIES GETTERS //
	
	public WorldBase getWorld() {
		return this.world;
	}
	
	public WorldDimension getServerWorld() {
		return this.serverWorld;
	}
	
	public boolean isInServer() {
		return this.isInServer;
	}
	
	public long getUid() {
		return this.uid;
	}
	
	// BASIC UPDATES //
	
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
	
	// POSITIONING //
	
	/**
	 * Teleport this entity to a position.
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @param z The Z coordinate.
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
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @param z The Z coordinate.
	 */
	protected void setupBoundingBoxPosition(double x, double y, double z) {
		this.boundingBox.setPositionUnsafe(x, y, z, x, y, z);
	}
	
	/**
	 * Set position of this entity, but not teleported because last position is not set in motion entity.
	 * Use this to move the entity near to itself.
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @param z The Z coordinate.
	 */
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
	
	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}
	
	public AxisAlignedBB getDebugBoundingBox() {
		this.debugBoundingBox.setPosition(this.boundingBox);
		return this.debugBoundingBox;
	}
	
	// BODY ROTATION //
	
	/**
	 * Set the entity body rotation but not the last rotation for motion entities.
	 * @param yaw The yaw rotation.
	 * @param pitch The pitch rotation.
	 */
	public void setRotation(float yaw, float pitch) {
		
		this.yaw = yaw;
		this.pitch = pitch;
		
	}
	
	/**
	 * Set the entity body rotation but instantly, use this for teleportation with specific rotations.
	 * @param yaw The yaw rotation.
	 * @param pitch The pitch rotation.
	 */
	public void setRotationInstant(float yaw, float pitch) {
		this.setRotation(yaw, pitch);
	}
	
	// ENTITY CONTROLLER //
	
	/**
	 * Set the entity controller.
	 * @param controller Entity controller.
	 */
	public void setController(EntityController controller) {
		this.controller = controller;
	}
	
	/**
	 * Internal method to update entity controller.
	 */
	protected void updateController() {
		if (this.controller != null) {
			this.controller.control(this);
		}
	}
	
	// PROPERTIES GET & SET //
	
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
	
	public boolean isInChunk() {
		return inChunk;
	}
	
	public void setInChunk(boolean inChunk) {
		this.inChunk = inChunk;
	}
	
	public int getChunkPosX() {
		return chunkPosX;
	}
	
	public int getChunkPosY() {
		return chunkPosY;
	}
	
	public int getChunkPosZ() {
		return chunkPosZ;
	}
	
	public void setChunkPos(int x, int y, int z) {
		this.chunkPosX = x;
		this.chunkPosY = y;
		this.chunkPosZ = z;
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
	
	/**
	 * @return Current effective block position X.
	 */
	public int getCurrentBlockPosX() {
		return MathHelper.floorDoubleInt(this.posX);
	}
	
	/**
	 * @return Current effective block position Y.
	 */
	public int getCurrentBlockPosY() {
		return MathHelper.floorDoubleInt(this.posY);
	}
	
	/**
	 * @return Current effective block position Z.
	 */
	public int getCurrentBlockPosZ() {
		return MathHelper.floorDoubleInt(this.posZ);
	}
	
	/**
	 * @return Current effective chunk position X.
	 */
	public int getCurrentChunkPosX() {
		return this.getCurrentBlockPosX() >> 4;
	}
	
	/**
	 * @return Current effective chunk position Y.
	 */
	public int getCurrentChunkPosY() {
		
		int cy = this.getCurrentBlockPosY() >> 4;
		
		if (cy < 0) {
			cy = 0;
		} else if (cy >= this.world.getVerticalChunkCount()) {
			cy = this.world.getVerticalChunkCount() - 1;
		}
		
		return cy;
	}
	
	/**
	 * @return Current effective chunk position Z.
	 */
	public int getCurrentChunkPosZ() {
		return this.getCurrentBlockPosZ() >> 4;
	}
	
	// DEFAULT IMPLEMENTATIONS //
	
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
	
	/**
	 * Development helper
	 */
	public void debugToConsole() {
		System.out.println("Debug entity n°" + this.uid + ", type: " + this.getClass().getSimpleName());
	}
	
}
