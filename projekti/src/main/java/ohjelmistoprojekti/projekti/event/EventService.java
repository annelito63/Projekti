package ohjelmistoprojekti.projekti.event;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class EventService {

	private final AtomicLong nextId = new AtomicLong(1);
	private final ConcurrentMap<Long, Event> events = new ConcurrentHashMap<>();

	public Event create(CreateEventRequest request) {
		if (request == null || request.name() == null || request.name().isBlank()) {
			throw new IllegalArgumentException("Tapahtuman nimi on pakollinen");
		}
		if (request.capacity() <= 0) {
			throw new IllegalArgumentException("Kapasiteetin pitää olla suurempi kuin nolla");
		}

		Event event = new Event(
				nextId.getAndIncrement(),
				request.name().trim(),
				request.city(),
				request.venue(),
				request.description(),
				request.capacity());
		events.put(event.id(), event);
		return event;
	}

	public List<Event> findAll() {
		return events.values().stream()
				.sorted((first, second) -> Long.compare(first.id(), second.id()))
				.toList();
	}
}