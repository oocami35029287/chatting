import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import java.util.ArrayList;

import java.security.SecureRandom;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Scanner;
import java.util.Arrays;
public class Main extends Application {
    String Account; 
    final int PORT = 9001;
    final String SERVER_ADDRESS = "localhost";
    BufferedReader in;
    PrintWriter out;
    private Stage stage;
    private GroupController controller4;
    private ChattingController  controller3;
    private FriendListController  controller2;
    private RegisterController  controller1;
    private Scene scene1,scene2,scene3,scene4;
    private ExecuteTimer timer;
    private String receiver="";
    private boolean reloadFlag=false;
    private ArrayList<String> reloadDialog;
    private LoadWait loadwait=new LoadWait();
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // the first scene
        this.stage=primaryStage;
        FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("Registered.fxml"));
        Parent root1 = fxmlLoader1.load();
        scene1 = new Scene(root1);
        controller1 = fxmlLoader1.getController();

        FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("FriendList.fxml"));
        Parent root2 = fxmlLoader2.load();
        scene2 = new Scene(root2);
        controller2 = fxmlLoader2.getController();
        
        FXMLLoader fxmlLoader3 = new FXMLLoader(getClass().getResource("Chatting.fxml"));
        Parent root3 = fxmlLoader3.load();
        scene3 = new Scene(root3);
        controller3 = fxmlLoader3.getController();
        controller3.setLoadWait(loadwait);

        FXMLLoader fxmlLoader4 = new FXMLLoader(getClass().getResource("Group.fxml"));
        Parent root4 = fxmlLoader4.load();
        scene4 = new Scene(root4);
        controller4 = fxmlLoader4.getController();

        
        timer=new ExecuteTimer();             //<----set timer
        timer.start();
        stage.setScene(scene1);
    
    

    
     
    
      
    //tesk register sent ...
        Task task = new Task<Void>() {
            boolean flag=false;
        
            @Override
            public Void call() {
                try {
                    Socket socket = new Socket(SERVER_ADDRESS, PORT);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    controller1.setOut(out,stage,scene1,scene2,controller2);
                    controller2.setOut(stage,scene3,scene4,receiver,out,controller3,controller4);
                    controller3.setOut(out,stage,scene2);
                    controller4.setOut(out,stage,scene2);
                    
                    while (true) {
                        String line = in.readLine();
                        System.out.println(line);
                        if (line.startsWith("SUBMIT_NAME")) {                   //1.帳密
                            controller1.retry();
                        } 
                        
                        else if (line.startsWith("NAME_ACCEPTED")) {            //2.成功登入
                            controller2.name=line.substring(13);
                            controller3.name=line.substring(13);
                        } 
                        
                        else if (line.startsWith("CREATE"))                     //3.創建帳密
                        {
                            controller1.retry();
                            controller1.createFlag = true;
                            controller1.type = line.substring(6);
                        }
                //***************************

                        else if(line.startsWith("ENTERRE"))                    // 登入成功
                        {
                            controller1.enterFlag=true;
                            controller1.type= line.substring(7);          //確認登入狀況
                            
                        }
                //***************************
                        else if (line.startsWith("REFRESH_FRIEND_LIST")){       //4.刷新好友名單
                            controller2.friends.clear();
                        }
                        else if(line.startsWith("FRIEND_LIST")){                //5.寫入名單
                            if(line.contains("@")){
                                String chatRoomID=line.substring(16,line.indexOf("@"));
                                String members=line.substring(line.indexOf("@")+1);
                                controller2.friends.add(new Friend(chatRoomID,members,line.substring(11,16)));
                            }
                            else
                                controller2.friends.add(new Friend(line.substring(16),line.substring(11,16))); //Friend(String chatRoomName(friendName), String chatRoomID)
                            
                        }
                        else if(line.startsWith("STOP_FRIEND_LIST")){           //6.寫入名單完畢
                            controller2.reflashflag=true;
                        }
                //*********************************

                        else if(line.startsWith("EXIST"))                       //7.加好友確認存在 
                        {
                            String person = line.substring(5);
                            boolean repeat = false;
                            for(Friend f: controller2.friends)
                            {
                                if(person.equals(f.getChatRoomName()) || person.equals(controller2.name))
                                {
                                    controller2.repeatFlag = true;
                                    repeat = true;
                                    break;
                                }
                            }

                            if(!repeat)
                            {
                                if(!controller2.name.equals(person))     //第一次不能加自己
                                    controller2.personExistFlag = true;
                                else
                                    controller2.repeatFlag = true;
                            }
                            

                    
                            
                        }

                        else if(line.startsWith("NOTEXIST"))                    //8.加好友確認存在
                        {
                            controller2.personNotExistFlag = true; 
                        }

                        else if (line.startsWith("MESSAGE")) {                  //9.傳送訊息
                            if(controller2.chatRoomSelected.equals(line.substring(8,13)))
                            controller3.string=(line.substring(13) + '\n');      //接收訊息---->加入syna
                            
                        } 
                        else if (line.startsWith("START_SEND_FILE")){             //10.傳送圖檔
                            new sentFile(controller3.selectedFile.getAbsolutePath(),Integer.valueOf(line.substring(15))).start();   //檔案位址
                        }
                        else if (line.startsWith("START_GET_FILE_SERVER")){     //11.接收檔案
                            new getFile(line.substring(21),controller3,out).start();
                            System.out.println("START_GET_FILE_SERVER~~");
                        }

                //******************************************************* 
                        else if(line.startsWith("REFRESH_GROUP_LIST"))
                        {
                            controller4.groups.clear();                           //刷新群族名單
                        }
                        
                        else if(line.startsWith("GROUP_LIST"))
                        {
                            controller4.groups.add(new Group(line.substring(10)));   //Group(String name)
                        }

                        else if(line.startsWith("STOP_GROUP_LIST"))
                        {
                            controller4.reflashflag = true;
                        }
                
                //********************************************************

                        else if(line.startsWith("STARTRELOAD")){                //12.Relaod對話紀錄
                            reloadFlag=true;
                            reloadDialog= new ArrayList<String>();
                        }
                        else if(line.startsWith("ENDRELOAD")){
                            reloadFlag=false;
                            if(controller2.chatRoomSelected.equals(line.substring(9,14))){
                                for(String string:reloadDialog){
                                    controller3.string=string;
                                    loadwait.loadWait();
                                    loadwait.loadWaitting();
                                    //try{Thread.sleep(30);}catch(InterruptedException ex){}
                                }
                            }
                        }
                        else if(reloadFlag==true){
                            reloadDialog.add(line);
                        }

                    } 
                } catch(IOException ioe) {
                    System.out.println("Server is offline.\nPlease exit.");
                }

                return null;
            }
        };

        Thread severIO = new Thread(task);
        severIO.setDaemon(true);
        severIO.start();
        stage.show();
        

    }
    
    
    class ExecuteTimer extends AnimationTimer {
        private long lastUpdate = 0L;

        @Override
        public void handle(long now) {
            if (this.lastUpdate > 1) {
                controller4.trigger.fire();
                controller3.trigger.fire();
                controller2.trigger.fire();
                controller1.trigger.fire();
            }
            this.lastUpdate = now;
        }
    }    

    public class sentFile extends Thread{
        private String fileName;
        private int port;
        public sentFile(String fileName,int port){
            this.fileName=fileName;
            this.port=port;
        }
        public synchronized void run(){
            try{System.out.println(port);
                Socket s = new Socket("127.0.0.1",port);
                System.out.println("已接到server"+port+"端口，準備傳送照片...");
                    
               FileInputStream fis = new FileInputStream(fileName);
                OutputStream out = s.getOutputStream();
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = fis.read(buf)) != -1)
                {
                    out.write(buf,0,len);
                }
                s.shutdownOutput();
                InputStream in = s.getInputStream();
                byte[] bufIn = new byte[1024];
                int num = in.read(bufIn);
                System.out.println(new String(bufIn,0,num));
                fis.close();
                out.close();
                in.close();
                s.close();
            }catch (IOException ioe) {}
        }  
    }
    
    public class getFile extends Thread{
        private String fileName;
        private ChattingController  controller3;
        private PrintWriter toServer;
        private SecureRandom random = new SecureRandom();
        public getFile(String fileName,ChattingController  controller3,PrintWriter toServer){
            this.fileName=fileName;
            this.controller3=controller3;
            this.toServer=toServer;
        }
        public synchronized void run(){
                try{int port=random.nextInt(999)+5600;
                    ServerSocket ss = new ServerSocket(port);
                    System.out.println("file server已開啟...");
                    toServer.println("START_GET_FILE_CLIENT"+port);
                    Socket s = ss.accept();
                    System.out.println("連接到client，開始接收...");
                    InputStream in = s.getInputStream();
                    FileOutputStream fos = new FileOutputStream("./storage/"+fileName);
                    byte[] buf = new byte[1024];
                    int len = 0;
                    while ((len = in.read(buf)) != -1)
                    {
                        fos.write(buf,0,len);
                    }
                    OutputStream out = s.getOutputStream();
                    out.write("上傳成功".getBytes());
                    fos.close();
                    in.close();
                    out.close();
                    s.close();
                    ss.close();
                    controller3.fileSentFlag=true;
                    controller3.sentFileName=fileName;
                }catch (IOException ioe) {}
        }
    }
    
    public static void main(String[] args) {
       launch(args);
    }
    
}


//********Sent File **************
/*
we have client A, client B, server S
1. A--FILE-->S (S--MASSAGE-->A,B)
2. S--START_GET_FILE_SERVER-->B (B start getfile server)
3. B--START_GET_FILE_CLIENT-->S
4. S--START_SEND_FILE-->A (A start sentfile client)
*/
//********************************

