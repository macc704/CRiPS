/**
* プログラム名：
* 作成者： 
* 作成日： Wed Oct 03 21:54:44 JST 2012
*/
public class _04SoundTest extends Turtle {
	
	//起動処理
	public static void main(String[] args) {
		Turtle.startTurtle(new _04SoundTest());
	}
	
	
	//タートルを動かす処理
	public void start() {
		SoundTurtle st = new SoundTurtle("abc");
		st.play();
		st.loop();
		st.stop();
		boolean d = st.isPlaying();
		st.setVolume(90);
		st.loadOnMemory();
		int aa = st.getVolume();
		st.file("filename");
		SoundTurtle st2 = new SoundTurtle();
		st2 = st;
	}
}						