import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.event.*;
import javafx.scene.layout.VBox;
import java.util.*;
import javafx.geometry.Insets;
import java.net.Socket;
import java.io.PrintWriter;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;

public class RegisterController{
    // instance variables that refer to GUI components
    @FXML private Button okButton;
    @FXML private TextField account; 
    @FXML private PasswordField password;
    @FXML private Button createButton;
    @FXML private TextField controlTextField;
    @FXML public  Button trigger;
    
    private PrintWriter out;
    private Stage stage;
    private Scene scene1,scene2;
    private BufferedReader in;
    private FriendListController controller2;
    public boolean changeflag=false;
    public  String type="";
    public  boolean enterFlag=false;
    public boolean createFlag = false;
    private Alert alert;
    
    //handler
    @FXML
    private void triggerAction(ActionEvent e){
        if(changeflag==true){
            stage.close();
            stage.setScene(scene2);
            stage.show();
            changeflag=false;  
            
        }
        if(enterFlag==true){
            enterFlag=false;
            alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("");
            if(type.equals("0"))
            {
                
                alert.setContentText("登入成功");
                Platform.runLater(() -> {
                    final Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() || result.get() == ButtonType.OK) {
                        changeflag=true;
                    }
                });
                
                    
            }

            else if(type.equals("1"))
            {   
                alert.setContentText("密碼錯誤"); 
                Platform.runLater(() -> {
                    alert.showAndWait();
                });
                    
            }

            else if(type.equals("2"))
            {
                alert.setContentText("此帳號不存在 , 請創立帳號");
                Platform.runLater(() -> {
                    alert.showAndWait();
                });
            }
            
            type="";
        }

        if(createFlag)
        {
            createFlag = false;
            alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("");
            if(!type.equals("1"))
            {
                
                alert.setContentText("帳號: "+type+" 已經創建");
                Platform.runLater(() -> {
                    alert.showAndWait();
                });
                
                    
            }

            else if(type.equals("1"))
            {   
                alert.setContentText("創建成功"); 
                Platform.runLater(() -> {
                    alert.showAndWait();
                });
                    
            }

            type="";
        }

    }

    @FXML
    private void OKbuttonPressed(ActionEvent e){
        out.println("ENTER "+account.getText()+","+password.getText());
        System.out.println(account.getText());
            
            
    }

    @FXML
    private void CreateButtonPress(ActionEvent e){
        out.println("CREATE "+account.getText()+","+password.getText());
    }
    
    public void retry(){
        account.setText("");
        password.setText("");
        
    }

    

    public void setOut(PrintWriter out,Stage stage,Scene scene1,Scene scene2,FriendListController controller2){
        this.out=out;
        this.stage=stage;
        this.scene1=scene1;
        this.scene2=scene2;
        this.controller2=controller2;
    }
}



