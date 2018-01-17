package logger;

public enum LogType {

    INFO("I"),
    WARNING("W"),
    ERROR("E");

    public String flag;

    private LogType(String flag) {
        this.flag = flag;
    }
}
