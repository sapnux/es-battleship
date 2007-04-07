package backend.test.state;

import java.util.Vector;

import junit.framework.TestCase;
import backend.constants.GameResult;
import backend.constants.Orientation;
import backend.state.Board;
import backend.state.Player;
import backend.state.ships.Ships;
import backend.util.BackendException;

public class PlayerTest extends TestCase {
	
	private String playerId;
	private Board board;
	private Player player;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		this.board = new Board();
		this.board.add(Ships.getAircraftCarrier(), 0, 0, Orientation.VERTICAL);
		this.board.add(Ships.getPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
		this.playerId = "testClient1";
		this.player = new Player(playerId, this.board);
	}
	
	protected void tearDown() throws BackendException {
		this.board.reset();
	}

	public void testPlayer() {
		// constructor takes in playerId and board. check those.
		testGetId();
		testGetMyBoard();
	}

	public void testGetMyBoard() {
		if (!this.board.toString().equals(this.player.getMyBoard().toString())) {
			fail("Board mismatch!");
		}
	}

	public void testGetId() {
		if (!this.playerId.equals(this.player.getId())) {
			fail("PlayerID mismatch!");
		}
	}

	public void testGetOppBoard() {
		Board tempBoard = new Board();
		if (!this.player.getOppBoard().toString().equals(tempBoard.toString())) {
			fail("getOppBoard was not empty.");
		}
	}

	public void testGetGameResult() {
		GameResult gameResult = this.player.getGameResult();
		if (gameResult != GameResult.UNKNOWN) {
			fail("Game Result was not unknown at the start of the match.");
		}
	}

	public void testSetGameResult() {
		this.player.setGameResult(GameResult.WIN);
		GameResult gameResult = this.player.getGameResult();
		if (gameResult != GameResult.WIN) {
			fail("Game Result was not set correctly.");
		}
	}

	public void testIsMyTurn() {
		this.player.setMyTurn(false);
		if (this.player.isMyTurn()) {
			fail("IsMyTurn was not set correctly.");
		}
	}

	public void testSetMyTurn() {
		this.player.setMyTurn(true);
		if (!this.player.isMyTurn()) {
			fail("SetMyTurn was not set correctly.");
		}
	}

	public void testGetMessages() {
		String firstMessage = "This is a test message";
		String secondMessage = "This is another test message";
		this.player.addMessage(firstMessage);
		this.player.addMessage(secondMessage);
		Vector<String> messages = this.player.getMessages();
		messages.get(0).equals(firstMessage);
		messages.get(1).equals(secondMessage);
	}

	public void testAddMessage() {
		this.player.addMessage("This is a test message");
		this.player.addMessage("This is another test message");
		if (this.player.getMessages().size() != 2) {
			fail("addMessage() did not work.");
		}
	}

	public void testResetMessages() {
		this.player.addMessage("This is a test message");
		this.player.resetMessages();
		if (this.player.getMessages().size() != 0) {
			fail("There were messages even though we cleared!");
		}
	}

}
