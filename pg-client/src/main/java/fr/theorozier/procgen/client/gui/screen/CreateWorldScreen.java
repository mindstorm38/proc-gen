package fr.theorozier.procgen.client.gui.screen;

import fr.theorozier.procgen.client.ProcGenGame;
import fr.theorozier.procgen.client.gui.Screen;
import fr.theorozier.procgen.client.gui.object.GuiButton;
import fr.theorozier.procgen.client.gui.object.GuiTextInput;
import fr.theorozier.procgen.client.gui.object.event.GuiButtonActionEvent;
import fr.theorozier.procgen.client.world.WorldList;
import fr.theorozier.procgen.common.world.WorldDimensionManager;
import fr.theorozier.procgen.common.world.WorldLoadingPosition;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.gen.WorldGenerators;
import fr.theorozier.procgen.common.world.gen.option.WorldGenerationOption;
import io.msengine.client.gui.GuiParent;
import io.msengine.client.gui.GuiScene;
import io.msengine.client.gui.GuiTextColorable;
import io.msengine.client.gui.event.GuiTextInputChangedEvent;

import java.io.File;
import java.util.Random;

public class CreateWorldScreen extends Screen {
	
	private final WorldList worldList;
	private final Random seedRandom;
	
	private final GuiParent mainBlock;
	private final GuiTextInput worldNameInput;
	private final GuiTextColorable futureWorldIdentifier;
	private final GuiTextInput worldSeedInput;
	private final GuiButton createButton;
	private final GuiButton cancelButton;
	
	private String worldIdentifier;
	
	public CreateWorldScreen() {
		
		super("Create world...");
		
		this.worldList = ProcGenGame.getGameInstance().getWorldList();
		this.seedRandom = new Random();
		
		// Main block
		this.mainBlock = new GuiParent();
		this.addChild(this.mainBlock);
		
		// World name input
		this.worldNameInput = new GuiTextInput("Enter the world name");
		this.worldNameInput.setPosition(0, -130);
		this.worldNameInput.setSize(400, 40);
		this.worldNameInput.addEventListener(GuiTextInputChangedEvent.class, this::onTextInputChanged);
		this.mainBlock.addChild(this.worldNameInput);
		
		// Future world identifier
		this.futureWorldIdentifier = new GuiTextColorable();
		this.futureWorldIdentifier.setIgnoreUnderline(true);
		this.futureWorldIdentifier.setAnchor(0, 0);
		this.futureWorldIdentifier.setPosition(0, -44);
		this.futureWorldIdentifier.setHeight(8);
		this.futureWorldIdentifier.setTextColor(120, 120, 120);
		this.mainBlock.addChild(this.futureWorldIdentifier);
		
		// World seed input
		this.worldSeedInput = new GuiTextInput("World seed (optional)");
		this.worldSeedInput.setPosition(0, -80);
		this.worldSeedInput.setSize(400, 40);
		this.mainBlock.addChild(this.worldSeedInput);
		
		// Create world button
		this.createButton = new GuiButton(400, 40, "Create world ...");
		this.createButton.setPosition(0, 0);
		this.createButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.mainBlock.addChild(this.createButton);
		
		// Cancel button
		this.cancelButton = new GuiButton(300, 40, "Cancel");
		this.cancelButton.setPosition(0, 100);
		this.cancelButton.addEventListener(GuiButtonActionEvent.class, this::onButtonClicked);
		this.mainBlock.addChild(this.cancelButton);
		
		this.updateFutureFileName("");
		
	}
	
	@Override
	protected void loaded(Class<? extends GuiScene> previousScene) {
		super.loaded(previousScene);
		this.worldNameInput.setInputActive(true);
	}
	
	@Override
	protected void onSceneResized(float width, float height) {
		
		super.onSceneResized(width, height);
		
		this.mainBlock.setPosition(width / 2f, height / 2f);
		
	}
	
	private void onButtonClicked(GuiButtonActionEvent event) {
		
		if (event.isOrigin(this.cancelButton)) {
			
			if (this.previousScene != null) {
				this.manager.loadScene(this.previousScene);
			}
			
		} else if (event.isOrigin(this.createButton)) {
			
			String worldName = this.worldNameInput.getInputText();
			
			if (!this.worldIdentifier.isEmpty() && !worldName.isEmpty()) {
				
				File worldDir = this.worldList.createNewWorldDirectory(this.worldIdentifier, worldName);
				
				System.out.println("world dir : " + worldDir);
				
				if (worldDir != null) {
					
					try {
						
						WorldDimensionManager servedWorld = new WorldDimensionManager(worldDir);
						ProcGenGame.getGameInstance().setServedWorld(servedWorld);
						
						WorldServer world = servedWorld.createNewDimension("testdim", this.getWorldSeed(), WorldGenerators.BETA_CHUNK_PROVIDER);
						
						WorldLoadingPosition loadingPosition = new WorldLoadingPosition(0, 0);
						world.addWorldLoadingPosition(loadingPosition);
						
						System.out.println("Chunk loading position added " + loadingPosition);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			}
		
		}
		
	}
	
	private void onTextInputChanged(GuiTextInputChangedEvent event) {
		
		if (event.isOrigin(this.worldNameInput)) {
			this.updateFutureFileName(event.getValue());
		}
		
	}
	
	private void updateFutureFileName(String value) {
		
		this.worldIdentifier = this.worldList.makeValidIdentifierFromName(value);
		this.futureWorldIdentifier.setText("Folder '" + this.worldIdentifier + "'");
		
		this.createButton.setDisabled(this.worldIdentifier.isEmpty());
		
	}
	
	private long getWorldSeed() {
		
		String seedRaw = this.worldNameInput.getInputText();
		
		if (seedRaw.isEmpty())
			return this.seedRandom.nextLong();
		
		try {
			return Long.parseLong(seedRaw);
		} catch (NumberFormatException e) {
			return WorldGenerationOption.getSeedFromString(seedRaw);
		}
		
	}
	
}
