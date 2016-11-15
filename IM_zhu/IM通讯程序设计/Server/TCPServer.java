/*************************************************************************
 *> File Name: TCPServer.java
 *> Author: XG_W
 *> Adress: SYSU 
 *> E-Mail: 359173080@qq.com 
 *> Created Time: Thu 22 Apr 2015 08:43:58 AM CST
 ************************************************************************/
import java.io.*;
import java.util.*;
import java.net.*;
class TCPServer
{
    public static String CapitalzedSentence;
    public static String Num;//返回用户编号
    public static String [] UserName=new String [100];//存放用户名
    public static String [] Password=new String [100];//存放密码
    public static int [] Type=new int [100];//存放每个用户的状态
    public static int end=0;//尾
    public static Socket [] v=new Socket[100]; //存放每个用户对应的链接

    public static void main(String [] args)throws Exception
    {
        for(int i=0;i<100;i++)//初始默认为离线（Type=0）
            Type[i]=0;
        ServerSocket WelcomeSocket = new ServerSocket(6789);//设置端口号
        while(true)
        {
            Socket ConnectionSocket = WelcomeSocket.accept();//连接Client
            new Thread(new Task(ConnectionSocket)).start();//采用线程
        }
    }
    static class Task implements Runnable
    {
        private Socket ConnectionSocket;
        public Task(Socket socket)
        {
            this.ConnectionSocket=socket;
        }
        public void run()
        {
            try{
                while(true)
                    handleSocket();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        private void handleSocket() throws Exception
        {
            DataOutputStream outToClient = new DataOutputStream(ConnectionSocket.getOutputStream());
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(ConnectionSocket.getInputStream()));
            String ClientSentence = inFromClient.readLine();
            //System.out.println(ClientSentence);
            if(ClientSentence.charAt(0)=='U')//表示登录信息，传送过来的格式为U：用户名：密码
            {
                String a[]=ClientSentence.split(":");//a[0]=U,a[1]=用户名,a[2]=密码;
                boolean bo=false;
                int i=0;
                for(;i<end;i++)//查找库中是否有此用户
                {
                    if(UserName[i].equals(a[1]))
                    {
                        bo=true;
                        break;
                    }
                }
                if(bo==false)//若没有进行注册
                {
                    UserName[i]=a[1];
                    end++;
                    Password[i]=a[2];
                    v[i]=this.ConnectionSocket;//保留链接
                    Type[i]=1;//转为线上状态
                    Num=Integer.toString(i);//返回给客户端一个num号
                    System.out.println("New UserID "+a[1]+" is registered successfully\n");
                }
                else//进行密码的匹配
                {
                    if(a[2].equals(Password[i]))//密码匹配
                    {
                        Num=Integer.toString(i);
                        Type[i]=1;//转为线上状态
                        v[i]=this.ConnectionSocket;
                        System.out.println(a[1]+" is successful login\n");
                    }
                    else//密码错误
                    {
                        System.out.println("Wrong Password\n");
                        Num="NO";//客户端接到No后会退出
                    }
                }
                outToClient.writeBytes(Num+"\n");//给客户端返回Num
            }
            else//表示传送的消息传送过来的格式为 num（XX用户发出）：消息内容
            {
                String a[]=ClientSentence.split(":");//a[0]=num,a[1]=消息内容
                int i=Integer.parseInt(a[0]);
                int k=Type[i];
                if(a[1].equals("exit"))
                {
                    Type[i]=0;
                    for(int j=0;j<end;j++)
                    {
                        if(Type[j]>=1)//找到为线上状态的
                        {
                        DataOutputStream outToClient1 = new DataOutputStream(v[j].getOutputStream());//建立输出链接
                        outToClient1.writeBytes(UserName[i]+" is exit\n");//输出
                        }
                    }
                }
                else if(a[1].equals("Change"))
                {
                    int ii=Integer.parseInt(a[2]);
                    Type[i]=ii;
                    outToClient.writeBytes("the chatroom is changed , you are in room "+a[2]+"\n");
                }
                else if(a[1].equals("Show"))
                {
                    outToClient.writeBytes("the room "+Integer.toString(k)+" 's onlion users:\n");
                    StringBuffer output=new StringBuffer();
                    for(int j=0;j<end;j++)
                    {
                        if(Type[j]==k)
                        {
                            output.append(UserName[j]+",");
                        }
                    }
                    //System.out.println("1\n");
                    String ou=output.toString();
                    //System.out.println("2\n");
                    outToClient.writeBytes(ou+"\n");
                    //System.out.println("3\n");
                }
                else if(a[1].equals("Send"))
                {
                    BufferedReader IN = new BufferedReader(new InputStreamReader(ConnectionSocket.getInputStream()));
                    String FNameBuffer = IN.readLine();
                    String b[]=FNameBuffer.split(":");
                    String FName="FILE/"+b[1];
                    int length = 0;
                    String filePath="FILE";
                    try{
                            DataInputStream dis =new DataInputStream(ConnectionSocket.getInputStream());
                            File f = new File(filePath);
                            if(!f.exists())
                            {
                                f.mkdir();
                            }
                            FileOutputStream fos = new FileOutputStream(new File(FName));
                            byte [] inputByte = new byte [1024];
                            System.out.println("开始接收数据。。。");
                            while((length = dis.read(inputByte,0,inputByte.length))>0)
                            {
                                fos.write(inputByte,0,length);
                                fos.flush();
                            }
                            System.out.println("完成接收："+FName);
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else
                {
                    for(int j=0;j<end;j++)
                    {
                        if(Type[j]==k)//找到为线上状态的
                        {
                        DataOutputStream outToClient1 = new DataOutputStream(v[j].getOutputStream());//建立输出链接
                        outToClient1.writeBytes(UserName[i]+":"+a[1]+"\n");//输出
                        }
                    }
                }
            }
        }
    }
}
