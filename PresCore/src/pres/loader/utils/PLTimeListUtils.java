/*
 * PLTimeListUtils.java
 * Created on 2011/06/30 by macchan
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import clib.common.time.CTime;

/**
 * PLTimeListUtils
 */
public class PLTimeListUtils {

	public static List<CTime> marge(List<CTime> listA, List<CTime> listB) {
		List<CTime> newList = new ArrayList<CTime>();
		Iterator<CTime> iterA = listA.iterator();
		Iterator<CTime> iterB = listB.iterator();
		CTime a = next(iterA);
		CTime b = next(iterB);
		while (a != null || b != null) {
			if (a == null) {
				newList.add(b);
				b = next(iterB);
				continue;
			}
			if (b == null) {
				newList.add(a);
				a = next(iterA);
				continue;
			}
			if (a.before(b)) {
				newList.add(a);
				a = next(iterA);
			} else {
				newList.add(b);
				b = next(iterB);
			}

		}
		return newList;
	}

	private static CTime next(Iterator<CTime> iter) {
		if (iter.hasNext()) {
			return iter.next();
		}
		return null;
	}

	public static void main(String[] args) {
		List<CTime> listA = new ArrayList<CTime>();
		listA.add(new CTime(1000000));
		listA.add(new CTime(2000000));
		List<CTime> listB = new ArrayList<CTime>();
		listB.add(new CTime(500000));
		listB.add(new CTime(1500000));
		listB.add(new CTime(2500000));
		List<CTime> newList = marge(listA, listB);
		System.out.println(newList);
	}
}
