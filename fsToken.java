//Opens a FTP connection to a NAO robot in the local network and transfers a generated Token to it. When the token is entered correctly by the user a file is transfered and executed.
//TODO: Implement a check if the file was transferred correctly (MD5?)
//@author: Janis Mohr
//@date: 27.10.2016

import java.io.IOException;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.lang.Math;
import java.lang.StringBuilder;
import java.security.*;

//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTPReply;
import com.jcraft.jsch.*;

public class fsToken {
	
    private static void showServerReply(FTPClient ftpClient) {
		
        String[] replies = ftpClient.getReplyStrings();
		
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
	
	//calculate the md5 of a string
	private String md5(String s) {
		try {
			MessageDigest m = new MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			BigInteger i = new BigInteger(1,m.digest());
			return String.format("%1$032x", i);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	/*
	**************************************************************send and check token**********************************************************************
	*/
	
	private static boolean tokencheck(String server, int port, int sshport, String user, String pass, FTPClient ftpClient, JSch jsch) {
		
		//variables
		boolean check = true;
		String generatedToken;
		
		//store generated Token
		generatedToken = generateToken();
		JOptionPane.showMessageDialog(null, generatedToken);										//for testing only
		
		while(check) {
			//transfer and execute the python file that makes NAO say the generated token
			System.out.println("tranferring token.py")												//for testing only
			ftpTransfer(server, port, user, pass, ftpClient, "token.py");
			System.out.println("executing token check file")										//for testing only
			sshCommand(server, sshport, user, pass, jsch, "python Roberta.py");
			
			//prompt the user to enter the token
			String inputToken = JOptionPane.showInputDialog("Please type the token NAO just said:");
		
			//check if the entered token equals the generated token
			if(!inputToken.equals(generatedToken)){
				int i = JOptionPane.showConfirmDialog(null,"The token you entered was wrong. Do you want to try again?", "Wrong token. Try again?" , JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (i == JOptionPane.YES_OPTION) {
					;
				} else {
					check = false;
					break;
				}
			} else {
				break;
			}
		}
		sshCommand(server, sshport, user, pass, jsch, "rm Roberta.py");
		return check;		
	}
	
	/*
	**************************************************************generate token**********************************************************************
	*/
	
	private static String getTokenString(int len, String chars) {
		String result = "";
		
		while(result.length() < len) {
			result = result + getChar(chars);
		}
		return result;
	}
	
	private static char getChar(String chars) {
		int i = getInt(chars.length());
		return chars.charAt(i - 1);
	}
	
	private static int getInt(int max) {
		return (int) (Math.ceil(Math.random()*max));
	}
	
	private static String generateToken() {
		//chars that are used for the token
		String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String token = getTokenString(8,chars);
		return token;
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
	
	/*
	private static void ftpTransfer(String server, int port, String user, String password, FTPClient ftpClient, String file) {
		
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
                System.out.println("Could not login to the ftp server");
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
            System.out.println("Something wrong happened during ftpTransfer!");
            ex.printStackTrace();
        }
	}*/
	
    public static void main(String[] args) {
        	
		/*
		**************************************************************FTP**********************************************************************
		*/
		
		/*
		//Parameters needed for FTP connection. Change these to fit your needs!
		String server = "169.254.235.8";
		int port = 21;
		String user = "nao";
		String pass = "nao";
		FTPClient ftpClient = new FTPClient();
		String file = "StandUp.py";
		*/

		/*
		***************************************************************SSH*********************************************************************
		*/
		
		StringBuilder sb = new StringBuilder();
		JSch jsch = new JSch();
		
		//these information should come from the robotconfiguration in the robertalab
		String server = "169.254.235.8";
		int sshport = 22;
		String user = "nao";
		String pass = "nao";
		
		String execute = "python Roberta.py";
		
		boolean tokenchecked = tokencheck(server, port, sshport, user, pass, ftpClient, jsch);
		
		if (tokenchecked) {
			System.out.println("Token is correct. Tranfer and execute file.")					//for testing only
			//ftpTransfer(server, port, user, pass, ftpClient, file);
			//edit the command to react on the variables for username and host username@remotehost
			//note: file that contains the generated python code should be named Roberta.py
			sshCommand(server, sshport, user, pass, jsch, "scp Roberta.py nao@169.254.235.8:/~/robertalab");
			sshCommand(server, sshport, user, pass, jsch, execute);
			sshCommand(server, sshport, user, pass, jsch, "rm Roberta.py");
		}
		
		System.out.println("Exit!");									//for testing only
		
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