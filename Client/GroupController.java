import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.beans.value.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.PrintWriter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.*;
import javafx.event.*;
import javafx.util.Callback;
import java.io.BufferedReader;
import java.io.IOException;

public class GroupController
{
    @FXML private Button selectButton;
    @FXML private ListView<Group> grouplist;
    @FXML public Button trigger;
    
    public boolean reflashflag = false;

    public ObservableList<Group> groups = FXCollections.observableArrayList();
    private ObservableList<Group> selectedItems = FXCollections.observableArrayList();
    private ArrayList<String> nameArray = new ArrayList<String>();
    final TextInputDialog addGroupDialog = new TextInputDialog();
    final Alert alert = new Alert(AlertType.INFORMATION);
    public String name;
    private Stage stage;
    private Scene scene2;
    private PrintWriter out;

    @FXML
    private void triggerAction(ActionEvent e)
    {
        if(reflashflag)
        {
           
            grouplist.setItems(groups);
            grouplist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            grouplist.setCellFactory(
                new Callback<ListView<Group>, ListCell<Group>>() {
                    @Override
                    public ListCell<Group> call(ListView<Group> listView) {
                        return new GroupCell();
                    }
                }
            );
            
            grouplist.setOnMouseClicked(new EventHandler<Event>() {

                @Override
                public void handle(Event event) {
                    selectedItems =  grouplist.getSelectionModel().getSelectedItems();
                    
                    for(Group s : selectedItems){
                        if(!nameArray.contains(s.getName()))              //不存在 nameArryalist 才加入 , 否則重覆點擊會加入兩次
                            nameArray.add(s.getName());
                    }
                }

            });
            
            

            reflashflag = false;

            
        }

        
    }

    @FXML
    private void selection()
    {
        addGroupDialog.setContentText("群族名稱: ");
        addGroupDialog.setHeaderText(null);
        alert.setHeaderText("");
        final Optional<String> opt = addGroupDialog.showAndWait();

        String rtn;

        try{
            rtn = opt.get();
        }
        catch(final NoSuchElementException ex)
        {
            rtn = null;
        }

        if(rtn == null || rtn.equals(""))
        {
            alert.setContentText("群族創建失敗");
        }

        else
        {
            alert.setContentText("群族創建成功");
            String value = rtn+"@";                              // rtn 為 group name
            
            for(int i=0;i<nameArray.size();i++)
            {
                value += nameArray.get(i)+",";
                System.out.println(nameArray.get(i));
            }

            value = value.substring(0,value.lastIndexOf(","));    //把最後一次逗號去掉

            System.out.println(value);

            nameArray.clear();

            out.println("STARTGROUPROOM"+value);
        }

        addGroupDialog.getEditor().clear();     //clear text input dialog

        alert.showAndWait();

        stage.close();
        stage.setScene(scene2);
        stage.show();
    }


    public void setOut(PrintWriter out,Stage stage,Scene scene2)
    {
        this.out = out;
        this.stage = stage;
        this.scene2 = scene2;
    }
}