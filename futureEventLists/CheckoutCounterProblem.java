package futureEventLists;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CheckoutCounterProblem {

	// Variables
	static int[] interArrivalTimes = {4, 5, 2, 8, 3, 7}; //{1, 1, 6, 3, 7, 5, 2, 4, 1};
	static int[] serviceTimes = {5, 3, 4, 6, 2, 7}; //{4, 2, 5, 4, 1, 5, 4, 1, 4};
	static int arrivalIndex = 0, serviceIndex = 0, time = 0, queueLength = 0, serviceLength = 0;
	static int nextArrivalTime, nextDepartureTime, busyTime, maxQueueLength = 0, timeIncrement;
	static int customerIdx = 0;
	static Map<Integer, Integer> custArrivals;
	static final int endTime = 40, numServers = 1;
	
	public static void main(String[] args) {	
		PrintHeader();
		
		//Initialize FEL
		serviceLength = 1;  // begin with one customer being serviced
		nextArrivalTime = interArrivalTimes[arrivalIndex++];
		nextDepartureTime = serviceTimes[serviceIndex++];
		
		int custNum = 1;
		custArrivals = new HashMap<Integer, Integer>();
		
		PrintFEL();
		//time = 1;
		
		// Loop
		try {
			while (time <= endTime) {
				timeIncrement = Math.min(nextArrivalTime, nextDepartureTime) - time;
				time += timeIncrement;
				//System.out.println("\t timeIncrement="+timeIncrement+", nextArrivalTime="+nextArrivalTime
				//	+", nextDepartureTime="+nextDepartureTime);
				
				// Increment busy time
				if (serviceLength == numServers) {
					busyTime += timeIncrement;
				}
				
				// Check for departure time
				if (time == nextDepartureTime) {
					serviceLength--;
					// Service from queue
					if (queueLength > 0) {
						queueLength--;
						serviceLength++;
					}
					nextDepartureTime = (serviceLength == 0 ? nextArrivalTime : time) + serviceTimes[serviceIndex++];
				}
				
				// Check for arrival time
				if (time == nextArrivalTime) {
					custNum++;
					
					// Server(s) busy
					if (serviceLength == numServers) {
						custArrivals.put(custNum, time);
						queueLength++;
					} else {
						serviceLength++;
					}
					
					nextArrivalTime = time + interArrivalTimes[arrivalIndex++];
				}
				
				// Set max queue length
				maxQueueLength = Math.max(maxQueueLength, queueLength);
				
				PrintFEL();
				
				if (arrivalIndex == interArrivalTimes.length || serviceIndex == serviceTimes.length) {
					//System.out.println("arrivalIndex="+arrivalIndex+", serviceIndex="+serviceIndex);
					//break;
				}
			}	
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	static void PrintHeader() {
		String output = "";
		output += String.format("%-5s", "T");
		output += String.format("%-5s", "LQt");
		output += String.format("%-5s", "LSt");
		output += String.format("%-15s", "Line");
		output += String.format("%-22s", "FEL");
		output += String.format("%-5s", "B");
		output += String.format("%-5s", "MaxQ");
		System.out.println(output);
	}
	
	static void PrintFEL() {
		String output = "";
		
		String line = "";
	    Iterator it = custArrivals.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        line = "(C" + pair.getKey() + "," + pair.getValue() + "),";
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    
	    line = line.length() == 0 ? "" : line.substring(0, line.length()-1);
	
		output += String.format("%-5d", time);
		output += String.format("%-5d", queueLength);
		output += String.format("%-5d", serviceLength);
		output += String.format("%-15s", line);
		if(nextArrivalTime<nextDepartureTime)output += String.format("%-22s", "(A," + nextArrivalTime + "), (D," + nextDepartureTime + ")");
		else output += String.format("%-22s", "(D," + nextDepartureTime + "), (A," + nextArrivalTime + ")");
		output += String.format("%-5d", busyTime);
		output += String.format("%-5d", maxQueueLength);
		
		System.out.println(output);
	}

}
