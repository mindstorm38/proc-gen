package fr.theorozier.procgen.client.renderer.entity;

import fr.theorozier.procgen.client.renderer.entity.part.EntityCubePart;
import fr.theorozier.procgen.common.entity.PlayerEntity;
import fr.theorozier.procgen.common.util.MathUtils;
import fr.theorozier.procgen.common.world.position.Direction;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.Texture;
import io.msengine.client.renderer.texture.TextureManager;
import io.msengine.client.renderer.texture.TexturePredefinedMap;

public class PlayerEntityRenderer extends MotionEntityRenderer<PlayerEntity> {
	
	private final TexturePredefinedMap baseTexture;
	
	private final EntityCubePart body;
	private final EntityCubePart head;
	private final EntityCubePart armRight;
	private final EntityCubePart armLeft;
	private final EntityCubePart legRight;
	private final EntityCubePart legLeft;
	
	public PlayerEntityRenderer() {
	
		this.baseTexture = new TexturePredefinedMap("textures/entities/player/steve.png", 64, 64);
	
		this.body = new EntityCubePart(-0.25f, 0f, -0.125f, 0.25f, 0.75f, 0.125f);
		this.body.setFaceTile(Direction.TOP, this.baseTexture.newPixelTile("body_top", 20, 16, 8, 4));
		this.body.setFaceTile(Direction.BOTTOM, this.baseTexture.newPixelTile("body_bottom", 28, 16, 8, 4));
		this.body.setFaceTile(Direction.NORTH, this.baseTexture.newPixelTile("body_side_left", 28, 20, 4, 12));
		this.body.setFaceTile(Direction.SOUTH, this.baseTexture.newPixelTile("body_side_right", 16, 20, 4, 12));
		this.body.setFaceTile(Direction.EAST, this.baseTexture.newPixelTile("body_front", 20, 20, 8, 12));
		this.body.setFaceTile(Direction.WEST, this.baseTexture.newPixelTile("body_back", 32, 20, 8, 12));
		this.addPart("body", this.body);
		
		this.head = new EntityCubePart(-0.25f, 0f, -0.25f, 0.25f, 0.5f, 0.25f);
		this.head.setFaceTile(Direction.TOP, this.baseTexture.newPixelTile("head_top", 8, 0, 8, 8));
		this.head.setFaceTile(Direction.BOTTOM, this.baseTexture.newPixelTile("head_bottom", 16, 0, 8, 8));
		this.head.setFaceTile(Direction.NORTH, this.baseTexture.newPixelTile("head_side_left", 16, 8, 8, 8));
		this.head.setFaceTile(Direction.SOUTH, this.baseTexture.newPixelTile("head_side_right", 0, 8, 8, 8));
		this.head.setFaceTile(Direction.EAST, this.baseTexture.newPixelTile("head_front", 8, 8, 8, 8));
		this.head.setFaceTile(Direction.WEST, this.baseTexture.newPixelTile("head_back", 24, 8, 8, 8));
		this.addPart("head", this.head);
		
		this.armRight = this.newArmOrLegPart(40, 16, "arm_right");
		this.armLeft = this.newArmOrLegPart(32, 48, "arm_left");
		
		this.legRight = this.newArmOrLegPart(0, 16, "leg_right");
		this.legLeft = this.newArmOrLegPart(16, 48, "leg_left");
		
	}
	
	private EntityCubePart newArmOrLegPart(int texX, int texY, String baseId) {
		
		EntityCubePart part = new EntityCubePart(-0.125f, -0.625f, -0.125f, 0.125f, 0.125f, 0.125f);
		part.setFaceTile(Direction.TOP, this.baseTexture.newPixelTile(baseId + "_top", texX + 4, texY, 4, 4));
		part.setFaceTile(Direction.BOTTOM, this.baseTexture.newPixelTile(baseId + "_bottom", texX + 8, texY, 4, 4));
		part.setFaceTile(Direction.NORTH, this.baseTexture.newPixelTile(baseId + "_side_left", texX + 8, texY + 4, 4, 12));
		part.setFaceTile(Direction.SOUTH, this.baseTexture.newPixelTile(baseId + "_side_right", texX, texY + 4, 4, 12));
		part.setFaceTile(Direction.EAST, this.baseTexture.newPixelTile(baseId + "_front", texX + 4, texY + 4, 4, 12));
		part.setFaceTile(Direction.WEST, this.baseTexture.newPixelTile(baseId + "_back", texX + 12, texY + 4, 4, 12));
		this.addPart(baseId, part);
		return part;
		
	}
	
	@Override
	public void initTexture() {
		TextureManager.getInstance().loadTexture(this.baseTexture);
	}
	
	@Override
	public Texture getTexture(PlayerEntity entity) {
		return this.baseTexture;
	}
	
	@Override
	public void renderMotionEntity(float alpha, ModelHandler model, PlayerEntity entity) {
	
		float sneakRot = 1f;
		float walkingInterpolation = entity.getWalkingInterpolation().getValue();
		
		float walkFrame = (float) Math.sin(entity.getWalkFrame().getLerped(alpha)) * 0.5f;
		float armsLateralFrame = (float) Math.sin(entity.getArmsFrame().getLerped(alpha)) * 0.03f;
		
		float armsEffectiveFrame = MathUtils.lerp(armsLateralFrame, -walkFrame, walkingInterpolation);

		model.push().translate(0, 0.75f, 0).apply();
		this.body.render();
		model.pop();
		
		model.push().translate(0, 1.5f, 0).apply();
		this.head.render();
		model.pop();
		
		model.push().translate(0, 1.375f, 0);
		
			model.push().translate(-0.375f, 0, 0).rotateX(armsEffectiveFrame).rotateZ(armsLateralFrame).apply();
			this.armRight.render();
			model.pop();
			
			model.push().translate(0.375f, 0, 0).rotateX(-armsEffectiveFrame).rotateZ(-armsLateralFrame).apply();
			this.armLeft.render();
			model.pop();
			
		model.pop();
		
		model.push().translate(0, 0.625f, 0);
		
			model.push().translate(-0.125f, 0, 0).rotateX(walkFrame).apply();
			this.legRight.render();
			model.pop();
		
			model.push().translate(0.125f, 0, 0).rotateX(-walkFrame).apply();
			this.legLeft.render();
			model.pop();
			
		model.pop();
		
	}
	
}
