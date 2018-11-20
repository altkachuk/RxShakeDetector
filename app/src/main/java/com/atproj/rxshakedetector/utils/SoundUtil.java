package com.atproj.rxshakedetector.utils;

import android.media.AudioManager;
import android.media.ToneGenerator;

/**
 * Created by andre on 15-Nov-18.
 */

public class SoundUtil {

    public static void beep() {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, 100);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
    }
}
