import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class GroupCell extends ListCell<Group> {
    private VBox vbox = new VBox(); // 8 points of gap between controls
    private Label Name = new Label();; // initially empty
    
    // constructor configures VBox, ImageView and Label
    public GroupCell() {
        vbox.setAlignment(Pos.CENTER); // center VBox contents horizontally
        Name.setWrapText(true); // wrap if text too wide to fit in label
        vbox.getChildren().add(Name); // attach to Vbox
        setPrefWidth(USE_PREF_SIZE); // use preferred size for cell width
        
    }
    
    // called to configure each custom ListView cell
    @Override
    protected void updateItem(Group item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null); // don't display anything
            
        }
        else {
            // set ImageView's thumbnail image
            Name.setText(item.getName());
            setGraphic(vbox); // attach custom layout to ListView cell
            
        }
    }
}