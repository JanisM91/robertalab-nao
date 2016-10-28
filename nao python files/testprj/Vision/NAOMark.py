'''
Created on 13.10.2016

@author: jmohr
'''

from naoqi import ALProxy

IP = "192.168.178.1"
PORT = 9559

# Create a proxy to ALLandMarkDetection
markProxy = ALProxy("ALLandMarkDetection", IP, PORT)

# Subscribe to the ALLandMarkDetection extractor
#The period parameter specifies - in milliseconds - how often ALLandMarkDetection tries to run its detection method
period = 500
markProxy.subscribe("Test_Mark", period, 0.0 )

# Create a proxy to ALMemory.
memProxy = ALProxy("ALMemory", IP, PORT)

# Get data from landmark detection (assuming landmark detection has been activated).
data = memProxy.getData("LandmarkDetected")


'''

If no Naomarks are detected, the variable is empty. More precisely, it is an array with zero element, (i.e., printed as [ ] in python).

If N Naomarks are detected, then the variable structure consists of two fields:

[ [ TimeStampField ] [ Mark_info_0 , Mark_info_1, . . . , Mark_info_N-1 ] ] with:

TimeStampField = [ TimeStamp_seconds, Timestamp_microseconds ]. This field is the time stamp of the image that was used to perform the detection.
Mark_info = [ ShapeInfo, ExtraInfo ]. For each detected mark, we have one Mark_info field.
ShapeInfo = [ 1, alpha, beta, sizeX, sizeY, heading]. alpha and beta represent the Naomark’s location in terms of camera angles - sizeX and sizeY are the mark’s size in camera angles - the heading angle describes how the Naomark is oriented about the vertical axis with regards to the robot’s head.
ExtraInfo = [ MarkID ] . Mark ID is the number written on the Naomark and which corresponds to its pattern.

'''