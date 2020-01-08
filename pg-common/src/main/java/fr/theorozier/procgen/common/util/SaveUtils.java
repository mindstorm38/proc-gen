package fr.theorozier.procgen.common.util;

import java.util.regex.Pattern;

/**
 *
 * Utilities for data saving (world, blocks, entities, ...).
 *
 * @author Theo Rozier
 *
 */
public class SaveUtils {
	
	private static final Pattern SAVABLE_NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
	
	/**
	 * Check if a name is suited for data saving (if match regex <b>^[a-z0-9_]+$</b>).
	 * @param name The name to check.
	 * @return True if this name is suited for data saving.
	 */
	public static boolean isValidSavableName(String name) {
		return SAVABLE_NAME_PATTERN.matcher(name).matches();
	}
	
	/**
	 * Throw an error if the passed name is not suited for data saving.
	 * @param name The name to test.
	 * @param containerName The name of the container that hold this name,
	 *                         this name is only used for exception message.
	 * @return The name if it passed the test.
	 * @throws IllegalStateException If the name passed as argument is not suited for data saving.
	 * @see #isValidSavableName(String)
	 */
	public static String validateSavableName(String name, String containerName) {
		
		if (!isValidSavableName(name))
			throw new IllegalStateException("The name '" + name + "' is not suited for data saving in " + containerName + ".");
		
		return name;
		
	}
	
}