/**
 * VolumeControler.java
 * Created on 2006/05/27
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package blib.bsound.components;

import javax.sound.sampled.FloatControl;

import blib.bsound.framework.BVolumeController;



/**
 * Class VolumeControler.
 * 
 * @author macchan
 */
public class BFloatVolumeController extends BVolumeController {

	private FloatControl volumeControl = null;

	public BFloatVolumeController(FloatControl volumeControl) {
		this.volumeControl = volumeControl;
	}

	public int getVolume() {
		// エラー処理
		if (volumeControl == null) {
			return 0;
		}

		// ボリュームを取得する
		return getIntVolume(volumeControl.getValue());
	}

	public void setVolume(int volume) {
		// エラー処理
		if (volumeControl == null) {
			return;
		}

		// ボリュームを設定する
		if (volume > 100) {
			volume = 100;
		} else if (volume < 0) {
			volume = 0;
		}
		volumeControl.setValue(getFloatVolume(volume));
	}
	
	public int getDefaultVolume() {
		// エラー処理
		if (volumeControl == null) {
			return 0;
		}

		// ボリュームを取得する
		return getIntVolume(0f);
	}

	private int getIntVolume(float floatVolume) {
		float range = volumeControl.getMaximum() - volumeControl.getMinimum();
		float absoluteFloatVolume = floatVolume - volumeControl.getMinimum();

		float intVolume = (absoluteFloatVolume / range) * 100f;
		return (int) (intVolume + 0.5);// 四捨五入
	}

	private float getFloatVolume(int intVolume) {
		float range = volumeControl.getMaximum() - volumeControl.getMinimum();
		float floatVolume = volumeControl.getMinimum() + range
				* (float) intVolume / 100f;
		return floatVolume;
	}
	
}
