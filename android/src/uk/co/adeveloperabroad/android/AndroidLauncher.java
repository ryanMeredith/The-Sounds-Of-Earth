package uk.co.adeveloperabroad.android;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import uk.co.adeveloperabroad.SoundsOfEarth;
import uk.co.adeveloperabroad.adMob.AdvertDisplay;

public class AndroidLauncher extends AndroidApplication implements AdvertDisplay{

	AdView adView;

	private static final String BANNER_AD_UNIT_ID = "ca-app-pub-6709972200159500/6012503873";

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		View gameView = initializeForView(new SoundsOfEarth(this), config);

		setupAds();
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		layout.addView(adView, params);

		setContentView(layout);

	}

	public void setupAds() {
		adView = new AdView(this);
		adView.setVisibility(View.INVISIBLE);
		adView.setBackgroundColor(0xff000000); // black
		adView.setAdUnitId(BANNER_AD_UNIT_ID);
		adView.setAdSize(AdSize.SMART_BANNER);
	}

	@Override
	public void showAdvert() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adView.setVisibility(View.VISIBLE);
				AdRequest.Builder builder = new AdRequest.Builder();
				AdRequest ad = builder.build();
				adView.loadAd(ad);
			}
		});
	}

	@Override
	public void hideAdvert() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adView.setVisibility(View.INVISIBLE);
			}
		});
	}
}
