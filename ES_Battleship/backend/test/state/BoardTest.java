package backend.test.state;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Random;

import junit.framework.TestCase;
import backend.constants.Constants;
import backend.constants.Orientation;
import backend.state.Board;
import backend.state.Coordinates;
import backend.util.BackendException;
import backend.state.ships.*;

public class BoardTest extends TestCase {

	private Board board;

	protected void setUp() throws BackendException {
		board = new Board();
		board.add(Ships.getAircraftCarrier(), 0, 0, Orientation.VERTICAL);
		board.add(Ships.getPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
	}
	
	protected void tearDown() throws BackendException {
		board.reset();
	}

	public void testSetHit() {
		for (int y = 0; y < this.board.length(); y++) {
			for (int x = 0; x < this.board.length(); x++) {
				try {
					this.board.setHit(x, y);
					assertEquals("Hit did not register", Constants.BOARD_HIT,
							this.board.getCoordinate(x, y));
				} catch (BackendException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void testSetMiss() {
		for (int y = 0; y < this.board.length(); y++) {
			for (int x = 0; x < this.board.length(); x++) {
				try {
					this.board.setMiss(x, y);
					assertEquals("Miss did not register", Constants.BOARD_MISS,
							this.board.getCoordinate(x, y));
				} catch (BackendException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void testIsHit() throws BackendException {
		assertEquals("Hit was not determined correctly", true, board.isHit(0, 0));
		assertEquals("Hit was not determined correctly", true, board.isHit(0, 2));
		assertEquals("Hit was not determined correctly", false, board.isHit(0,6));
		assertEquals("Hit was not determined correctly", false, board.isHit(6,6));
		assertEquals("Hit was not determined correctly", false, board.isHit(6,7));
		for (int y = 0; y < this.board.length(); y++) {
			for (int x = 0; x < this.board.length(); x++) {
				this.board.setHit(x, y);
				assertEquals("Hit was not determined correctly", true,
						this.board.isHit(x, y));
				this.board.setMiss(x, y);
				assertEquals("Miss was not determined correctly", false,
						this.board.isHit(x, y));
			}
		}
	}

	public void testHasLost() {
		Board tmpBoard = new Board();
		//Make 17 HIT moves
		for (int i=1; i<=17; i++) {
			Coordinates tmpCoords = makeMove(tmpBoard);
			try {
				tmpBoard.setHit(tmpCoords.getX(), tmpCoords.getY());
			} catch (BackendException e) {
				e.printStackTrace();
			}
		}
		assertEquals("Losing condition not determined correctly", true, tmpBoard.hasLost());
	}
	
	/**
	 * Contains the tests for setCoordinate as well as the tests for
	 * validateCoordinates, which is private and called by 
	 * setCoordinates.
	 */
	public void testSetCoordinate() {
		//Test border cases by starting @ -1 and finishing @ length
		//There should be 44 exceptions thrown
		int tNumExceptions = 0;
		for (int y = -1; y <= this.board.length(); y++) {
			for (int x = -1; x <= this.board.length(); x++) {
				try {
					this.board.setCoordinate('!', x, y);
					assertEquals('!', this.board.getCoordinate(x, y));
				} catch (BackendException e) {
					//e.printStackTrace();
					tNumExceptions++;
					assertEquals(e.getMessage(), 
							"The coordinates (" + x + ", " + y + ") is out of bounds.");
					assertTrue((x<0)||(y<0)||(x>=this.board.length())||(y>=this.board.length()));
				}			
			}
		}
		assertEquals(tNumExceptions, 44);
	}

	public void testGetCoordinate() {
		Board tmpBoard = new Board();
		for (int y = 0; y < tmpBoard.length(); y++) {
			for (int x = 0; x < tmpBoard.length(); x++) {
				try {
					assertEquals(Constants.BOARD_EMPTY, tmpBoard.getCoordinate(x, y));
				} catch (BackendException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void testGetShipCoordinates() {
		try {
			List<Coordinates> list = board.getShipCoordinates(Ships.getAircraftCarrier());
			for (int i=0; i<list.size(); i++) {
				Coordinates coordinates = list.get(i);
				int x = coordinates.getX();
				int y = coordinates.getY();
				assertEquals("Ship not at coordinates", Ships.getAircraftCarrier().getSymbol(), 
						this.board.getCoordinate(x, y));
			}			
		} catch (BackendException e) {
			e.printStackTrace();
		}
	}
	
	public void testNullCheck(){
		try {
			board.getShipCoordinates(null);
		} catch (BackendException e) {
			assertEquals(e.getMessage(),
					"Some object instance is null.");
		}
	}
	
	public void testValidate(){
		List<IShip> tShips = Ships.getAllShips();
		Iterator tShipIter = tShips.iterator();
		IShip tCurShip;
		
		//test boundaries
		while (tShipIter.hasNext()) {			
			tCurShip = (IShip)tShipIter.next();
			int tStartCell = (this.board.length()+1) - tCurShip.getSize();
			for(int i=tStartCell;i<this.board.length(); i++){
				for(int j = 0; j<this.board.length(); j++){
					try {
						board.add(tCurShip, i, j, Orientation.HORIZONTAL);
						fail("Exception not thrown, horizontal, "+ 
								tCurShip.getName()+", i: "+
								i + ", j: "+j);
					} catch (BackendException e) {
						assertEquals(e.getMessage(),
								"The placement of the " + tCurShip.getName()
								+ " is out of bounds.");
					}
					try {
						board.add(tCurShip, j, i, Orientation.VERTICAL);
						fail("Exception not thrown, vertical, "+ 
								tCurShip.getName()+", i: "+
								i + ", j: "+j);
					} catch (BackendException e) {
						assertEquals(e.getMessage(),
								"The placement of the " + tCurShip.getName()
								+ " is out of bounds.");
					}
				}//for j
			}// for i
		}// while
		
		//test overlap
		try {
			board.add(Ships.getAircraftCarrier(), 3, 4, Orientation.HORIZONTAL);
		} catch (BackendException e) {
			e.printStackTrace();
		}
		tShipIter = tShips.iterator();
		while(tShipIter.hasNext()){
			tCurShip = (IShip)tShipIter.next();
			
			//cross Vertically
			try {
				board.add(tCurShip, 4, 4, Orientation.VERTICAL);
				fail("Exception not thrown, vertical, "+ 
						tCurShip.getName());
			} catch (BackendException e) {
				assertEquals(e.getMessage(),
						"The coordinate (" + 4 + "," + 4
						+ ") is already occupied. Cannot place "
						+ tCurShip.getName() + ".");
			}
			
			//cross Horizontally
			try {
				board.add(tCurShip, 4, 4, Orientation.HORIZONTAL);
				fail("Exception not thrown, horizontal, "+ 
						tCurShip.getName());
			} catch (BackendException e) {
				assertEquals(e.getMessage(),
						"The coordinate (" + 4 + "," + 4
						+ ") is already occupied. Cannot place "
						+ tCurShip.getName() + ".");
			}
		}//while
	}

	public void testGetAllMissedCoordinates() {
		//Make 15 MISS moves
		Board tmpBoard = new Board();
		for (int i=1; i<=15; i++) {
			Coordinates tmpCoords = makeMove(tmpBoard);
			try {
				tmpBoard.setMiss(tmpCoords.getX(), tmpCoords.getY());
			} catch (BackendException e) {
				e.printStackTrace();
			}
		}
		List<Coordinates> list = tmpBoard.getAllMissedCoordinates();
		assertEquals(15, list.size());
		for (int index = 0; index < list.size(); index++) {
			Coordinates coordinates = list.get(index);
			int x = coordinates.getX();
			int y = coordinates.getY();
			try {
				assertEquals(Constants.BOARD_MISS, tmpBoard.getCoordinate(x, y));
			} catch (BackendException e) {
				e.printStackTrace();
			}
		}
	}

	public void testGetAllHitCoordinates() {
		//Make 15 HIT moves
		Board tmpBoard = new Board();
		for (int i=1; i<=15; i++) {
			Coordinates tmpCoords = makeMove(tmpBoard);
			try {
				tmpBoard.setHit(tmpCoords.getX(), tmpCoords.getY());
			} catch (BackendException e) {
				e.printStackTrace();
			}
		}
		List<Coordinates> list = tmpBoard.getAllHitCoordinates();
		assertEquals(15, list.size());
		for (int index = 0; index < list.size(); index++) {
			Coordinates coordinates = list.get(index);
			int x = coordinates.getX();
			int y = coordinates.getY();
			try {
				assertEquals(Constants.BOARD_HIT, tmpBoard.getCoordinate(x, y));
			} catch (BackendException e) {
				e.printStackTrace();
			}
		}
	}

	public void testGetAllEmptyCoordinates() {
		Board tmpBoard = new Board();
		List<Coordinates> list = tmpBoard.getAllEmptyCoordinates();
		assertEquals(100, list.size());
		for (int index = 0; index < list.size(); index++) {
			Coordinates coordinates = list.get(index);
			int x = coordinates.getX();
			int y = coordinates.getY();
			try {
				assertEquals(Constants.BOARD_EMPTY, tmpBoard.getCoordinate(x, y));
			} catch (BackendException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void testLength() {
		assertEquals("length() return incorrect", 10, board.length());
	}

	public void testAddIShipCoordinatesOrientation() throws BackendException {
		Board tmpBoard = new Board();
		Coordinates tmpCoordinates = new Coordinates(5,5);
		tmpBoard.add(Ships.getSubmarine(), tmpCoordinates, Orientation.HORIZONTAL);
		assertEquals("Incorrect ship placement", Ships.getSubmarine()
				.getSymbol(), tmpBoard.getCoordinate(5,5));
		assertEquals("Incorrect ship placement", Ships.getSubmarine()
				.getSymbol(), tmpBoard.getCoordinate(6,5));
		assertEquals("Incorrect ship placement", Ships.getSubmarine()
				.getSymbol(), tmpBoard.getCoordinate(7,5));
		assertEquals("Incorrect ship placement", Constants.BOARD_EMPTY,
				tmpBoard.getCoordinate(4, 5));
		assertEquals("Incorrect ship placement", Constants.BOARD_EMPTY, 
				tmpBoard.getCoordinate(8, 5));
		assertEquals("Incorrect ship placement", Constants.BOARD_EMPTY, 
				tmpBoard.getCoordinate(6, 4));
		assertEquals("Incorrect ship placement", Constants.BOARD_EMPTY, 
				tmpBoard.getCoordinate(6, 6));
		//TODO:  Exception testing
	}

	public void testAddIShipIntIntOrientation() throws BackendException {
		Board tmpBoard = new Board();
		tmpBoard.add(Ships.getSubmarine(), 5, 5, Orientation.HORIZONTAL);
		assertEquals("Incorrect ship placement", Ships.getSubmarine()
				.getSymbol(), tmpBoard.getCoordinate(5,5));
		assertEquals("Incorrect ship placement", Ships.getSubmarine()
				.getSymbol(), tmpBoard.getCoordinate(6,5));
		assertEquals("Incorrect ship placement", Ships.getSubmarine()
				.getSymbol(), tmpBoard.getCoordinate(7,5));
		assertEquals("Incorrect ship placement", Constants.BOARD_EMPTY,
				tmpBoard.getCoordinate(4, 5));
		assertEquals("Incorrect ship placement", Constants.BOARD_EMPTY, 
				tmpBoard.getCoordinate(8, 5));
		assertEquals("Incorrect ship placement", Constants.BOARD_EMPTY, 
				tmpBoard.getCoordinate(6, 4));
		assertEquals("Incorrect ship placement", Constants.BOARD_EMPTY, 
				tmpBoard.getCoordinate(6, 6));
	}

	public void testEquals() throws BackendException {
		Board testBoard = new Board();
		testBoard.add(Ships.getAircraftCarrier(), 0, 0, Orientation.VERTICAL);
		testBoard.add(Ships.getPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
		assertEquals("equals() implementation incorrect", testBoard, board);
		
		assertFalse(testBoard.equals(null));
		assertFalse(testBoard.equals(new Coordinates(1,0)));
		
		assertFalse(testBoard.equals(new Board()));		
	}
	
	public void testToString() throws BackendException {
		String expected = "a*********a*********a*********a*********a*******************************************************pp**";
		String actual = this.board.toString();
		assertEquals("Serialization failed", expected, actual);
	}

	public void testDeserialize(){
		String tmpString = "a*********a*********a*********a*********a*******************************************************pp**";
		Board tmpBoard;
		try {
			tmpBoard = Board.deserialize(tmpString);
			assertEquals("Deserialization failed", this.board, tmpBoard);			
		} catch (BackendException e1) {
			e1.printStackTrace();
		}

		//test null value
		try {
			Board.deserialize(null);
			fail("no exception thrown for null");
		} catch (BackendException e) {
			assertEquals(e.getMessage(),
				"The String paramater was null.");
		}
		
		//test invalid length string
		String tTestStr = "";
		for(int i=0; i<Math.pow(this.board.length(), 2)+1; i++){
			try {
				Board.deserialize(tTestStr);
				if(i!=100)
					fail("string of length " + i + "and no exception");
			} catch (BackendException e) {
				assertEquals(e.getMessage(),
						"The String paramater was the wrong length.");
			}
			
			tTestStr+="a";
		}
		
		//test a string with bad characters in it
		tmpString = tmpString.replace('a', 'F');
		try {
			Board.deserialize(tmpString);
			fail("Did not pickup on bad character.");
		} catch (BackendException e) {
			assertEquals(e.getMessage(),
					"Character F is an invalid character.");
		}
	}

	public void testReset() throws BackendException {
		board.reset();
		List<Coordinates> emptyCoords = board.getAllEmptyCoordinates();
		assertEquals("reset() did not clear the board", emptyCoords.size(), 100);
	}
	
	/**
	 * Returns a random move (x,y) from all the empty coordinates remaining on
	 * the game board.
	 * 
	 * @return
	 */
	private Coordinates makeMove(Board board) {
		List<Coordinates> list = board.getAllEmptyCoordinates();
		Random random = new Random();
		int index = random.nextInt(list.size()); // nextInt returns [0, size)
		return list.get(index);
	}
}
