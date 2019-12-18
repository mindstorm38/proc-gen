package fr.theorozier.procgen.util;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapUtils {
	
	public static <K, V> HashMap<K, V> createMap(Iterable<K> keysIterable, Iterable<V> valuesIterable) {
		return populateMap(keysIterable, valuesIterable, Maps.newHashMap());
	}

	public static <K, V> HashMap<K, V> populateMap(Iterable<K> keysIterable, Iterable<V> valuesIterable, HashMap<K, V> map) {
		
		Iterator<V> valuesIt = valuesIterable.iterator();
		
		for (K key : keysIterable) {
			map.put(key, valuesIt.next());
		}
		
		if (valuesIt.hasNext())
			throw new NoSuchElementException();
		
		return map;
		
	}

}
