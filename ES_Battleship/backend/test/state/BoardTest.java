package backend.test.state;

import junit.framework.TestCase;
import backend.state.Board;
import backend.state.Constants;
import backend.state.Orientation;
import backend.state.ships.Ships;

public class BoardTest extends TestCase {

	private Board board;
	
	protected void setUp() throws Exception {
		board = new Board();
		board.add(Ships.GetAircraftCarrier(), 0, 0, Orientation.VERTICAL);
		board.add(Ships.GetBattleship(), 1, 1, Orientation.HORIZONTAL);
		board.add(Ships.GetCruiser(), 6, 4, Orientation.VERTICAL);
		board.add(Ships.GetPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
		board.add(Ships.GetSubmarine(), 5, 5, Orientation.VERTICAL);
	}

	public void testSetHit() {
		for(int x = 0; x < this.board.length(); x++)
		{
			for(int y = 0; y < this.board.length(); y++)
			{
				board.setHit(x, y);
				assertEquals("Hit did not register", Constants.BOARD_HIT, board.getCoordinate(x, y));
			}
		}
	}

	public void testSetMiss() {
		for(int x = 0; x < this.board.length(); x++)
		{
			for(int y = 0; y < this.board.length(); y++)
			{
				board.setMiss(x, y);
				assertEquals("Miss did not register", Constants.BOARD_MISS, board.getCoordinate(x, y));
			}
		}
	}

	public void testIsHit() {
		
		assertEquals("Hit was not determined correctly", true, board.isHit(0, 0));
		assertEquals("Hit was not determined correctly", true, board.isHit(0, 2));
		
		assertEquals("Hit was not determined correctly", false, board.isHit(0, 6));
		assertEquals("Hit was not determined correctly", false, board.isHit(6, 6));
		assertEquals("Hit was not determined correctly", false, board.isHit(6, 7));
		
		for(int x = 0; x < this.board.length(); x++)
		{
			for(int y = 0; y < this.board.length(); y++)
			{
				board.setHit(x, y);
				assertEquals("Hit was not determined correctly", true, board.isHit(x, y));
				board.setMiss(x, y);
				assertEquals("Miss was not determined correctly", false, board.isHit(x, y));
			}
		}
	}

	public void testHasLost() {
		fail("Not yet implemented");
	}

	public void testSetCoordinate() {
		fail("Not yet implemented");
	}

	public void testGetCoordinate() {
		fail("Not yet implemented");
	}

	public void testGetShipCoordinates() {
		fail("Not yet implemented");
	}

	public void testGetAllMissedCoordinates() {
		fail("Not yet implemented");
	}

	public void testGetAllHitCoordinates() {
		fail("Not yet implemented");
	}

	public void testGetAllEmptyCoordinates() {
		fail("Not yet implemented");
	}

	public void testAddIShipCoordinatesOrientation() {
		fail("Not yet implemented");
	}

	public void testAddIShipIntIntOrientation() {
		Board board2 = new Board();
		board2.add(Ships.GetPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
		assertEquals("Incorrect ship placement", 
				Ships.GetPatrolBoat().getSymbol(), board2.getCoordinate(6, 9));
		assertEquals("Incorrect ship placement", 
				Constants.BOARD_EMPTY, board2.getCoordinate(6, 6));
		assertEquals("Incorrect ship placement", 
				Constants.BOARD_EMPTY, board2.getCoordinate(6, 7));
	}

	public void testSerialize() {
		String expected = "a*********abbbb*****a*********a*********a**************s********cspp******cs********c***************";
		fail("Not yet implemented");
	}

	public void testDeserialize() {
		fail("Not yet implemented");
	}

}
