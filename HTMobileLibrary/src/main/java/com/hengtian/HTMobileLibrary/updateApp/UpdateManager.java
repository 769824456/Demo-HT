package com.hengtian.HTMobileLibrary.updateApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.hengtian.HTMobileLibrary.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * update App
 * @author haixiali
 *
 */
public class UpdateManager {
	private Context mContext;
	private static final int DOWNLOAD_ING = 1;
	private static final int DOWNLOAD_FINISH = 2;
	private int progress;
	private boolean cancelUpdate = false;
	private ProgressBar mProgress;
	private TextView mTextViewProgress;
	private Dialog mUpdateDialog;
	
	private String mUpdateUrl;
	private String mSaveName;
	private String mSavePath;

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_ING:
				mProgress.setProgress(progress);
				mTextViewProgress.setText(progress + "%");
				break;
			case DOWNLOAD_FINISH:
				installApk();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * get the app's version code
	 * @param mContext
	 * @return
	 */
	public int getVersionCode(Context mContext) {
		int versionCode = 0;
		PackageManager packageManager = mContext.getPackageManager();
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(
					mContext.getPackageName(), 0);
			versionCode = packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * get the app's version name
	 * 
	 * @param mContext
	 * @return
	 */
	public String getVersionName(Context mContext) {
		PackageManager packageManager = mContext.getPackageManager();
		String versionName = "";
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(
					mContext.getPackageName(), 0);
			versionName = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	
	public void updateAppByStoreId(String packageName){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" +packageName));
		mContext.startActivity(intent); 
	}

	public void updateAppByStoreName(String searchName){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://search?q="+searchName));
		mContext.startActivity(intent); 
	}
	public void updateAppByUrl(String updateUrl, String saveName) {
		this.mUpdateUrl = updateUrl;
		this.mSaveName = saveName;
		Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		mTextViewProgress = (TextView) v.findViewById(R.id.tv_progress);
		builder.setView(v);
		builder.setNegativeButton(R.string.soft_update_cancel,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						cancelUpdate = true;
					}
				});
		mUpdateDialog = builder.create();
		mUpdateDialog.show();
		downloadApk();
	}

	private void downloadApk() {
		new downloadApkThread().start();
	}

	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mUpdateUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mSaveName);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					byte buf[] = new byte[1024];
					do {
						int numread = is.read(buf);
						count += numread;
						progress = (int) (((float) count / length) * 100);
						mHandler.sendEmptyMessage(DOWNLOAD_ING);
						if (numread <= 0) {
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mUpdateDialog.dismiss();
		}
	};

	private void installApk() {
		File apkfile = new File(mSavePath, mSaveName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
