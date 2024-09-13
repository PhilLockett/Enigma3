/*  CustomRotorController - a JavaFX based Custom Controller representing a Rotor.
 *
 *  Copyright 2024 Philip Lockett.
 *
 *  This file is part of CustomRotorController.
 *
 *  CustomRotorController is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CustomRotorController is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CustomRotorController.  If not, see <https://www.gnu.org/licenses/>.
 */
package phillockett65.PairSelect;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Phil
 */
public class PairSelectControl extends Stage {

    private Scene scene;

    private VBox root;
    private HBox topBar;
    private Label heading;
    private PairSelect pairSelect;
    private TextField field;
    private HBox options;

    private Button done;
    private Button clear;
    private Region region;

    private Pane cancel;
    private Line cancelLine1;
    private Line cancelLine2;
    private double cancelPadding = 0.3;
    private double iconSize = 28.0;

    private double x = 0.0;
    private double y = 0.0;

    private boolean plugboard = false;
    private boolean result = false;

    private void syncDoneButton() {
        boolean disabled = !plugboard;
        if (!plugboard) {
            disabled = pairSelect.size() < 12;
        }
        done.setDisable(disabled);
    }

    private void syncPairs() {
        field.setText(pairSelect.getPairString());
    }

    private void syncUI() {
        syncDoneButton();
        syncPairs();
    }


    private void buildCancel() {
        cancel = new Pane();
        cancel.setPrefWidth(iconSize);
        cancel.setPrefHeight(iconSize);
        cancel.getStyleClass().add("top-bar-icon");

        double a = iconSize * cancelPadding;
        double b = iconSize - a;
        cancelLine1 = new Line(a, a, b, b);
        cancelLine1.setStroke(Color.WHITE);
        cancelLine1.setStrokeWidth(4.0);
        cancelLine1.setStrokeLineCap(StrokeLineCap.ROUND);

        cancelLine2 = new Line(a, b, b, a);
        cancelLine2.setStroke(Color.WHITE);
        cancelLine2.setStrokeWidth(4.0);
        cancelLine2.setStrokeLineCap(StrokeLineCap.ROUND);

        cancel.getChildren().addAll(cancelLine1, cancelLine2);

        cancel.setOnMouseClicked(event -> {
            restoreSnapshot();
            result = false;
            close();
        });

    }

    private void loadTopBar() {
        topBar = new HBox();
        topBar.getStyleClass().add("top-bar");

        // Make window dragable.
        topBar.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        topBar.setOnMouseDragged(mouseEvent -> {
            this.setX(mouseEvent.getScreenX() - x);
            this.setY(mouseEvent.getScreenY() - y);
        });

        heading = new Label();
        region = new Region();
        buildCancel();


        topBar.getChildren().add(heading);
        topBar.getChildren().add(region);
        HBox.setHgrow(region, Priority.ALWAYS);
        topBar.getChildren().add(cancel);
    }

    public void setHeading(String title) {
        heading.setText(" " + title);
    }

    private void loadOptions() {
        options = new HBox();
        options.setSpacing(10);
        options.setPadding(new Insets(10.0));

        done = new Button("Done");
        clear = new Button("Clear");
    
        done.setOnAction(event -> {
            result = true;
            close();
        });

        clear.setOnAction(event -> {
            clear();
            syncUI();
        });

        
        options.getChildren().add(done);
        options.getChildren().add(clear);
    }


    private void init() {
        resizableProperty().setValue(false);
        // setOnCloseRequest(e -> Platform.exit());
        initStyle(StageStyle.UNDECORATED);
        initModality(Modality.APPLICATION_MODAL);

        root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(0, 1.0, 0, 0));

        loadTopBar();
        root.getChildren().add(topBar);

        pairSelect = new PairSelect(plugboard);
        root.getChildren().add(pairSelect);

        pairSelect.addEventHandler(SelectEvent.LINK_CHANGE, 
            new EventHandler<SelectEvent>() {
                @Override public void handle(SelectEvent event) {
                    syncUI();
                }
        });


        field = new TextField();
        field.setEditable(false);
        field.setFocusTraversable(false);
        root.getChildren().add(field);

        loadOptions();
        root.getChildren().add(options);

        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("PairSelect.css").toExternalForm());

        this.setScene(scene);
    }


    public ArrayList<String> getLinks() { return pairSelect.getLinks(); }
    public String getText(int index) { return pairSelect.getText(index); }
    public int size() { return pairSelect.size(); }
    public boolean hasLinks() { return pairSelect.hasPairs(); }
    public int[] getMap() { return pairSelect.getMap(); }
    public boolean isValid() { return pairSelect.isValid(); }
    public boolean isValid(int index) { return pairSelect.isValid(index); }
    public void clear() { pairSelect.clear(); }
    public void setLinks(ArrayList<String> links) { pairSelect.setLinks(links); }

    public void defaultSettings() {
        ArrayList<String> links = new ArrayList<String>();
        for (int i = 0; i  < 12; ++i) {
            final int a = i * 2;
            String pair = PairSelect.indexToLetter(a) + PairSelect.indexToLetter(a+1);
            links.add(pair);
        }
        setLinks(links);
    }

    private ArrayList<String> snapshot = new ArrayList<String>();

    private void takeSnapshot() {
        snapshot.clear();
        ArrayList<String> links = getLinks();

        for (String link : links) {
            snapshot.add(link);
        }
    }

    private void restoreSnapshot() {
        setLinks(snapshot);
    }

    /**
     * Constructor.
     */
    public PairSelectControl(boolean isPlugboard, String title) {
        super();
        plugboard = isPlugboard;
        init();

        setHeading(title);
    }


    public boolean showControl() {
        takeSnapshot();
        syncUI();
        this.showAndWait();

        return result;
    }

}
