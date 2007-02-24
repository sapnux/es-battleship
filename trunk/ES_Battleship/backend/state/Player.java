package backend.state;

public class Player {
	
	private String id;
	private Board myBoard;
	private Board oppBoard;
	
	public Player(String id, Board board) {
		this.id = id;
		this.myBoard = board;
		this.oppBoard = new Board();
	}

	public Board getMyBoard() {
		return myBoard;
	}

	public String getId() {
		return id;
	}

	public Board getOppBoard() {
		return oppBoard;
	}
}
