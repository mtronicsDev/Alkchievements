package de.daschubbm.alkchievements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Maxi on 06.10.2016.
 */
class UpdateDialogProcedure {
    static final File DOWNLOAD_FILE = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/app-release.apk");
    private static String url;

    static void showUpdateDialog(final Context context, String buildNumber, final String changelog, final String url) {
        UpdateDialogProcedure.url = url;

        DialogInterface.OnClickListener updateDialogListener = new DialogInterface.OnClickListener() {
            int negativeButtonPresses = 0;

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (!(context instanceof Activity))
                            throw new ApplicationFuckedUpError("Update-Dialog muss von Activity aufgerufen werden!");
                        checkPermissionsAndDownload((Activity) context);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        negativeButtonPresses++;
                        showNextDialog(context, this, negativeButtonPresses);
                        break;
                }
            }
        };

        createDialog(context, "Neu's Update is do!",
                "Neue Version: " + buildNumber
                        + "\nDes gibt's ois neu's:\n"
                        + changelog
                        + "\n\nIch rate dir davon ab, des Update ned obaz'lodn " +
                        "weil da vielleicht dei App verreckt.\n"
                        + "As Bier schmeckt ja bekanntlich nur, wenn ma's aa zahlen kann! \ud83c\udf7a",
                "Ja sowieso!", "Naa, koa Bock.", Color.GREEN, Color.LTGRAY, updateDialogListener);
    }

    static void checkPermissionsAndDownload(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 69);
        } else {
            download(activity);
        }
    }

    private static void download(Context context) {
        DownloadManager.Request request =
                new DownloadManager.Request(Uri.parse(url));
        request.setDestinationUri(Uri.fromFile(DOWNLOAD_FILE));
        request.setMimeType("application/vnd.android.package-archive");

        final DownloadManager downloadManager =
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadID = downloadManager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadID);
                    Cursor cursor = downloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        int stateIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(stateIndex)) {
                            String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                            Log.d("UPDATE-INSTALL", uri);

                            Uri compiledUri = Uri.fromFile(DOWNLOAD_FILE);

                            Log.d("UPDATE-INSTALL", compiledUri.getPath());

                            context.unregisterReceiver(this);

                            Intent hansl = new Intent(Intent.ACTION_VIEW);
                            hansl.setDataAndType(compiledUri, "application/vnd.android.package-archive");
                            hansl.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            hansl.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            hansl.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            context.startActivity(hansl);
                        }
                    }
                }
            }
        };

        context.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private static void showNextDialog(final Context context, DialogInterface.OnClickListener listener, int negativeButtonPresses) {
        String title, message, positiveText, negativeText;
        boolean flipColors = false;

        switch (negativeButtonPresses) {
            default:
            case 1:
                title = "Sicher ned?";
                message = "I sag's nummoi: As Bier schmeckt nur, wenn ma's aa zahlen kann! \uD83C\uDF7A";
                positiveText = "Vo mir aus";
                negativeText = "Naa i mog ned!";
                break;
            case 2:
                title = "Ganz sicher?";
                message = "Geh kimm, sei ned so stur!";
                positiveText = "Naa, i lads ma doch oba.";
                negativeText = "Ja, sicher.";
                break;
            case 3:
                title = "Geh iatz mach des hoid.";
                message = "Lass de ned bedln, de App wird ja ned schlechter mim Update!";
                positiveText = "Wennst unbedingt moanst...";
                negativeText = "NAA hab i gsogt!!";
                break;
            case 4:
                title = "Ja dann hoid ned.";
                message = "Wennst so fortschrittsresistent bist lasst as hoid bleim.";
                positiveText = "Basst.";
                negativeText = "Hoit, i wui's doch!";
                flipColors = true;
                break;
            case 5:
                title = "Oaschloch!";
                message = "";
                positiveText = "Installieren.";
                negativeText = "Weg damit!";
                break;
            case 6:
                Toast.makeText(context, "Jo Freindal, jetz gibt's a Bestrafung!", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < 20; i++) {
                    Toast.makeText(context, "Kennst den guadn Witz scho?", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Kimmt a Mann aus 'm Urlaub heim und ratscht mit seinem Kollegen.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "I war im Urlaub, und zwar in Kenia!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "- Wos? In Kenia? Was hast denn da gmacht?", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "I war auf Safari!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "- Echt? Und was hast da für Tiere gseng?", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Mei, a Giraffe, Nilpferde, Zebras UND AN LÖWEN!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "- Wos? An Löwen? Und was hat der so g'macht?", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Der hat de ganze Zeit so g'macht: ", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "\ud83d\ude36", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "\ud83d\ude2e", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "\ud83d\ude36", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "\ud83d\ude2e", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "- Hm. Is der ned normal aggressiv? Und beißt?", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Nö du, i hab na ja von hinten g'sehn.", Toast.LENGTH_SHORT).show();
                    if (i < 19)
                        Toast.makeText(context, "Und nochmal MUHAHA!", Toast.LENGTH_SHORT).show();
                }
                return;
        }

        createDialog(context, title, message, positiveText, negativeText,
                flipColors ? Color.LTGRAY : Color.GREEN, flipColors ? Color.GREEN : Color.LTGRAY, listener);

    }

    private static void createDialog(Context context, String title, String message,
                                     String positiveText, String negativeText,
                                     int positiveColor, int negativeColor,
                                     DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, listener)
                .setNegativeButton(negativeText, listener)
                .show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(positiveColor);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(negativeColor);
    }
}
