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
package phillockett65.Enigma;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import phillockett65.PairSelect.PairSelectControl;

public class Model {

    private final static String DATAFILE = "Settings.dat";
    private final static String CONFIGURABLE = "CONFIGURABLE";

    public static final int ROTOR_COUNT = 4;
    public final static int FULL_COUNT = 13;
    public final static int PLUG_COUNT = 10;
    public final static int PAIR_COUNT = 12;

    private static final int SLOW = 0;
    private static final int LEFT = 1;
    private static final int MIDDLE = 2;
    private static final int RIGHT = 3;
    private static final int PLUGS = 4;
    private static final int REFLECT = 5;
    private static final int MAPPER_COUNT = 6;

    private boolean defaulted = false;
    public boolean isDefaulted() { return defaulted; }
    
    private Stage stage;
    public void setMainPos(double x, double y) { stage.setX(x); stage.setY(y); }
    public double getMainXPos() { return stage.getX(); }
    public double getMainYPos() { return stage.getY(); }

    public void setReflectorPos(double x, double y) { reflectorControl.setPos(x, y); }
    public double getReflectorXPos() { return reflectorControl.getXPos(); }
    public double getReflectorYPos() { return reflectorControl.getYPos(); }

    public void setPlugboardPos(double x, double y) { plugboardControl.setPos(x, y); }
    public double getPlugboardXPos() { return plugboardControl.getXPos(); }
    public double getPlugboardYPos() { return plugboardControl.getYPos(); }


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

