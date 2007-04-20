package backend.constants;

//TODO: should be an enum
public abstract class MsgHeader {
	public final static int READY = 0;
	public final static int MOVE_INFO= 1;
	public final static int MOVE_RESULT = 2;
	public final static int TURN_INFO = 3;
	public final static int MOVE_NOTICE = 4;
	public final static int GAME_OVER = 5;
	public final static int ERROR = 6;
}
