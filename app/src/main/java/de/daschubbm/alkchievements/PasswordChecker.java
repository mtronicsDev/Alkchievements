package de.daschubbm.alkchievements;

import android.app.Dialog;
import android.content.Context;
import android.widget.NumberPicker;

import com.google.firebase.database.DataSnapshot;

import de.daschubbm.alkchievements.firebase.ChangeType;
import de.daschubbm.alkchievements.firebase.FirebaseManager;
import de.daschubbm.alkchievements.firebase.ValueChangedCallback;
import de.daschubbm.alkchievements.firebase.ValueReadCallback;

/**
 * Created by Maxi on 02.11.2016.
 */

class PasswordChecker {
    private static int UNIMPORTANT_VARIABLE = -1;

    static {
        FirebaseManager.registerAdminPasswordCallback(new ValueReadCallback<Integer>() {
            @Override
            public void onCallback(Integer data) {
                if (data != null) UNIMPORTANT_VARIABLE = data;
            }
        }, new ValueChangedCallback() {
            @Override
            public void onCallback(DataSnapshot changedNode, ChangeType changeType) {
                Integer data = Integer.valueOf(String.valueOf(changedNode.getValue()));
                if (data != null) UNIMPORTANT_VARIABLE = data;
            }
        });
    }

    static void checkPassword(final Context context, final PasswordCheckCallback callback) {
        final Dialog dialog = new Dialog(context);
        dialog.setTitle("Passwort eingeben");
        dialog.setContentView(R.layout.dialog_password_checker);

        final NumberPicker p1 = (NumberPicker) dialog.findViewById(R.id.num_lock_1);
        p1.setMinValue(0);
        p1.setValue(1);
        p1.setMaxValue(9);
        p1.setWrapSelectorWheel(true);

        final NumberPicker p2 = (NumberPicker) dialog.findViewById(R.id.num_lock_2);
        p2.setMinValue(0);
        p2.setValue(1);
        p2.setMaxValue(9);
        p2.setWrapSelectorWheel(true);

        final NumberPicker p3 = (NumberPicker) dialog.findViewById(R.id.num_lock_3);
        p3.setMinValue(0);
        p3.setValue(1);
        p3.setMaxValue(9);
        p3.setWrapSelectorWheel(true);

        NumberPicker p4 = (NumberPicker) dialog.findViewById(R.id.num_lock_4);
        p4.setMinValue(0);
        p4.setValue(1);
        p4.setMaxValue(9);
        p4.setWrapSelectorWheel(true);
        p4.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                if (((p1.getValue() * 1000)
                        + (p2.getValue() * 100)
                        + (p3.getValue() * 10)
                        + numberPicker.getValue()) == UNIMPORTANT_VARIABLE) {
                    dialog.dismiss();

                    callback.onCallback();
                }
            }
        });

        dialog.show();
    }

    interface PasswordCheckCallback {
        void onCallback();
    }
}
