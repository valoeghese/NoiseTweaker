package tk.valoeghese.noisetweaker.util;

import java.util.List;

public final class Debug {
	private Debug() {
	}
	
	public static <T> void list(List<T> list) {
		System.out.println("Debug List ---------");
		list.forEach(System.out::println);
		System.out.println("----------------");
	}
	
	public static <T> void array(T[] array) {
		System.out.println("Debug Array ---------");
		for (T t : array) {
			System.out.println(t);
		}
		System.out.println("----------------");
	}
}
