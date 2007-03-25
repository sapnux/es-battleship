package backend.test.state;

import java.util.List;
import java.util.Random;

import junit.framework.TestCase;
import backend.constants.Constants;
import backend.constants.Orientation;
import backend.state.Board;
import backend.state.Coordinates;
import backend.state.ships.Ships;
import backend.util.BackendException;

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
				this.board.setHit(x, y);
				assertEquals("Hit did not register", Constants.BOARD_HIT,
						this.board.getCoordinate(x, y));
			}
		}
	}

	public void testSetMiss() {
		for (int y = 0; y < this.board.length(); y++) {
			for (int x = 0; x < this.board.length(); x++) {
				this.board.setMiss(x, y);
				assertEquals("Miss did not register", Constants.BOARD_MISS,
						this.board.getCoordinate(x, y));
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
			tmpBoard.setHit(tmpCoords.getX(), tmpCoords.getY());
		}
		assertEquals("Losing condition not determined correctly", true, tmpBoard.hasLost());
	}

	public void testSetCoordinate() {
		for (int y = 0; y < this.board.length(); y++) {
			for (int x = 0; x < this.board.length(); x++) {
				this.board.setCoordinate('!', x, y);
				assertEquals('!', this.board.getCoordinate(x, y));
			}
		}
	}

	public void testGetCoordinate() {
		Board tmpBoard = new Board();
		for (int y = 0; y < tmpBoard.length(); y++) {
			for (int x = 0; x < tmpBoard.length(); x++) {
				assertEquals(Constants.BOARD_EMPTY, tmpBoard.getCoordinate(x, y));
			}
		}
	}

	public void testGetShipCoordinates() {
		List<Coordinates> list = board.getShipCoordinates(Ships.getAircraftCarrier());
		for (int i=0; i<list.size(); i++) {
			Coordinates coordinates = list.get(i);
			int x = coordinates.getX();
			int y = coordinates.getY();
			assertEquals("Ship not at coordinates", Ships.getAircraftCarrier().getSymbol(), 
					this.board.getCoordinate(x, y));
		}
	}

	public void testGetAllMissedCoordinates() {
		//Make 15 MISS moves
		Board tmpBoard = new Board();
		for (int i=1; i<=15; i++) {
			Coordinates tmpCoords = makeMove(tmpBoard);
			tmpBoard.setMiss(tmpCoords.getX(), tmpCoords.getY());
		}
		List<Coordinates> list = tmpBoard.getAllMissedCoordinates();
		assertEquals(15, list.size());
		for (int index = 0; index < list.size(); index++) {
			Coordinates coordinates = list.get(index);
			int x = coordinates.getX();
			int y = coordinates.getY();
			assertEquals(Constants.BOARD_MISS, tmpBoard.getCoordinate(x, y));
		}
	}

	public void testGetAllHitCoordinates() {
		//Make 15 HIT moves
		Board tmpBoard = new Board();
		for (int i=1; i<=15; i++) {
			Coordinates tmpCoords = makeMove(tmpBoard);
			tmpBoard.setHit(tmpCoords.getX(), tmpCoords.getY());
		}
		List<Coordinates> list = tmpBoard.getAllHitCoordinates();
		assertEquals(15, list.size());
		for (int index = 0; index < list.size(); index++) {
			Coordinates coordinates = list.get(index);
			int x = coordinates.getX();
			int y = coordinates.getY();
			assertEquals(Constants.BOARD_HIT, tmpBoard.getCoordinate(x, y));
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
			assertEquals(Constants.BOARD_EMPTY, tmpBoard.getCoordinate(x, y));
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
	}
	
	public void testToString() throws BackendException {
		String expected = "a*********a*********a*********a*********a*******************************************************pp**";
		String actual = this.board.toString();
		assertEquals("Serialization failed", expected, actual);
	}

	public void testDeserialize() throws BackendException {
		String tmpString = "a*********a*********a*********a*********a*******************************************************pp**";
		Board tmpBoard = Board.deserialize(tmpString);
		assertEquals("Deserialization failed", this.board, tmpBoard);
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
