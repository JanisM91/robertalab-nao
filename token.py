'''
Created on 06.10.2016

@author: jmohr
'''
#Import ALProxy
from naoqi import ALProxy
from naoqi import ALBroker
from optparse import OptionParser

NAO_IP = "169.254.235.8"
#just for testing. the generated token from the .java file should be inserted here.
generatedToken = "abcdefg"

#add information for pip
parser = OptionParser()
parser.add_option("--pip",help="Parent broker port. The IP address or your robot", dest="pip")
parser.add_option("--pport",help="Parent broker port. The port NAOqi is listening to",dest="pport",type="int")
parser.set_defaults(pip=NAO_IP,pport=9559)

(opts, args_) = parser.parse_args()
pip = opts.pip
pport = opts.pport


#We need this Borker to be able to construct NAOqi modules and subscribe to other modules
#The broker must stay alive until the program exits
myBroker = ALBroker("myBroker","0.0.0.0",   #Listen to anyone
                    0,                      #find a free port and use it
                    pip,                    #parent broker ip
                    pport)                  #parent broker port

#Create ALProxy to desired module(here: AlRobotPosture)
tts = ALProxy("ALTextToSpeech")

#Call StandUp method
tts.say(generatedToken)
