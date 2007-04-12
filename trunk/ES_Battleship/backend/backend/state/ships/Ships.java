package backend.state.ships;

import java.util.ArrayList;
import java.util.List;

/*
 * Returns instance of each ship using the singleton pattern.
 */
public abstract class Ships {
	
	private static AircraftCarrier aircraft;
	private static Battleship battleship;
	private static Cruiser cruiser;
	private static PatrolBoat patrol;
	private static Submarine submarine;
	
	/*
	 * Returns the instance of the Aircraft Carrier piece.
	 */
	public static IShip getAircraftCarrier()
	{
		if(aircraft == null)
		{
			aircraft = new AircraftCarrier();
		}
		return aircraft;
	}
	
	/*
	 * Returns the instance of the Battleship piece.
	 */
	public static IShip getBattleship()
	{
		if(battleship == null)
		{
			battleship = new Battleship();
		}
		return battleship;
	}
	
	/*
	 * Returns the instance of the Cruiser piece.
	 */
	public static IShip getCruiser()
	{
		if(cruiser == null)
		{
			cruiser = new Cruiser();
		}
		return cruiser;
	}
	
	/*
	 * Returns the instance of the Patrol Boat piece.
	 */
	public static IShip getPatrolBoat()
	{
		if(patrol == null)
		{
			patrol = new PatrolBoat();
		}
		return patrol;
	}
	
	/*
	 * Returns the instance of the Submarine piece.
	 */
	public static IShip getSubmarine()
	{
		if(submarine == null)
		{
			submarine = new Submarine();
		}
		return submarine;
	}

	/*
	 * Returns a list of all ship objects.
	 */
	public static List<IShip> getAllShips() {
		List<IShip> list = new ArrayList<IShip>();
		list.add(getAircraftCarrier());
		list.add(getBattleship());
		list.add(getCruiser());
		list.add(getPatrolBoat());
		list.add(getSubmarine());
		return list;
	}
}
