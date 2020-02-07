package fr.theorozier.procgen.common.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class FunctionUtils {
	
	public static <T> Predicate<T> toPredicate(Consumer<T> consumer, boolean ret) {
		
		return (o) -> {
			consumer.accept(o);
			return ret;
		};
		
	}
	
}
