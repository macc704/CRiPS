package postprocessor;

import postprocessor.strategy.PostProcessorStrategy;
import clib.view.framework.DnDFramework;

public class PostProcessorMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DnDFramework.open("PostProcessor", "データのあるフォルダをドロップしてください",
				new PostProcessorStrategy());
	}

}
