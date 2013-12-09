/*
 * Copyright (C) 2011-2013 GUIGUI Simon, fyhertz@gmail.com
 * 
 * This file is part of Spydroid (http://code.google.com/p/spydroid-ipcamera/)
 * 
 * Spydroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package app.camdroid.ui;

import app.camdroid.R;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import app.camdroid.api.CustomHttpServer;
import app.camdroid.server.TinyHttpServer;
import app.camdroid.streaming.SessionBuilder;
import app.camdroid.streaming.Stream;
import app.camdroid.streaming.video.*;

public class PreviewFragment extends Fragment {

	public final static String TAG = "PreviewFragment";

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private TextView mTextView;
    private CustomHttpServer mHttpServer;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onPause() {
		super.onPause();
    	getActivity().unbindService(mHttpServiceConnection);
	}
	
	@Override
    public void onResume() {
    	super.onResume();
		getActivity().bindService(new Intent(getActivity(),CustomHttpServer.class), mHttpServiceConnection, Context.BIND_AUTO_CREATE);
		}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.preview,container,false);

		mTextView = (TextView)rootView.findViewById(R.id.tooltip);
			mSurfaceView = (SurfaceView)rootView.findViewById(R.id.tablet_camera_view);
			mSurfaceHolder = mSurfaceView.getHolder();
			SessionBuilder.getInstance().setSurfaceHolder(mSurfaceHolder);
			
		
		return rootView;
	}
	
	public void update() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mTextView != null) {
					if ((mHttpServer != null && mHttpServer.isStreaming()))
						mTextView.setVisibility(View.INVISIBLE);
					else 
						mTextView.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	

    
    private final ServiceConnection mHttpServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mHttpServer = (CustomHttpServer) ((TinyHttpServer.LocalBinder)service).getService();
			update();
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {}
	};
	
}
