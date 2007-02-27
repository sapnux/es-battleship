package frontend.test;

import java.util.Random;

import backend.state.Player;

public class TestOpponent implements Runnable {

	private Player mPlayer = null;	
	
	public TestOpponent(Player aPlayer)
	{
		mPlayer = aPlayer;
	}
	public void run() {
		Random tRandGen= new Random(System.nanoTime());
		boolean rValue = false;
		while(!rValue){
			// Simulate opponent's turn
			int tRandNum = tRandGen.nextInt(2);

			try {
				Thread.sleep(3000);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}

			System.out.println("Opponent's move was a hit: "+(tRandNum == 1));

			//if opponent misses
			if(tRandNum == 0){
				rValue = true;
			}		
			mPlayer.setMyTurn(rValue);	
		}
	}

}
