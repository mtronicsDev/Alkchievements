package de.daschubbm.alkchievements;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.daschubbm.alkchievements.firebase.FirebaseManager;
import de.daschubbm.alkchievements.firebase.ValuePair;
import de.daschubbm.alkchievements.firebase.ValueReadCallback;

/**
 * Created by Maxi on 17.10.2016.
 */
class AlkchievementsDatabase {
    private static final Map<String, String[]> alkchievementDescriptions;
    private static AlkchievementsDatabase currentInstance;

    static {
        alkchievementDescriptions = new HashMap<>(12);

        alkchievementDescriptions.put("armerSchlucker",
                new String[]{"Armer Schlucker", "Erhalte eine Rechnung von über %5,10,20%€!"});
        alkchievementDescriptions.put("bierkenner",
                new String[]{"Bierkenner", "Trinke %2,4,6% Bier an einem Abend!"});
        alkchievementDescriptions.put("stammgast",
                new String[]{"Stammgast", "Beschließe %3,5,7% Tage in Folge eine Transaktion im Schubbm!"});
        alkchievementDescriptions.put("kegelsportverein",
                new String[]{"Kegelsportverein", "Trinke 5 Radler an einem Abend!"});
        alkchievementDescriptions.put("nullKommaNull",
                new String[]{"0,0", "Trinke 5 antialkoholische Getränke an einem Abend!"});
        alkchievementDescriptions.put("blauWieDasMeer",
                new String[]{"Blau wie das Meer", "Trinke 5 Shots an einem Abend!"});
        alkchievementDescriptions.put("kastenLeer",
                new String[]{"Kasten leer", "Trinke insgesamt 20 Bier"});
        alkchievementDescriptions.put("schuldnerNummerEins",
                new String[]{"Schuldner Nr. 1", "Erreiche die höchste Summe auf der gesamten Rechnung!"});
        alkchievementDescriptions.put("hobbylos",
                new String[]{"Hobbylos", "Drücke 100 mal auf einen Kasten!"});
        alkchievementDescriptions.put("sparfuchs",
                new String[]{"Sparfuchs", "Bleibe bei 10 Getränken bei unter 7 €!"});
        alkchievementDescriptions.put("wurstfinger",
                new String[]{"Wurschtfinger", "Storniere 5 Getränkbestellungen!"});
        alkchievementDescriptions.put("sprengmeister",
                new String[]{"Sprengmeister", "Sprenge ein rotes Fass!"});
    }

    private final Map<String, Integer> alkchievementStates;
    private final DatabaseReference baseRef;

    AlkchievementsDatabase(final String person) {
        alkchievementStates = new HashMap<>(alkchievementDescriptions.size());

        baseRef = FirebaseDatabase.getInstance().getReference("people/" + person + "/achievements");

        FirebaseManager.registerPersonCallback(new ValueReadCallback<Map<String, ValuePair[]>>() {
            @Override
            public void onCallback(Map<String, ValuePair[]> data) {
                if (data == null) return;

                if (data.get("achievements") == null) {
                    data.put("achievements", new ValuePair[0]);
                    FirebaseDatabase.getInstance()
                            .getReference("people/" + person + "/achievements/nullKommaNull").setValue(0);
                }

                for (ValuePair valuePair : data.get("achievements")) {
                    alkchievementStates.put(valuePair.key,
                            Integer.valueOf(String.valueOf(valuePair.value)));
                }

                //Self-repair
                for (String alkchievement : alkchievementDescriptions.keySet()) {
                    if (!alkchievementStates.containsKey(alkchievement)) {
                        alkchievementStates.put(alkchievement, 0);
                        baseRef.child(alkchievement).setValue(0);
                    }
                }
            }
        }, null);

        currentInstance = this;
    }

    static AlkchievementsDatabase getInstance() {
        return currentInstance;
    }

    int getState(String alkchievement) {
        Integer re = alkchievementStates.get(alkchievement);
        return re != null ? re : 0;
    }

    void setState(String alkchievement, int state) {
        alkchievementStates.put(alkchievement, state);
        baseRef.child(alkchievement).setValue(state);
    }

    void addToState(@SuppressWarnings("SameParameterValue") String alkchievement, int deltaState) {
        int newState = getState(alkchievement) + deltaState;

        alkchievementStates.put(alkchievement, newState);
        baseRef.child(alkchievement).setValue(newState);
    }

    boolean isReady() {
        return alkchievementStates.size() == alkchievementDescriptions.size();
    }

    @SuppressWarnings("unused")
    public void resetAchievementStates() {
        baseRef.removeValue();
    }

    String[] getNameAndDescription(String alkchievement) {
        String[] re = new String[2];
        String[] desc = alkchievementDescriptions.get(alkchievement);

        re[0] = desc[0];

        if (desc[1].matches("(.*?)%(.*?)%(.*?)")) {
            //-1 because array starts @ 0, achievement has to be at least 1 to show description
            int level = getState(alkchievement) - 1;
            if (level == -1) {
                re[1] = desc[1];
                return re;
            }

            String[] valueTiers = desc[1].split("%")[1].split(",");

            if (valueTiers.length <= level) level = valueTiers.length - 1;

            re[1] = desc[1].replaceAll("%(.*?)%", valueTiers[level]);
        } else re[1] = desc[1];

        return re;
    }

    Map<String, Integer> getAlkchievementStates() {
        return Collections.unmodifiableMap(alkchievementStates);
    }

    Map<String, String[]> getAlkchievementDescriptions() {
        return Collections.unmodifiableMap(alkchievementDescriptions);
    }

    String getName(String alkchievement) {
        return alkchievementDescriptions.get(alkchievement)[0];
    }
}
