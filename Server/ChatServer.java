import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Map;
import java.io.*;
import java.security.SecureRandom;
import javax.swing.*;  

public class ChatServer {
    private static final int PORT = 9001;
    private static HashSet<String> names = new HashSet<>();
    private static HashSet<String> userNames = new HashSet<>();
    private static HashSet<PrintWriter> writers = new HashSet<>();
    private static Map<String, PrintWriter> writersMap = new HashMap();
    private static int usersConnected = 0;
    private static final String Namelist = "./Storage/Namelist.txt";
    private static final String chatRoomList = "./Storage/chatRoomList.txt";
    private static SecureRandom random = new SecureRandom();
    private static String snetFilePerson="";
    public static void main(String[] args) {
        System.out.println(new Date() + "\nChat Server online.\n");

        try (ServerSocket chatServer = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = chatServer.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException ioe) {}
    }

    private static String names() {
        StringBuilder nameList = new StringBuilder();

        for (String name : userNames) {
            nameList.append(", ").append(name);
        }

        return "In lobby: " + nameList.substring(2);
    }

    private static class ClientHandler extends Thread {
        private String name;
        private String password;
        private String serverSideName;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String[] receiver,otherReceiver;
        private boolean receiverFlag=false;
        private String chatRoomIDSented;
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                
                out.println("SUBMIT_NAME");
                while ( true ) {
                    String line = in.readLine();
                    if(line.startsWith("ENTER")){
                        name = line.substring(6,line.indexOf(","));
                        password = line.substring(line.indexOf(",")+1);

                        if(account(Namelist,name,password)==true && !name.trim().isEmpty()){   //name 不能是空的
                            serverSideName = name;
                            out.println("ENTERRE"+"0");
                            //JOptionPane.showMessageDialog(null,"登入成功");
                            break;
                        }
                        else
                        {
                            if(findName(Namelist,name) == true)
                                out.println("ENTERRE" + "1");
                                //JOptionPane.showMessageDialog(null,"密碼錯誤");
                            
                            else
                                out.println("ENTERRE"+"2");
                                //JOptionPane.showMessageDialog(null,"此帳號不存在 , 請創立新帳號");
                            out.println("SUBMIT_NAME");
                        } 
                            
                    }
                    
                    if(line.startsWith("CREATE")){
                        name = line.substring(7,line.indexOf(","));
                        password = line.substring(line.indexOf(",")+1);

                        if(findName(Namelist,name) == true)  //帳號已存在
                        {
                            //JOptionPane.showMessageDialog(null,"帳號: "+name+" 已經存在");
                            out.println("CREATE"+name);
                        }

                        else{  //創立新帳號
                            //JOptionPane.showMessageDialog(null,"帳號創立成功");
                            System.out.println("hello");
                            out.println("CREATE"+"1");
                            writeFile(Namelist, name+","+password);

                        }
                    }   
                }
               
                out.println("NAME_ACCEPTED"+name);   //登入
                System.out.println(name + " connected. IP: " + socket.getInetAddress().getHostAddress());
                
                refreshFriendList(out,chatRoomList,name);             //刷新好友名單 
                createGroup(out,Namelist,name);
                         
                
                messageAll("CONNECT" + name);
                userNames.add(name);
                names.add(serverSideName);
                writers.add(out);
                writersMap.put(name,out);
                out.println("INFO" + ++usersConnected + names());


