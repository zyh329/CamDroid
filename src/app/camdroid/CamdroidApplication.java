package app.camdroid;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import app.camdroid.streaming.SessionBuilder;
import app.camdroid.streaming.video.VideoQuality;

public class CamdroidApplication extends android.app.Application {

	public final static String TAG = "CamdroidApplication";
	
	/** We will be using this as the default quality */
	public VideoQuality videoQuality = new VideoQuality(640,480,15,500000);

	/** We will be using AAC */
	public int audioEncoder = SessionBuilder.AUDIO_AAC;

	/** We will be using Video_H264 */
	public int videoEncoder = SessionBuilder.VIDEO_H264;

	/** The HttpServer will use those variables to send reports about the state of the app to the web interface. */
	public boolean applicationForeground = true;
	public Exception lastCaughtException = null;

	
	private static CamdroidApplication sApplication;

	@Override
	public void onCreate() {

		sApplication = this;

		super.onCreate();
		
		SessionBuilder.getInstance() 
		.setContext(getApplicationContext())
		.setAudioEncoder(audioEncoder)
		.setVideoEncoder(videoEncoder)
		.setVideoQuality(videoQuality);

		
	}

	public static CamdroidApplication getInstance() {
		return sApplication;
	}


}
