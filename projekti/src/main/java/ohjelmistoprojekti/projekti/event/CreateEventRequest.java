package ohjelmistoprojekti.projekti.event;

public record CreateEventRequest(
		String name,
		String city,
		String venue,
		String description,
		int capacity) {
}