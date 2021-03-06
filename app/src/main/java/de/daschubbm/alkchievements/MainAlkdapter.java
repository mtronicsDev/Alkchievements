package de.daschubbm.alkchievements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static de.daschubbm.alkchievements.R.id.add_flasche;

/**
 * Created by Jonathan on 28.09.2016.
 */

class MainAlkdapter extends ArrayAdapter<String[]> {
    private final MainAlktivity main;

    private final Random random;

    private final Map<String, Integer> images = new HashMap<>();
    private final List<String[]> drinks;

    private View.OnClickListener onAddRequest;
    private View.OnLongClickListener onRemoveRequest;
    private View.OnClickListener onKastenTap;

    private ExecutorService uiInteractionThreadPool;
    private Runnable kastenClick;
    private StatefulRunnable addClick, removeClick;

    MainAlkdapter(MainAlktivity main, List<String[]> drinks) {
        super(main, R.layout.alk_item, drinks);

        this.main = main;
        this.drinks = drinks;

        random = new Random();

        images.put("Bier", R.drawable.kasten_bier);
        images.put("Radler", R.drawable.kasten_radler);
        images.put("Weizen", R.drawable.kasten_weizen);
        images.put("Almdudler", R.drawable.kasten_almdudler);
        images.put("Apfelschorle", R.drawable.kasten_apfelschorle);
        images.put("Spezi", R.drawable.kasten_spezi);
        images.put("Wasser", R.drawable.kasten_wasser);
        images.put("Schnaps", R.drawable.kasten_schnaps);

        uiInteractionThreadPool = Executors.newSingleThreadExecutor();

        initializeRunnables();
        initializeListeners();
    }

    private void initializeRunnables() {
        kastenClick = new Runnable() {
            @Override
            public void run() {
                main.addClickKasten();
            }
        };

        addClick = new StatefulRunnable() {
            private String[] drink;
            private int newStock;
            private int newGschwoabt;

            @Override
            public void setup(String[] drink, int newStock, int newGschwoabt) {

                this.drink = drink;
                this.newStock = newStock;
                this.newGschwoabt = newGschwoabt;
            }

            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference("drinks/" + drink[0] + "/stock")
                        .setValue(newStock);

                main.updateDrink(drink[0], newGschwoabt);
                main.addFollowDay();
                main.checkSum(Float.parseFloat(drink[1]));

                switch (drink[0]) {
                    case "Bier":
                        main.addEverBeer(true);
                        main.addSessionBeer(true);
                        break;
                    case "Weizen":
                        main.addSessionBeer(true);
                        break;
                    case "Radler":
                        main.addSessionRadler(true);
                        main.addSessionNonAlk(true);
                        break;
                    case "Wasser":
                    case "Almdudler":
                    case "Spezi":
                    case "Apfelschorle":
                        main.addSessionNonAlk(true);
                        break;
                    case "Schnaps":
                        main.addSessionShot(true);
                        break;
                }
            }
        };

        removeClick = new StatefulRunnable() {
            private String[] drink;
            private int newStock;
            private int newGschwoabt;

            @Override
            public void setup(String[] drink, int newStock, int newGschwoabt) {

                this.drink = drink;
                this.newStock = newStock;
                this.newGschwoabt = newGschwoabt;
            }

            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference("drinks/" + drink[0] + "/stock")
                        .setValue(newStock);

                main.updateDrink(drink[0], newGschwoabt);
                main.addStorno();

                switch (drink[0]) {
                    case "Bier":
                        main.addEverBeer(false);
                        main.addSessionBeer(false);
                        break;
                    case "Weizen":
                        main.addSessionBeer(false);
                        break;
                    case "Radler":
                        main.addSessionRadler(false);
                        main.addSessionNonAlk(false);
                        break;
                    case "Wasser":
                    case "Almdudler":
                    case "Spezi":
                    case "Apfelschorle":
                        main.addSessionNonAlk(false);
                        break;
                    case "Schnaps":
                        main.addSessionShot(false);
                        break;
                }
            }
        };
    }

    private void initializeListeners() {
        onAddRequest = new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                addDrinkAsync(view);
            }
        };

        onRemoveRequest = new View.OnLongClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onLongClick(View view) {
                removeDrinkAsync(view);
                return true;
            }
        };

        onKastenTap = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uiInteractionThreadPool.execute(kastenClick);

                if (random.nextInt(30) == 1) {
                    main.showFassl();
                }
            }
        };
    }

    @SuppressLint("SetTextI18n")
    private void addDrinkAsync(final View view) {
        final String[] drink = (String[]) view.getTag();
        final TextView gschwoabt = (TextView) ((ViewGroup) view.getParent()).findViewById(R.id.anzahl);

        final int newGschwoabt = Integer.parseInt(drink[2]) + 1;
        drink[2] = String.valueOf(newGschwoabt);
        final int newStock = Integer.parseInt(drink[3]) - 1;
        drink[3] = String.valueOf(newStock);

        gschwoabt.setText("G'schwoabt: " + drink[2]);

        addClick.setup(drink, newStock, newGschwoabt);
        uiInteractionThreadPool.execute(addClick);

        System.out.println("SYNC: " + Thread.currentThread().getName());
    }

    private void removeDrinkAsync(View view) {
        final String[] drink = (String[]) view.getTag();
        int gschwoabtCount = Integer.parseInt(drink[2]);

        if (gschwoabtCount > 0) {
            TextView gschwoabt = (TextView) ((ViewGroup) view.getParent()).findViewById(R.id.anzahl);

            gschwoabtCount = Integer.parseInt(drink[2]) - 1;
            drink[2] = String.valueOf(gschwoabtCount);

            gschwoabt.setText("G'schwoabt: " + drink[2]);

            int newStock = Integer.parseInt(drink[3]) + 1;
            drink[3] = String.valueOf(newStock);

            Toast.makeText(main, "Storniert \ud83d\ude12 Fettfinger!", Toast.LENGTH_SHORT).show();

            removeClick.setup(drink, newStock, gschwoabtCount);
            uiInteractionThreadPool.execute(removeClick);
        }
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @NonNull
    @Override
    public View getView(final int position, View v, @NonNull ViewGroup parent) {
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.alk_item, null);
        }

        TextView preis = (TextView) v.findViewById(R.id.preis);
        TextView gschwoabt = (TextView) v.findViewById(R.id.anzahl);
        TextView name = (TextView) v.findViewById(R.id.name);
        ImageView kasten = (ImageView) v.findViewById(R.id.kasten);
        ImageView addButton = (ImageView) v.findViewById(add_flasche);

        String[] drink = drinks.get(position);

        name.setText(drink[0]);
        addButton.setTag(drink);
        preis.setText("Preis: " + drink[1] + " €");
        gschwoabt.setText("G'schwoabt: " + drink[2]);

        if (images.containsKey(drink[0])) kasten.setImageResource(images.get(drink[0]));
        else kasten.setImageResource(R.drawable.kasten_sonstige);

        addButton.setOnClickListener(onAddRequest);
        addButton.setOnLongClickListener(onRemoveRequest);
        kasten.setOnClickListener(onKastenTap);

        return v;
    }

    void updateDrink(DataSnapshot changedNode) {
        String drinkName = changedNode.getKey();
        String stock = String.valueOf(changedNode.child("stock").getValue());

        for (String[] drink : drinks) {
            if (drink[0].equals(drinkName)) {
                if (!drink[3].equals(stock)) {
                    drink[3] = stock;
                }

                return;
            }
        }
    }

    private interface StatefulRunnable extends Runnable {
        void setup(String[] drink, int newStock, int newGschwoabt);
    }
}
