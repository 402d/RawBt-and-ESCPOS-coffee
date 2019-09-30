package ru.a402d.escpos_coffee;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;

import java.io.ByteArrayOutputStream;


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


    public void test1(View button) {
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
            ((Button)button).setText("x");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test2(View button) {
        try {
            // assign output into memory
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            EscPos escpos = new EscPos(buf);

            //  do check
            Style title = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);

            Style subtitle = new Style(escpos.getStyle())
                    .setBold(true)
                    .setUnderline(Style.Underline.OneDotThick);
            Style bold = new Style(escpos.getStyle())
                    .setBold(true);

            escpos.initializePrinter()
                    .setCharacterCodeTable(EscPos.CharacterCodeTable.CP437_USA_Standard_Europe)
                    .write(title,"My Market").write(10)
                    .feed(1)
                    .write("Client: ")
                    .write(subtitle, "John Doe").write(10)
                    .feed(1)
                    .write("Cup of coffee            $1.00").write(10)
                    .write("Botle of water           $0.50").write(10)
                    .write("-------------------------------").write(10)
                    .write(bold,
                            "TOTAL                     $1.50").write(10)
                    .write("--------------------------------").write(10)
                    .feed(2)
                    .cut(EscPos.CutMode.FULL);


            escpos.close();


            // take output and encode to base64
            String base64ToPrint = Base64.encodeToString(buf.toByteArray(), Base64.DEFAULT);

            // call intent with rawbt:base64,
            String url = "rawbt:base64," + base64ToPrint;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            sendToPrint(intent);
            ((Button)button).setText("x");

            // it's all
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
