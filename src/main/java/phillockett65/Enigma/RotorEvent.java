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
     * The only valid EventTypes for the SelectEvent.
     */
    public static final EventType<RotorEvent> WHEEL_CHOICE =
        new EventType<>(Event.ANY, "WHEEL_CHOICE");
    public static final EventType<RotorEvent> RING_SETTING =
        new EventType<>(Event.ANY, "RING_SETTING");
    public static final EventType<RotorEvent> ROTOR_OFFSET =
        new EventType<>(Event.ANY, "ROTOR_OFFSET");

    /**
     * Creates a new {@code RotorEvent} with an event type of {@code ANY}.
     * The source and target of the event is set to {@code NULL_SOURCE_TARGET}.
     */
    public RotorEvent() { super(ANY); }

    /**
     * Construct a new {@code RotorEvent} with the specified event source and target.
     *
     * @param source    the event source which sent the event
     * @param target    the event target to associate with the event
     * @param type      the event type of the event
     * @param index     the id of the event
     */
    public RotorEvent(Object source, EventTarget target, EventType<RotorEvent> type, int index) {
        super(source, target, type);

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
