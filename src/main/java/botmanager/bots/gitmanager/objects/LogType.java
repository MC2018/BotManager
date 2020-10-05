package botmanager.bots.gitmanager.objects;

import java.util.ArrayList;

public enum LogType {

    ART("art", "Art"),
    STORY("pencil", "Story"),
    MUSIC("musical_note", "Music"),
    DEVELOPMENT("keyboard", "Development"),
    UNDECIDED_OTHER("shrug", "Undecided/Other");

    private String emoteName;
    private String name;

    LogType(String emoteName, String name) {
        this.emoteName = emoteName;
        this.name = name;
    }

    public String getEmoteName() {
        return emoteName;
    }

    public String getName() {
        return name;
    }

    public static ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList();

        for (LogType logType : LogType.values()) {
            names.add(logType.getName());
        }

        return names;
    }

    public static ArrayList<String> getEmoteNames() {
        ArrayList<String> emoteNames = new ArrayList();

        for (LogType logType : LogType.values()) {
            emoteNames.add(logType.getEmoteName());
        }

        return emoteNames;
    }

    public static LogType fromEmoteName(String emoteName) {
        for (LogType logType : LogType.values()) {
            if (logType.getEmoteName().equalsIgnoreCase(emoteName)) {
                return logType;
            }
        }

        return null;
    }

}
