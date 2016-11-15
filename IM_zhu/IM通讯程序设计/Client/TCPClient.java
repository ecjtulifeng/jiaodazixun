/*************************************************************************
    *> File Name: TCPClient.java
    *> Author: XG_W
    *> Adress: SYSU
    *> E-Mail: 359173080@qq.com
    *> Created Time: Thu 22 Apr 2015 8:23:29 AM CST
 ************************************************************************/
import java.io.*;
import java.net.*;

class TCPClient
{
    public static String num;
    public static void main(String [] args)throws Exception
    {
        boolean bo=true;
        System.out.print("please input your ID and Password:");
        Socket ClientSocket0 =new Socket("AI-K55VD",6789);//建立Socket
        DataOutputStream outToServer0 = new DataOutputStream(ClientSocket0.getOutputStream());//建立输出链接
        BufferedReader Uname = new BufferedReader(new InputStreamReader(System.in));//用户名输入buffer
        BufferedReader Password = new BufferedReader(new InputStreamReader(System.in));//密码输入buffer
        outToServer0.writeBytes("U:"+Uname.readLine()+":"+Password.readLine()+'\n');//消息传送
        BufferedReader inFromServer0 = new BufferedReader(new InputStreamReader(ClientSocket0.getInputStream()));//建立接受链接
        num = inFromServer0.readLine();//接受到的num
        if(num.equals("NO"))//若为NO表示密码错误直接退出
        {
            bo=false;
        }
        new Thread(new Task(ClientSocket0)).start();
        while(bo)
        {
            DataOutputStream outToServer = new DataOutputStream(ClientSocket0.getOutputStream());//:::
            BufferedReader Word = new BufferedReader(new InputStreamReader(System.in));//输入buffer
            String sentencs = Word.readLine();
            outToServer.writeBytes(num+":"+sentencs+"\n");//输出
            if(sentencs.equals("Send"))
            {
                new Thread(new Send(ClientSocket0)).start();
            }
            else if(sentencs.equals("exit"))
                    break;
        }
        System.out.println("The Program is end");
    }
    static class Send implements Runnable 
    {
        public int length=0;
        public double sumL=0;
        public boolean bool =false;
        private Socket ClientSocket;
        Send(Socket ClientSocket)
        {
            this.ClientSocket = ClientSocket;
        }
        public void run()
        {
            try
            {
                BufferedReader FN = new BufferedReader(new InputStreamReader(System.in));
                String FName=FN.readLine();
                File file = new File(FName);
                long l = file.length();
                DataOutputStream dos = new DataOutputStream(ClientSocket.getOutputStream());
                dos.writeBytes(FName);
                FileInputStream fis = new FileInputStream(file);
                byte []  sendBytes = new byte[1024];
                while((length = fis.read(sendBytes,0,sendBytes.length))>0)
                {
                    sumL += length;
                    System.out.println("已传输："+((sumL/l)*100)+"%");
                    dos.write(sendBytes,0,length);
                    dos.flush();
                }
                if(sumL==l)
                {
                    bool = true;
                }
            }
            catch(Exception e)
            {
                System.out.println("客户端文件传输异常");
                bool =false;
                e.printStackTrace();
            }
            System.out.println(bool?"成功":"失败");
        }
    }
    static class Task implements Runnable
    {
        private Socket ClientSocket;
        Task(Socket ClientSocket)
        {
            this.ClientSocket=ClientSocket;
        }
        public void run()
        {
            try
            {
                while(true)
                {
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));//输入监听
                String modifiedSentence = inFromServer.readLine();
                System.out.println(modifiedSentence);//显示终端
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
/*
 *
 *
 *
 *
 * 先运行服务端程序再运行客户端程序，然后输入格式为
 * 用户名《CR》
 * 密码《CR》
 * 传输信息
 *
 * ***《CR》只回车*/
