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
 * PrimaryController is the class that is responsible for centralizing control.
 * It is instantiated by the FXML loader creates the Model.
 * 
 * Each handler should always update the model with the changed data first, 
 * then act on the new state of the model.
 * 
 * The order of the code should match the order of the layout when possible.
 */
package phillockett65.Enigma3;

import io.github.palexdev.materialfx.controls.MFXToggleButton;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

public class PrimaryController {

    private Model model;



    /************************************************************************
     * General support code.
     */

    private static final String TOPBARICON = "top-bar-icon";

    private final double cancelStroke = 2.5;



    /************************************************************************
     * Support code for the Initialization of the Controller.
     */

    private Stage stage;


    /**
     * Responsible for constructing the Model and any local objects. Called by 
     * the FXMLLoader().
     */
    public PrimaryController() {
        // System.out.println("PrimaryController constructed.");
        model = new Model();
    }

    /**
     * Called by the FXML mechanism to initialize the controller. Called after 
     * the constructor to initialise all the controls.
     */
    @FXML public void initialize() {
        // System.out.println("PrimaryController initialized.");
        model.initialize();

        initializeTopBar();
        initializeReflector();
        initializeRotorSetup();
        initializePlugboardConnections();
        initializeEncipher();
    }

    /**
     * Called by Application after the stage has been set. Completes any 
     * initialization dependent on other components being initialized.
     */
    public void init(Stage stage) {
        // System.out.println("PrimaryController init.");
        this.stage = stage;
        model.init();
        syncUI();
    }

    /**
     * Called by Application on shutdown.
     */
    public void saveState() {
        DataStore.writeData(model);
    }

    /**
     * Synchronise all controls with the model. This should be the last step 
     * in the initialisation.
     */
    public void syncUI() {
        reflectorChoicebox.setValue(model.getReflectorChoice());
        reconfigurableCheckbox.setSelected(model.isReconfigurable());

        for (int i = 0; i < pairs.size(); ++i) {
            TextField pair = pairs.get(i);
            pair.setText(model.getPairText(i));
        }

        fourthWheelCheckbox.setSelected(model.isFourthWheel());
        useNumbersCheckbox.setSelected(model.isUseNumbers());
        showStepsCheckbox.setSelected(model.isShow());

        for (int i = 0; i < plugs.size(); ++i) {
            TextField plug = plugs.get(i);
            plug.setText(model.getPlugText(i));
        }
    }



    /************************************************************************
     * Support code for "Top Bar" panel.
     */

    private final double iconSize = 28.0;
    private double x = 0.0;
    private double y = 0.0;
    private Pane cancel;

    @FXML
    private HBox topBar;

    @FXML
    private Label headingLabel;

    @FXML
    void topBarOnMousePressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    void topBarOnMouseDragged(MouseEvent event) {
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }
 
 
    private void buildCancel() {
        cancel = new Pane();
        cancel.setPrefWidth(iconSize);
        cancel.setPrefHeight(iconSize);
        cancel.getStyleClass().add(TOPBARICON);

        double centre = iconSize / 2;
        double radius = centre * 0.7;

        Circle cancelCircle = new Circle(centre, centre, radius);
        cancelCircle.setFill(Color.TRANSPARENT);
        cancelCircle.getStyleClass().add(TOPBARICON);
        cancelCircle.setStrokeWidth(cancelStroke);

        double length = radius * 0.6;
        double a = centre + length;
        double b = centre - length;
        Line cancelLine = new Line(centre, a, centre, b);
        cancelLine.getStyleClass().add(TOPBARICON);
        cancelLine.setStrokeWidth(cancelStroke);
        cancelLine.setStrokeLineCap(StrokeLineCap.ROUND);

        cancel.getChildren().addAll(cancelCircle, cancelLine);

        cancel.setOnMouseClicked(event -> {
            stage.close();
        });

    }


    /**
     * Initialize "Reflector" panel.
     */
    private void initializeTopBar() {
        buildCancel();
        topBar.getChildren().add(cancel);
    }
  

     /************************************************************************
     * Support code for "Reflector Set-Up" panel.
     */

    @FXML
    private TitledPane reflectorSetUpTitledPane;
  
