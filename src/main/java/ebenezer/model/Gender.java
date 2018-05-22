package ebenezer.model;

public enum Gender {
    MALE("Male", "M"),
    FEMALE("Female", "F"),
    OTHER("Other", "O");

    private String description;
    private String code;

    Gender(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public static Gender fromCode(String code){
        if (code == null) {
            return null;
        }

        switch (code) {
            case "M":
                return MALE;
            case "F":
                return FEMALE;
            case "O":
                return OTHER;
            default:
                return null;
        }
    }

    public static Gender fromName(String name){
        if (name == null) {
            return null;
        }

        switch (name.toLowerCase()) {
            case "male":
                return MALE;
            case "female":
                return FEMALE;
            case "other":
                return OTHER;
            default:
                return null;
        }
    }
}
