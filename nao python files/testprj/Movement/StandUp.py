#Import ALProxy
from naoqi import ALProxy

#Create ALProxy to desired module(here: AlRobotPosture)
posture = ALProxy("ALRobotPosture","169.254.235.8",9559)

#Call StandUp method
posture.goToPosture("Stand",1.0)
