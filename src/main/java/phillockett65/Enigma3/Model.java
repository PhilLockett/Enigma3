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
 * Model is the class that captures the dynamic shared data plus some 
 * supporting constants and provides access via getters and setters.
 */
package phillockett65.Enigma3;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import phillockett65.PairSelect.PairSelectControl;

public class Model {

    private final static String DATAFILE = "Settings.dat";

    public static final int ROTOR_COUNT = 4;
    public final static int FULL_COUNT = 13;
    public final static int PLUG_COUNT = 10;
    public final static int PAIR_COUNT = 12;

    private static final int OTHER = -1;
    private static final int SLOW = 0;
    private static final int LEFT = 1;
    private static final int MIDDLE = 2;
    private static final int RIGHT = 3;


    /************************************************************************
     * General support code.
     */

    private int idToIndex(String id) { return Integer.valueOf(id); }

    /**
     * @return the file path of the settings data file.
     */
    public String getSettingsFile() {
        return DATAFILE;
    }



    /************************************************************************
     * Support code for the Initialization of the Model.
     */

    /**
     * Responsible for constructing the Model and any local objects. Called by 
     * the controller.
     */
    public Model() {
        initRotorWiring();
    }


    /**
     * Called by the controller after the constructor to initialise any 
     * objects after the controls have been initialised.
     */
    public void initialize() {
        // System.out.println("Model initialized.");

        initializeReflector();
        initializeRotorSetup();
        initializePlugboardConnections();
        initializeEncipher();

        if (!DataStore.readData(this))
            defaultSettings();
    }

    /**
     * Called by the controller after the stage has been set. Completes any 
     * initialization dependent on other components being initialized.
     */
    public void init() {
        // System.out.println("Model init.");
        setRotorControlsSpacing(8);
    }

    /**
     * Set all attributes to the default values.
     */
    public void defaultSettings() {
        setReflectorChoice("Reflector B");

        setReconfigurable(false);
        reflectorControl.defaultSettings();
        syncReflector();

        setFourthWheel(false);
        setRotorState(SLOW, "IV", 0, 0);
        setRotorState(LEFT, "I", 1, 0);
        setRotorState(MIDDLE, "II", 10, 20);
        setRotorState(RIGHT, "III", 1, 25);

        setUseNumbers(false);
        setShow(false);

        plugboardControl.clear();
        syncPlugboard();

        setEncipher(false);
    }



    /************************************************************************
     * Support code for Rotor definitions.
     */

    ObservableList<RotorData> rotors = FXCollections.observableArrayList();
    ObservableList<RotorData> reflectors = FXCollections.observableArrayList();
    ObservableList<Rotor> activeRotors = FXCollections.observableArrayList();

