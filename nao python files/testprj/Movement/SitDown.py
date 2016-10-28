'''
Created on 06.10.2016

@author: jmohr
'''

from naoqi import ALProxy

posture = ALProxy("ALRobotPosture","127.0.0.1",4969)

posture.goToPosture("Sit",1.0)