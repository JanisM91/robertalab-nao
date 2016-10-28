//Opens a FTP connection to a NAO robot in the local network and transfers a file to it.
//Warning: Change the IP!
//@author: Janis Mohr
//@date: 25.10.2016

//import java.io.IOException;
import java.io.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPconnect {
	
    private static void showServerReply(FTPClient ftpClient) {
		
        String[] replies = ftpClient.getReplyStrings();
		
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
	
    public static void main(String[] args) {
        String server = "169.254.235.8";
        int port = 21;
        String user = "nao";
        String pass = "nao";
		String remote = null;
        FTPClient ftpClient = new FTPClient();
		
        try {
            ftpClient.connect(server, port);
            showServerReply(ftpClient);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Operation failed. Server reply code: " + replyCode);
                return;
            }
            boolean success = ftpClient.login(user, pass);
            showServerReply(ftpClient);
            if (!success) {
                System.out.println("Could not login to the server");
                return;
            } else {
                System.out.println("LOGGED IN SERVER!");
				//Use local passive mode to avoid problems with firewalls
				ftpClient.enterLocalPassiveMode();
				//open Input Stream and store file on the robot
				InputStream input1 = new FileInputStream("StandUp.py");
				InputStream input2 = new FileInputStream("SitDown.py");
				System.out.println("Transfering first file.");
				ftpClient.storeFile("StandUp.py",input1);
				System.out.println("Transfering second file");
				ftpClient.storeFile("SitDown.py",input2);
				input1.close();
				input2.close();
				System.out.println("LOGGING OUT!");
				ftpClient.logout();
            }
        } catch (IOException ex) {
            System.out.println("Something wrong happened!");
            ex.printStackTrace();
        }
    }
}