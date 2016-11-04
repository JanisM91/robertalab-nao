from naoqi import ALProxy

trackerProxy = ALProxy("ALTracker","192.167.178.1",9559)

#coordinates of point NAO will point at in metres
x = 0.1
y = 0.1
z = 0.1

#percentage of maximum speed
maxSpeed = 1.0

#define the baseframe for the given coordinates
# 0 - Torso  1 - World  2 - Robot
frame = 0

#make NAO point at desired position
trackerProxy.pointAt("Arms", [x,y,z],frame,maxSpeed)
