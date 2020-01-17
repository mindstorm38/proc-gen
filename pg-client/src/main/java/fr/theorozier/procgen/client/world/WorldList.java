package fr.theorozier.procgen.client.world;

import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static io.msengine.common.util.GameLogger.LOGGER;

public class WorldList {
	
	private static final Pattern REMOVE_DIACRITICAL_MARKS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	private static final Pattern REMOVE_ALL_NON_ASCII_PATTERN     = Pattern.compile("[^a-z0-9_]");
	private static final int MAX_IDENTIFIER_LENGTH                = 32;
	
	private final File worldsDirectory;
	private final Map<String, Entry> worldEntries;
	private final Map<String, Entry> worldEntriesView;
	
	public WorldList(File worldsDirectory) {
		
		this.worldsDirectory = worldsDirectory;
		this.worldEntries = new HashMap<>();
		this.worldEntriesView = Collections.unmodifiableMap(this.worldEntries);
		
		if (!worldsDirectory.isDirectory())
			worldsDirectory.mkdirs();
		
	}
	
	public void reload() {
		
		ArrayList<String> deprecatedWorlds = new ArrayList<>();
		
		for (Map.Entry<String, Entry> entry : this.worldEntries.entrySet()) {
			
			File descriptionFile = getValidWorldDescription(entry.getValue().getDirectory());
			
			if (descriptionFile == null || !decodeWorldDescriptionToEntry(descriptionFile, entry.getValue())) {
				deprecatedWorlds.add(entry.getKey());
			}
			
		}
		
		for (String deprecatedWorld : deprecatedWorlds)
			this.worldEntries.remove(deprecatedWorld);
		
		this.worldsDirectory.listFiles((File dir) -> {
			
			if (dir.isDirectory()) {
				
				File descriptionFile = getValidWorldDescription(dir);
				
				if (descriptionFile != null) {
					
					Entry entry = new Entry(dir, dir.getName());
					if (decodeWorldDescriptionToEntry(descriptionFile, entry)) {
						this.worldEntries.put(entry.identifier, entry);
					}
					
				}
				
			}
			
			return false;
			
		});
		
	}
	
	public Map<String, Entry> getWorldEntriesView() {
		return this.worldEntriesView;
	}
	
	public String makeValidIdentifierFromName(String name) {
	
		if (name.length() > MAX_IDENTIFIER_LENGTH)
			name = name.substring(0, MAX_IDENTIFIER_LENGTH);
		
		String identifier = Normalizer.normalize(name.toLowerCase(), Normalizer.Form.NFD);
		identifier = REMOVE_DIACRITICAL_MARKS_PATTERN.matcher(identifier).replaceAll("");
		identifier = REMOVE_ALL_NON_ASCII_PATTERN.matcher(identifier).replaceAll("_");
		
		if (this.worldEntries.containsKey(identifier)) {
			
			String newId = identifier + '0';
			
			for (int uid = 1; uid < 256 && this.worldEntries.containsKey(newId); ++uid) {
				newId = identifier + uid;
			}
			
			if (this.worldEntries.containsKey(newId)) {
				identifier = "cant_make_id";
			} else {
				identifier = newId;
			}
			
		}
		
		return identifier;
		
	}
	
	private static boolean decodeWorldDescriptionToEntry(File file, Entry entry) {
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String val = reader.readLine();
			
			if (val != null) {
				
				entry.setName(val);
				
				val = reader.readLine();
				
				if (val != null) {
					
					try {
						
						long lastPlayed = Long.parseLong(val);
						entry.setLastPlayed(lastPlayed < 0 ? -1 : lastPlayed);
						
					} catch (NumberFormatException e) {
						LOGGER.warning("Failed to decode world last played timestamp at " + file + ".");
					}
					
				}
				
			}
			
			return true;
			
		} catch (IOException e) {
			
			LOGGER.log(Level.WARNING, "Failed to read world description file.", e);
			return false;
			
		}
	
	}
	
	/**
	 * Get a world description if the world directory is valid.
	 * @param dir The world directory.
	 * @return The world list description file, or null if the world directory is invalid.
	 */
	public static File getValidWorldDescription(File dir) {
		
		if (!dir.isDirectory())
			return null;
		
		File descriptionFile = new File(dir, "description");
		return descriptionFile.isFile() ? descriptionFile : null;
		
	}
	
	public static class Entry {
	
		private final File directory;
		private final String identifier;
		private String name;
		private long lastPlayed;
		
		private Entry(File directory, String identifier) {
			
			this.directory = directory;
			this.identifier = identifier;
			this.name = identifier;
			this.lastPlayed = -1;
			
		}
		
		public File getDirectory() {
			return this.directory;
		}
		
		public String getIdentifier() {
			return this.identifier;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public long getLastPlayed() {
			return lastPlayed;
		}
		
		public void setLastPlayed(long lastPlayed) {
			this.lastPlayed = lastPlayed;
		}
		
	}
	
}