                while (true) {
                    String input = in.readLine();
                    System.out.println(input);
                    if (input == null || input.isEmpty()) {
                        continue;
                    }
                    //***************************
                    if(input.startsWith("DISLINK")){                 //離開聊天室
                        receiverFlag=false;
                        chatRoomIDSented="";
                    }
                    if(receiverFlag==true){
                        if((!input.startsWith("START_GET_FILE_CLIENT")) && (!input.startsWith("LINK"))){
                            messageTo("MESSAGE "+chatRoomIDSented+ name + ": " +input,receiver);
                            writeFile("./Storage/"+chatRoomIDSented+".txt", name + ": " +input);        //寫入聊天內容
                        }  
                    }   
                    if(input.startsWith("LINK")){                   //進入聊天室
                        chatRoomIDSented=input.substring(4);
                        receiver=getReceiver(input.substring(4),chatRoomList);
                        otherReceiver=getOtherReceiver(input.substring(4),chatRoomList,name);
                        receiverFlag=true;
                        
                        out.println("STARTRELOAD");                                    //登入聊天室窗時reload紀錄
                        for(String message:readFileByLines("./Storage/"+chatRoomIDSented+".txt")){  
                            System.out.println(message);        
                            out.println(message);
                            //messageTo("MESSAGE "+chatRoomIDSented+message,name);                    //bug: 前面有一段不會送出 但確實有讀到
                        }
                        out.println("ENDRELOAD"+chatRoomIDSented);
                    }
                    //******************************
                    
                    if(input.startsWith("STARTCHATROOM")){          //新增好友(新增聊天室)
                        String friend = "";
                        while(true){
                            
                            String chatRoomID=String.format("%05d",random.nextInt(99999));
                            if(!findName(chatRoomList,chatRoomID)){
                                System.out.println("Enter startchatroom");
                                friend = input.substring(13);
                                String string=String.format(chatRoomID+"$"+friend+","+name);
                                writeFile(chatRoomList, string);
                                break;
                            }    
                        }   
                        refreshFriendList(out,chatRoomList,name);
                        if(names.contains(friend)){
                            refreshFriendList(writersMap.get(friend),chatRoomList,friend); 
                        }                    //friend 被加好友也要 refresh
                    }

                    if(input.startsWith("STARTGROUPROOM"))           //新增群族
                    {
                        String people[] = new String[0];
                        while(true)
                        {
                            String chatRoomID=String.format("%05d",random.nextInt(99999));
                            if(!findName(chatRoomList,chatRoomID)){
                                String string=String.format(chatRoomID+"$"+input.substring(14)+","+name);
                                people = input.substring(input.indexOf("@")+1).split(",");
                                writeFile(chatRoomList, string);
                                break;
                            }    
                        }

                        refreshFriendList(out,chatRoomList,name);

                        for(String person: people)
                        {
                            if(names.contains(person))
                                refreshFriendList(writersMap.get(person),chatRoomList,person);
                        }
                    }
                    
                    //******************************
                    if(input.startsWith("CHECK"))                   //檢查新增的好友有沒有存在server
                    {
                        String new_member = input.substring(5);
                        if(findName(Namelist,new_member))
                            out.println("EXIST"+new_member);
                        else
                            out.println("NOTEXIST");
                    }
                    if(input.startsWith("FILE")){                   //接收檔案
                        messageTo("START_GET_FILE_SERVER"+input.substring(4),otherReceiver);
                        snetFilePerson=name;
                        System.out.println("sentfileperson:"+snetFilePerson);
                    }
                    if(input.startsWith("START_GET_FILE_CLIENT")){
                        messageTo("START_SEND_FILE"+input.substring(21),snetFilePerson);
                        System.out.println("sentfileperson:"+snetFilePerson);
                        snetFilePerson="";
                    }
                    System.out.println(input);
                }
            } catch (IOException e) {
                if (name != null) {
                    System.out.println(name + " disconnected.");
                    userNames.remove(name);
                    names.remove(serverSideName);
                    writers.remove(out);
                    messageAll("DISCONNECT" + name);
                    usersConnected--;
                }   
            } finally {     
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }
    }

    private static void messageAll(String... messages) {
        if (!writers.isEmpty()){
            for (String message : messages) {
                for (PrintWriter writer : writers) {
                    writer.println(message);
                }
            }
        }
    }

    private static void messageTo(String messages,String... receivers){
        
        for(String receiver:receivers){
            System.out.println("receiver: "+receiver);
            if(writersMap.containsKey(receiver)){
                PrintWriter writer=writersMap.get(receiver);
                writer.println(messages);
            }   
            else System.out.println("cannot find this person");   
        }
        
    }   
    
    
    public static ArrayList<String> readFileByLines(String fileName){ 
        File file = new File(fileName); 
        BufferedReader reader = null; 
        ArrayList<String> stringArr = new ArrayList<String>();
        try { 
            reader = new BufferedReader(new FileReader(file)); 
            String tempString = null; 
            while ((tempString = reader.readLine()) != null  ){ 
                if(tempString!="")
                    stringArr.add(new String(tempString));
            } 
            reader.close(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } finally { 
            if (reader != null){ 
                try { 
                    reader.close(); 
                } catch (IOException e1) {} 
            } 
        } 
        return stringArr;
    }
    
    public static void writeFile(String fileName, String content){ 
        try { 
            //開啟一個寫檔案器，建構函式中的第二個引數true表示以追加形式寫檔案 
            FileWriter writer = new FileWriter(fileName, true); 
            writer.write(content+"\r\n"); 
            writer.close(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    } 
    
    public static boolean account(String nameList,String name,String password)
    {
        boolean accountFlag = false;
        boolean passwordFlag = false;
        for(String namecheck:readFileByLines(nameList)){
            accountFlag = namecheck.substring(0,namecheck.indexOf(",")).equals(name);
            passwordFlag = namecheck.substring(namecheck.indexOf(",")+1).equals(password);
            if(accountFlag && (!password.equals("")) && passwordFlag)
                return true;
        }
        return false;
    }
    
    public static boolean findName(String namelist,String name){
        boolean flag = false;
        for(String namecheck:readFileByLines(namelist)){
            if(namelist.equals(Namelist) && !namecheck.equals(""))
            {
                flag = namecheck.substring(0,namecheck.indexOf(",")).equals(name);
            }
                
            else if(namelist.equals(chatRoomList) && !namecheck.equals(""))
            {
                flag = namecheck.substring(0,5).equals(name);
            }
                
            
            if(flag)
                return true;
        }
        
        return false;
    }
    
    public static void refreshFriendList(PrintWriter out,String chatRoomList,String name){
        boolean sendflag=false;
        String[] receivers = new String[0];
        String chatRoomName="XXX";
        int checkflag = 0;
        out.println("REFRESH_FRIEND_LIST");
        for(String check:readFileByLines(chatRoomList)){
            if(!check.equals(""))                 // 防止chatRoomList有空白 , 導致後面 code 運算有錯誤
            {
                checkflag = check.indexOf("@");
                if(checkflag < 0)               // 不是群族列
                {
                        
                    receivers=check.substring(6).split(",");
                    
                    for(String receiver:receivers){
                        if(receiver.equals(name)){      //當有聊天室裡有這人時 sendflag
                            sendflag=true;
                        }
                        if(!receiver.equals(name))      //用對方的名子當作聊天室名稱
                            chatRoomName=receiver;
                    }

                    if(sendflag==true){          
                        out.println("FRIEND_LIST"+check.substring(0,5)+chatRoomName);         //寫入frindlist
                        sendflag=false;
                    }
            
                }

                else if(checkflag > 0)
                {
                    
                    receivers = check.substring(checkflag+1).split(",");
                    
                    chatRoomName = check.substring(6,checkflag);
                    
                    for(String receiver:receivers){
                        if(receiver.equals(name)){      //當有聊天室裡有這人時 sendflag
                            sendflag=true;
                        }

                        if(sendflag==true){          
                            out.println("FRIEND_LIST"+check.substring(0,5)+chatRoomName+"@"+check.substring(checkflag+1));         //寫入frindlist
                            sendflag=false;
                        }
                    }
                }
            }
            
        }
        out.println("STOP_FRIEND_LIST");   
    }


    public static void createGroup(PrintWriter out,String nameList,String name)
    {
        String person = ""; 
        
        out.println("REFRESH_GROUP_LIST");

        for(String check:readFileByLines(nameList))
        {
            if(!check.equals(""))
            {
                person = check.substring(0,check.indexOf(","));
                if(!name.equals(person))                            //GroupList 除了自己
                    out.println("GROUP_LIST"+person);    
            }
                
        }

        out.println("STOP_GROUP_LIST");
    }
    
    public static String[] getReceiver(String ID,String chatRoomList){
        String[] receiver=new String[0];
        for(String IDcheck:readFileByLines(chatRoomList)){
            
            if(IDcheck.startsWith(ID) && IDcheck.indexOf("@") < 0){     //not group
                receiver=IDcheck.substring(6).split(",");
            }

            else if(IDcheck.startsWith(ID) && IDcheck.indexOf("@") > 0)  //for group
            {
                receiver=IDcheck.substring(10).split(",");
                
                for(String name: receiver)
                    System.out.println(name);
            }
        }
        return receiver;
    }
    public static String[] getOtherReceiver(String ID,String chatRoomList,String name){
        String[] receivers=new String[0];
        for(String IDcheck:readFileByLines(chatRoomList)){
            if(IDcheck.startsWith(ID) && IDcheck.indexOf("@") < 0){
                receivers=IDcheck.substring(6).split(",");
                ArrayList<String> list=new ArrayList<String>(Arrays.asList(receivers));
                list.remove(name);
                receivers = list.toArray(new String[0]);
            }

            else if(IDcheck.startsWith(ID) && IDcheck.indexOf("@") > 0)
            {
                receivers=IDcheck.substring(10).split(",");
                ArrayList<String> list=new ArrayList<String>(Arrays.asList(receivers));
                list.remove(name);
                receivers = list.toArray(new String[0]);
            }
        }
        return receivers;
        
    }
    
    
}