    private static final RotorData[] rotorData = {

        new RotorData("IC",     "DMTWSILRUYQNKFEJCAZBPGXOHV",	"1924",	"Commercial Enigma A, B", "R"),
        new RotorData("IIC",    "HQZGPJTMOBLNCIFDYAWVEUSRKX",	"1924",	"Commercial Enigma A, B", "F"),
        new RotorData("IIIC",   "UQNTLSZFMREHDPXKIBVYGJCWOA",	"1924",	"Commercial Enigma A, B", "W"),

        new RotorData("I-R",    "JGDQOXUSCAMIFRVTPNEWKBLZYH",	"7 February 1941",	"German Railway (Rocket)", "R"),
        new RotorData("II-R",   "NTZPSFBOKMWRCJDIVLAEYUXHGQ",	"7 February 1941",	"German Railway (Rocket)", "F"),
        new RotorData("III-R",  "JVIUBHTCDYAKEQZPOSGXNRMWFL",	"7 February 1941",	"German Railway (Rocket)", "W"),
        new RotorData("UKW-R",  "QYHOGNECVPUZTFDJAXWMKISRBL",	"7 February 1941",	"German Railway (Rocket)", ""),
        new RotorData("ETW-R",  "QWERTZUIOASDFGHJKPYXCVBNML",	"7 February 1941",	"German Railway (Rocket)", ""),

        new RotorData("I-K",    "PEZUOHXSCVFMTBGLRINQJWAYDK",	"February 1939",	"Swiss K", "R"),
        new RotorData("II-K",   "ZOUESYDKFWPCIQXHMVBLGNJRAT",	"February 1939",	"Swiss K", "F"),
        new RotorData("III-K",  "EHRVXGAOBQUSIMZFLYNWKTPDJC",	"February 1939",	"Swiss K", "W"),
        new RotorData("UKW-K",  "IMETCGFRAYSQBZXWLHKDVUPOJN",	"February 1939",	"Swiss K", ""),
        new RotorData("ETW-K",  "QWERTZUIOASDFGHJKPYXCVBNML",	"February 1939",	"Swiss K", ""),

        new RotorData("I",      "EKMFLGDQVZNTOWYHXUSPAIBRCJ",	"1930",	"Enigma I", "R"),
        new RotorData("II",     "AJDKSIRUXBLHWTMCQGZNPYFVOE",	"1930",	"Enigma I", "F"),
        new RotorData("III",    "BDFHJLCPRTXVZNYEIWGAKMUSQO",	"1930",	"Enigma I", "W"),
        new RotorData("IV",     "ESOVPZJAYQUIRHXLNFTGKDCMWB",	"December 1938",	"M3 Army", "K"),
        new RotorData("V",      "VZBRGITYUPSDNHLXAWMJQOFECK",	"December 1938",	"M3 Army", "A"),
        new RotorData("VI",     "JPGVOUMFYQBENHZRDKASXLICTW",	"1939",	"M3 & M4 Naval (FEB 1942)", "AN"),
        new RotorData("VII",    "NZJHGRCXMYSWBOUFAIVLPEKQDT",	"1939",	"M3 & M4 Naval (FEB 1942)", "AN"),
        new RotorData("VIII",   "FKQHTLXOCBJSPDZRAMEWNIUYGV",	"1939",	"M3 & M4 Naval (FEB 1942)", "AN"),

        new RotorData("Beta",               "LEYJVCNIXWPBQMDRTAKZGFUHOS",	"Spring 1941",	"M4 R2", ""),
        new RotorData("Gamma",              "FSOKANUERHMBTIYCWLQPZXVGJD",	"Spring 1942",	"M4 R2", ""),
        new RotorData("Reflector A",        "EJMZALYXVBWFCRQUONTSPIKHGD",	"",	"", ""),
        new RotorData("Reflector B",        "YRUHQSLDPXNGOKMIEBFZCWVJAT",	"",	"", ""),
        new RotorData("Reflector C",        "FVPJIAOYEDRZXWGCTKUQSBNMHL",	"",	"", ""),
        new RotorData("Reflector B Thin",   "ENKQAUYWJICOPBLMDXZVFTHRGS",	"1940",	"M4 R1 (M3 + Thin)", ""),
        new RotorData("Reflector C Thin",   "RDOBJNTKVEHMLFCWZAXGYIPSUQ",	"1940",	"M4 R1 (M3 + Thin)", ""),
        new RotorData("ETW",                "ABCDEFGHIJKLMNOPQRSTUVWXYZ",	"",	"Enigma I", ""),

    };

    /**
     * Construct all the Rotor collections.
     * 
     * Note: for the commercial, rocket and swissK Rotors, the turnover points 
     * are guesses and may be incorrect.
     */
    private void initRotorWiring() {

        // Build list of rotors and list of reflectors that can be selected.
        for (RotorData rotor : rotorData)
            if (rotor.isReflector())
                reflectors.add(rotor);
            else
                rotors.add(rotor);

    }



    /************************************************************************
     * Support code for "Reflector Set-Up" panel.
     */

    ObservableList<String> reflectorList = FXCollections.observableArrayList();
    private String reflectorChoice;
    private boolean reconfigurable = false;
    
    private PairSelectControl reflectorControl;
    private ArrayList<String> reflectorPairs;
    private int[] reflectorMap;
    private int[] customReflectorMap;
    private Mapper reflector;

    public ObservableList<String> getReflectorList()   { return reflectorList; }
    public String getReflectorChoice()   { return reflectorChoice; }
    public void setReflectorChoice(String choice)   { reflectorChoice = choice; }

    public void setReconfigurable(boolean state) { reconfigurable = state; }
    public boolean isReconfigurable() { return reconfigurable; }

    private void syncReflector() {
        reflectorPairs = reflectorControl.getLinks();
        customReflectorMap = reflectorControl.getMap();
    }

    // Called by DataStore on start up.
    public void setPairText(ArrayList<String> links) {
        reflectorControl.setLinks(links);
        syncReflector();
    }

    public ArrayList<String> getPairText()  { return reflectorPairs; }
    public boolean hasPairText()            { return reflectorControl.hasLinks(); }

    public int getPairCount()               { return reflectorControl.size(); }
    public String getPairText(int index)	{ return reflectorControl.getText(index); }
    public int getPairCount(int index)		{ return getPairText(index).length(); }

    public boolean isPairValid(int index)   { return reflectorControl.isValid(index); }

