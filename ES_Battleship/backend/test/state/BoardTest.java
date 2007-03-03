package backend.test.state;

import java.util.List;

import junit.framework.TestCase;
import backend.state.Board;
import backend.state.Constants;
import backend.state.Coordinates;
import backend.state.Orientation;
import backend.state.ships.Ships;
import backend.util.BackendException;

public class BoardTest extends TestCase {

	private Board board;

	protected void setUp() throws BackendException {
		board = new Board();
		board.add(Ships.getAircraftCarrier(), 0, 0, Orientation.VERTICAL);
		board.add(Ships.getBattleship(), 1, 1, Orientation.HORIZONTAL);
		board.add(Ships.getCruiser(), 6, 4, Orientation.VERTICAL);
		board.add(Ships.getPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
		board.add(Ships.getSubmarine(), 5, 5, Orientation.VERTICAL);
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

	public void testIsHit() {
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
		fail("Not yet implemented");
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
		fail("Not yet implemented");
	}

	public void testGetAllMissedCoordinates() {
		List<Coordinates> list = this.board.getAllMissedCoordinates();
		assertEquals(100, list.size()); //TODO: fix this unit test, currently broken
		for (int index = 0; index < list.size(); index++) {
			Coordinates coordinates = list.get(index);
			int x = coordinates.getX();
			int y = coordinates.getY();
			assertEquals(Constants.BOARD_MISS, this.board.getCoordinate(x, y));
		}
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

	public void testAddIShipIntIntOrientation() throws BackendException {
		Board tmpBoard = new Board();
		tmpBoard.add(Ships.getPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
		assertEquals("Incorrect ship placement", Ships.getPatrolBoat()
				.getSymbol(), tmpBoard.getCoordinate(6, 9));
		assertEquals("Incorrect ship placement", Constants.BOARD_EMPTY, tmpBoard
				.getCoordinate(6, 6));
		assertEquals("Incorrect ship placement", Constants.BOARD_EMPTY, tmpBoard
				.getCoordinate(6, 7));
	}

	public void testToString() {
		String expected = "a*********abbbb*****a*********a*********a*****c********sc********sc********s********************pp**";
		String actual = this.board.toString();
		assertEquals("Serialization failed", expected, actual);
	}

	public void testDeserialize() {
		String tmpString = "a*********abbbb*****a*********a*********a*****c********sc********sc********s********************pp**";
		Board tmpBoard = Board.deserialize(tmpString);
		assertEquals("Deserialization failed", this.board, tmpBoard);
	}

}
