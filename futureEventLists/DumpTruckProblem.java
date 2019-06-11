package futureEventLists;

import java.util.ArrayList;
import java.util.Arrays;

public class DumpTruckProblem {

	static int time = 0;
	static int[] loadingTimes = {10, 5, 10, 10, 5, 10, 5}; //{10, 5, 5, 10, 15, 10, 10};
	static int[] scaleTimes = {12, 16, 12, 12, 16, 12, 12}; //{12, 12, 12, 16, 12, 16};
	static int[] travelTimes = {40, 60, 40, 80, 100, 40}; //{60, 100, 40, 40, 80};
	static int loadingIdx = 0, scaleIdx = 0, travelIdx = 0;
	static ArrayList<Integer> loaderQueue = new ArrayList<Integer>();
	static ArrayList<Integer> loadingTrucks = new ArrayList<Integer>();
	static ArrayList<Integer> scaleQueue = new ArrayList<Integer>();
	static ArrayList<Integer> scalingTrucks = new ArrayList<Integer>();
	static int busyLoader = 0, busyScale = 0;
	static int timeIncrement;
	static DumpTruckEventList events;
	static final int numTrucks = 6, numLoaders = 2, numScales = 1, endTime = 100;
	// 6 Trucks
	// 2 Loaders
	// 1 Scale
	
	public static void main(String[] args) {
		PrintHeader();
		
		DumpTruckEvent newEvent;
		
		// Initialization
		events = new DumpTruckEventList();
		loaderQueue.add(4); loaderQueue.add(5); loaderQueue.add(6);
		loadingTrucks.add(2); loadingTrucks.add(3);
		scalingTrucks.add(1);
		events.add(new DumpTruckEvent(DumpTruckEvent.Type.EndLoader, loadingTimes[loadingIdx++], 2));
		events.add(new DumpTruckEvent(DumpTruckEvent.Type.EndLoader, loadingTimes[loadingIdx++], 3));
		events.add(new DumpTruckEvent(DumpTruckEvent.Type.EndScale, scaleTimes[scaleIdx++], 1));
		
		PrintFEL();
		
		// Loop
		while (time <= endTime) {
			timeIncrement = events.nextTime() - time;
			time += timeIncrement;
			DumpTruckEvent nextEvent = events.pop();
			
			//System.out.println("\tT:"+time+"(+"+timeIncrement+"), E:"+nextEvent);
		
			// Set busy time
			busyLoader += loadingTrucks.size() * timeIncrement;
			busyScale += scalingTrucks.size() * timeIncrement;
			
			switch (nextEvent.getType()) {
			
				// ARRIVE LOADER EVENT
				case ArriveLoader:
					if (loadingTrucks.size() == numLoaders) {
						// Loader(s) full
						loaderQueue.add(nextEvent.getTruckNum());		//System.out.println("\tAdding DT:"+nextEvent.getTruckNum()+" to loader queue");
					} else {
						// Loader(s) available
						loadingTrucks.add(nextEvent.getTruckNum());
						newEvent = new DumpTruckEvent(DumpTruckEvent.Type.EndLoader, time + loadingTimes[loadingIdx++], nextEvent.getTruckNum());
						events.add(newEvent);	//System.out.println("\tAdding DT:"+nextEvent.getTruckNum()+" to loader : " + newEvent);
					}
					break;
				
				// END LOADER EVENT
				case EndLoader:
					loadingTrucks.remove(new Integer(nextEvent.getTruckNum()));
					
					if (scalingTrucks.size() == numScales) {
						// Scale full
						scaleQueue.add(nextEvent.getTruckNum());		//System.out.println("\tAdding DT:"+nextEvent.getTruckNum()+" to scale queue");
					} else {
						// Scale available
						scalingTrucks.add(nextEvent.getTruckNum());
						newEvent = new DumpTruckEvent(DumpTruckEvent.Type.EndScale, time + scaleTimes[scaleIdx++], nextEvent.getTruckNum());
						events.add(newEvent);	//System.out.println("\tAdding DT:"+nextEvent.getTruckNum()+" to scale : " + newEvent);
					}
					
					// Pull another truck into Loader
					if (loaderQueue.size() > 0) {
						int toLoaderTruck = loaderQueue.remove(0);
						loadingTrucks.add(toLoaderTruck);
						newEvent = new DumpTruckEvent(DumpTruckEvent.Type.EndLoader, time + loadingTimes[loadingIdx++], toLoaderTruck);
						events.add(newEvent);	//System.out.println("\tAdding DT:"+toLoaderTruck+" to loader : " + newEvent);
					}
					break;
					
				// END SCALE EVENT
				case EndScale:
					scalingTrucks.remove(new Integer(nextEvent.getTruckNum()));
					
					newEvent = new DumpTruckEvent(DumpTruckEvent.Type.ArriveLoader, time + travelTimes[travelIdx++], nextEvent.getTruckNum());
					events.add(newEvent);	//System.out.println("\tAdding DT:"+nextEvent.getTruckNum()+" to travel : " + newEvent);
					
					// Pull another truck into scale
					if (scaleQueue.size() > 0) {
						int toScaleTruck = scaleQueue.remove(0);
						scalingTrucks.add(toScaleTruck);
						newEvent = new DumpTruckEvent(DumpTruckEvent.Type.EndScale, time + scaleTimes[scaleIdx++], toScaleTruck);
						events.add(newEvent);	//System.out.println("\tAdding DT:"+toScaleTruck+" to scale : " + newEvent);
					}
					
					break;
			}
			
			PrintFEL();
			
			// TODO: prevent ArrayIndexOutOfBoundsException
		}
		
	}
	
	static void PrintHeader() {
		String output = "";
		output += String.format("%-5s", "T");
		output += String.format("%-5s", "LQ");
		output += String.format("%-5s", "L");
		output += String.format("%-5s", "WQ");
		output += String.format("%-5s", "W");
		output += String.format("%-13s", "LoadQ");
		output += String.format("%-13s", "ScaleQ");
		output += String.format("%-20s", "FEL");
		output += String.format("%-5s", "BL");
		output += String.format("%-5s", "BS");
		System.out.println(output);
	}
	
	static void PrintFEL() {
		String output = "";
		String[] eventsString = events.toStringList();
		output += String.format("%-5d", time);
		output += String.format("%-5d", loaderQueue.size());
		output += String.format("%-5d", loadingTrucks.size());
		output += String.format("%-5d", scaleQueue.size());
		output += String.format("%-5d", scalingTrucks.size());
		output += String.format("%-13s", Arrays.toString(loaderQueue.toArray()));
		output += String.format("%-13s", Arrays.toString(scaleQueue.toArray()));
		output += String.format("%-20s", eventsString[0]);
		output += String.format("%-5d", busyLoader);
		output += String.format("%-5d", busyScale);
		for (int idx = 1; idx < eventsString.length; idx++) {
			output += String.format("\n%-51s%-20s", "", eventsString[idx]);
		}
		System.out.println(output);
	}

}
