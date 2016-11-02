package de.daschubbm.alkchievements;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Maxi on 30.09.2016.
 */
class SettingsAlkdapter extends ArrayAdapter<String[]> {

    private final ListView parentList;
    private final Context context;
    private final int layoutResourceId;
    private ArrayList<String[]> drinks = new ArrayList<>();


    SettingsAlkdapter(Context context, ArrayList<String[]> data, ListView parentList) {

        super(context, R.layout.settings_drink_item, data);
        this.parentList = parentList;

        this.layoutResourceId = R.layout.settings_drink_item;
        this.context = context;
        drinks = data;
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layoutResourceId, null);
        }

        final String drinkName = drinks.get(position)[0];
        String drinkPrice = drinks.get(position)[1];

        Switch toggle = (Switch) v.findViewById(R.id.drink_toggle);
        toggle.setLongClickable(true);

        toggle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogInterface.OnClickListener deleteDialogListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (drinkName.equals(drinks.get(position)[0])) {
                                    drinks.remove(position);

                                    DatabaseReference drink = FirebaseDatabase.getInstance()
                                            .getReference("drinks/" + drinkName + "/price");
                                    drink.removeValue();

                                    drink = FirebaseDatabase.getInstance()
                                            .getReference("drinks/" + drinkName + "/stock");
                                    drink.removeValue();

                                    FirebaseDatabase.getInstance().getReference("people")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                                                        user.child("drinks").child(drinkName).getRef().removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                    parentList.setAdapter(SettingsAlkdapter.this);

                                    Toast.makeText(context, drinkName + " wurde entfernt!",
                                            Toast.LENGTH_SHORT).show();
                                } else throw new ApplicationFuckedUpError("Listenindex stimmt " +
                                        "nicht mit Adapterindex überein!");
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(context, "Dann lass ma's hoid bleim.",
                                        Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("Weg damit!", deleteDialogListener)
                        .setNegativeButton("B'hoidn!", deleteDialogListener)
                        .setTitle("\"" + drinkName + "\" löschen?")
                        .setMessage("Das Getränk und alle damit verbundenen Rechnungen werden gelöscht! \n\n" +
                                "Falls nur der Preis geändert werden soll, einfach neues Getränk " +
                                "mit selbem Namen und neuem Preis hinzufügen aber NICHT LÖSCHEN!");
                AlertDialog dialog = builder.show();

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.LTGRAY);

                return true;
            }
        });

        if (drinkName != null && drinkPrice != null) {
            TextView name = (TextView) v.findViewById(R.id.drink_name);
            name.setText(drinkName);

            TextView price = (TextView) v.findViewById(R.id.drink_price);
            price.setText(drinkPrice);
        }
        return v;
    }
}
