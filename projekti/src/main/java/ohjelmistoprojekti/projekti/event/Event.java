package ohjelmistoprojekti.projekti.event;

public record Event(
		long id,
		String name,
		String city,
		String venue,
		String description,
		int capacity) {
}