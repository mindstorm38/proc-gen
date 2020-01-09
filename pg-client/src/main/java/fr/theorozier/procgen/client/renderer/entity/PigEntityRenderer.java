package fr.theorozier.procgen.client.renderer.entity;

import fr.theorozier.procgen.client.renderer.entity.part.EntityCubePart;
import fr.theorozier.procgen.common.entity.PigEntity;
import fr.theorozier.procgen.common.world.position.Direction;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.Texture;
import io.msengine.client.renderer.texture.TextureManager;
import io.msengine.client.renderer.texture.TexturePredefinedMap;

public class PigEntityRenderer extends MotionEntityRenderer<PigEntity> {
	
	private static final float MIN_LEG_ROTATION = (float) Math.toRadians(10.0);
	
	private final TexturePredefinedMap baseTexture;
	
	private final EntityCubePart body;
	private final EntityCubePart leg;
	private final EntityCubePart head;
	private final EntityCubePart muzzle;
	
	public PigEntityRenderer() {
		
		this.baseTexture = new TexturePredefinedMap("textures/entities/pig/pig.png", 64, 32);
		
		this.body = new EntityCubePart(-0.3125f, 0.375f, -0.5f, 0.3125f, 0.875f, 0.5f);
		this.body.setFaceTile(Direction.TOP, this.baseTexture.newPixelTile("body_top", 54, 16, 10, 16), 2);
		this.body.setFaceTile(Direction.BOTTOM, this.baseTexture.newPixelTile("body_bottom", 36, 16, 10, 16));
		this.body.setFaceTile(Direction.NORTH, this.baseTexture.newPixelTile("body_side_left", 46, 16, 8, 16), 3);
		this.body.setFaceTile(Direction.SOUTH, this.baseTexture.newPixelTile("body_side_right", 28, 16, 8, 16), 1);
		this.body.setFaceTile(Direction.EAST, this.baseTexture.newPixelTile("body_front", 36, 8, 10, 8));
		this.body.setFaceTile(Direction.WEST, this.baseTexture.newPixelTile("body_back", 46, 8, 10, 8));
		this.addPart("body", this.body);
		
		this.leg = new EntityCubePart(-0.125f, -0.375f, -0.125f, 0.125f, 0f, 0.125f);
		this.leg.setFaceTile(Direction.TOP, this.baseTexture.newPixelTile("leg_top", 4, 16, 4, 4));
		this.leg.setFaceTile(Direction.BOTTOM, this.baseTexture.newPixelTile("leg_bottom", 8, 16, 4, 4));
		this.leg.setFaceTile(Direction.NORTH, this.baseTexture.newPixelTile("leg_left", 8, 20, 4, 6));
		this.leg.setFaceTile(Direction.SOUTH, this.baseTexture.newPixelTile("leg_right", 0, 20, 4, 6));
		this.leg.setFaceTile(Direction.EAST, this.baseTexture.newPixelTile("leg_front", 4, 20, 4, 6));
		this.leg.setFaceTile(Direction.WEST, this.baseTexture.newPixelTile("leg_back", 12, 20, 4, 6));
		this.addPart("leg", this.leg);
		
		this.head = new EntityCubePart(-0.25f, -0.375f, -0.375f, 0.25f, 0.125f, 0.125f);
		this.head.setFaceTile(Direction.TOP, this.baseTexture.newPixelTile("head_top", 8, 0, 8, 8));
		this.head.setFaceTile(Direction.BOTTOM, this.baseTexture.newPixelTile("head_bottom", 24, 8, 8, 8));
		this.head.setFaceTile(Direction.NORTH, this.baseTexture.newPixelTile("head_left", 16, 8, 8, 8));
		this.head.setFaceTile(Direction.SOUTH, this.baseTexture.newPixelTile("head_right", 0, 8, 8, 8));
		this.head.setFaceTile(Direction.EAST, this.baseTexture.newPixelTile("head_front", 8, 8, 8, 8));
		this.head.setFaceTile(Direction.WEST, this.baseTexture.newPixelTile("head_back", 24, 8, 8, 8));
		this.addPart("head", this.head);
		
		this.muzzle = new EntityCubePart(-0.125f, -0.3125f, 0.125f, 0.125f, -0.125f, 0.1875f);
		this.muzzle.setFaceTile(Direction.TOP, this.baseTexture.newPixelTile("muzzle_top", 17, 16, 4, 1));
		this.muzzle.setFaceTile(Direction.BOTTOM, this.baseTexture.newPixelTile("muzzle_bottom", 21, 16, 4, 1));
		this.muzzle.setFaceTile(Direction.NORTH, this.baseTexture.newPixelTile("muzzle_left", 21, 17, 1, 3));
		this.muzzle.setFaceTile(Direction.SOUTH, this.baseTexture.newPixelTile("muzzle_right", 16, 17, 1, 3));
		this.muzzle.setFaceTile(Direction.EAST, this.baseTexture.newPixelTile("muzzle_east", 17, 17, 4, 3));
		this.muzzle.getFaces().setFace(Direction.WEST, false);
		this.addPart("muzzle", this.muzzle);
		
	}
	
	@Override
	public void initTexture() {
		TextureManager.getInstance().loadTexture(this.baseTexture);
	}
	
	@Override
	public Texture getTexture(PigEntity entity) {
		return this.baseTexture;
	}
	
	@Override
	public void renderMotionEntity(float alpha, ModelHandler model, PigEntity entity) {
		
		this.body.render();
		
		model.push().translate(0, 0.875f, 0.625f).apply();
		this.head.render();
		this.muzzle.render();
		model.pop();
		
		model.push().translate(0, 0.375f, 0.3125f);
		this.renderTwinLegs(alpha, model, entity);
		model.pop();
		
		model.push().translate(0, 0.375f, -0.4375f);
		this.renderTwinLegs(alpha, model, entity);
		model.pop();
		
	}
	
	private void renderTwinLegs(float alpha, ModelHandler model, PigEntity entity) {
		
		model.push().translateX(-0.1875f).apply();
		this.leg.render();
		model.pop();
		
		model.push().translateX(0.1875f).apply();
		this.leg.render();
		model.pop();
		
	}
	
}
