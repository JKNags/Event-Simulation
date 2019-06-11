package futureEventLists;

public class DumpTruckEvent implements Comparable<DumpTruckEvent>  {

	public static enum Type {
		ArriveLoader,
		EndLoader,
		EndScale
	}
	
	private Type type;
	private int time;
	private int truckNum;
	
	public DumpTruckEvent(Type _type, int _time, int _truckNum) {
		this.type = _type;
		this.time = _time;
		this.truckNum = _truckNum;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public int getTime() {
		return this.time;
	}
	
	public int getTruckNum() {
		return this.truckNum;
	}
	
	public int compareTo(DumpTruckEvent otherEvent) {          
		return (this.getTime() - otherEvent.getTime());
	}   
	
	public String toString() {
		String output = "(";
		
		switch (this.type) {
			case ArriveLoader:
				output += "ALQ, "; break; 
			case EndLoader:
				output += "EL, "; break; 
			case EndScale:
				output += "EW, "; break; 
		}
		
		output += "t:" + time + ", DT:" + truckNum + ")";
		return output;
	}
}
