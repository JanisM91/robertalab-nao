from naoqi import ALProxy

videoProxy = ALProxy("ALVideoDevice","192.167.178.1",9559)

# 0 - Top  1 - Bottom
videoProxy.setActiveCamera(0)
