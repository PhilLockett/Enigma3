/*  Enigma3 - a JavaFX based enigma machine simulator.
 *
 *  Copyright 2024 Philip Lockett.
 *
 *  This file is part of Enigma3.
 *
 *  Enigma3 is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Enigma3 is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Enigma3.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * Rotor is a class that extends Mapper and captures the details of a rotor 
 * including the ring setting which is set at instantiation and the rotation
 * (offset) which is dynamically updated in normal use. Note, the turnover point
 * immediately follows the notch point.
 */
package phillockett65.Enigma;

import java.util.Arrays;

import javafx.util.Pair;
import phillockett65.Debug.Debug;

public class Rotor extends Mapper {

    // Debug delta used to adjust the local logging level.
    private static final int DD = 0;

    private final RotorData data;
    private int[] leftMap;
    private int[] rightMap;

    private int offset;
    private int back;       // Inverse of offset.

    private final boolean[] turnover;
    private final boolean[] notches;


    /************************************************************************
     * Initialization support code.
     */

    /**
     * Convert a String representing 1 or more turnover points into an array 
     * of flags.
     * @param turnovers String representation of the turnover points.
     * @return array of flags indicating turnover points.
     */
    private boolean[] buildTurnover(String turnovers) {
        boolean [] output = new boolean[26];

        for (int i = 0; i < output.length; ++i)
            output[i] = false;

        for (int i = 0; i < turnovers.length(); ++i) {
            final int c = charToIndex(turnovers.charAt(i));
            output[c] = true;
        }

        return output;
    }

    /**
     * Translate the turnover points to notch points which occur 1 letter 
     * before the turnover point.
     * @return array of flags indicating notch points.
     */
    private boolean[] buildNotches() {
        boolean [] output = new boolean[26];

        for (int i = 1; i < turnover.length; ++i)
            output[i - 1] = turnover[i];

        output[turnover.length - 1] = turnover[0];

        return output;
    }

    /**
     * Constructor.
     * @param rd RotorData for this Rotor.
     * @param ring setting for this instance of a Rotor.
     */
    public Rotor(RotorData rd, int ring) {
        super(rd.getId(), rd.getCipher());

        data = rd;
        rightMap = new int[26];
        leftMap = new int[26];

        setRingSetting(ring);
        setOffset(0);

        turnover = buildTurnover(rd.getTurnovers());
        notches = buildNotches();
    }


    /************************************************************************
     * Getters support code.
     */

    public boolean isTurnoverPoint(int index) { return turnover[index]; }
    public boolean isNotchPoint(int index) { return notches[index]; }

    private int leftToRight(int index) { return leftMap[index]; }
    private int rightToLeft(int index) { return rightMap[index]; }


    /************************************************************************
     * Setters support code.
     */

    public void setOffset(int value) { 
        Debug.trace(DD, "setOffset(" + getId() + " " + value + ")");
        offset = value % 26;
        back = 26 - offset;
    }

    /**
     * Translates (swaps) an index (numerical equivalent of the letter) to 
     * another using the map.
     * @param direction of mapping.
     * @param index to translate.
     * @return the translated index.
     */
    private int swapWorker(int direction, int index) {
        if (direction == RIGHT_TO_LEFT) 
            return rightToLeft(index);

        return leftToRight(index);
    }

    /**
     * Rotate an index by offset.
     * @param index (0..25) to rotate.
     * @param offset (0..25) amount to rotate.
     * @return rotated index.
     */
    private int rotate(int index, int offset) { return (index + offset) % 26; }

    /**
     * Translates (swaps) an index (numerical equivalent of the letter) to 
     * another using the map.
     * @param direction of mapping. Eg A may map to J, but J may not map to A.
     * @param index to translate.
     * @return the translated index AND the translation String.
     */
    public Pair<Integer, String> swap(int direction, int index) {
        int output = swapWorker(direction, rotate(index, offset));
        output = rotate(output, back);

        final String message = "" + getId() + "[" + indexToLetter(offset) + "](" + indexToLetter(index) + "->" + indexToLetter(output) + ")  ";

        return new Pair<Integer, String>(output, message);
    }

    /**
     * Update the left and right mappings using the map and ring setting.
     * @param index of the required ring setting.
     */
    public void setRingSetting(int index) {
        Debug.trace(DD, "setRingSetting(" + getId() + " " + index + ")");

        for (int i = 0; i < getMapLength(); ++i)
            rightMap[rotate(i, index)] = rotate(getMapItem(i), index);

        for (int i = 0; i < getMapLength(); ++i)
            leftMap[rightMap[i]] = i;
    }


    /************************************************************************
     * Debug support code.
     */

    @Override
    public String toString() {
        return "Rotor [" + 
            "id=" + getId() + 
            ", map=" + Arrays.toString(getMap()) + 
            ", name=" + data.getName() + 
            ", reflect=" + isReflector() + 
            ", offset=" + offset + 
            "]";
    }

    public void dumpFlags(boolean[] flags) {
        String output = "";
        for (int i = 0; i < flags.length; ++i) {
            output += flags[i] ? "1" : "0";
        }
        Debug.info(DD, output);
    }

    public void dumpLeftMap() { dumpMapping(leftMap); }
    public void dumpRightMap() { dumpMapping(rightMap); }
    public void dumpTurnover() { dumpFlags(turnover); }
    public void dumpNotches() { dumpFlags(notches); }

}
