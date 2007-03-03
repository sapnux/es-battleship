package frontend.test;

import java.util.Random;

import backend.state.*;

public class TestOpponent implements Runnable {

	private Player mPlayer = null;	
	
	public TestOpponent(Player aPlayer)
	{
		mPlayer = aPlayer;
	}
	public void run() {
		Random tRandGen= new Random(System.nanoTime());
		boolean rValue = false, tValidMove = false;
		int tRandXCoord = 0, tRandYCoord = 0;
		Board tPlayerBoard = mPlayer.getMyBoard();
		char tCellValue = Constants.BOARD_EMPTY;

		//while it's opponent's turn
		while(!rValue){

			try {
				Thread.sleep(500);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}

			//while we are looking for a legal move
			tValidMove = false;
			while (!tValidMove) {	
				tRandXCoord = tRandGen.nextInt(10);
				tRandYCoord = tRandGen.nextInt(10);

				tCellValue = tPlayerBoard.getCoordinate(tRandXCoord, tRandYCoord);
				if ((Constants.BOARD_HIT  == tCellValue) ||
					(Constants.BOARD_MISS == tCellValue)	) {
					tValidMove = false;
				} else {
					tValidMove = true;
				}
			}

			if (Constants.BOARD_EMPTY == tCellValue) {	
				tPlayerBoard.setMiss(tRandXCoord, tRandYCoord);
				rValue = true;
			} else {
				tPlayerBoard.setHit(tRandXCoord, tRandYCoord);
				rValue = false;			
			}
			
			System.out.println("Opponent's move was: " 
					+ tRandXCoord + " " + tRandYCoord);
			mPlayer.setMyTurn(rValue);	
		}
	}

}
