package backend.state.ships;

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
	public static IShip GetAircraftCarrier()
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
	public static IShip GetBattleship()
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
	public static IShip GetCruiser()
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
	public static IShip GetPatrolBoat()
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
	public static IShip GetSubmarine()
	{
		if(submarine == null)
		{
			submarine = new Submarine();
		}
		return submarine;
	}
}
