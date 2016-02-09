package uk.co.adeveloperabroad.android;

import android.os.Bundle;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import uk.co.adeveloperabroad.SoundsOfEarth;
import uk.co.adeveloperabroad.adMob.AdvertDisplay;

public class AndroidLauncher extends AndroidApplication implements AdvertDisplay{


	private static final String ADVERT_ID = "ca-app-pub-6709972200159500/6012503873";
	private static final String Test_AD_ID = AdRequest.DEVICE_ID_EMULATOR;
	private InterstitialAd interstitialAd;

	public Boolean isAdvertLoaded = false;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new SoundsOfEarth(this), config);


		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(ADVERT_ID);
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				isAdvertLoaded = true;
//				Toast.makeText(getApplicationContext(), "Finished Loading Interstitial", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAdClosed() {
//				Toast.makeText(getApplicationContext(), "Closed Interstitial", Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public void showAdvert() {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (interstitialAd.isLoaded()) {
						interstitialAd.show();
						Toast.makeText(getApplicationContext(), "Showing Interstitial", Toast.LENGTH_SHORT).show();
					} else {
						loadAdvert();
					}
				}
			});
		} catch (Exception e) {

		}
	}
	@Override
	public void loadAdvert() {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!interstitialAd.isLoaded()) {
						//	AdRequest.Builder builder = new AdRequest.Builder().addTestDevice(Test_AD_ID);
//						AdRequest interstitialRequest = builder.build();
						AdRequest interstitialRequest = new AdRequest.Builder().build();
						interstitialAd.loadAd(interstitialRequest);
						Toast.makeText(getApplicationContext(), "Loading Interstitial", Toast.LENGTH_SHORT).show();

					}
				}
			});
		} catch (Exception e) {

		}
	}


	@Override
	public void hideAdvert() {

	}
}
