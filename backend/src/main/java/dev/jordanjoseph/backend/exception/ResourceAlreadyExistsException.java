package dev.jordanjoseph.backend.exception;

public class ResourceAlreadyExistsException extends BusinessException {

    private final String resourceName;
    private final String field;
    private final String value;


    public ResourceAlreadyExistsException(String resourceName, String field, String value) {
        super(String.format("%s already exists with %s: %s", resourceName, field, value));
        this.resourceName = resourceName;
        this.field = field;
        this.value = value;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

}
