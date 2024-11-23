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
package phillockett65.PairSelect;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 * @author Phil
 */
class PairEvent extends Event {

    private static final long serialVersionUID = 202408191851L;

    /**
     * The only valid EventTypes for the SelectEvent.
     */
    public static final EventType<PairEvent> SELECT_LINK =
        new EventType<>(Event.ANY, "SELECT_LINK");
    public static final EventType<PairEvent> ANY = SELECT_LINK;

    public static final EventType<PairEvent> LINK_CHANGE =
        new EventType<>(PairEvent.ANY, "LINK_CHANGE");

    /**
     * Creates a new {@code SelectEvent} with an event type of {@code ANY}.
     * The source and target of the event is set to {@code NULL_SOURCE_TARGET}.
     */
    public PairEvent() { super(ANY); }

    /**
     * Construct a new {@code SelectEvent} with the specified event type.
     *
     * @param eventType this event represents.
     */
    public PairEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }


    @Override
    public PairEvent copyFor(Object newSource, EventTarget newTarget) {
        return (PairEvent) super.copyFor(newSource, newTarget);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<? extends PairEvent> getEventType() {
        return (EventType<? extends PairEvent>) super.getEventType();
    }

}
