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
 * Pair is a class that captures a connection between two letters.
 */
package phillockett65.Enigma3;

public class Pair {
    private String letters = "";
    private boolean enabled = true;

    public void set(String text) { letters = text; }
    public void clear() { letters = ""; }

    public String get() { return letters; }
    public int count() { return enabled ? letters.length() : 0; }
    public char charAt(int index) { return letters.charAt(index); }
    public int indexAt(int index) { return Rotor.charToIndex(charAt(index)); }
    public int first() { return count() > 0 ? indexAt(0) : -1; }
    public int second() { return count() > 1 ? indexAt(1) : -1; }

    public boolean isEmpty() { return count() == 0; }
    public boolean isCharAt(int index) { return Character.isAlphabetic(charAt(index)); }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean state) { enabled = state; }

    /**
     * Check the validity of the Pair.
     * @return true if the pair is valid, false otherwise.
     */
    public boolean isValid() {
        if (count() != 2)
            return false;

        if (!isCharAt(0))
            return false;

        if (!isCharAt(1))
            return false;

        if (charAt(0) == charAt(1))
            return false;

        return true;
    }

    /**
     * Perform clean up on the pair.
     */
    public void sanitize() {
        letters = letters.toUpperCase();
    }
}
