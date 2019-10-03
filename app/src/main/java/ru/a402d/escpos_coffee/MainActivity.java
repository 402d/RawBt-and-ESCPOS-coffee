package ru.a402d.escpos_coffee;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.barcode.BarCode;
import com.github.anastaciocintra.escpos.barcode.PDF417;
import com.github.anastaciocintra.escpos.barcode.QRCode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Checks and if the application is not installed, then offers to download it from the Play Market
     */
    protected void sendToPrint(Intent intent) {
        final String appPackageName = "ru.a402d.rawbtprinter";
        PackageManager pm = getPackageManager();

        // check app installed
        PackageInfo pi = null;
        if (pm != null) {
            try {
                pi = pm.getPackageInfo(appPackageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (pi == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            TextView title = new TextView(this);
            title.setText(R.string.dialog_title);
            title.setBackgroundColor(Color.DKGRAY);
            title.setPadding(10, 10, 10, 10);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(14);
            ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.baseline_print_black_48);
            builder.setMessage(R.string.dialog_message)
                    .setView(image).setCustomTitle(title);
            builder.setPositiveButton(R.string.btn_install, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            // send to print
            intent.setPackage(appPackageName);
            startActivity(intent);

        }
    }


    public void test_Hello(View button) {
        try {
            // assign output into memory
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            EscPos escpos = new EscPos(buf);

            //  do check
            escpos.initializePrinter();
            escpos.writeLF("Hello World");
            escpos.feed(5);
            escpos.cut(EscPos.CutMode.FULL);
            escpos.close();


            // take output and encode to base64
            String base64ToPrint = Base64.encodeToString(buf.toByteArray(), Base64.DEFAULT);

            // call intent with rawbt:base64,
            String url = "rawbt:base64," + base64ToPrint;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            sendToPrint(intent);
            ((Button) button).setText("x");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_Getstart(View button) {
        try {
            // assign output into memory
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            EscPos escpos = new EscPos(buf);

            // ------------------ DO ----------------

            escpos.info();

            // ------------------ /DO ----------------

            escpos.close();
            // take output and encode to base64
            String base64ToPrint = Base64.encodeToString(buf.toByteArray(), Base64.DEFAULT);

            // call intent with rawbt:base64,
            String url = "rawbt:base64," + base64ToPrint;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            sendToPrint(intent);
            ((Button) button).setText("x");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_Barcode(View button) {
        try {
            // assign output into memory
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            EscPos escpos = new EscPos(buf);

            // ------------------ DO ----------------

            Style title = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);

            escpos.writeLF(title, "Barcode");
            escpos.feed(2);
            BarCode barcode = new BarCode();

            escpos.writeLF("barcode default options CODE93 system");
            escpos.feed(1);
            escpos.write(barcode, "hello barcode");
            escpos.feed(1);

            escpos.writeLF("barcode write HRI above");
            escpos.feed(1);
            barcode.setHRIPosition(BarCode.BarCodeHRIPosition.AboveBarCode);
            escpos.write(barcode, "hello barcode");
            escpos.feed(3);

            escpos.writeLF("barcode write HRI below");
            escpos.feed(1);
            barcode.setHRIPosition(BarCode.BarCodeHRIPosition.BelowBarCode);
            escpos.write(barcode, "hello barcode");
            escpos.feed(3);

            escpos.writeLF("barcode right justification ");
            escpos.feed(1);
            barcode.setHRIPosition(BarCode.BarCodeHRIPosition.NotPrinted_Default);
            barcode.setJustification(EscPosConst.Justification.Right);
            escpos.write(barcode, "barcode");
            escpos.feed(3);

            escpos.writeLF("barcode height 200 ");
            escpos.feed(1);
            barcode.setJustification(EscPosConst.Justification.Left_Default);
            barcode.setBarCodeSize(2, 200);
            escpos.write(barcode, "hello barcode");
            escpos.feed(3);

            escpos.writeLF("barcode UPCA system ");
            escpos.feed(1);
            barcode.setSystem(BarCode.BarCodeSystem.UPCA);
            barcode.setHRIPosition(BarCode.BarCodeHRIPosition.BelowBarCode);
            barcode.setBarCodeSize(2, 100);
            escpos.write(barcode, "12345678901");
            escpos.feed(3);

            escpos.feed(5);
            escpos.cut(EscPos.CutMode.PART);

            escpos.writeLF(title, "QR Code");
            escpos.feed(1);
            QRCode qrcode = new QRCode();

            escpos.writeLF("QRCode default options");
            escpos.feed(1);
            escpos.write(qrcode, "hello qrcode");
            escpos.feed(3);

            escpos.writeLF("QRCode size 6 and center justified");
            escpos.feed(1);
            qrcode.setSize(7);
            qrcode.setJustification(EscPosConst.Justification.Center);
            escpos.write(qrcode, "hello qrcode");
            escpos.feed(3);

            escpos.feed(5);
            escpos.cut(EscPos.CutMode.PART);

            escpos.writeLF(title, "PDF 417");
            escpos.feed(1);
            PDF417 pdf417 = new PDF417();

            escpos.writeLF("pdf417 default options");
            escpos.feed(1);
            escpos.write(pdf417, "hello PDF 417");
            escpos.feed(3);

            escpos.writeLF("pdf417 height 5");
            escpos.feed(1);
            pdf417.setHeight(5);
            escpos.write(pdf417, "hello PDF 417");
            escpos.feed(3);

            escpos.writeLF("pdf417 error level 4");
            escpos.feed(1);
            pdf417 = new PDF417().setErrorLevel(PDF417.PDF417ErrorLevel._4);
            escpos.write(pdf417, "hello PDF 417");
            escpos.feed(3);

            escpos.feed(5);
            escpos.cut(EscPos.CutMode.FULL);

            // ------------------ /DO ----------------

            escpos.close();
            // take output and encode to base64
            String base64ToPrint = Base64.encodeToString(buf.toByteArray(), Base64.DEFAULT);

            // call intent with rawbt:base64,
            String url = "rawbt:base64," + base64ToPrint;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            sendToPrint(intent);
            ((Button) button).setText("x");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void test_BitImage(View button) {
        try {
            // assign output into memory
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            EscPos escpos = new EscPos(buf);

            // ------------------ DO ----------------

            escpos.writeLF("The AWT package is not supported in Android.");

            // ------------------ /DO ----------------

            escpos.close();
            // take output and encode to base64
            String base64ToPrint = Base64.encodeToString(buf.toByteArray(), Base64.DEFAULT);

            // call intent with rawbt:base64,
            String url = "rawbt:base64," + base64ToPrint;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            sendToPrint(intent);
            ((Button) button).setText("- not do -");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_CodeTable(View button) {
        try {
            // assign output into memory
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            EscPos escpos = new EscPos(buf);

            // ------------------ DO ----------------

            Style title = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);

            escpos.writeLF(title, "Code Table");
            escpos.feed(2);

            escpos.writeLF("Using code table of the France");
            escpos.setCharacterCodeTable(EscPos.CharacterCodeTable.CP863_Canadian_French);
            escpos.feed(2);
            escpos.writeLF("Liberté et Fraternité.");
            escpos.feed(3);


            escpos.writeLF("Using Portuguese code table");
            escpos.setCharacterCodeTable(EscPos.CharacterCodeTable.CP860_Portuguese);
            escpos.writeLF("Programação java.");

            escpos.feed(5);
            escpos.cut(EscPos.CutMode.FULL);


            // ------------------ /DO ----------------

            escpos.close();
            // take output and encode to base64
            String base64ToPrint = Base64.encodeToString(buf.toByteArray(), Base64.DEFAULT);

            // call intent with rawbt:base64,
            String url = "rawbt:base64," + base64ToPrint;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            sendToPrint(intent);
            ((Button) button).setText("x");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_Dithering(View button) {
        try {
            // assign output into memory
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            EscPos escpos = new EscPos(buf);

            // ------------------ DO ----------------

            escpos.writeLF("The AWT package is not supported in Android.");

            // ------------------ /DO ----------------

            escpos.close();
            // take output and encode to base64
            String base64ToPrint = Base64.encodeToString(buf.toByteArray(), Base64.DEFAULT);

            // call intent with rawbt:base64,
            String url = "rawbt:base64," + base64ToPrint;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            sendToPrint(intent);
            ((Button) button).setText("- not do -");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_Graphics(View button) {
        try {
            // assign output into memory
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            EscPos escpos = new EscPos(buf);

            // ------------------ DO ----------------

            escpos.writeLF("The AWT package is not supported in Android.");

            // ------------------ /DO ----------------

            escpos.close();
            // take output and encode to base64
            String base64ToPrint = Base64.encodeToString(buf.toByteArray(), Base64.DEFAULT);

            // call intent with rawbt:base64,
            String url = "rawbt:base64," + base64ToPrint;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            sendToPrint(intent);
            ((Button) button).setText("- not do -");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_Pulsepin(View button) {
        try {
            // assign output into memory
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            EscPos escpos = new EscPos(buf);

            // ------------------ DO ----------------

            escpos.pulsePin(EscPos.PinConnector.Pin_2, 50, 75);

            // ------------------ /DO ----------------

            escpos.close();
            // take output and encode to base64
            String base64ToPrint = Base64.encodeToString(buf.toByteArray(), Base64.DEFAULT);

            // call intent with rawbt:base64,
            String url = "rawbt:base64," + base64ToPrint;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            sendToPrint(intent);
            ((Button) button).setText("x");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_RasterImage(View button) {
        try {
            // assign output into memory
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            EscPos escpos = new EscPos(buf);

            // ------------------ DO ----------------


            // ------------------ /DO ----------------

            escpos.close();
            // take output and encode to base64
            String base64ToPrint = Base64.encodeToString(buf.toByteArray(), Base64.DEFAULT);

            // call intent with rawbt:base64,
            String url = "rawbt:base64," + base64ToPrint;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            sendToPrint(intent);
            ((Button) button).setText("x");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_TextStyle(View button) {
        try {
            // assign output into memory
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            EscPos escpos = new EscPos(buf);

            // ------------------ DO ----------------

            Style title = new Style()
                    .setFontSize(Style.FontSize._3, Style.FontSize._3)
                    .setJustification(EscPosConst.Justification.Center);

            Style subtitle = new Style(escpos.getStyle())
                    .setBold(true)
                    .setUnderline(Style.Underline.OneDotThick);
            Style bold = new Style(escpos.getStyle())
                    .setBold(true);

            escpos.writeLF(title, "My Market")
                    .feed(3)
                    .write("Client: ")
                    .writeLF(subtitle, "John Doe")
                    .feed(3)
                    .writeLF("Cup of coffee                      $1.00")
                    .writeLF("Botle of water                     $0.50")
                    .writeLF("----------------------------------------")
                    .feed(2)
                    .writeLF(bold,
                            "TOTAL                              $1.50")
                    .writeLF("----------------------------------------")
                    .feed(8)
                    .cut(EscPos.CutMode.FULL);

            // ------------------ /DO ----------------

            escpos.close();
            // take output and encode to base64
            String base64ToPrint = Base64.encodeToString(buf.toByteArray(), Base64.DEFAULT);

            // call intent with rawbt:base64,
            String url = "rawbt:base64," + base64ToPrint;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            sendToPrint(intent);
            ((Button) button).setText("x");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_TextStyle58mm(View button) {
        try {
            // assign output into memory
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            EscPos escpos = new EscPos(buf);

            // ------------------ DO ----------------

            Style title = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);

            Style subtitle = new Style(escpos.getStyle())
                    .setBold(true)
                    .setUnderline(Style.Underline.OneDotThick);
            Style total = new Style()
                    .setFontSize(Style.FontSize._1, Style.FontSize._2).setBold(true);

            escpos.writeLF(title, "My Market")
                    .feed(2)
                    .write("Client: ")
                    .writeLF(subtitle, "John Doe")
                    .feed(2)
                    .writeLF("Cup of coffee             $1.00") // 32 char
                    .writeLF("Botle of water            $0.50")
                    .writeLF("--------------------------------")
                    .writeLF(total,
                            "TOTAL                      $1.50")
                    .writeLF("--------------------------------")
                    .feed(5)
                    .cut(EscPos.CutMode.FULL);

            // ------------------ /DO ----------------

            escpos.close();
            // take output and encode to base64
            String base64ToPrint = Base64.encodeToString(buf.toByteArray(), Base64.DEFAULT);

            // call intent with rawbt:base64,
            String url = "rawbt:base64," + base64ToPrint;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            sendToPrint(intent);
            ((Button) button).setText("x");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