    public String getPairText(String id)	{ return getPairText(idToIndex(id)); }
    public int getPairCount(String id)		{ return getPairCount(idToIndex(id)); }
    public boolean isPairValid(String id)	{ return isPairValid(idToIndex(id)); }

    /**
     * Determine if the reflector is valid.
     * @return true if the reflector is valid, false otherwise.
     */
    public boolean isReflectorValid() {
        if (reconfigurable)
            return reflectorControl.isValid();

        return true;
    }

    public boolean launchReflector() {
        reflectorControl.setLinks(reflectorPairs);
        if (reflectorControl.showControl()) {
            syncReflector();

            return true;
        }

        return false;
    }

    /**
     * Ascertain the active reflector map and assign to the global variable 
     * reflectorMap.
     */
    private void lockdownReflector() {
        if (reconfigurable) {
            reflectorMap = customReflectorMap;
        } else {
            RotorData rotor = getRotorData(reflectors, reflectorChoice);
            reflectorMap = rotor.getMap();
        }
    }

    /**
     * Construct the list of reflector names.
     */
    private void fillReflectorList() {
        reflectorList.clear();

        for (RotorData rotor : reflectors)
            reflectorList.add(rotor.getId());
    }

    /**
     * Initialize "Reflector Set-Up" panel.
     */
    private void initializeReflector() {
        fillReflectorList();

        reflectorControl = new PairSelectControl(false, "Configure Reflector connections");
    }



    /************************************************************************
     * Support code for "Rotor Set-Up" panel.
     */

    private ObservableList<String> wheelList = FXCollections.observableArrayList();

    private ArrayList<RotorControl> rotorControls = new ArrayList<RotorControl>(ROTOR_COUNT);

    private boolean fourthWheel = false;
    private boolean useNumbers = false;
    private boolean show = false;


    public ObservableList<String> getWheelList() { return wheelList; }

    public ArrayList<RotorControl> getRotorControls() { return rotorControls; }
    public int getRotorStateCount() { return rotorControls.size(); }

    public void setFourthWheel(boolean state) {
        fourthWheel = state;
        getState(SLOW).setDisable(!fourthWheel);
    }
    public boolean isFourthWheel() { return fourthWheel; }

    /**
     * Update useLetters and synchronise the ring setting and rotor offset 
     * Spinners.
     * @param state assigned to useLetters;
     */
    public void setUseNumbers(boolean state) {
        if (useNumbers == state)
            return;

        useNumbers = state;
        for (RotorControl rotor : rotorControls)
            rotor.setUseNumbers(useNumbers);
    }

    public boolean isUseNumbers() { return useNumbers; }
    
    public boolean isShow() { return show; }
    public void setShow(boolean state) { show = state; }


    public void setRotorState(int index, String wheelChoice, int ringIndex, int rotorIndex) { 
        getState(index).set(wheelChoice, ringIndex, rotorIndex); 
    }

    public void setTranslate(boolean selected) {
        for (RotorControl rotor : rotorControls)
            rotor.setLockDown(selected);
    }

    public void setRotorControlsSpacing(double value) {
        for (RotorControl rotor : rotorControls)
            rotor.setSpacing(value);
    }


    private RotorControl getState(int index) { return rotorControls.get(index); }

    public String getWheelChoice(int index) { return getState(index).getWheelChoice(); }
    public void setWheelChoice(int index, String choice) { getState(index).setWheelChoice(choice); }

    public int getRingIndex(int index) { return getState(index).getRingIndex(); }
    public void setRingSetting(int index, String value) { getState(index).setRingIndex(Mapper.stringToIndex(value)); }
    public void setRingIndex(int index, int value) { getState(index).setRingIndex(value); }

    public int getRotorIndex(int index) { return getState(index).getRotorIndex(); }
    public void setRotorOffset(int index, String value) { getState(index).setRotorIndex(Mapper.stringToIndex(value)); }
    public void setRotorIndex(int index, int value) { getState(index).setRotorIndex(value); }
    private void incrementRotorOffset(int index, int step) { getState(index).increment(step); }


    /**
     * Initialize "Rotor Set-Up".
     */
    private void initializeRotorSetup() {
        // Initialize wheelList.
        for (RotorData rotor : rotors)
            wheelList.add(rotor.getId());

        // Initialize "Rotor Control Set-Up".
        final String[] names = { "fourth", "left", "middle", "right" };
        for (int i = 0; i < ROTOR_COUNT; ++i) {
            rotorControls.add(new RotorControl());
            getState(i).init(names[i], wheelList);
        }
    }



    /************************************************************************
     * Support code for "Plugboard Connections" panel.
     */
    
