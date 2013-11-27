package gs.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DivideRoom {
	public static void main(String[] args) {

	}

	List<Integer> rooms = new ArrayList<Integer>();

	public int selectRoomNum() throws IOException {

		System.out.print("•”‰®”Ô† : ");
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		String str = input.readLine();
		return Integer.parseInt(str);
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
