//Opens a SSH connection to a NAO robot in the local network and transfers a generated Token to it. When the token is entered correctly by the user a file is transferred and executed.

import java.io.InputStream;
import javax.swing.JOptionPane;
import java.math.BigInteger;
import java.lang.Number;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.lang.Object;

import com.jcraft.jsch.*;

public class fsToken {
	
	//calculate the md5 of a string
	private String md5(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(s.getBytes(), 0, s.length());
			BigInteger i = new BigInteger(1,md.digest());
			return String.format("%1$032x", i);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	/*
	**************************************************************send and check token**********************************************************************
	*/
	
	//neds to be edited to be compatible with the server. Are Dialogs possible?
	private static boolean tokencheck(String server, int sshport, String user, String pass, JSch jsch) {
		
		//variables
		boolean check = true;
		String generatedToken;
		String transfer = "scp token.py " + user + "@" + server + ":/~/robertalab";
		
		//store generated Token
		generatedToken = generateToken();
		System.out.println(generatedToken);															//for testing only
		
		while(check) {
			//transfer and execute the python file that makes NAO say the generated token
			System.out.println("tranferring token.py");												//for testing only
			sshCommand(server, sshport, user, pass, jsch, "scp token.py");
			System.out.println("executing token check file");										//for testing only
			sshCommand(server, sshport, user, pass, jsch, "python token.py");
			
			//prompt the user to enter the token
			String inputToken = JOptionPane.showInputDialog("Please type the token NAO just said:");
		
			//check if the entered token equals the generated token
			if(!inputToken.equals(generatedToken)){
				int i = JOptionPane.showConfirmDialog(null,"The token you entered was wrong. Do you want to try again?", "Wrong token. Try again?" , JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (i == JOptionPane.YES_OPTION) {
					;//repeat the tokencheck
				} else {
					check = false;
					break;
				}
			} else {
				break;
			}
		}
		//delete the transferred file
		sshCommand(server, sshport, user, pass, jsch, "rm token.py");
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
		//currently all chars are used for the token. test if some are hard to understand and consider ot delete them.
		String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String token = getTokenString(8,chars);
		return token;
	}
	
	/*
	**************************************************************open SSH connection and send command**********************************************************************
	*/
	
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
	
    public static void main(String[] args) {
		
		JSch jsch = new JSch();
		
		//these information should come from the robotconfiguration in the robertalab
		String server = "169.254.235.8";
		int sshport = 22;
		String user = "nao";
		String pass = "nao";
		//note: file that contains the generated python code should be named Roberta.py
		String filename = "Roberta.py";
		
		String execute = "python " + filename;
		String remove = "rm " + filename;
		String transfer = "scp " + filename + " " + user + "@" + server + ":/~/robertalab";
		
		//edit, currently not working
		boolean tokenchecked = tokencheck(server, sshport, user, pass, jsch);
		
		if (tokenchecked) {
			System.out.println("Token is correct. Tranfer file.");					//for testing only
			sshCommand(server, sshport, user, pass, jsch, transfer);
			System.out.println("Token is correct. Execute file.");					//for testing only
			sshCommand(server, sshport, user, pass, jsch, execute);
			System.out.println("Token is correct. Remove file.");					//for testing only
			sshCommand(server, sshport, user, pass, jsch, remove);
		}
		
		System.out.println("Exit!");												//for testing only
		
	}

	//abstract MyUserInfo needed for ssh conection. maybe not required. test the ssh connection without it.
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
