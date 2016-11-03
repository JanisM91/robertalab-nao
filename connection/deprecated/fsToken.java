//Opens a SSH connection to a NAO robot in the local network and transfers a generated Token to it. When the token is entered correctly by the user a file is transferred and executed.

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import javax.swing.JOptionPane;
import java.math.BigInteger;
import java.lang.Number;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.lang.Object;

import com.jcraft.jsch.*;

public class fsToken {
	
	/*
	**************************************************************send and check token**********************************************************************
	*/
	
	//neds to be edited to be compatible with the server. Are Dialogs possible on serverside?
	private static boolean tokencheck(String server, int sshport, String user, String pass, JSch jsch) {
		
		//set this as true to enable the comparison of the copied token with the generated one
		final boolean MD5CHECK = false;
		
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
			
			if(MD5CHECK) {
				if(!validateTransfer(server, sshport, user, pass, generatedToken, jsch)) {
					System.out.println("Error! MD5 sums don't match. Transfer failed!");
					System.exit(-1);
				}
			}
			
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
		//currently all chars are used for the token. test if some are hard to understand and consider to delete them.
		String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String token = getTokenString(8,chars);
		return token;
	}
	
	/*
	**************************************************************validate if the transfer was correct**********************************************************************
	*/
	
	//get the md5 sum of a string
	private static String getMd5(String s) {
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
	
	public static String getMd5FromFile(String filename) throws Exception {
       byte[] b = createChecksum(filename);
       String result = null;

       for (int i=0; i < b.length; i++) {
           result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
       }
       return result;
   }
   
   public static byte[] createChecksum(String filename) throws Exception {
       InputStream fis =  new FileInputStream(filename);

       byte[] buffer = new byte[1024];
       MessageDigest complete = MessageDigest.getInstance("MD5");
       int numRead;

       do {
           numRead = fis.read(buffer);
           if (numRead > 0) {
               complete.update(buffer, 0, numRead);
           }
       } while (numRead != -1);

       fis.close();
       return complete.digest();
   }

	
	//get the content of a .txt file as a String
	private static String readFile(String filename)throws Exception {
		String data = null;
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			data = sb.toString();
		}
		return data;
	}
	
	//merge to one method
	private static boolean validateTransfer(String server, int sshport, String user, String pass, JSch jsch) {
		
		//transfer and execute the python file that gets the md5 sum of a string and copy it back to local
		System.out.println("tranferring md5.py \n");												//for testing only
		sshCommand(server, sshport, user, pass, jsch, "scp md5.py");
		System.out.println("executing md5.py \n");													//for testing only
		sshCommand(server, sshport, user, pass, jsch, "python md5.py");
		System.out.println("transferring the md5.txt to local \n");									//for testing only
		String command = "scp " + user + "@" + server + ":/~/md5nao.txt" + " md5nao.txt";
		sshCommand(server, sshport, user, pass, jsch, command);
		System.out.println("removing the md5.py file \n");											//for testing only
		sshCommand(server, sshport, user, pass, jsch, "rm md5.py");
		sshCommand(server, sshport, user, pass, jsch, "rm md5nao.txt");
		
		
		String md5nao = null, md5local = null;
		
		System.out.println("getting the md5 sum of the copied token \n");							//for testing only
		try {
			md5nao = readFile("md5nao.txt");
		} catch (Exception e) {
			System.out.println("Can't read file." + e);
		}
		
		System.out.println("getting the md5 sum of the local token \n");		//for testing only
		try{
			md5local = getMd5FromFile("Roberta.py");
		} catch (Exception e) {
			System.out.println("Can't open or read file");
			System.exit(-1);
		}
		
		
		System.out.println("comparing the md5 sums \n");											//for testing only
		if(md5nao.equals(md5local))
			return true;
		
		return false;
	}
	
	//only for validating that the token is copied correct.
	private static boolean validateTransfer(String server, int sshport, String user, String pass, String generatedToken, JSch jsch) {
		
		//transfer and execute the python file that gets the md5 sum of a string and copy it back to local
		System.out.println("tranferring md5token.py \n");												//for testing only
		sshCommand(server, sshport, user, pass, jsch, "scp md5.py");
		System.out.println("executing md5token.py \n");													//for testing only
		sshCommand(server, sshport, user, pass, jsch, "python md5token.py");
		System.out.println("transferring the md5naotoken.txt to local \n");									//for testing only
		String command = "scp " + user + "@" + server + ":/~/md5naotoken.txt" + " md5naotoken.txt";
		sshCommand(server, sshport, user, pass, jsch, command);
		System.out.println("removing the md5token.py file \n");											//for testing only
		sshCommand(server, sshport, user, pass, jsch, "rm md5token.py");
		sshCommand(server, sshport, user, pass, jsch, "rm md5naotoken.txt");
		
		
		String md5nao = null, md5local = null;
		
		System.out.println("getting the md5 sum of the copied token \n");							//for testing only
		try {
			md5nao = readFile("md5naotoken.txt");
		} catch (Exception e) {
			System.out.println("Can't read file." + e);
		}
		
		System.out.println("getting the md5 sum of the local token \n");							//for testing only
		md5local = getMd5(generatedToken);
		
		System.out.println("comparing the md5 sums \n");											//for testing only
		if(md5nao.equals(md5local))
			return true;
		
		return false;
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
		
		//only set true if the transfer should be validated with MD5 Checksum
		final boolean CHECKTRANSFER = false;
		
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
		String transfer = "scp " + filename + " " + user + "@" + server + ":/~/";
		
		boolean tokenchecked = tokencheck(server, sshport, user, pass, jsch);
		
		if (tokenchecked) {
			System.out.println("Token is correct. Transfer file.\n");					//for testing only
			sshCommand(server, sshport, user, pass, jsch, transfer);
			
			if(CHECKTRANSFER){
				if(!validateTransfer(server, sshport, user, pass, jsch)) {
					System.out.println("Error! MD5 sums don't match. Transfer failed!");
					System.exit(-1);
				}
			}
			System.out.println("Token is correct. Execute file.\n");					//for testing only
			sshCommand(server, sshport, user, pass, jsch, execute);
			System.out.println("Token is correct. Remove file.\n");					//for testing only
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
