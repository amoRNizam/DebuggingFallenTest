public class Config {
    public static String PREFIX_ERROR_IMG;
    public static String F_ERROR_EXT;
    public static String F_REFERENCE_EXT;

    Config() {
        PREFIX_ERROR_IMG = Utils.getProperty().get("prefixErDiffImg");
        F_ERROR_EXT = Utils.getProperty().get("errorFileExtension");
        F_REFERENCE_EXT = Utils.getProperty().get("referenceFileExtension");
    }
}
