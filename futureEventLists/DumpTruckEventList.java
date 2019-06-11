package futureEventLists;

import java.util.ArrayList;

public class DumpTruckEventList {

	private ArrayList<DumpTruckEvent> events;
	
	public DumpTruckEventList() {
		events = new ArrayList<DumpTruckEvent>();
	}
	
	public void add(DumpTruckEvent event) {
		events.add(event);
		events.sort(null);
	}
	
	public DumpTruckEvent pop() {
		if (events.size() == 0) return null;
		
		DumpTruckEvent nextEvent = events.remove(0);
		return nextEvent;
	}
	
	public int nextTime() {
		if (events.size() == 0) return 0;
		return events.get(0).getTime();
	}
	
	public String toString() {
		String output = "";
		for (DumpTruckEvent event : events) {
			output += event + ", ";
		}
		return output;
	}
	
	public String[] toStringList() {
		String[] output = new String[events.size()];
		for (int idx = 0; idx < events.size(); idx++) {
			output[idx] = events.get(idx).toString();
		}
		return output;
	}

}
