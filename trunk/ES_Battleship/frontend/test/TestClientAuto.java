package frontend.test;

import backend.client.*;
import backend.state.Board;
import backend.state.Coordinates;
import backend.state.Player;

public class TestClientAuto implements IClient, IEJBClient {
	private Board mBoard   = null;
	private Player mPlayer = null;
	
	protected Coordinates mLastClick = null;

	public TestClientAuto(String aid, Board aBoard){
		mBoard = aBoard;
		mPlayer = new Player(aid, mBoard);
		mLastClick = new Coordinates(-1, -1);
	}
	
	public void connect(String server, String port) {
		// TODO Auto-generated method stub

	}

	public void disconnect() {
		// TODO Auto-generated method stub

	}

	public Player getPlayer() {
		return mPlayer;
	}
	
	public void setMyTurn(boolean aMyTurn){
		mPlayer.setMyTurn(aMyTurn);
	}
	
	public Coordinates getLastClick(){
		return mLastClick;
	}
	
	public void clearLastClick(){
		mLastClick.setX(-1);
		mLastClick.setY(-1);
	}
	
	public void setWinLose(backend.constants.GameResult aResult){
		mPlayer.setGameResult(aResult);		
	}
	
	public void sendMessage(String aMessage){
		mPlayer.addMessage(aMessage);
	}
	
	public void move(int x, int y) {
		mLastClick.setX(x);
		mLastClick.setY(y);
	}

	public void signalReadiness() {
		// TODO Auto-generated method stub

	}

	public void waitForTurn() {
		// TODO Auto-generated method stub

	}

}
