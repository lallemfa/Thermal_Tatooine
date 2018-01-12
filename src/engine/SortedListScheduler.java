package engine;

import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

public class SortedListScheduler implements IEventScheduler {

	private final ObservableList<IEvent> internalList;
	private final SortedList<IEvent> sortedList;
	
	public SortedListScheduler() {
		internalList = FXCollections.observableList(new LinkedList<IEvent>());
		sortedList = internalList.sorted();
	}
	
	@Override
	public IEvent popNextEvent() {
		IEvent event = sortedList.get(0);
		removeEvent(event);
		return event;
	}

	@Override
	public void postEvent(IEvent event) {
		internalList.add(event);
	}

	@Override
	public boolean hasNoEvent() {
		return internalList.isEmpty();
	}

	@Override
	public boolean removeEvent(IEvent event) {
		return internalList.remove(event);
	}

}
