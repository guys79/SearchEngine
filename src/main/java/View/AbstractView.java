package View;

import javafx.fxml.Initializable;

/**
 * This class represents an abstract view in this project
 */
public abstract class AbstractView implements Initializable {
    protected ViewChanger viewChanger;//The ViewChanger

    /**
     * This fucntion will set the ViewChanger
     * @param viewChanger - The given ViewChanger
     */
    public void setViewChanger(ViewChanger viewChanger )
    {
        this.viewChanger = viewChanger;
    }
}
