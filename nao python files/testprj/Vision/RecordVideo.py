'''
Created on 13.10.2016

@author: jmohr
'''

from naoqi import ALProxy

videoRecorder = ALProxy("ALVideoRecorder","192.167.178.1",9559)

recordFolder = "/home/nao/recordings/cameras/"

# 0 - 160*120  1 - 320*240  2 - 640*480
videoRecorder.setResolution(2)

# 0 - Top  1 - Bottom
videoRecorder.setCameraID(0)

videoRecorder.setVideoFormat("MJPG")

videoRecorder.startRecording(recordFolder, "fileName")