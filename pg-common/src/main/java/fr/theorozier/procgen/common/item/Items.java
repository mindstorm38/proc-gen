package fr.theorozier.procgen.common.item;

import java.util.HashMap;
import java.util.Map;

public class Items {

	private static final Map<Short, Item> uidRegister = new HashMap<>();
	private static final Map<String, Item> identifierRegister = new HashMap<>();
	
	
	
	public static <I extends Item> I registerItem(I item) {
		
		uidRegister.put(item.getUid(), item);
		identifierRegister.put(item.getIdentifier(), item);
		
		return item;
		
	}
	
}
