package ee.mkv.estonian.model;

public interface DiscrepancyProjection {
    Long getId(); // paradigm id

    String getInflected(); // representation of an actual inflected form that does not match suffixed
}
