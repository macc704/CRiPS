package coco.view;

import java.awt.Color;

public class CCGraphBackgroundColor {

	private Color color = Color.black;

	public CCGraphBackgroundColor() {

	}

	public Color graphColor(int rare) {
		switch (rare) {
		case 1:
			color = white();
			break;
		case 2:
			color = green();
			break;
		case 3:
			color = pink();
			break;
		case 4:
			color = blue();
			break;
		case 5:
			color = perple();
			break;
		case 6:
			color = red();
			break;
		default:
			color = Color.black;
		}

		return color;
	}

	// color name : white
	private Color white() {
		return new Color(255, 255, 255);
	}

	// color name : palegreen
	private Color green() {
		return new Color(152, 251, 152);
	}

	// color name : lightpink
	private Color pink() {
		return new Color(255, 182, 193);
	}

	// color name : paleturquoise
	private Color blue() {
		return new Color(175, 238, 238);
	}

	// color name : plum
	private Color perple() {
		return new Color(221, 160, 221);
	}

	// color name : salmon
	private Color red() {
		return new Color(250, 128, 114);
	}
}
