'''
Created on 13.10.2016

@author: jmohr
'''

from naoqi import ALProxy

trackerProxy = ALProxy("ALTracker","192.167.178.1",9559)

#coordinates of point NAO will look at in metres
x = 0.1
y = 0.1
z = 0.1

#percentage of maximum speed
maxSpeed = 0.4

#define the frame for the given coordinates
# 0 - Torso  1 - World  2 - Robot
frame = 0

#shall NAO use his whole body or just move his head to look at point
body = False

#make NAO look at desired point
trackerProxy.lookAt([x,y,z], frame, maxSpeed, body)