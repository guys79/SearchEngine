package View;

import javafx.fxml.Initializable;

public abstract class AbstractView implements Initializable {
    protected ViewChanger viewChanger;

    public void setViewChanger(ViewChanger viewChanger )
    {
        this.viewChanger = viewChanger;
    }
}
