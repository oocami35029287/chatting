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
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Insets;

public class FriendListController{
    // instance variables that refer to GUI components
    @FXML private Button addFriendButton;
    @FXML private VBox friendList;
    @FXML public  Button trigger;
    @FXML private Label nameLabel;
    @FXML private Button addGroupButton;

    // private instance
    private Scene scene3,scene4;
    private String line;
    private Stage stage;
    private String receiver;
    private PrintWriter out;
    private ChattingController  controller3;
    private GroupController controller4;
    private String rtn;
    final TextInputDialog addFriendDialog = new TextInputDialog();  //�[�n�͸��X����
    final Alert alert = new Alert(AlertType.INFORMATION);           //���\�[�n��
    public boolean reflashflag=false;
    public boolean repeatFlag = false;
    public boolean personExistFlag = false;
    public boolean personNotExistFlag = false;
    public String chatRoomSelected;
    public ArrayList<Friend> friends=new ArrayList<Friend>();
    public ObservableList<Friend> friendsData;
    public String name;
    
    
    //handlers
    @FXML 
    private void AddFriend(){
        final TextInputDialog addFriendDialog = new TextInputDialog();  //�[�n�͸��X����
        final Alert alert = new Alert(AlertType.INFORMATION);           //���\�[�n��
        
        addFriendDialog.setContentText("�n��: "); 
        addFriendDialog.setHeaderText(null);
        alert.setHeaderText("");
        final Optional<String> opt = addFriendDialog.showAndWait();
        
        
        
        try{
            rtn = opt.get(); 
        }catch(final NoSuchElementException ex){
            rtn = null;
        }
        
        if(rtn == null){
            alert.setContentText("����"); //�]�w��ܮت��T����r
            alert.showAndWait();
        }
        
        else{
            out.println("CHECK"+rtn);            //�ˬdserver ���S���o�ӤH
        }
        
    }
    //functions

    @FXML
    private void addGroup()
    {
        stage.close();
        stage.setScene(scene4);
        stage.show();
    }

        
    @FXML
    private void triggerAction(ActionEvent e){
        if(personExistFlag){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("");
            alert.setContentText("���\�[���n��"); //�]�w��ܮت��T����r
            out.println("STARTCHATROOM"+rtn);        //�Ыز�ѫ�
            Platform.runLater(()->{alert.showAndWait();});
            personExistFlag = false;
        }

        if(personNotExistFlag){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("");
            alert.setContentText("���W�٤��s�b");
            Platform.runLater(()->{alert.showAndWait();});
            personNotExistFlag = false;
        }

        if(repeatFlag){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("");
            alert.setContentText("���W�٤��s�b");
            Platform.runLater(()->{alert.showAndWait();});
            repeatFlag = false;
        }
        

        if(reflashflag==true){
            
            friendList.getChildren().clear();
            nameLabel.setText(name);
            for(Friend friend:friends){
                friendList.getChildren().add(friend);
                friendList.setSpacing(10);
                friendList.setPadding(new Insets(10));
                friend.addEventFilter(
                    MouseEvent.MOUSE_CLICKED,eh->{
                        try{
                            System.out.println("Selected item: " + friend.getChatRoomID());
                            controller3.nameLabel.setText(friend.getChatRoomName());
                            stage.close();
                            stage.setScene(scene3);
                            stage.show();
                            receiver=friend.getChatRoomID();
                            out.println("LINK"+friend.getChatRoomID());       //���}��ѫ�
                            chatRoomSelected=friend.getChatRoomID();          //��ѫ�ID��
                            controller3.clearChatRoom();
                        }catch(Exception ev){System.out.println("XXX");}
                    }
                );
            }
            reflashflag=false;  
            
        }
    }
    
    
    public void setOut(Stage stage,Scene scene3,Scene scene4,String receiver,PrintWriter out,ChattingController  controller3,GroupController controller4){
        this.scene3=scene3;
        this.scene4 = scene4;
        this.stage=stage;
        this.receiver=receiver;
        this.out=out;
        this.controller3=controller3;
        this.controller4 = controller4;
    }   
    
    
}