package backend.util;

import java.io.PrintWriter;

import backend.constants.MoveResult;
import backend.state.Board;

public class MsgUtils {
	
	//Messages from Clients to Server
	public static void sendTestMessage (PrintWriter out) {
		String pack = "0|test";
		out.println(pack);
	}
	
	public static void sendReadyMessage(PrintWriter out, String pId, Board myBoard) {
		String pack = "0|"+pId+"|"+myBoard.toString();
		out.println(pack);
	}

	public static void sendMoveMessage (PrintWriter out, String pId, int x, int y) {
		String pack = "1|"+pId+"|"+Integer.toString(x)+"|"+Integer.toString(y);
		out.println(pack);
	}
	
	//Messages from Server to Clients
	public static void sendIsHitMessage (PrintWriter out, MoveResult moveResult) {
		String isHit = "";
		if (moveResult == MoveResult.HIT) {
			isHit = "true";
		} else {
			isHit = "false";
		}
		String pack = "2|"+isHit;
		out.println(pack);
	}
	
	public static void sendTurnMessage (PrintWriter out, boolean turn) {
		String pack = "3|"+turn;
		out.println(pack);
	}
	
	public static void sendMoveNotifyMessage (PrintWriter out, String x, String y) {
		String pack = "4|"+x+"|"+y;
		out.println(pack);
	}

	public static void sendGameOverMessage(PrintWriter out, String x, String y, String result) {
		String pack = "5|"+x+"|"+y+"|"+result;
		out.println(pack);
	}


}
