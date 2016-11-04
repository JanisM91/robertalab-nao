from naoqi import ALProxy

posture = ALProxy("ALRobotPosture","192.168.178.1",9559)

pose = "SitRelax"

posture.goToPosture(position,1.0)
