package mate.simple.bluetoothdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.zj.btsdk.BluetoothService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.security.SecureRandom;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import mate.simple.bluetoothdemo.PrintImage.dither;

/*MATE TECHNOLOGIES - ANDROID SDK INTEGRATION
	Android SDK integration
-	First of all, include library btsdk.jar to your project (Find in libs folder)
-	In AndroidManifest.xml file, set following permissions
   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
-   Note that Android 6 + versions require run time permissions, you need to add those permissions if your target SDK version is 24+ Current project does not show these permissions due to lower target SDK version     
-	In AndroidManifest.xml file, add activity under Application tag
<activity android:name="DeviceListActivity" android:label="@string/select_device" android:theme="@android:style/Theme.Dialog"></activity>
-	Include files device_name.xml and device_list.xml into res->layout folder (res->layout folder in project)
-	Include DeviceListActivity.java and Bluetooth.java file into src folder under package you want
-	You only need to change program in MainActivity.java file
 */
public class MainActivity extends Activity {
    int PERMISSION_ALL = 1;
    int REQUEST_ENABLE_BT = 4, REQUEST_CONNECT_DEVICE = 6;
    int effectivePrintWidth = 48;//set effective print width of bluetooth thermal printer e.g. set 48 for 2 inch/58 mm and 72 for 3 inch/80 mm printer

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnText = (Button) findViewById(R.id.btnText);
        btnText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //following is the function to send text data
                if (Bluetooth.isPrinterConnected(getApplicationContext(), MainActivity.this)) {
                    printTextData("Teste Mini Printer");
                } else {
                    //Printer not connected and send request for connecting printer
                    Bluetooth.connectPrinter(getApplicationContext(), MainActivity.this);
                }
            }
        });

        Button btnImage = (Button) findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //following is the function to send image data
                if (Bluetooth.isPrinterConnected(getApplicationContext(), MainActivity.this)) {
                    selectImageFromSDCard();
                } else {
                    //Printer not connected and send request for connecting printer
                    Bluetooth.connectPrinter(getApplicationContext(), MainActivity.this);
                }
            }
        });

        Button btnMultilingual = (Button) findViewById(R.id.btnMultilingual);
        btnMultilingual.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                /*You will need this if you want multilingual / regional language print
                 * Just pass HTML content to following activity. Don't change anything PrintMultilingualText class since errors in the class may result in damage to printer
                 */
                if (Bluetooth.isPrinterConnected(getApplicationContext(), MainActivity.this)) {
                    String htmlContent = "<body style=\"font-size:20px\">यह एक सैंपल पैराग्राफ है जो हिंदी लैंग्वेज में दिया गया है ! आप किसी भी लैंग्वेज में डाटा प्रिंट कर सकते हो !<br /><center><b>मते टेक्नोलॉजीज</b></center></body>";
                    String dirpath = Environment.getExternalStorageDirectory().toString() + "/BluetoothPrint";
                    if (Bluetooth.isPrinterConnected(getApplicationContext(), MainActivity.this)) {
                        BluetoothService mService = null;
                        mService = Bluetooth.getServiceInstance();
                        PrintMultilingualText pml = new PrintMultilingualText(MainActivity.this, MainActivity.this, mService, dirpath, effectivePrintWidth);
                        pml.startPrinting(htmlContent);
                    } else {
                        //Printer not connected and send request for connecting printer
                        Bluetooth.connectPrinter(getApplicationContext(), MainActivity.this);
                    }
                } else {
                    //Printer not connected and send request for connecting printer
                    Bluetooth.connectPrinter(getApplicationContext(), MainActivity.this);
                }
            }
        });

        Button btnQr = (Button) findViewById(R.id.btnQr);
        btnQr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                /*following is the function to send data for qr code printing
                 * Just create bitmap with qr code and send for printing which is the same as image printing
                 */
                if (Bluetooth.isPrinterConnected(getApplicationContext(), MainActivity.this)) {
                    printQrCode();
                } else {
                    //Printer not connected and send request for connecting printer
                    Bluetooth.connectPrinter(getApplicationContext(), MainActivity.this);
                }
            }
        });

        Button btnBarCode = (Button) findViewById(R.id.btnBarCode);
        btnBarCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                /*following is the function to send data for qr code printing
                 * Just create bitmap with qr code and send for printing which is the same as image printing
                 */
                if (Bluetooth.isPrinterConnected(getApplicationContext(), MainActivity.this)) {
                    printBarCode();
                } else {
                    //Printer not connected and send request for connecting printer
                    Bluetooth.connectPrinter(getApplicationContext(), MainActivity.this);
                }
            }
        });

        Button btnTabular = (Button) findViewById(R.id.btnTabular);
        btnTabular.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //following is the function to print data in tabular format
                if (Bluetooth.isPrinterConnected(getApplicationContext(), MainActivity.this)) {
                    printTabularData();
                } else {
                    //Printer not connected and send request for connecting printer
                    Bluetooth.connectPrinter(getApplicationContext(), MainActivity.this);
                }
            }
        });

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        // Get current android os version.
        int currentAndroidVersion = Build.VERSION.SDK_INT;
        // Build.VERSION_CODES.M's value is 23.
        if (currentAndroidVersion >= Build.VERSION_CODES.M) {
            if (context != null && permissions != null) {
                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected void printTabularData() {
        int totalNoOfColumns = 4;//no of columns
        int minChars = 4;/*this defines minimum characters per column; since you can
		create multiple rows, this value will define how many columns you want per row */
        boolean doubleWidthColumnExists = true;//this is true if you want a column with double space than other others
        int doubleWidthColRank = 1;//this is the rank of column which will have double space than other columns; starts from 0
        int rightAlignColRank = 3;//this is the rank of column whose content is right aligned; starts from 0

        PlainPrint pp = new PlainPrint(getApplicationContext(), effectivePrintWidth, minChars);
        pp.prepareTabularForm(totalNoOfColumns, doubleWidthColRank, rightAlignColRank, doubleWidthColumnExists);
        //create array list
        ArrayList<String> itemsList = new ArrayList<String>();

        BluetoothService mService = null;
        mService = Bluetooth.getServiceInstance();

        mService.sendMessage(pp.getDashesFullLine(), "GBK");

        itemsList.add("Sr");
        itemsList.add("Item");
        itemsList.add("Qty");
        itemsList.add("Total");
        Log.v("mainact", "bbbeb");
        pp.startAddingContent4printFields();
        pp.addItemContent(itemsList);
        Log.v("mainact", pp.getContent4PrintFields());
        mService.write(PrinterComands.BOLD_TEXT);
        mService.sendMessage(pp.getContent4PrintFields(), "GBK");

        mService.sendMessage(pp.getDashesFullLine(), "GBK");

        itemsList.clear();

        itemsList.add("1");
        itemsList.add("Tata Tea");
        itemsList.add("2");
        itemsList.add("10.00");

        pp.startAddingContent4printFields();
        pp.addItemContent(itemsList);
        mService.write(PrinterComands.NORMAL_TEXT);
        mService.sendMessage(pp.getContent4PrintFields(), "GBK");

        itemsList.clear();

        itemsList.add("2");
        itemsList.add("Rin Supreme");
        itemsList.add("3");
        itemsList.add("45.00");

        pp.startAddingContent4printFields();
        pp.addItemContent(itemsList);
        mService.sendMessage(pp.getContent4PrintFields() + "\n\n", "GBK");

        mService.write(PrinterComands.FEED_LINE);
        mService.write(PrinterComands.FEED_LINE);
    }

    private static String generateLocator(String chave) {
        SecureRandom rnd = new SecureRandom();
        String chavePos = "999";
        String AB = chave.replaceAll("(([0-9])(\2)+)", "$2");
        int len = chave.length();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));

        AB = sb.toString();

        int partsCodigo = len / chavePos.length();

        String part1 = AB.substring(0, partsCodigo);
        String part2 = AB.substring(part1.length(), partsCodigo + part1.length());
        String part3 = AB.replace(part1.concat(part2), "");

        AB = Character.toString(chavePos.charAt(0)).concat(part1);
        AB = AB.concat(Character.toString(chavePos.charAt(1)));
        AB = AB.concat(part2);
        AB = AB.concat(Character.toString(chavePos.charAt(2)));
        AB = AB.concat(part3);

        return AB;
    }

    protected void printBarCode() {
        String date = LocalDateTime.now(DateTimeZone.getDefault()).toString(DateTimeFormat.forPattern("MMddyy"));
        String chave = "2";

        String content = generateLocator(date.concat(chave));
        int qrsize = 40;//barcode size

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            int imgWidth = qrsize * 8, imgHeight = qrsize * 4;
            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.CODE_128, imgWidth, imgHeight);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            //following code is the same as image printing
            BluetoothService mService = null;
            mService = Bluetooth.getServiceInstance();

            PrintImage PrintImage = new PrintImage(getResizedBitmap(bmp));
            PrintImage.PrepareImage(dither.floyd_steinberg, 128);
            mService.write(PrinterComands.ESC_ALIGN_CENTER);
            printText(content);
            mService.write(PrintImage.getPrintImageData());
            mService.write(PrinterComands.FEED_LINE);
            mService.write(PrinterComands.FEED_LINE);
            mService.write(PrinterComands.FEED_LINE);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    protected void printQrCode() {
        String content = "123456789";//content for qr code
        int qrsize = 40;//qr code size
        QRCodeWriter writer = new QRCodeWriter();

        try {
            int imgWidth = qrsize * 8, imgHeight = qrsize * 8;
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, imgWidth, imgHeight);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            //following code is the same as image printing
            BluetoothService mService = null;
            mService = Bluetooth.getServiceInstance();

            PrintImage PrintImage = new PrintImage(getResizedBitmap(bmp));
            PrintImage.PrepareImage(dither.floyd_steinberg, 128);
            mService.write(PrinterComands.ESC_ALIGN_CENTER);
            printText(content);
            mService.write(PrintImage.getPrintImageData());
            mService.write(PrinterComands.FEED_LINE);
            mService.write(PrinterComands.FEED_LINE);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    protected void selectImageFromSDCard() {
        FileDialog fileDialog;
        File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
        fileDialog = new FileDialog(MainActivity.this, mPath);
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            @Override
            public void fileSelected(File file) {
                String img_file_path = file.toString();
                String filenameArray[] = img_file_path.split("\\.");
                String extension = filenameArray[filenameArray.length - 1];
                String[] img = {"png", "jpg", "bmp", "PNG", "JPG", "BMP"};
                if (Arrays.asList(img).contains(extension)) {
                    printImage(img_file_path);
                } else {
                    Toast.makeText(getApplicationContext(), "invalid file type", Toast.LENGTH_LONG).show();
                }
            }
        });
        fileDialog.showDialog();
    }

    protected void printImage(String img_file_path) {
        BluetoothService mService = null;
        mService = Bluetooth.getServiceInstance();

        if (new File(img_file_path).exists()) {
            Bitmap image = BitmapFactory.decodeFile(img_file_path);
            if (image != null) {
                PrintImage PrintImage = new PrintImage(getResizedBitmap(image));
                PrintImage.PrepareImage(dither.floyd_steinberg, 64);
                mService.write(PrintImage.getPrintImageData());
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm) {
        int newWidth = 248;
        int newHeight = 297;
        int reqWidth = (int) Math.round(effectivePrintWidth * 8);
        int width = bm.getWidth();
        int height = bm.getHeight();
        if (width == reqWidth) {
            return bm;
        } else if (width < reqWidth && width > 16) {
            int diff = width % 8;
            if (diff != 0) {
                newWidth = width - diff;
                newHeight = (int) (width - diff) * height / width;
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
        } else if (width > 16) {
            newWidth = reqWidth;
            newHeight = (int) reqWidth * height / width;
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

    private void printText(String text) {
        BluetoothService mService = null;
        mService = Bluetooth.getServiceInstance();

        mService.write(PrinterComands.BOLD_TEXT);
        mService.sendMessage(text, "GBK");
    }

    private void printTextData(String text) {

        BluetoothService mService = null;
        mService = Bluetooth.getServiceInstance();

        mService.write(PrinterComands.BOLD_TEXT);

        mService.sendMessage(text, "GBK");

        mService.write(PrinterComands.DOUBLE_HEIGHT_TEXT);
        mService.sendMessage(text, "GBK");

        mService.write(PrinterComands.DOUBLE_HEIGHT_BOLD_TEXT);
        mService.sendMessage(text, "GBK");

        mService.write(PrinterComands.DOUBLE_WIDTH_TEXT);
        mService.sendMessage(text, "GBK");

        mService.write(PrinterComands.DOUBLE_WIDTH_HEIGHT_TEXT);
        mService.sendMessage(text, "GBK");

        mService.write(PrinterComands.BOLD_DOUBLE_WIDTH_HEIGHT_TEXT);
        mService.sendMessage(text, "GBK");

        mService.write(PrinterComands.BOLD_DOUBLE_WIDTH_HEIGHT_TEXT1);
        mService.sendMessage(text, "GBK");

        mService.write(PrinterComands.DOUBLE_HEIGHT_TEXT);
        mService.sendMessage(text, "GBK");

        mService.write(PrinterComands.FEED_LINE);
        mService.write(PrinterComands.FEED_LINE);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            //bluetooth enabled and request for showing available bluetooth devices
            Bluetooth.pairPrinter(getApplicationContext(), MainActivity.this);
        } else if (requestCode == REQUEST_CONNECT_DEVICE && resultCode == RESULT_OK) {
            //bluetooth device selected and request pairing with device
            String address = data.getExtras()
                    .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            Bluetooth.pairedPrinterAddress(getApplicationContext(), MainActivity.this, address);
        }
    }
}
