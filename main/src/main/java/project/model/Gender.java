package project.model;

public enum Gender {
    Male,
    Female;

    @Override
    public String toString() {
        switch (this) {
            case Female:
                return "Kobieta";
            case Male:
                return "Mężczyzna";
            default:
                throw new IllegalStateException();
        }
    }
}
