package de.daschubbm.alkchievements;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import de.daschubbm.alkchievements.firebase.FirebaseManager;
import de.daschubbm.alkchievements.firebase.ValuePair;
import de.daschubbm.alkchievements.firebase.ValueReadCallback;
import de.daschubbm.alkchievements.util.ConnectivityChecker;

import static de.daschubbm.alkchievements.NumberFormatter.formatPrice;

public class SettingsAlktivity extends AppCompatActivity {

    private SettingsAlkdapter alkdapter;
    private ListView drinks;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_alktivity);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle("Einstellungen");

        context = this;

        ConnectivityChecker.checkConnectivity(context);

        FirebaseManager.registerDrinksCallback(new ValueReadCallback<Map<String, ValuePair[]>>() {
            @Override
            public void onCallback(Map<String, ValuePair[]> data) {
                ArrayList<String[]> names = new ArrayList<>(data.size());

                for (Map.Entry<String, ValuePair[]> drink : data.entrySet()) {
                    for (ValuePair pair : drink.getValue()) {
                        if (pair.key.equals("price")) {
                            names.add(new String[]{drink.getKey(), formatPrice(String.valueOf(pair.value))});
                            break;
                        }
                    }
                }

                drinks = (ListView) findViewById(R.id.settings_list);
                drinks.setAdapter(alkdapter = new SettingsAlkdapter(context, names, drinks));

                findViewById(R.id.loading).setVisibility(View.GONE);
            }
        }, null);
    }

    @Override
    public void onBackPressed() {
        Intent mStartActivity = new Intent(context, MainAlktivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1, mPendingIntent);
        System.exit(0);
    }

    public void performRequestedAction(final View view) {
        switch ((String) view.getTag()) {
            case "billing":
                launchBilling();
                break;
            case "add_drink":
                addDrink();
                break;
            case "change_pin":
                changePin();
                break;
        }
    }

    private void changePin() {
        EditText pinField = (EditText) findViewById(R.id.pinText);
        String newPin = pinField.getText().toString();
        if (!newPin.matches("[0-9]{4}")) {
            Toast.makeText(context, "Die PIN muss genau 4 Stellen haben!", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseManager.writeValue("adminPassword", Integer.parseInt(newPin));

            Toast.makeText(context, "Die PIN wurde zu \"" + newPin + "\" geändert. Gut merken!",
                    Toast.LENGTH_SHORT).show();
        }

        pinField.setText("");
    }

    private void launchBilling() {
        Intent hansl = new Intent(this, BillAlktivity.class);
        hansl.putExtra("ADMIN", true);
        startActivity(hansl);
    }

    private void addDrink() {
        EditText drinkNameField = (EditText) findViewById(R.id.add_drink_name);
        EditText drinkPriceField = (EditText) findViewById(R.id.add_drink_price);

        final String drinkName = drinkNameField.getText().toString();
        String drinkPrice = drinkPriceField.getText().toString();

        if (drinkName.matches("[A-ZÄÖÜ][a-zäöüß]+") && drinkPrice.matches("[0-9]+(\\.[0-9]+)?")) {
            String[] newDrink = new String[]{drinkName, formatPrice(drinkPrice)};

            for (int i = 0; i < alkdapter.getCount(); i++) {
                String[] item = alkdapter.getItem(i);
                if (item != null && drinkName.equals(item[0])) alkdapter.remove(item);
            }

            alkdapter.add(newDrink);
            drinks.setAdapter(alkdapter);

            drinkNameField.setText("");
            drinkPriceField.setText("");

            DatabaseReference drinks = FirebaseDatabase.getInstance().getReference("drinks");
            drinks.child(drinkName).child("price").setValue(Float.valueOf(drinkPrice));

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_add_new_drink_stock);

            TextView drinkNameText = (TextView) dialog.findViewById(R.id.drink_name);
            drinkNameText.setText(((String) drinkNameText.getText()).replace("%drink%", drinkName));

            final NumberPicker picker = (NumberPicker) dialog.findViewById(R.id.stock_number);
            picker.setMinValue(0);
            picker.setMaxValue(50);
            picker.setValue(20);

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    FirebaseDatabase.getInstance()
                            .getReference("drinks/" + drinkName + "/stock")
                            .setValue(picker.getValue());
                    Toast.makeText(context, "Der Bestand von " + drinkName
                            + " wurde auf " + picker.getValue()
                            + " erhöht.", Toast.LENGTH_SHORT).show();
                }
            });

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            };

            dialog.findViewById(R.id.accept_button).setOnClickListener(listener);

            dialog.show();
        }
    }
}
