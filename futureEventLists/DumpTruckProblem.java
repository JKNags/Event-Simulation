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
	static ArrayList<Integer> travellingTrucks = new ArrayList<Integer>();
	static int busyLoader = 0, busyScale = 0;
	static int timeIncrement;
	static DumpTruckEventList events;
	static int[] loaderQueueTotalTimes, loadingTotalTimes, scaleQueueTotalTimes, scalingTotalTimes, travelTotalTimes;
	static final int numTrucks = 6, numLoaders = 2, numScales = 1, endTime = 100;
	// 6 Trucks
	// 2 Loaders
	// 1 Scale
	
	public static void main(String[] args) {
		PrintHeaderTable();
		
		DumpTruckEvent newEvent;
		
		// Initialization
		events = new DumpTruckEventList();
		loaderQueue.add(4); loaderQueue.add(5); loaderQueue.add(6);
		loadingTrucks.add(2); loadingTrucks.add(3);
		scalingTrucks.add(1);
		events.add(new DumpTruckEvent(DumpTruckEvent.Type.EndLoader, loadingTimes[loadingIdx++], 2));
		events.add(new DumpTruckEvent(DumpTruckEvent.Type.EndLoader, loadingTimes[loadingIdx++], 3));
		events.add(new DumpTruckEvent(DumpTruckEvent.Type.EndScale, scaleTimes[scaleIdx++], 1));
		loaderQueueTotalTimes = new int[6];
		loadingTotalTimes = new int[6];
		scaleQueueTotalTimes = new int[6];
		scalingTotalTimes = new int[6];
		travelTotalTimes = new int[6];
		boolean debug = false;
		PrintFelTable();
		
		// Loop
		try {
			while (time <= endTime) {
				timeIncrement = events.nextTime() - time;
				time += timeIncrement;
				DumpTruckEvent nextEvent = events.pop();
				
				// Ran out of times
				if ((loadingIdx == loadingTimes.length && nextEvent.getType().equals(DumpTruckEvent.Type.ArriveLoader))
						|| (scaleIdx == scaleTimes.length && nextEvent.getType().equals(DumpTruckEvent.Type.EndLoader) && scalingTrucks.size() != numScales)
						|| (travelIdx == travelTimes.length && nextEvent.getType().equals(DumpTruckEvent.Type.EndScale))) {
					break;
				}
				
				System.out.println("loadingIdx="+loadingIdx+", scaleIdx="+scaleIdx+", travelIdx="+travelIdx);
				
				// Set busy time
				busyLoader += loadingTrucks.size() * timeIncrement;
				busyScale += scalingTrucks.size() * timeIncrement;
				
				// Set total times
				for (int t : loaderQueue) {
					loaderQueueTotalTimes[t-1] += timeIncrement;
				}
				for(int t : loadingTrucks) {
					loadingTotalTimes[t-1] += timeIncrement;
				}
				for(int t : scaleQueue) {
					scaleQueueTotalTimes[t-1] += timeIncrement;
				}
				for(int t : scalingTrucks) {
					scalingTotalTimes[t-1] += timeIncrement;
				}
				for(int t : travellingTrucks) {
					travelTotalTimes[t-1] += timeIncrement;
				}
				
				// Respond to event
				switch (nextEvent.getType()) {
				
					// ARRIVE LOADER EVENT
					case ArriveLoader:
						travellingTrucks.remove(new Integer(nextEvent.getTruckNum()));
						
						if (loadingTrucks.size() == numLoaders) {
							// Loader(s) full
							loaderQueue.add(nextEvent.getTruckNum());		if (debug) System.out.println("\tAdding DT:"+nextEvent.getTruckNum()+" to loader queue");
						} else {
							// Loader(s) available
							loadingTrucks.add(nextEvent.getTruckNum());
							newEvent = new DumpTruckEvent(DumpTruckEvent.Type.EndLoader, time + loadingTimes[loadingIdx++], nextEvent.getTruckNum());
							events.add(newEvent);	if (debug) System.out.println("\tAdding DT:"+nextEvent.getTruckNum()+" to loader : " + newEvent);
						}
						break;
					
					// END LOADER EVENT
					case EndLoader:
						loadingTrucks.remove(new Integer(nextEvent.getTruckNum()));
						
						if (scalingTrucks.size() == numScales) {
							// Scale full
							scaleQueue.add(nextEvent.getTruckNum());		if (debug) System.out.println("\tAdding DT:"+nextEvent.getTruckNum()+" to scale queue");
						} else {
							// Scale available
							scalingTrucks.add(nextEvent.getTruckNum());
							newEvent = new DumpTruckEvent(DumpTruckEvent.Type.EndScale, time + scaleTimes[scaleIdx++], nextEvent.getTruckNum());
							events.add(newEvent);	if (debug) System.out.println("\tAdding DT:"+nextEvent.getTruckNum()+" to scale : " + newEvent);
						}
						
						// Pull another truck into Loader
						if (loaderQueue.size() > 0) {
							int toLoaderTruck = loaderQueue.remove(0);
							loadingTrucks.add(toLoaderTruck);
							newEvent = new DumpTruckEvent(DumpTruckEvent.Type.EndLoader, time + loadingTimes[loadingIdx++], toLoaderTruck);
							events.add(newEvent);	if (debug) System.out.println("\tAdding DT:"+toLoaderTruck+" to loader : " + newEvent);
						}
						break;
						
					// END SCALE EVENT
					case EndScale:
						scalingTrucks.remove(new Integer(nextEvent.getTruckNum()));
						
						newEvent = new DumpTruckEvent(DumpTruckEvent.Type.ArriveLoader, time + travelTimes[travelIdx++], nextEvent.getTruckNum());
						events.add(newEvent);	if (debug) System.out.println("\tAdding DT:"+nextEvent.getTruckNum()+" to travel : " + newEvent);
						
						// Pull another truck into scale
						if (scaleQueue.size() > 0) {
							int toScaleTruck = scaleQueue.remove(0);
							scalingTrucks.add(toScaleTruck);
							newEvent = new DumpTruckEvent(DumpTruckEvent.Type.EndScale, time + scaleTimes[scaleIdx++], toScaleTruck);
							events.add(newEvent);	if (debug) System.out.println("\tAdding DT:"+toScaleTruck+" to scale : " + newEvent);
						}

						travellingTrucks.add(nextEvent.getTruckNum());
						break;
				}
				
				PrintFelTable();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("\n\nDT\tLQ\tL\tWQ\tW\tT");
		for (int t = 0; t < 6; t++) {
			System.out.println("DT" + (t+1) + "\t" + loaderQueueTotalTimes[t] + "\t"  + loadingTotalTimes[t] + "\t" + scaleQueueTotalTimes[t] + "\t" + scalingTotalTimes[t] + "\t" + travelTotalTimes[t]);
		}
	}
	
	static void PrintHeader() {
		String output = "";
		output += String.format("%-5s", "t");
		output += String.format("%-13s", "LQ");
		output += String.format("%-10s", "L");
		output += String.format("%-13s", "WQ");
		output += String.format("%-10s", "W");
		output += String.format("%-15s", "T");
		output += String.format("%-20s", "FEL");
		output += String.format("%-5s", "BL");
		output += String.format("%-5s", "BS");
		System.out.println(output);
	}
	
	static void PrintHeaderTable() {
		System.out.println("t\t"+"LQ\t"+"L\t"+"WQ\t"+"W\t"+"T\t"+"FEL\t"+"BL\t"+"BS\t");
	}
	
	static void PrintFel() {
		String output = "";
		String[] eventsString = events.toStringList();
		output += String.format("%-5d", time);
		output += String.format("%-13s", Arrays.toString(loaderQueue.toArray()));
		output += String.format("%-10s", Arrays.toString(loadingTrucks.toArray()));
		output += String.format("%-13s", Arrays.toString(scaleQueue.toArray()));
		output += String.format("%-10s", Arrays.toString(scalingTrucks.toArray()));
		output += String.format("%-15s", Arrays.toString(travellingTrucks.toArray()));
		output += String.format("%-20s", eventsString[0]);
		output += String.format("%-5d", busyLoader);
		output += String.format("%-5d", busyScale);
		
		for (int idx = 1; idx < eventsString.length; idx++) {
			output += String.format("\n%-56s%-20s", "", eventsString[idx]);
		}
		
		System.out.println(output);
	}
	
	static void PrintFelTable() {
		String output = "";
		String[] eventsString = events.toStringList();
		System.out.println(time+"\t"
				+Arrays.toString(loaderQueue.toArray())+"\t"
				+Arrays.toString(loadingTrucks.toArray())+"\t"
				+Arrays.toString(scaleQueue.toArray())+"\t"
				+Arrays.toString(scalingTrucks.toArray())+"\t"
				+Arrays.toString(travellingTrucks.toArray())+"\t"
				+eventsString[0]+"\t"
				+busyLoader+"\t"
				+busyScale);
		for (int idx = 1; idx < eventsString.length; idx++) {
			System.out.println("\t\t\t\t\t\t"+eventsString[idx]);
		}	
	}

}
