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
 */
package phillockett65.Enigma;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 * @author Phil
 */
class RotorEvent extends Event {

    private static final long serialVersionUID = 202409111243L;

    private int id = 0;

    public int getId() { return id; }

    /**
     * The only valid EventTypes for the RotorEvent.
     */
    public static final EventType<RotorEvent> SELECT_ROTOR =
        new EventType<>(Event.ANY, "SELECT_ROTOR");
    public static final EventType<RotorEvent> ANY = SELECT_ROTOR;

    public static final EventType<RotorEvent> WHEEL_CHOICE =
        new EventType<>(RotorEvent.ANY, "WHEEL_CHOICE");
    public static final EventType<RotorEvent> RING_SETTING =
        new EventType<>(RotorEvent.ANY, "RING_SETTING");
    public static final EventType<RotorEvent> ROTOR_OFFSET =
        new EventType<>(RotorEvent.ANY, "ROTOR_OFFSET");

    /**
     * Creates a new {@code RotorEvent} with an event type of {@code ANY}.
     * The source and target of the event is set to {@code NULL_SOURCE_TARGET}.
     */
    public RotorEvent() {
        super(ANY);
        id = 0;
    }

    /**
     * Construct a new {@code RotorEvent} with the specified event type and 
     * rotor id.
     *
     * @param eventType this event represents.
     * @param index     the id of the rotor.
     */
    public RotorEvent(EventType<? extends Event> eventType, int index) {
        super(eventType);
        id = index;
    }


    @Override
    public RotorEvent copyFor(Object newSource, EventTarget newTarget) {
        return (RotorEvent) super.copyFor(newSource, newTarget);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<? extends RotorEvent> getEventType() {
        return (EventType<? extends RotorEvent>) super.getEventType();
    }

}
