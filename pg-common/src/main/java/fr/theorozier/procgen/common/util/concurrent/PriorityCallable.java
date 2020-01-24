package fr.theorozier.procgen.common.util.concurrent;

import java.util.concurrent.Callable;

public interface PriorityCallable<V> extends Callable<V>, PrioritySupplier {

}