    @FXML
    private ChoiceBox<String> reflectorChoicebox;

    @FXML
    private MFXToggleButton reconfigurableCheckbox;

    @FXML
    private Button reflectorButton;

    @FXML
    void reconfigurableCheckboxActionPerformed(ActionEvent event) {
        model.setReconfigurable(reconfigurableCheckbox.isSelected());
        setReconfigurable();
    }

    @FXML
    private TextField pair0;

    @FXML
    private TextField pair1;

    @FXML
    private TextField pair2;

    @FXML
    private TextField pair3;

    @FXML
    private TextField pair4;

    @FXML
    private TextField pair5;

    @FXML
    private TextField pair6;

    @FXML
    private TextField pair7;

    @FXML
    private TextField pair8;

    @FXML
    private TextField pair9;

    @FXML
    private TextField pair10;

    @FXML
    private TextField pair11;

    private ArrayList<TextField> pairs = new ArrayList<TextField>(Model.PAIR_COUNT);

    private void displayPairs() {
        final int max = model.getPairCount();
        for (int index = 0; index < max; ++index) {
            TextField pair = pairs.get(index);
            pair.setText(model.getPairText(index));
        }
    }

    @FXML
    void reflectorButtonOnAction(ActionEvent event) {
        if (model.launchReflector()) {
            displayPairs();
        }
    }


    /**
     * Switch between being able to select a hard-wired reflector or set-up a 
     * reconfigurable reflector depending on isReconfigurable().
     */
    private void setReconfigurable() {
        final boolean reconfigurable = model.isReconfigurable();

        reflectorChoicebox.setDisable(reconfigurable);
        reflectorButton.setDisable(!reconfigurable);
        for (TextField field : pairs) {
            field.setDisable(!reconfigurable);
        }
    }

