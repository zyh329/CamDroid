package app.camdroid.streaming;

import java.io.IOException;
import java.net.InetAddress;

import android.content.Context;
import android.hardware.Camera.CameraInfo;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import app.camdroid.streaming.audio.AACStream;
import app.camdroid.streaming.audio.AudioQuality;
import app.camdroid.streaming.audio.AudioStream;
import app.camdroid.streaming.video.VideoQuality;
import app.camdroid.streaming.video.VideoStream;

/**
 * Call {@link #getInstance()} to get access to the SessionBuilder.
 */
public class SessionBuilder {

	public final static String TAG = "SessionBuilder";

	/** Can be used with {@link #setVideoEncoder}. */
	public final static int VIDEO_NONE = 0;

	/** Can be used with {@link #setVideoEncoder}. */
	public final static int VIDEO_H264 = 1;

	/** Can be used with {@link #setAudioEncoder}. */
	public final static int AUDIO_NONE = 0;

	/** Can be used with {@link #setAudioEncoder}. */
	public final static int AUDIO_AMRNB = 3;

	/** Can be used with {@link #setAudioEncoder}. */
	public final static int AUDIO_AAC = 5;

	// Default configuration
	private VideoQuality mVideoQuality = new VideoQuality();
	private AudioQuality mAudioQuality = new AudioQuality();
	private Context mContext;
	private int mVideoEncoder = VIDEO_H264; 
	private int mAudioEncoder = AUDIO_AAC;
	private int mCamera = CameraInfo.CAMERA_FACING_BACK;
	private SurfaceHolder mSurfaceHolder = null;
	private InetAddress mOrigin = null;
	private InetAddress mDestination = null;

	// Removes the default public constructor
	private SessionBuilder() {}

	// The SessionManager implements the singleton pattern
	private static volatile SessionBuilder sInstance = null; 

	/**
	 * Returns a reference to the {@link SessionBuilder}.
	 * @return The reference to the {@link SessionBuilder}
	 */
	public final static SessionBuilder getInstance() {
		if (sInstance == null) {
			synchronized (SessionBuilder.class) {
				if (sInstance == null) {
					SessionBuilder.sInstance = new SessionBuilder();
				}
			}
		}
		return sInstance;
	}	

	/**
	 * Creates a new {@link Session}.
	 * @return The new Session
	 * @throws IOException 
	 */
	public Session build() throws IOException {
		Session session;

		session = new Session();
		session.setOrigin(mOrigin);
		session.setDestination(mDestination);

		switch (mAudioEncoder) {
		case AUDIO_AAC:
			AACStream stream = new AACStream();
			session.addAudioTrack(stream);
			if (mContext!=null) 
				stream.setPreferences(PreferenceManager.getDefaultSharedPreferences(mContext));
			break;
		}

		switch (mVideoEncoder) {
		case VIDEO_H264:
			VideoStream stream = new VideoStream(mCamera);
			if (mContext!=null) 
				stream.setPreferences(PreferenceManager.getDefaultSharedPreferences(mContext));
			session.addVideoTrack(stream);
			break;
		}

		if (session.getVideoTrack()!=null) {
			VideoStream video = session.getVideoTrack();
			video.setVideoQuality(VideoQuality.merge(mVideoQuality,video.getVideoQuality()));
			video.setPreviewDisplay(mSurfaceHolder);
			video.setDestinationPorts(5006);
		}

		if (session.getAudioTrack()!=null) {
			AudioStream audio = session.getAudioTrack();
			audio.setAudioQuality(AudioQuality.merge(mAudioQuality,audio.getAudioQuality()));
			audio.setDestinationPorts(5004);
		}

		return session;

	}

	/** 
	 * Access to the context is needed for the H264Stream class to store some stuff in the SharedPreferences.
	 * Note that you should pass the Application context, not the context of an Activity.
	 **/
	public SessionBuilder setContext(Context context) {
		mContext = context;
		return this;
	}

	/** Sets the destination of the session. */
	public SessionBuilder setDestination(InetAddress destination) {
		mDestination = destination;
		return this; 
	}

	/** Sets the origin of the session. It appears in the SDP of the session. */
	public SessionBuilder setOrigin(InetAddress origin) {
		mOrigin = origin;
		return this;
	}

	/** Sets the video stream quality. */
	public SessionBuilder setVideoQuality(VideoQuality quality) {
		mVideoQuality = VideoQuality.merge(quality, mVideoQuality);
		return this;
	}

	/** Sets the audio encoder. */
	public SessionBuilder setAudioEncoder(int encoder) {
		mAudioEncoder = encoder;
		return this;
	}
	
	/** Sets the audio quality. */
	public SessionBuilder setAudioQuality(AudioQuality quality) {
		mAudioQuality = AudioQuality.merge(quality, mAudioQuality);
		return this;
	}

	/** Sets the default video encoder. */
	public SessionBuilder setVideoEncoder(int encoder) {
		mVideoEncoder = encoder;
		return this;
	}

	public SessionBuilder setCamera(int camera) {
		mCamera = camera;
		return this;
	}

	/** 
	 * Sets the Surface required by MediaRecorder to record video. 
	 * @param surfaceHolder A SurfaceHolder wrapping a valid surface
	 **/
	public SessionBuilder setSurfaceHolder(SurfaceHolder surfaceHolder) {
		mSurfaceHolder = surfaceHolder;
		return this;
	}

	/** Returns the context set with {@link #setContext(Context)}*/
	public Context getContext() {
		return mContext;	
	}

	/** Returns the destination ip address set with {@link #setDestination(InetAddress)}. */
	public InetAddress getDestination() {
		return mDestination;
	}

	/** Returns the origin ip address set with {@link #setOrigin(InetAddress)}. */
	public InetAddress getOrigin() {
		return mOrigin;
	}

	/** Returns the audio encoder set with {@link #setAudioEncoder(int)}. */
	public int getAudioEncoder() {
		return mAudioEncoder;
	}

	/** Returns the id of the {@link android.hardware.Camera} set with {@link #setCamera(int)}. */
	public int getCamera() {
		return mCamera;
	}

	/** Returns the video encoder set with {@link #setVideoEncoder(int)}. */
	public int getVideoEncoder() {
		return mVideoEncoder;
	}

	/** Returns the VideoQuality set with {@link #setVideoQuality(VideoQuality)}. */
	public VideoQuality getVideoQuality() {
		return mVideoQuality;
	}
	
	/** Returns the AudioQuality set with {@link #setAudioQuality(AudioQuality)}. */
	public AudioQuality getAudioQuality() {
		return mAudioQuality;
	}

	/** Returns the SurfaceHolder set with {@link #setSurfaceHolder(SurfaceHolder)}. */
	public SurfaceHolder getSurfaceHolder() {
		return mSurfaceHolder;
	}

	/** Returns a new  with the same configuration. */
	public SessionBuilder clone() {
		return new SessionBuilder()
		.setDestination(mDestination)
		.setOrigin(mOrigin)
		.setSurfaceHolder(mSurfaceHolder)
		.setVideoQuality(mVideoQuality)
		.setVideoEncoder(mVideoEncoder)
		.setCamera(mCamera)
		.setAudioEncoder(mAudioEncoder)
		.setAudioQuality(mAudioQuality)
		.setContext(mContext);
	}

}