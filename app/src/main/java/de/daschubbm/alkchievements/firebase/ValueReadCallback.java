package de.daschubbm.alkchievements.firebase;

/**
 * Created by Maxi on 12.10.2016.
 */
@FunctionalInterface
public interface ValueReadCallback<T> {
    void onCallback(T data);
}
