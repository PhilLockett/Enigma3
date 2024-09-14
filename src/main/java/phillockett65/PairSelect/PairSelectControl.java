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
import javafx.scene.control.Tooltip;
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
    private Label heading;
    private PairSelect pairSelect;
    private TextField field;

    private Button done;
    private Button clear;

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


    private Pane buildCancel() {
        final double cancelPadding = 0.3;
        final double iconSize = 28.0;
    
        Pane cancel = new Pane();
        cancel.setPrefWidth(iconSize);
        cancel.setPrefHeight(iconSize);
        cancel.getStyleClass().add("top-bar-icon");

        double a = iconSize * cancelPadding;
        double b = iconSize - a;
        Line line1 = new Line(a, a, b, b);
        line1.setStroke(Color.WHITE);
        line1.setStrokeWidth(4.0);
        line1.setStrokeLineCap(StrokeLineCap.ROUND);

        Line line2 = new Line(a, b, b, a);
        line2.setStroke(Color.WHITE);
        line2.setStrokeWidth(4.0);
        line2.setStrokeLineCap(StrokeLineCap.ROUND);

        cancel.getChildren().addAll(line1, line2);

        cancel.setOnMouseClicked(event -> {
            restoreSnapshot();
            result = false;
            close();
        });

        return cancel;
    }

    private HBox buildTopBar() {
        HBox topBar = new HBox();
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
        Region region = new Region();


        topBar.getChildren().add(heading);
        topBar.getChildren().add(region);
        HBox.setHgrow(region, Priority.ALWAYS);
        topBar.getChildren().add(buildCancel());


        return topBar;
    }

    private HBox buildOptions() {
        HBox options = new HBox();
        options.setSpacing(10);
        options.setPadding(new Insets(10.0));

        done = new Button("Done");
        Region region = new Region();
        clear = new Button("Clear");
    
        done.setOnAction(event -> {
            result = true;
            close();
        });

        clear.setOnAction(event -> {
            clear();
            syncUI();
        });

        done.setTooltip(new Tooltip("Click when all pairs have been entered"));
        clear.setTooltip(new Tooltip("Click to delete all entered pairs"));

        options.getChildren().add(clear);
        options.getChildren().add(region);
        HBox.setHgrow(region, Priority.ALWAYS);
        options.getChildren().add(done);

        return options;
    }

    private HBox buildSelected() {
        HBox selection = new HBox();
        // selection.setSpacing(10);
        selection.setPadding(new Insets(0.0, 10.0, 0.0, 10.0));

        field = new TextField();
        field.setEditable(false);
        field.setFocusTraversable(false);

        HBox.setHgrow(field, Priority.ALWAYS);
        selection.getChildren().add(field);

        return selection;
    }

    private void init() {
        resizableProperty().setValue(false);
        // setOnCloseRequest(e -> Platform.exit());
        initStyle(StageStyle.UNDECORATED);
        initModality(Modality.APPLICATION_MODAL);

        root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(0, 1.0, 0, 0));


        pairSelect = new PairSelect(plugboard);

        pairSelect.addEventHandler(SelectEvent.LINK_CHANGE, 
            new EventHandler<SelectEvent>() {
                @Override public void handle(SelectEvent event) {
                    syncUI();
                }
        });


        root.getChildren().add(buildTopBar());
        root.getChildren().add(pairSelect);
        root.getChildren().add(buildSelected());
        root.getChildren().add(buildOptions());

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
    public void setHeading(String title) { heading.setText(" " + title); }

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
