'''
Created on 13.10.2016

@author: jmohr
'''

from naoqi import ALProxy

photoProxy = ALProxy("ALPhotoCapture","192.167.178.1",9559)

recordFolder = "/home/nao/recordings/cameras/"

# 0 - 160*120  1 - 320*240  2 - 640*480  3 - 1280*960
photoProxy.setResolution(1)

# 0 - Top  1 - Bottom
photoProxy.setCameraID(0)

photoProxy.setPictureFormat("jpg")

photoProxy.takePicture(recordFolder, "testfile")