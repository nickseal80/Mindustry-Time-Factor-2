package org.seal.mindustry.timeFactor.model;

import arc.util.Log;
import org.seal.mindustry.timeFactor.api.PositionController;
import org.seal.mindustry.timeFactor.api.PositionListener;
import org.seal.mindustry.timeFactor.util.TimeSpeedFormat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages the speed position and notifies listeners about changes.
 * Uses CopyOnWriteArrayList for thread-safe listener management.
 */
public class PositionManager implements PositionController {

    /** Current speed position. Range: -4 to 4, 0 = normal speed */
    private int position;

    /** Thread-safe list of listeners */
    private final List<PositionListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Creates PositionManager with default position 0
     */
    public PositionManager() {
        position = 0;
    }

    /**
     * Creates PositionManager with specified initial position
     * @param position Initial position value
     */
    public PositionManager(int position) {
        this.position = position;
    }

    /**
     * Gets current position
     * @return Current position value
     */
    @Override
    public int getPosition() {
        return position;
    }

    /**
     * Sets new position and notifies all listeners
     * @param newPosition New position value
     */
    @Override
    public void setPosition(int newPosition) {
        if (this.position != newPosition) {
            this.position = newPosition;
            notifyListeners();
        }
    }

    /**
     * Adds a listener to receive position change notifications
     * @param listener The listener to add
     */
    @Override
    public void addListener(PositionListener listener) {
        if (listener != null && !this.listeners.contains(listener)) {
            this.listeners.add(listener);
            Log.debug("Position listener added. Total listeners: " + listeners.size());
        }
    }

    /**
     * Removes a listener
     * @param listener The listener to remove
     */
    @Override
    public void removeListener(PositionListener listener) {
        if (listener != null) {
            this.listeners.remove(listener);
            Log.debug("Position listener removed. Total listeners: " + listeners.size());
        }
    }

    /**
     * Removes all listeners
     */
    @Override
    public void clearListeners() {
        this.listeners.clear();
        Log.debug("All position listeners cleared");
    }

    /**
     * Notifies all registered listeners about position change
     * Catches individual listener errors to prevent one faulty listener from breaking others
     */
    private void notifyListeners() {
        for (PositionListener listener : this.listeners) {
            try {
                listener.onPositionChanged(position);
            } catch (Exception e) {
                Log.err("Error in position listener:", e);
            }
        }
    }

    /**
     * Converts position to human-readable speed string
     * @return Formatted speed string (e.g., "x1", "x4", "x1/2")
     */
    @Override
    public String pos2str() {
        return TimeSpeedFormat.pos2str(position);
    }
}
