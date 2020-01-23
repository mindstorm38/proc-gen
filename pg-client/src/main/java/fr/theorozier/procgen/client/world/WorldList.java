package fr.theorozier.procgen.client.world;

import fr.theorozier.procgen.common.util.SaveUtils;

import java.io.*;
import java.text.Normalizer;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static io.msengine.common.util.GameLogger.LOGGER;

public class WorldList {
	
	private static final Pattern REMOVE_DIACRITICAL_MARKS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	private static final Pattern REMOVE_ALL_NON_ASCII_PATTERN     = Pattern.compile("[^a-z0-9_]");
	private static final int MAX_IDENTIFIER_LENGTH                = 32;
	private static final String WORLD_DESCRIPTION_FILE            = "description";
	
	private final File worldsDirectory;
	private final Map<String, Entry> worldEntries;
	private final Map<String, Entry> worldEntriesView;
	
	public WorldList(File worldsDirectory) {
		
		SaveUtils.mkdirOrThrowException(worldsDirectory, "The worlds directory already exists but it's a file.");
		
		this.worldsDirectory = worldsDirectory;
		this.worldEntries = new HashMap<>();
		this.worldEntriesView = Collections.unmodifiableMap(this.worldEntries);
		
	}
	
	/**
	 * Reload known world list entries.
	 * @return The internal world list entries view.
	 * @see #getWorldEntriesView()
	 */
	public Map<String, Entry> reload() {
		
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
		
		return this.worldEntriesView;
		
	}
	
	public Map<String, Entry> getWorldEntriesView() {
		return this.worldEntriesView;
	}
	
	/**
	 * Check if a world identifier is not already used.
	 * @param identifier A world identifier.
	 * @return True if this identifier is already used.
	 */
	public boolean isWorldIdentifierValid(String identifier) {
		return this.worldEntries.containsKey(identifier);
	}
	
	/**
	 * Create a valid identifier from a world name.
	 * @param name A world name.
	 * @return A valid identifier that can be used to create a world directory.
	 */
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
				identifier = "cant_make_id_" + Math.abs(new Random().nextInt());
			} else {
				identifier = newId;
			}
			
		}
		
		return identifier;
		
	}
	
	/**
	 * Make directory of a specified world and create the world description into.
	 * @param worldIdentifier The world identifier.
	 * @param worldName The world name.
	 * @return The file directory created for the world. If null returned, a warning message has been logged.
	 */
	public File createNewWorldDirectory(String worldIdentifier, String worldName) {
		
		if (this.worldEntries.containsKey(worldIdentifier)) {
			
			LOGGER.warning("Can't create new world directory for '" + worldIdentifier + "' because a world already has this name in internal list entries.");
			return null;
			
		}
		
		File dir = new File(this.worldsDirectory, worldIdentifier);
		
		if (dir.exists()) {
			
			LOGGER.warning("Can't create new world directory for '" + worldIdentifier + "' because a file or dir already exists with this name.");
			return null;
			
		}
		
		dir.mkdir();
		
		Entry listEntry = new Entry(dir, worldIdentifier);
		listEntry.setName(worldName);
		
		File descriptionFile = new File(dir, WORLD_DESCRIPTION_FILE);
		
		if (!encodeWorldDescriptionFromEntry(descriptionFile, listEntry)) {
			
			LOGGER.warning("Failed to the world description file for '" + worldIdentifier + "'.");
			
			// Delete description file if created and the directory, to avoid problem with re-try of creation of this world.
			if (descriptionFile.exists())
				descriptionFile.delete();
			
			dir.delete();
			
			return null;
			
		}
		
		this.worldEntries.put(worldIdentifier, listEntry);
		
		return dir;
		
	}
	
	/**
	 * Decode a world description file to a world list entry.
	 * @param file The world description file.
	 * @param entry The world list entry.
	 * @return True if the world description has been successfuly read.
	 */
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
			
			reader.close();
			
			return true;
			
		} catch (IOException e) {
			
			LOGGER.log(Level.WARNING, "Failed to read world description file.", e);
			return false;
			
		}
	
	}
	
	/**
	 * Encode a world description file from a world list entry.
	 * @param file The description file.
	 * @param entry The world list entry.
	 * @return True if the world description was successfuly written.
	 */
	private static boolean encodeWorldDescriptionFromEntry(File file, Entry entry) {
		
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(entry.getName());
			writer.newLine();
			writer.write(Long.toString(entry.getLastPlayed()));
			writer.newLine();
			writer.close();
			
			return true;
			
		} catch (IOException e) {
			
			LOGGER.log(Level.WARNING, "Failed to write world description file.", e);
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
		
		File descriptionFile = new File(dir, WORLD_DESCRIPTION_FILE);
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
		
		@Override
		public String toString() {
			return "Entry{" +
					"directory=" + directory +
					", identifier='" + identifier + '\'' +
					", name='" + name + '\'' +
					", lastPlayed=" + lastPlayed +
					'}';
		}
		
	}
	
}
