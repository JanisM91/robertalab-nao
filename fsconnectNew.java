//Opens a FTP connection to a NAO robot in the local network and transfers a specified file to it. Then it opens a ssh connection and sends a command.
//@author: Janis Mohr
//@date: 27.10.2016

import java.io.IOException;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import com.jcraft.jsch.*;

public class fsconnectNew {
	
    private static void showServerReply(FTPClient ftpClient) {
		
        String[] replies = ftpClient.getReplyStrings();
		
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
	
	private static void sshCommand(String server, int port, String user, String pass, JSch jsch, String command) {
		
		//implement the abstract class MyUserInfo
		UserInfo ui = new MyUserInfo(){
		
			public void showMessage(String message){
				JOptionPane.showMessageDialog(null,message);
			}
		
			public boolean promptYesNo(String message){
				Object[] options = {"yes", "no"};
				int foo = JOptionPane.showOptionDialog(null,message,"Warning",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,options,options[0]);
				return foo == 0;
			}
		};
		
		try{
			Session session = jsch.getSession(user, server, port);
			session.setPassword(pass);
			session.setUserInfo(ui);
			/*
			//uncomment this part if key fingerprint is not accepted
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			*/
			
			//establish session connection
			session.connect();
		
			//open channel and send command
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
		
			//set streams
			channel.setInputStream(null);
			((ChannelExec)channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();

			//establish channel connection
			channel.connect();

			//show messages on the ssh channel
			byte[] tmp=new byte[1024];
			while(true){
				while(in.available()>0){
					int i=in.read(tmp, 0, 1024);
					if(i<0)break;
					System.out.print(new String(tmp, 0, i));
				}
				if(channel.isClosed()){
					if(in.available()>0) continue; 
					System.out.println("exit-status: "+channel.getExitStatus());
					break;
				}
				try{Thread.sleep(1000);}catch(Exception ee){}
			}
			
			//disconnect from channel and session
			channel.disconnect();
			session.disconnect();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	
	private static void ftpTransfer(String server, int port, String user, String password, String file, FTPClient ftpClient) {
		
		try {
			//connect to ftp server(NAO)
            ftpClient.connect(server, port);
			
            showServerReply(ftpClient);
            int replyCode = ftpClient.getReplyCode();
			
			//print error message if connection is not established
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Operation failed. Server reply code: " + replyCode);
                return;
            }
            boolean success = ftpClient.login(user, password);
            showServerReply(ftpClient);
			
            if (!success) {
                System.out.println("Could not login to the server");
                return;
            } else {
                System.out.println("LOGGED IN SERVER!");				//for testing only
				
				//Use local passive mode to avoid problems with firewalls
				ftpClient.enterLocalPassiveMode();
				
				//open Input Stream and store file on the robot
				InputStream input = new FileInputStream(file);
				System.out.println("Transferring file.");				//for testing only
				ftpClient.storeFile("Roberta.py",input);
				
				//close stream and logout
				input.close();
				System.out.println("LOGGING OUT!");						//for testing only
				ftpClient.logout();
            }
        } catch (IOException ex) {
            System.out.println("Something wrong happened!");
            ex.printStackTrace();
        }
	}
	
    public static void main(String[] args) {
        	
		/*
		**************************************************************FTP**********************************************************************
		*/
		
		//Parameters needed for FTP connection. Change these to fit your needs!
		String server = "169.254.235.8";
		int port = 21;
		String user = "nao";
		String pass = "nao";
		FTPClient ftpClient = new FTPClient();
		String file = "StandUp.py";
	
		ftpTransfer(server, port, user, pass, file, ftpClient);
		
		
		/*
		***************************************************************SSH*********************************************************************
		*/
		
		//Parameters needed for SSH connection. Change these to fit your needs!
		//same as ftp
		//String user = "nao";
	
		//same as ftp server
		//String host = "169.254.235.8";
	
		//same as ftp password
		//String sshpass = "nao";
		
		int sshport = 22;
		String command = "python SitDown.py";
		JSch jsch = new JSch();
		
		sshCommand(server, sshport, user, pass, jsch, command);
	}

	//abstract MyUserInfo needed for ssh conection
	public static abstract class MyUserInfo implements UserInfo, UIKeyboardInteractive{
		public String getPassword(){return null;}
		public boolean promptYesNo(String str){return false;}
		public String getPassphrase(){return null;}
		public boolean promptPassphrase(String message){return false;}
		public boolean promptPassword(String message){return false;}
		public void showMessage(String message){}
		public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo){return null;}
	}
}