package frontend;

import java.util.*;

import frontend.state.ships.*;
import backend.client.*;
import backend.constants.GameResult;
import backend.state.*;

/**
 * Proper usage of this class requires interaction between the 
 * EsbFrontendController and an EsbBattleWindow object.  Therefore the 
 * setBattleWindow mutator must be passed an EsbBattleWindow object before
 * this class is used.
 * @author Frank A. Smith & Alvin Ting
 *
 */
public class EsbFrontendController implements Observer {

	private IClient mClient;
	private EsbBattleWindow mBattleWindow;
	private Player mPlayer;
	private List<CanDrawShip> mShipList;
	
	
	public EsbFrontendController(IClient aClient) {
		mClient = aClient;
		mPlayer = mClient.getPlayer();
		mPlayer.addObserver(this);
		mShipList = new ArrayList<CanDrawShip>();
	}

	/**
	 * This function must be called in order for EsbFrontEndController to
	 * interact with the window.
	 * @param aBattleWindow
	 */
	public void setBattleWindow(EsbBattleWindow aBattleWindow){
		mBattleWindow = aBattleWindow;
	}
	
	public List<Coordinates> getOpponentMisses() {
		return mPlayer.getOppBoard().getAllMissedCoordinates();
	}
	
	public List<Coordinates> getOpponentHits() {
		return mPlayer.getOppBoard().getAllHitCoordinates();
	}
	
	public List<Coordinates> getPlayerMisses() { 
		return mPlayer.getMyBoard().getAllMissedCoordinates();
	}
	
	public List<Coordinates> getPlayerHits() {
		return mPlayer.getMyBoard().getAllHitCoordinates();
	}
	
	public List<CanDrawShip> getShips() {
		return mShipList;
	}

	public void setShips(List<CanDrawShip> shipList) {
		mShipList = shipList;
	}
	
	public boolean makeMove(int x, int y) {
		return mClient.move(x, y);
	}
	
	/**
	 * Returns Vector of message strings to be displayed
	 * 
	 * @return Vector of Strings
	 */
	public Vector<String> getMessages() {
		return mPlayer.getMessages();
	}
	
	/**
	 * Empties the messages Vector
	 * 
	 */
	public void resetMessages() {
		mPlayer.resetMessages();
	}
	
	/**
	 * Get the current game result.
	 * 
	 * @return
	 */
	public GameResult getGameResult() {
		return mPlayer.getGameResult();
	}
	
	public void disconnect() throws Exception{
		mClient.disconnect();
	}
	
	public void update(Observable o, Object obj) {
		mBattleWindow.setTurn(mPlayer.isMyTurn());
		mBattleWindow.notifyComponents();
		mBattleWindow.repaint();

		
		//TEST CODE
//		System.out.println("Move was made");
	}
}