    /**
     * Initialize "Reflector" panel.
     */
    private void initializeReflector() {
        reflectorSetUpTitledPane.setTooltip(new Tooltip("Select which Reflector (reversing drum) to use"));
        reconfigurableCheckbox.setSelected(model.isReconfigurable());
        reconfigurableCheckbox.setTooltip(new Tooltip("Select to set up and use a reconfigurable Reflector"));

        reflectorChoicebox.setItems(model.getReflectorList());
        reflectorChoicebox.setTooltip(new Tooltip("Select a Reflector"));

        reflectorChoicebox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
            model.setReflectorChoice(newValue);
        });

        reflectorButton.setTooltip(new Tooltip("Edit the reconfigurable Reflector"));

        pairs.add(pair0);
        pairs.add(pair1);
        pairs.add(pair2);
        pairs.add(pair3);
        pairs.add(pair4);
        pairs.add(pair5);
        pairs.add(pair6);
        pairs.add(pair7);
        pairs.add(pair8);
        pairs.add(pair9);
        pairs.add(pair10);
        pairs.add(pair11);

        for (int i = 0; i < pairs.size(); ++i) {
            String id = String.valueOf(i);
            TextField pair = pairs.get(i);
            pair.setId(id);         // Use id as an index.
            pair.setEditable(false);
            pair.setText(model.getPairText(i));
        }

        setReconfigurable();
    }



    /************************************************************************
     * Support code for "Rotor Set-Up" panel.
     */

    @FXML
    private TitledPane rotorSetUpTitledPane;
 
    @FXML
    private HBox rotorSetUpHBox;

    @FXML
    private MFXToggleButton fourthWheelCheckbox;

    @FXML
    private MFXToggleButton useNumbersCheckbox;

    @FXML
    void fourthWheelCheckboxActionPerformed(ActionEvent event) {
        model.setFourthWheel(fourthWheelCheckbox.isSelected());
    }

    @FXML
    void useNumbersCheckboxActionPerformed(ActionEvent event) {
        model.setUseNumbers(useNumbersCheckbox.isSelected());
    }

    /**
     * Initialize "Rotor Set-Up".
     */
    private void initializeRotorSetup() {
        rotorSetUpHBox.getChildren().addAll(model.getRotorControls());

        rotorSetUpTitledPane.setTooltip(new Tooltip("Select and set up the Rotors (wheels / drums)"));
        fourthWheelCheckbox.setTooltip(new Tooltip("Select to use a fourth Rotor"));
        useNumbersCheckbox.setTooltip(new Tooltip("Select to use Numbers on the Rotors instead of Letters"));
    }



    /************************************************************************
     * Support code for "Plugboard Connections" panel.
     */

    @FXML
    private TitledPane plugboardConnectionsTitledPane;

    @FXML
    private Button plugboardButton;

    @FXML
    private TextField plug0;

    @FXML
    private TextField plug1;

    @FXML
    private TextField plug2;

    @FXML
    private TextField plug3;

    @FXML
    private TextField plug4;

    @FXML
    private TextField plug5;

    @FXML
    private TextField plug6;

    @FXML
    private TextField plug7;

    @FXML
    private TextField plug8;

    @FXML
    private TextField plug9;

    @FXML
    private TextField plug10;

    @FXML
    private TextField plug11;

    @FXML
    private TextField plug12;

    private ArrayList<TextField> plugs = new ArrayList<TextField>(Model.FULL_COUNT);


    private void displayPlugs() {
        for (TextField field : plugs) {
            field.setText("");
        }

        final int max = model.getPlugCount();
        for (int index = 0; index < max; ++index) {
            TextField plug = plugs.get(index);
            plug.setText(model.getPlugText(index));
        }
    }

    @FXML
    void plugboardButtonOnAction(ActionEvent event) {
        if (model.launchPlugboard()) {
            displayPlugs();
        }
    }

    /**
     * Initialize "Plugboard Connections" panel.
     */
    private void initializePlugboardConnections() {

        plugboardConnectionsTitledPane.setTooltip(new Tooltip("Configure the Plugboard using unique wiring pairs"));
        plugboardButton.setTooltip(new Tooltip("Edit the Plugboard"));

        plugs.add(plug0);
        plugs.add(plug1);
        plugs.add(plug2);
        plugs.add(plug3);
        plugs.add(plug4);
        plugs.add(plug5);
        plugs.add(plug6);
        plugs.add(plug7);
        plugs.add(plug8);
        plugs.add(plug9);
        plugs.add(plug10);
        plugs.add(plug11);
        plugs.add(plug12);

        for (int i = 0; i < plugs.size(); ++i) {
            String id = String.valueOf(i);
            TextField plug = plugs.get(i);
            plug.setId(id);         // Use id as an index.
            plug.setEditable(false);
            plug.setText(model.getPlugText(i));
        }
    }



    /************************************************************************
     * Support code for "Translation" panel.
     */

    private int currentKey = -1;

    @FXML
    private MFXToggleButton showStepsCheckbox;

    @FXML
    private Button resetButton;

    @FXML
    private HBox mainIO;

    @FXML
    private TextField keyIO;

    @FXML
    private Label labelIO;

    @FXML
    private TextField lampIO;

    @FXML
    void showStepsCheckboxActionPerformed(ActionEvent event) {
        model.setShow(showStepsCheckbox.isSelected());
    }

    @FXML
    void resetButtonActionPerformed(ActionEvent event) {
        model.defaultSettings();
        syncUI();
    }

    /**
     * Initialize "Translation" panel.
     */
    private void initializeEncipher() {
        showStepsCheckbox.setTooltip(new Tooltip("Select to show each translation step on the command line"));
        resetButton.setTooltip(new Tooltip("Click to return all settings to the default values"));

        final char arrow = '\u2799';
        labelIO.setText("" + arrow);
    }


    /**
     * Called by the Application when a new key is pressed.
     * @param keyCode key to be processed
     */
    public void keyPress(KeyCode keyCode) {
        if (currentKey == -1) {
            currentKey = Mapper.letterToIndex(keyCode.getChar());
            final int index = model.translate(currentKey);

            keyIO.setText(keyCode.getChar());
            lampIO.setText(Mapper.indexToLetter(index));
        }
    }

    /**
     * Called by the Application when a current key is released.
     * @param keyCode key to be processed
     */
    public void keyRelease(KeyCode keyCode) {
        final int index = Mapper.letterToIndex(keyCode.getChar());
        if (currentKey == index) {
            currentKey = -1;
        }
    }

}
