# robertalab-nao

### Introduction
Make a connection between robertalab and NAO possible and allow the robertalab to generate python code for NAO.

### Folder nao python files

These python files are made to run on a NAO. Some will only work as remote module. You need to change the ip in order to make these work on your robot. The structure of the folders reprents the structure that is planned for the implementation of the robot NAO into the Open Roberta Lab.

The playground.py is used to test the qimessaging service.

### Folder connection

These files are a first approach to make it possible to establish an connection between the robertalab server and the robot NAO.

This is currently under development. Further information are in the comments in the files.

* The naoconnect.java file is the latest version. It transfers a token and checks it and afterwards sends a file to NAO and executes it.
* SitDown.py and StandUp.py are two files that are transferred for testing purposes.
* token.py is transferred to a remote NAO by naoconnect.java and makes NAO say a randomly generated token.

* the following files are deprecated and are considered to document the progress before the project was made available on GitHub
* fsconnectNew.java is the first version where the ftp and ssh parts are separated in two different methods.
* FTPconnect.java is a very early version that only allows to transfer files to a remote NAO via ftp.
* fsToken.java is a late version that allows (transfer) and commands only via SSH. Transfer won't work properly because NAO rejects the RSA fingerprint for security reasons



### Testing

A NAO V4 or V5 is required and you need the latest version of [Java](https://java.com/de/download/).
Additionaly these .jar's are required:
* For the ftp connection: [ApacheCommonsNet](https://commons.apache.org/proper/commons-net/download_net.cgi)
* For the ssh connection: [JavaSecureChannel](http://www.jcraft.com/jsch/)

If you want to edit the .py files that are made for NAO you also need [Python](https://www.python.org/) and the latest Python SDK from [Softbank Robotics](https://www.ald.softbankrobotics.com/en).

To try it out:

    Clone the repository: git clone https://github.com/OpenRoberta/robertalab-nao.git

Open naoconnect.java in your favourite editor. Edit the configuration parameters(ip,port,...) to fit your needs. You need the additional .jar's mentioned above in the same path as your naoconnect.java file.
* Compile it: javac -cp commons-net-3.1.jar;jsch-0.1.54.jar naoconnect.java
* Execute it: java -cp commons-net-3.1.jar;jsch-0.1.54.jar;. naoconnect


### TODO

* informations (server, user, password) should be obtained from robotconfiguration
* improve the random token generation **NOT NECESSARY?**
* make the connection compatible to robertalab
* ~~add a hal file for methods that would otherwise generate more than one line of code~~ **DONE**
* ~~transfer the hal file to the robot~~ **DONE**
* edit the python files to import the hal file

* ~~The transferred files should be checked. (Maybe via MD5) Method is implemented but not used yet. An additional file needs to be transferred on the robot to do this. Implement this with a switch to disable it.~~ **DONE**
* ~~The transfer can be done via SSH. The ftp part is not necessary and should therefore be deleted. The method 'sshcommand' has to be extended.~~ **DONE**
* ~~String for file copying over SSH/SCP should be generated out of variables~~ **DONE**
* ~~rename/translate the python files and folders~~ **DONE**