        initializeReflector();
        initializeRotorSetup();
        initializePlugboardConnections();
    }


    /**
     * Called by the controller after the constructor to initialise any 
     * objects after the controls have been initialised.
     */
    public void initialize() {
        // System.out.println("Model initialized.");
    }

    /**
     * Called by the controller after the stage has been set. Completes any 
     * initialization dependent on other components being initialized.
     */
    public void init(Stage stage) {
        // System.out.println("Model init.");
        this.stage = stage;
        if (!DataStore.readData(this))
            defaultSettings();

        initializeEncipher();

        // Initialize "Rotor Control" listeners that fire rotor change events.
        for (RotorControl rotor : rotorControls) {
            rotor.initListeners();
        }
        updatePipelineReflector();
    }

    /**
     * Set all attributes to the default values.
     */
    public void defaultSettings() {
        defaulted = true;

        initReflectorChoice("Reflector B");

        reflectorControl.defaultSettings();

        initFourthWheel(false);
        setRotorState(SLOW, "I", 0, 0);
        setRotorState(LEFT, "IV", 14, 11);
        setRotorState(MIDDLE, "II", 22, 18);
        setRotorState(RIGHT, "V", 25, 0);

        setUseNumbers(false);
        setShow(false);

        plugboardControl.clear();
    }



    /************************************************************************
     * Support code for Rotor definitions.
     */

    private ObservableList<RotorData> rotors = FXCollections.observableArrayList();
    private ObservableList<RotorData> reflectors = FXCollections.observableArrayList();

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
        for (RotorData rotor : rotorData) {

            if (rotor.isReflector()) {
                reflectors.add(rotor);
                reflectorList.add(rotor.getId());
            } else {
                rotors.add(rotor);
                wheelList.add(rotor.getId());
            }
        }

        reflectorList.add(CONFIGURABLE);
    }



    /************************************************************************
     * Support code for "Reflector Set-Up" panel.
     */

    private ObservableList<String> reflectorList = FXCollections.observableArrayList();
    private String reflectorChoice;
    private boolean reconfigurable = false;
    
    private PairSelectControl reflectorControl;

    public ObservableList<String> getReflectorList()   { return reflectorList; }
    public String getReflectorChoice()   { return reflectorChoice; }
    public void initReflectorChoice(String choice)   {
        reflectorChoice = choice;
        reconfigurable = CONFIGURABLE.equals(reflectorChoice);
    }

    public void setReflectorChoice(String choice)   {
        initReflectorChoice(choice);
        updatePipelineReflector();
    }

    public Mapper buildNewReconfigurable() {
        int[] reflectorMap;

        if (reconfigurable) {
            reflectorMap = reflectorControl.getMap();
        } else {
            RotorData rotor = getRotorData(reflectors, reflectorChoice);
            reflectorMap = rotor.getMap();
        }

        return new Mapper("Reflector", reflectorMap);
    }

    public void updatePipelineReflector() {
        updatePipeline(REFLECT, buildNewReconfigurable(), Mapper.RIGHT_TO_LEFT);
    }

    public boolean isReconfigurable() { return reconfigurable; }

    // Called by DataStore on start up.
    public void initPairText(ArrayList<String> links) {
        reflectorControl.setLinks(links);
    }

    public ArrayList<String> getPairText()  { return reflectorControl.getLinks(); }
    public boolean hasPairText()            { return reflectorControl.hasLinks(); }

    public int getPairCount()               { return reflectorControl.size(); }
    public String getPairText(int index)	{ return reflectorControl.getText(index); }
    public int getPairCount(int index)		{ return getPairText(index).length(); }

    public boolean isPairValid(int index)   { return reflectorControl.isValid(index); }

    public String getPairText(String id)	{ return getPairText(idToIndex(id)); }
    public int getPairCount(String id)		{ return getPairCount(idToIndex(id)); }
    public boolean isPairValid(String id)	{ return isPairValid(idToIndex(id)); }

    public boolean launchReflector() {
        if (reflectorControl.showControl()) {
            updatePipelineReflector();

            return true;
        }

        return false;
    }

    /**
     * Initialize "Reflector Set-Up" panel.
     */
    private void initializeReflector() {
        reflectorControl = new PairSelectControl(false, "Configure Reflector connections");
    }



    /************************************************************************
     * Support code for "Rotor Set-Up" panel.
     */

    private ObservableList<String> wheelList = FXCollections.observableArrayList();

    private ArrayList<RotorControl> rotorControls = new ArrayList<RotorControl>(ROTOR_COUNT);

    private boolean fourthWheel = false;
    private boolean useNumbers = false;


    public ObservableList<String> getWheelList() { return wheelList; }

    public ArrayList<RotorControl> getRotorControls() { return rotorControls; }
    public int getRotorStateCount() { return rotorControls.size(); }

    public void initFourthWheel(boolean state) {
        fourthWheel = state;
        getState(SLOW).setDisable(!fourthWheel);
    }

    public void setFourthWheel(boolean state) {
        initFourthWheel(state);

        setPipelineItemActive(SLOW, Mapper.RIGHT_TO_LEFT, fourthWheel);
        setPipelineItemActive(SLOW, Mapper.LEFT_TO_RIGHT, fourthWheel);
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
    

    public void setRotorState(int index, String wheelChoice, int ringIndex, int rotorIndex) { 
        getState(index).set(wheelChoice, ringIndex, rotorIndex); 
    }

    public void setTranslate(boolean selected) {
        for (RotorControl rotor : rotorControls)
            rotor.setLockDown(selected);
    }


    private RotorControl getState(int index) { return rotorControls.get(index); }

    public String getWheelChoice(int index) { return getState(index).getWheelChoice(); }

    public int getRingIndex(int index) { return getState(index).getRingIndex(); }

    public int getRotorIndex(int index) { return getState(index).getRotorIndex(); }
    private void incrementRotorOffset(int index, int step) { getState(index).increment(step); }


    /**
     * Initialize "Rotor Set-Up".
     */
    private void initializeRotorSetup() {
        // Initialize "Rotor Control Set-Up".
        for (int i = 0; i < ROTOR_COUNT; ++i) {
            RotorControl rotorControl = new RotorControl();
            rotorControl.init(i, wheelList);
            rotorControl.setSpacing(8);

            rotorControl.addEventHandler(RotorEvent.WHEEL_CHOICE, 
                new EventHandler<RotorEvent>() {
                    @Override public void handle(RotorEvent event) {
                        final int id = event.getId();
                        updatePipeline(id);
                        // System.out.println("WHEEL_CHOICE[" + id + "] = " + getWheelChoice(id));
                    }
            });

            rotorControl.addEventHandler(RotorEvent.RING_SETTING, 
                new EventHandler<RotorEvent>() {
                    @Override public void handle(RotorEvent event) {
                        final int id = event.getId();
                        updatePipeline(id);
                        // System.out.println("RING_SETTING[" + id + "] = " + getRingIndex(id));
                    }
            });

            rotorControls.add(rotorControl);
        }
    }



    /************************************************************************
     * Support code for "Plugboard Connections" panel.
     */
    
    private PairSelectControl plugboardControl;

    private Mapper buildNewPlugboard() {
        int[] plugboardMap = plugboardControl.getMap();
        return new Mapper("Plugboard", plugboardMap);
    }

    // Called by DataStore on start up.
    public void initPlugText(ArrayList<String> links) {
        plugboardControl.setLinks(links);
    }

    public ArrayList<String> getPlugText()  { return plugboardControl.getLinks(); }
    public boolean hasPlugText()            { return plugboardControl.hasLinks(); }

    public int getPlugCount()               { return plugboardControl.size(); }
    public String getPlugText(int index)	{ return plugboardControl.getText(index); }
    public int getPlugCount(int index)		{ return getPlugText(index).length(); }

    public boolean isPlugValid(int index)   { return plugboardControl.isValid(index); }

    public String getPlugText(String id)	{ return getPlugText(idToIndex(id)); }
    public int getPlugCount(String id)		{ return getPlugCount(idToIndex(id)); }
    public boolean isPlugValid(String id)	{ return isPlugValid(idToIndex(id)); }

    public boolean launchPlugboard() {
        if (plugboardControl.showControl()) {
            updatePipeline(PLUGS, buildNewPlugboard());

            return true;
        }

        return false;
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

    private boolean show = false;

    public boolean isShow() { return show; }
    public void setShow(boolean state) { show = state; }


    private ArrayList<Translation> pipeline = new ArrayList<Translation>(9);

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

        Rotor rotor = getRotorFromPipeline(MIDDLE);
        if (rotor.isNotchPoint(getRotorIndex(MIDDLE))) {
            // Double step of the spinner of the middle rotor, normal step of 
            // the spinner of the left rotor.
            incrementRotorOffset(MIDDLE, 1);
            incrementRotorOffset(LEFT, 1);
        }

        rotor = getRotorFromPipeline(RIGHT);
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
        private Mapper mapper;
        private final int dir;
        private boolean active;

        public Translation(int id, Mapper mapper, int dir) {
            this.pos = id;
            this.mapper = mapper;
            this.dir = dir;
            this.active = true;
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
            if (!active)
                return index;

            return mapper.swap(dir, index, isShow());
        }

        public void setActive(boolean active) { this.active = active; }
        public void setMapper(Mapper mapper) { this.mapper = mapper; }
        public Mapper getMapper() { return mapper; }

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
            final int offset = getRotorIndex(i);

            for (Translation translator : pipeline)
                if (translator.conditionallyUpdate(i, offset))
                    break;
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

    private void addToPipeline(int id, Mapper mapper, int dir) {
        setPipelineIndex(id, dir, pipeline.size());

        pipeline.add(new Translation(id, mapper, dir));
    }

    private void updatePipeline(int id, Mapper mapper, int dir) {
        pipeline.get(getPipelineIndex(id, dir)).setMapper(mapper);
    }

    private void updatePipeline(int id, Mapper mapper) {
        updatePipeline(id, mapper, Mapper.RIGHT_TO_LEFT);
        updatePipeline(id, mapper, Mapper.LEFT_TO_RIGHT);
    }

    private void updatePipeline(int id) {
        Rotor rotor = buildNewRotor(id);
        updatePipeline(id, rotor);
    }

    private Rotor buildNewRotor(int id) {
        return new Rotor(getRotorData(rotors, getWheelChoice(id)), getRingIndex(id));
    }

    private Rotor getRotorFromPipeline(int id) {
        return (Rotor)pipeline.get(getPipelineIndex(id, Mapper.RIGHT_TO_LEFT)).getMapper();
    }

    /**
     * Build the pipeline of Mappers (Rotors) including the identifiers for 
     * offset updates and the direction of translation.
     */
    private void buildPipeline() {

        // Build all the Mappers.
        Mapper plugboard = buildNewPlugboard();
        Mapper reflector = buildNewReconfigurable();

        Rotor slow = buildNewRotor(SLOW);

        Rotor left = buildNewRotor(LEFT);
        Rotor middle = buildNewRotor(MIDDLE);
        Rotor right = buildNewRotor(RIGHT);

        // Add the Mappers to the pipeline and log the index.
        addToPipeline(PLUGS, plugboard, Mapper.RIGHT_TO_LEFT);

        addToPipeline(RIGHT, right, Mapper.RIGHT_TO_LEFT);
        addToPipeline(MIDDLE, middle, Mapper.RIGHT_TO_LEFT);
        addToPipeline(LEFT, left, Mapper.RIGHT_TO_LEFT);
        addToPipeline(SLOW, slow, Mapper.RIGHT_TO_LEFT);

        addToPipeline(REFLECT, reflector, Mapper.RIGHT_TO_LEFT);

        addToPipeline(SLOW, slow, Mapper.LEFT_TO_RIGHT);
        addToPipeline(LEFT, left, Mapper.LEFT_TO_RIGHT);
        addToPipeline(MIDDLE, middle, Mapper.LEFT_TO_RIGHT);
        addToPipeline(RIGHT, right, Mapper.LEFT_TO_RIGHT);

        addToPipeline(PLUGS, plugboard, Mapper.LEFT_TO_RIGHT);

        // Enable / disable the Fourth rotor as requested.
        setPipelineItemActive(SLOW, Mapper.RIGHT_TO_LEFT, fourthWheel);
        setPipelineItemActive(SLOW, Mapper.LEFT_TO_RIGHT, fourthWheel);
    }


    private class Couple {
        private int first = 0;
        private int second = 0;

        public int getFirst() { return first; }
        public int getSecond() { return second; }

        public void setFirst(int value) { first = value; }
        public void setSecond(int value) { second = value; }
    };

    private ArrayList<Couple> pipelineIndices = new ArrayList<Couple>();

    private int getPipelineIndex(int id, int dir) {
        if (dir == Mapper.RIGHT_TO_LEFT)
            return pipelineIndices.get(id).getFirst();
            
        return pipelineIndices.get(id).getSecond();
    }

    private void setPipelineIndex(int id, int dir, int val) {
        if (dir == Mapper.RIGHT_TO_LEFT)
            pipelineIndices.get(id).setFirst(val);
        else
            pipelineIndices.get(id).setSecond(val);
    }

    private void setPipelineItemActive(int id, int dir, boolean val) {
        pipeline.get(getPipelineIndex(id, dir)).setActive(val);
    }


    /**
     * Initialize "Translation" panel.
     */
    private void initializeEncipher() {
        for (int i = 0; i < MAPPER_COUNT; ++i) {
            pipelineIndices.add(new Couple());
        }

        buildPipeline();
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