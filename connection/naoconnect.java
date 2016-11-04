//Opens a SSH connection to a NAO robot in the local network and transfers a generated Token to it. When the token is entered correctly by the user a file is transferred and executed.

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.swing.JOptionPane;
import java.math.BigInteger;
import java.lang.Number;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.lang.Object;

import com.jcraft.jsch.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class naoconnect {
	
	/*
	**************************************************************send and check token**********************************************************************
	*/
	
	//neds to be edited to be compatible with the server. Are Dialogs possible on serverside?
	private static boolean tokencheck(String server, int sshport, String user, String pass, JSch jsch, FTPClient ftpClient) {
		
		//set this as true to enable the comparison of the copied token with the generated one
		final boolean MD5CHECK = false;
		
		//variables
		boolean check = true;
		String generatedToken;
		
		//store generated Token
		generatedToken = generateToken();
		System.out.println("generated Token: " + generatedToken);									//for testing only
		
		try {
			writeToken(generatedToken);
		} catch (Exception e) {
			System.out.println("Can't write file.");
			System.exit(-1);
		}
		
		while(check) {
			//transfer and execute the python file that makes NAO say the generated token
			System.out.println("tranferring token.py");												//for testing only
			ftpTransfer(server, 21, user, pass, "token.py", false, false, ftpClient);
			System.out.println("executing token check file");										//for testing only
			sshCommand(server, sshport, user, pass, jsch, "python token.py");
			
			if(MD5CHECK) {
				if(!validateTransfer(server, sshport, 21, user, pass, jsch, ftpClient, true)) {
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
		//the speech engine of the robot makes certain combinations to words instead of single chars 
		//for example: 7 W is spoken as 7 Watt
		//maybe edit the token.py generation to add more spaces between the single characters **DONE, requires testing**
		String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String token = getTokenString(8,chars);
		return token;
	}
	
	//write the the generated Token into token.py
	private static void writeToken(String generatedToken)throws Exception {
		
		try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("token.py"),"utf-8"))) {
			writer.write("from naoqi import ALProxy");
			writer.write(System.lineSeparator());
			writer.write("from naoqi import ALBroker");
			writer.write(System.lineSeparator());
			writer.write("from optparse import OptionParser");
			writer.write(System.lineSeparator());
			//from robot configuration
			writer.write("NAO_IP = \"169.254.235.8\"");
			writer.write(System.lineSeparator());
			writer.write("generatedToken = \"" + generatedToken.charAt(0) + "  " + generatedToken.charAt(1) + "  " +  generatedToken.charAt(2) + "  " + generatedToken.charAt(3) + "  " + generatedToken.charAt(4) + "  " + generatedToken.charAt(5) + "  " + generatedToken.charAt(6) + "  " + generatedToken.charAt(7) + "\"" );
			writer.write(System.lineSeparator());
			writer.write("parser = OptionParser()");
			writer.write(System.lineSeparator());
			writer.write("parser.add_option(\"--pip\",help=\"Parent broker port. The IP address or your robot\", dest=\"pip\")");
			writer.write(System.lineSeparator());
			writer.write("parser.add_option(\"--pport\",help=\"Parent broker port. The port NAOqi is listening to\",dest=\"pport\",type=\"int\")");
			writer.write(System.lineSeparator());
			writer.write("parser.set_defaults(pip=NAO_IP,pport=9559)");
			writer.write(System.lineSeparator());
			writer.write("(opts, args_) = parser.parse_args()");
			writer.write(System.lineSeparator());
			writer.write("pip = opts.pip");
			writer.write(System.lineSeparator());
			writer.write("pport = opts.pport");
			writer.write(System.lineSeparator());
			writer.write("myBroker = ALBroker(\"myBroker\",\"0.0.0.0\", 0, pip, pport)");
			writer.write(System.lineSeparator());
			writer.write("tts = ALProxy(\"ALTextToSpeech\")");
			writer.write(System.lineSeparator());
			writer.write("tts.say(\"Hello. I read out the token now: \" + generatedToken)");
			writer.write(System.lineSeparator());
		}
	}
	
	/*
	**************************************************************validate if the transfer was correct**********************************************************************
	*/
	
	/*get the md5 sum of a string, not used after recent changes in validateTransfer
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
	}*/
	
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
	private static boolean validateTransfer(String server, int sshport, int ftpport, String user, String pass, JSch jsch, FTPClient ftpClient, boolean token) {
		
		String pyFile, txtFile, localFile, md5nao = null, md5local = null;
		if(token) {
			pyFile = "md5token.py";
			txtFile = "md5naotoken.txt";
			localFile = "token.py";
		} else {
			pyFile = "md5.py";
			txtFile = "md5nao.txt";
			localFile = "Roberta.py";
		}
			
		//transfer and execute the python file that gets the md5 sum of a string and copy it back to local
		System.out.println("transferring md5token.py \n");												//for testing only
		ftpTransfer(server, ftpport, user, pass, pyFile, false, false, ftpClient);
		System.out.println("executing md5token.py \n");													//for testing only
		sshCommand(server, sshport, user, pass, jsch, "python " + pyFile);
		System.out.println("transferring the md5naotoken.txt to local \n");								//for testing only
		ftpTransfer(server, ftpport, user, pass, txtFile, false, true, ftpClient);
		System.out.println("removing the md5token.py file \n");											//for testing only
		sshCommand(server, sshport, user, pass, jsch, "rm " + pyFile);
		sshCommand(server, sshport, user, pass, jsch, "rm " + txtFile);
		
		System.out.println("getting the md5 sum of the copied file \n");								//for testing only
		try {
			md5nao = readFile(txtFile);
			} catch (Exception e) {
				System.out.println("Can't read file. " + e);
			}
		System.out.println(md5nao);																		//for testing only
		
		System.out.println("getting the md5 sum of the local file \n");									//for testing only
		try{
			md5local = getMd5FromFile(localFile);
		} catch (Exception e) {
			System.out.println("Can't open or read file");
			System.exit(-1);
		}
		
		System.out.println("comparing the md5 sums: \n");												//for testing only
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
			
			//fingerprint is not accepted
			//uncomment this part if key fingerprint is not accepted
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			
			//establish session connection
			session.connect();
		
			//open channel and send command
			System.out.println("sshCommand: Open Channel");						//for testing only
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
	**************************************************************open FTP connection and transfer file**********************************************************************
	*/	
	
	//change to FTPS?
	private static void showServerReply(FTPClient ftpClient) {
		
        String[] replies = ftpClient.getReplyStrings();
		
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
	
	private static void ftpTransfer(String server, int port, String user, String password, String file, boolean rename, boolean getFile, FTPClient ftpClient) {
		
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
                System.out.println("LOGGED IN SERVER!");					//for testing only
				
				//Use local passive mode to avoid problems with firewalls
				ftpClient.enterLocalPassiveMode();
				
				if(getFile) {
					FileOutputStream output = new FileOutputStream("md5nao.txt");
					System.out.println("Retrieve file");					//for testing only
					ftpClient.retrieveFile("md5nao.txt",output);
					output.close();
				} else {
					//store file on the robot
					InputStream input = new FileInputStream(file);
					System.out.println("Transferring file.");				//for testing only
					if(rename) {
						ftpClient.storeFile("Roberta.py",input);
					} else {
						ftpClient.storeFile(file,input);
					}
					input.close();
				}
				//logout
				System.out.println("LOGGING OUT!");							//for testing only
				ftpClient.logout();
            }
        } catch (IOException e) {
            System.out.println("Something wrong happened!");
            e.printStackTrace();
        }
	}
	
    public static void main(String[] args) {
		
		//only set true if the transfer should be validated with MD5 Checksum
		final boolean CHECKTRANSFER = false;
		//set true if a token should be generated, tranferred and checked
		final boolean CHECKTOKEN = true;
		//set true if the hal file should be transferred first
		final boolean TRANSFERHAL = false;
		
		JSch jsch = new JSch();
		FTPClient ftpClient = new FTPClient();
		
		//these information should come from the robotconfiguration in the robertalab
		String server = "169.254.235.8";
		String user = "nao";
		String pass = "nao";
		
		int sshport = 22;
		int ftpport = 21;
		
		//note: file that contains the generated python code should be named Roberta.py
		String filename = "Roberta.py";
		String execute = "python " + filename;
		String remove = "rm " + filename;
		boolean tokenchecked = false;
		
		if(CHECKTOKEN)
			tokenchecked = tokencheck(server, sshport, user, pass, jsch, ftpClient);
	
		if(TRANSFERHAL)
			ftpTransfer(server, ftpport, user, pass, "hal.py", false, false, ftpClient);
		
		if (tokenchecked) {
			System.out.println("Token is correct. Transfer file.\n");				//for testing only
			ftpTransfer(server, ftpport, user, pass, "SitDown.py", true, false, ftpClient);
			
			if(CHECKTRANSFER){
				if(!validateTransfer(server, sshport, ftpport, user, pass, jsch, ftpClient, false)) {
					System.out.println("Error! MD5 sums don't match. Transfer failed! Please try again.");
					System.exit(-1);
				}
			}
			System.out.println("Token is correct. Execute file.\n");				//for testing only
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