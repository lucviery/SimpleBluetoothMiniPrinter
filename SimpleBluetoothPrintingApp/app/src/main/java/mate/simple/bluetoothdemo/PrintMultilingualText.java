package mate.simple.bluetoothdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

import mate.simple.bluetoothdemo.PrintImage.dither;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.zj.btsdk.BluetoothService;

public class PrintMultilingualText {
	boolean initilized = false;
	Context con;
	Activity act;
	int effectivePrintWidth = 48;
	BluetoothService mService = null;
	String imageFilePath = "";
	@SuppressLint("NewApi")
	public PrintMultilingualText(Activity activity,Context context,BluetoothService mService,String dirPath,int pageWidth){
		/*Don't change anything in this class 
		 * since errors/bugs due to wrong calculation in the class may result in damage to printer mechanism
		 */
		con = context;
		act = activity;		
		if(pageWidth>20&&pageWidth<150)
			effectivePrintWidth = pageWidth;
		else
			effectivePrintWidth = 48;
		File sd = new File(dirPath);
		sd.mkdirs();
		imageFilePath = dirPath+"/temp.jpg";
		if(getPlatformVersion()>21)
			WebView.enableSlowWholeDocumentDraw();
		initilized = true;
	}
	public static int getPlatformVersion() {
		try {
			Field verField = Class.forName("android.os.Build$VERSION")
					.getField("SDK_INT");
			int ver = verField.getInt(verField);
			return ver;
		} catch (Exception e) {
			// android.os.Build$VERSION is not there on Cupcake
			return 1; //not sure
		}
	}
	public void startPrinting(String htmlContent) {
		if(initilized){
			myWebview(htmlContent,new File(imageFilePath));	
		}		
	}
	private void myWebview(String content,final File file){
		LayoutInflater li = LayoutInflater.from(con);
		final View promptsView = li.inflate(R.layout.webview, null);
		final WebView webView = (WebView)promptsView.findViewById(R.id.mywebView);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				con);
		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);		alertDialogBuilder
		.setCancelable(true);

		// create alert dialog
		final AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

		webView.setWebViewClient(new WebViewClient()
		{
			@Override
			public void onPageFinished(final WebView view, String url)
			{
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if(webView.getWidth()>0&&webView.getHeight()>0){
							act.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									//capture webview and create bitmap
									createBitmap(webView,file,alertDialog);									
								}
							});
						}
					}
				}, 1500);				
			}
		});

		int pageWidthInPixels = (int) Math.round(effectivePrintWidth*8);

		webView.setLayoutParams(new LinearLayout.LayoutParams(pageWidthInPixels,LayoutParams.WRAP_CONTENT));
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
	}
	public void createBitmap(WebView w, File file, AlertDialog alertDialog){
		Bitmap bitmap = null;
		String str = file.getAbsolutePath();
		if (null != str && str.length() > 0)
		{
			int endIndex = str.lastIndexOf("/");
			if (endIndex != -1)
			{
				String newstr = str.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
				File fTemp = new File(newstr);
				fTemp.mkdirs();
			}
		}
		float scale = con.getResources().getDisplayMetrics().density;
		int imgWidth = w.getWidth();
		int imgHeight = (int) (w.getContentHeight()*scale);
		if(imgWidth==0||imgHeight==0)
			alertDialog.dismiss();
		else{
			bitmap = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
			//bitmap.setDensity(500);
			final Canvas c = new Canvas(bitmap);
			c.drawColor(Color.WHITE);
			w.draw(c);
			try {
				alertDialog.dismiss();
				FileOutputStream out = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
				out.flush();
				out.close();
				//remaning code is the same as image printing
				printImage(file.getAbsolutePath());
			} catch (Exception e) {
				alertDialog.dismiss();
				e.printStackTrace();
			}
		}
	}

	protected void printImage(String img_file_path) {
		if(Bluetooth.isPrinterConnected(con,act)){
			BluetoothService mService = null;
			mService = Bluetooth.getServiceInstance();

			if(new File(img_file_path).exists()){
				Bitmap image = BitmapFactory.decodeFile(img_file_path);
				if(image!=null){
					try{
						PrintImage PrintImage = new PrintImage(getResizedBitmap(image));
						PrintImage.PrepareImage(dither.floyd_steinberg, 128);
						mService.write(PrintImage.getPrintImageData());
					}
					catch(OutOfMemoryError e){
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inSampleSize = 8;
						//String str = Environment.getExternalStorageDirectory().toString()+"/A/a.jpg";
						Bitmap image1 = BitmapFactory.decodeFile(img_file_path,options);
						if(image1!=null){
							PrintImage PrintImage = new PrintImage(getResizedBitmap(image1));
							PrintImage.PrepareImage(dither.floyd_steinberg, 128);
							mService.write(PrintImage.getPrintImageData());
						}
					}
				}	
			}
		}
	}
	public Bitmap getResizedBitmap(Bitmap bm) {
		int newWidth = 248;
		int newHeight = 297;
		int reqWidth = (int) Math.round(effectivePrintWidth*8);
		int width = bm.getWidth();
		int height = bm.getHeight();
		if(width==reqWidth){
			return bm;
		}
		else if(width<reqWidth&&width>16){
			int diff = width%8;
			if(diff!=0){
				newWidth = width - diff;
				newHeight = (int) (width - diff)*height/width;
				float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;
				// CREATE A MATRIX FOR THE MANIPULATION
				Matrix matrix = new Matrix();
				// RESIZE THE BIT MAP
				matrix.postScale(scaleWidth, scaleHeight);

				// "RECREATE" THE NEW BITMAP
				Bitmap resizedBitmap = Bitmap.createBitmap(
						bm, 0, 0, width, height, matrix, false);
				bm.recycle();
				return resizedBitmap;
			}
		}
		else if(width>16){
			newWidth = reqWidth;
			newHeight = (int) reqWidth*height/width;
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			// CREATE A MATRIX FOR THE MANIPULATION
			Matrix matrix = new Matrix();
			// RESIZE THE BIT MAP
			matrix.postScale(scaleWidth, scaleHeight);

			// "RECREATE" THE NEW BITMAP
			Bitmap resizedBitmap = Bitmap.createBitmap(
					bm, 0, 0, width, height, matrix, false);
			bm.recycle();
			return resizedBitmap;
		}
		return bm;
	}
}
