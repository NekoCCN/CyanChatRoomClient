package cc.nekocc.cyanchatroom.util;


import cc.nekocc.cyanchatroom.Navigator;
import javafx.fxml.FXMLLoader;

import java.util.Objects;

public class ViewTool {



    public static Object loadFXML(String fxml_file)
    {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Navigator.class.getResource(fxml_file)));
            loader.load();
            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}