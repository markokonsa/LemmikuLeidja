package ee.qualitylab.lemmikuleidja.app.objects;

/**
 * Created by Marko on 1.12.2015.
 */
public class Settings {

    boolean isHeader;
    String title;

    public Settings(boolean isHeader, String title) {
        this.isHeader = isHeader;
        this.title = title;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
