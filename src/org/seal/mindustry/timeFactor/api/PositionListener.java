package org.seal.mindustry.timeFactor.api;

/**
 * Listener interface for receiving speed position change events.
 *
 * @author Seal
 * @version 1.0
 */
public interface PositionListener {

    /**
     * Called when the speed position changes.
     *
     * @param newPosition The new speed position value (range: -4 to 4, step size 1)
     */
    void onPositionChanged(int newPosition);
}