package zx.soft.weibo.mapred.source;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SourceId {

	private static LinkedList<String> usefuls = new LinkedList<>();
	private static LinkedList<String> uselesses = new LinkedList<>();

	public static String getFirstUseful() {
		String tmp = null;
		synchronized (usefuls) {
			while (usefuls.isEmpty()) {
				try {
					Thread.sleep(10 * 60 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			tmp = usefuls.remove(0);
			usefuls.add(tmp);
		}
		return tmp;
	}

	public static void removeIdUseful(String id) {
		synchronized (usefuls) {
			Iterator<String> iterator = usefuls.iterator();
			while (iterator.hasNext()) {
				String tmp = iterator.next();
				if (tmp.equals(id)) {
					iterator.remove();
				}
			}
		}
	}

	public static void addIdUseful(String id) {
		synchronized (usefuls) {
			if (!usefuls.contains(id)) {
				usefuls.add(id);
			}
		}
	}

	public static void addIdUselesses(String id) {
		synchronized (uselesses) {
			if (!uselesses.contains(id)) {
				uselesses.add(id);
			}
		}
	}

	public static void removeIdUseless(String id) throws InterruptedException {
		synchronized (uselesses) {
			Iterator<String> iterator = uselesses.iterator();
			while (iterator.hasNext()) {
				String tmp = iterator.next();
				if (tmp.equals(id)) {
					iterator.remove();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static List<String> getUsefuls() {
		return (List<String>) usefuls.clone();
	}

	@SuppressWarnings("unchecked")
	public static List<String> getUselesses() {
		return (List<String>) uselesses.clone();
	}

	public static int getLenUseless() {
		return uselesses.size();
	}

	public static int getLenUseful() {
		return usefuls.size();
	}

}