    private PairSelectControl plugboardControl;
    private ArrayList<String> plugboardPairs;
    private int[] plugboardMap;
    private Mapper plugboard;

    private void syncPlugboard() {
        plugboardPairs = plugboardControl.getLinks();
        plugboardMap = plugboardControl.getMap();
    }

    // Called by DataStore on start up.
    public void setPlugText(ArrayList<String> links) {
        plugboardControl.setLinks(links);
        syncPlugboard();
    }

    public ArrayList<String> getPlugText()  { return plugboardPairs; }
    public boolean hasPlugText()            { return plugboardControl.hasLinks(); }

    public int getPlugCount()               { return plugboardControl.size(); }
    public String getPlugText(int index)	{ return plugboardControl.getText(index); }
    public int getPlugCount(int index)		{ return getPlugText(index).length(); }

    public boolean isPlugValid(int index)   { return plugboardControl.isValid(index); }

    public String getPlugText(String id)	{ return getPlugText(idToIndex(id)); }
    public int getPlugCount(String id)		{ return getPlugCount(idToIndex(id)); }
    public boolean isPlugValid(String id)	{ return isPlugValid(idToIndex(id)); }

    public boolean isPlugboardValid()       { return plugboardControl.isValid(); }

    public boolean launchPlugboard() {
        plugboardControl.setLinks(plugboardPairs);
        if (plugboardControl.showControl()) {
            syncPlugboard();

            return true;
        }

        return false;
    }


    /**
     * Lockdown the plugboardMap.
     */
    private void lockdownPlugboard() {
    }

    /**
     * Initialize "Plugboard Connections" panel.
     */
    private void initializePlugboardConnections() {
        plugboardControl = new PairSelectControl(true, "Select Plugboard connections");
    }



    /************************************************************************
     * Support code for "Translation" panel.
     */

    private boolean encipher = false;

    private ArrayList<Translation> pipeline = new ArrayList<Translation>(9);

    /**
     * Determine if all settings are valid which requires checking the 
     * reflector, the plugboard is always valid.
     * @return true if the settings are valid, false otherwise.
     */
    public boolean isConfigValid() {
        return isReflectorValid();
    }

    public boolean isEncipher() { return encipher; }

    /**
     * Find a Rotor with the given id in the given list,
     * @param list of Rotors to search.
     * @param target id of Rotor.
     * @return Rotor with matching id if found, null otherwise.
     */
    private RotorData getRotorData(ObservableList<RotorData> list, String target) {
        for (RotorData rotor : list)
             if (rotor.is(target))
                return rotor;

        return list.get(0);
    }


    /**
     * Advances the spinner of the right rotor then checks the other rotors. 
     * The notch point of the middle rotor is used to check for a step of the 
     * left rotor and a double step of the middle rotor. The turnover point of 
     * the right rotor is used to check for a step of the middle rotor
     */
    private void advanceRotors() {
        // Normal step of the spinner of the right rotor.
        incrementRotorOffset(RIGHT, 1);

        Rotor rotor = activeRotors.get(MIDDLE);
        if (rotor.isNotchPoint(getRotorIndex(MIDDLE))) {
            // Double step of the spinner of the middle rotor, normal step of 
            // the spinner of the left rotor.
            incrementRotorOffset(MIDDLE, 1);
            incrementRotorOffset(LEFT, 1);
        }

        rotor = activeRotors.get(RIGHT);
        if (rotor.isTurnoverPoint(getRotorIndex(RIGHT))) {
            // The right rotor takes the spinner of the middle rotor one step 
            // further.
            incrementRotorOffset(MIDDLE, 1);
        }
    }


    /**
     * Translation is a class that is used in the construction of the pipeline 
     * to maintain direction and helps manage the offsets of the Rotors.
     */
    private class Translation {
        private final int pos;
        private final Mapper mapper;
        private final int dir;

        public Translation(int id, Mapper mapper, int dir) {
            this.pos = id;
            this.mapper = mapper;
            this.dir = dir;
        }

        /**
         * Update the offset of this mapper only if target matches pos.
         * @param target position to match with this pos.
         * @param offset to set this offset to.
         * @return true if the offset is updated, false otherwise.
         */
        public boolean conditionallyUpdate(int target, int offset) {
            if (target == pos) {
                mapper.setOffset(offset);

                return true;
            }

            return false;
        }
    
        /**
         * Translates an index (numerical equivalent of the letter) to another 
         * using this directional Mapper (Rotor).
         * @param index to translate.
         * @return the translated index.
         */
        public int translate(int index) {
            return mapper.swap(dir, index, isShow());
        }	

    }

