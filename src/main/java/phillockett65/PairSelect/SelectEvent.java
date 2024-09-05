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
class SelectEvent extends Event {

    private static final long serialVersionUID = 202408191851L;

    private Pair pair;

    public Pair getPair() { return pair; }

    /**
     * The only valid EventTypes for the SelectEvent.
     */
    public static final EventType<SelectEvent> LINK_ADDED =
        new EventType<>(Event.ANY, "LINK_ADDED");
    public static final EventType<SelectEvent> LINK_REMOVED =
        new EventType<>(Event.ANY, "LINK_REMOVED");

    /**
     * Creates a new {@code SelectEvent} with an event type of {@code ANY}.
     * The source and target of the event is set to {@code NULL_SOURCE_TARGET}.
     */
    public SelectEvent() { super(ANY); }

    /**
     * Construct a new {@code SelectEvent} with the specified event source and target.
     *
     * @param source    the event source which sent the event
     * @param target    the event target to associate with the event
     * @param type      the event type of the event
     */
    public SelectEvent(Object source, EventTarget target, EventType<SelectEvent> type) {
        super(source, target, type);

        pair = ((Pair)target);
    }

    @Override
    public SelectEvent copyFor(Object newSource, EventTarget newTarget) {
        return (SelectEvent) super.copyFor(newSource, newTarget);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<? extends SelectEvent> getEventType() {
        return (EventType<? extends SelectEvent>) super.getEventType();
    }

}
