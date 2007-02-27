package frontend;

import java.util.*;

import backend.client.*;
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
	
	public EsbFrontendController(IClient aClient) {
		mClient = aClient;
		mPlayer = mClient.getPlayer();
		mPlayer.addObserver(this);
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
	
	public boolean makeMove(int x, int y) {
		return mClient.move(x, y);
	}
	
	public void update(Observable o, Object obj) {
		mBattleWindow.notifyComponents();
		mBattleWindow.repaint();
		//TEST CODE
		System.out.println("Move was made");
	}
}