    /**
     * Translates an index (numerical equivalent of the letter) to another for 
     * every Mapper in the pipeline.
     * @param index to translate.
     * @return the translated index.
     */
    private int translatePipeline(int index) {
        if (isShow())
            System.out.print("Key: " + Mapper.indexToLetter(index) + "  ");

        for (Translation translator : pipeline)
            index = translator.translate(index);

        if (isShow())
            System.out.println("Lamp: " + Mapper.indexToLetter(index));

        return index;
    }

    /**
     * Advance the Rotor Spinners then update the Rotors to match.
     */
    private void updatePipeline() {
        advanceRotors();

        for (int i = 0; i < ROTOR_COUNT; ++i) {
            int offset = getRotorIndex(i);

            for (Translation translator : pipeline)
                translator.conditionallyUpdate(i, offset);
        }
    }

    /**
     * Advance the Rotors and translate an index (numerical equivalent of the 
     * letter) through the pipeline.
     * @param index to translate.
     * @return the translated index.
     */
    public int translate(int index) {
        updatePipeline();
        return translatePipeline(index);
    }

    /**
     * Build the pipeline of Mappers (Rotors) including the identifiers for 
     * offset updates and the direction of translation.
     */
    private void buildPipeline() {

        pipeline.clear();

        Rotor slow = activeRotors.get(SLOW);

        Rotor left = activeRotors.get(LEFT);
        Rotor middle = activeRotors.get(MIDDLE);
        Rotor right = activeRotors.get(RIGHT);

        pipeline.add(new Translation(OTHER, plugboard, Mapper.RIGHT_TO_LEFT));

        pipeline.add(new Translation(RIGHT, right, Mapper.RIGHT_TO_LEFT));
        pipeline.add(new Translation(MIDDLE, middle, Mapper.RIGHT_TO_LEFT));
        pipeline.add(new Translation(LEFT, left, Mapper.RIGHT_TO_LEFT));

        if (fourthWheel)
            pipeline.add(new Translation(SLOW, slow, Mapper.RIGHT_TO_LEFT));

        pipeline.add(new Translation(OTHER, reflector, Mapper.RIGHT_TO_LEFT));

        if (fourthWheel)
            pipeline.add(new Translation(SLOW, slow, Mapper.LEFT_TO_RIGHT));

        pipeline.add(new Translation(LEFT, left, Mapper.LEFT_TO_RIGHT));
        pipeline.add(new Translation(MIDDLE, middle, Mapper.LEFT_TO_RIGHT));
        pipeline.add(new Translation(RIGHT, right, Mapper.LEFT_TO_RIGHT));

        pipeline.add(new Translation(OTHER, plugboard, Mapper.LEFT_TO_RIGHT));
    }

    /**
     * Lockdown all the settings ready for translation. This involves building 
     * letter mappings as necessary, constructing needed Mappers, finalizing
     * ring settings and building the pipeline.
     */
    private void lockdownSettings() {
        lockdownPlugboard();
        lockdownReflector();

        plugboard = new Mapper("Plugboard", plugboardMap);
        reflector = new Mapper("Reflector", reflectorMap);

        activeRotors.clear();
        for (int i = 0; i < ROTOR_COUNT; ++i) {
            Rotor rotor = new Rotor(getRotorData(rotors, getWheelChoice(i)));
            rotor.setRingSetting(getRingIndex(i));
            activeRotors.add(rotor);

            // rotor.dumpRightMap();
            // rotor.dumpLeftMap();
        }

        buildPipeline();
    }

    /**
     * Set the encipher state and lockdown all the data if we are about to 
     * translate keys.
     * @param state
     */
    public void setEncipher(boolean state) {
        // System.out.println("setEncipher(" + state + ").");
        encipher = state;
        if (encipher)
            lockdownSettings();
    }

    /**
     * Initialize "Translation" panel.
     */
    private void initializeEncipher() {
    }



    /************************************************************************
     * Support code for debug stuff.
     */

    public void dumpRotorWiring() {
        for (RotorData rotor : rotorData)
            System.out.println(rotor.toString());
        System.out.println();
    }

    public int test1(char key) {
        updatePipeline();
        return translatePipeline(Rotor.charToIndex(key));
        // return translate(Rotor.charToIndex(key));
    }

    public int test5() {
        int output = 0;
        
        final String input = "AAAAA";
        for (int i = 0; i < input.length(); ++i)
            output = test1(input.charAt(i));

        return output;
    }

    public int test() {
        // dumpRotorWiring();
        // lockdownSettings();

        return test1('A');
        // return test5();
    }


}