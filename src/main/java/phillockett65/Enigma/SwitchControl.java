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

/*
 * Implementation of a toggle switch custom control. 
 */
package phillockett65.Enigma;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

public class SwitchControl extends HBox {

    private static final String STYLESHEET = "SwitchControl.css";
    private static final String STYLE = "myswitch-selected";

    private int id = 0;
    private boolean state = false;

    private Rectangle thumb;
    private Label switchLabel;

    // Dimensions for the track and thumb.
    private final double h = 22.0;
    private final double w = h * 2.0;
    private final double b = 2.0;
    private final double s = h - (2.0 * b);
    private final double x0 = b;
    private final double x1 = w - (b + s);
    private final double y = b;


    /**
     * Use CSS to change the style of a Control.
     * @param field to adjust the appearance of.
     */
    private void setSliderStyleClass(Control field) {
        if (!state) {
            field.getStyleClass().remove(STYLE);
        } else {
            if (!field.getStyleClass().contains(STYLE))
                field.getStyleClass().add(STYLE);
        }
    }

    private void syncUI() {
        setSliderStyleClass(switchLabel);
        if (!state) {
            thumb.setX(x0);
        } else {
            thumb.setX(x1);
        }
    }

    private void toggleSwitch() {
        state = !state;
        syncUI();
        fireEvent(new SwitchEvent(SwitchEvent.SWITCH_TOGGLED, id, state));
    }


    /************************************************************************
     * Public interface.
     */

    public void setTooltip(String tip) {
        switchLabel.setTooltip(new Tooltip(tip));
    }

    public void setSelected(boolean newState) {
        if (newState == state) {
            return;
        }

        toggleSwitch();
    }

    public boolean isSelected() { return state; }

    public void setIdent(int ident) { id = ident; }
    public int getIdent() { return id; }


    /**
     * Constructor.
     */
    public SwitchControl(String label) {
        super();

        this.setAlignment(Pos.CENTER_LEFT);

        // Build the track.
        Rectangle track = new Rectangle(0, 0, w, h);
        Stop[] trackStops = new Stop[]{
            new Stop(0, Color.web("#444")),
            new Stop(1, Color.web("#888"))
        };
        LinearGradient trackLG = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, trackStops);

        track.setStrokeWidth(2.0);
        track.setStroke(Color.SILVER);
        track.setFill(trackLG);
        track.setArcWidth(h);
        track.setArcHeight(h);

        // Build the thumb.
        thumb = new Rectangle(x0, y, s, s);
        Stop[] thumbStops = new Stop[]{
            new Stop(0, Color.web("#fff")),
            new Stop(1, Color.web("#999"))
        };
        RadialGradient thumbRG = new RadialGradient(0, 0, 0.5, 0.25, 0.5, true, CycleMethod.NO_CYCLE, thumbStops);

        thumb.setFill(thumbRG);
        thumb.setArcWidth(s);
        thumb.setArcHeight(s);

        // Group track and thumb together.
        Group group = new Group(track, thumb);
        group.setOnMouseClicked(event -> toggleSwitch());

        // Build the label.
        switchLabel = new Label(" " + label);
        switchLabel.setOnMouseClicked(event -> toggleSwitch());
        switchLabel.getStylesheets().add(App.class.getResource(STYLESHEET).toExternalForm());

        this.getChildren().addAll(group, switchLabel);

        syncUI();
    }



    /************************************************************************
     * SwitchControl Event class.
     */

    public static class SwitchEvent extends Event {

        private static final long serialVersionUID = 202411222117L;

        /**
         * The only valid EventTypes for the SwitchEvent.
         */
        public static final EventType<SwitchEvent> SWITCH_BASE =
            new EventType<>(Event.ANY, "SWITCH_BASE");
        public static final EventType<SwitchEvent> ANY = SWITCH_BASE;
        public static final EventType<SwitchEvent> SWITCH_TOGGLED =
            new EventType<>(SwitchEvent.ANY, "SWITCH_TOGGLED");

        private final int id;
        private final boolean on;

        public int getIdent() { return id; }
        public boolean isOn() { return on; }

        /**
         * Creates a new {@code SwitchEvent} with an event type of {@code ANY}.
         * The source and target of the event is set to {@code NULL_SOURCE_TARGET}.
         */
        public SwitchEvent() { super(ANY); id = 0; on = false; }

        /**
         * Construct a new {@code SwitchEvent} with the specified event type and 
         * selected colour.
         * The source and target of the event are set to {@code NULL_SOURCE_TARGET}.
         *
         * @param eventType this event represents.
         * @param ident     of the source of this event.
         * @param state     of selection.
         */
        public SwitchEvent(EventType<? extends Event> eventType, int ident, boolean state) {
            super(eventType);
            id = ident;
            on = state;
        }

        @Override
        public SwitchEvent copyFor(Object newSource, EventTarget newTarget) {
            return (SwitchEvent) super.copyFor(newSource, newTarget);
        }

        @SuppressWarnings("unchecked")
        @Override
        public EventType<? extends SwitchEvent> getEventType() {
            return (EventType<? extends SwitchEvent>) super.getEventType();
        }
    }

}
