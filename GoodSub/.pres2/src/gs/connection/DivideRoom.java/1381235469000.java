package gs.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DivideRoom {
	public static void main(String[] args) {

	}

	private List<Integer> rooms = new ArrayList<Integer>();

	public int selectRoomNum() throws IOException {
		int roomNum;

		System.out.print("•”‰®”Ô† : ");
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		String str = input.readLine();
		roomNum = Integer.parseInt(str);
		if (checkRoomNum(roomNum)) {
			rooms.add(roomNum);
		}
		return roomNum;
	}

	public boolean checkRoomNum(int roomNum) {
		for (int aRoomNum : rooms) {
			if (aRoomNum == roomNum) {
				return true;
			}
		}
		return false;
	}
}
