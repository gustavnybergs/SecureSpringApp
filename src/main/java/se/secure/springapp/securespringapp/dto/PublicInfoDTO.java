package se.secure.springapp.securespringapp.dto;


public class PublicInfoDTO {
    private String appName;
    private String version;
    private String description;

    public PublicInfoDTO(String appName, String version, String description) {
        this.appName = appName;
        this.version = version;
        this.description = description;
    }

    public String getAppName() {
        return appName;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }
}

