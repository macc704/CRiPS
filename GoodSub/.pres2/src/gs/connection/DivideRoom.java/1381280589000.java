package gs.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DivideRoom {
	public static void main(String[] args) {

	}

	public int selectRoomNum() throws IOException {
		System.out.print("•”‰®”Ô† : ");
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		String str = input.readLine();
		int roomNum = Integer.parseInt(str);
		return roomNum;
	}

	public static boolean checkRoomNum() {
		return false;
	}

}
