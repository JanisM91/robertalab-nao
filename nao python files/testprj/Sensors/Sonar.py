from naoqi import ALProxy

memoryProxy = ALProxy("AlMemory","192.168.178.1",9559)
sonarProxy = ALProxy("ALSonar","192.168.178.1",9559)

#Subscribe to Sonars, launch them(at Hardware level) and start data acquisition
sonarProxy.subscribe("TestApplication")

#Retrieve sonar data from ALMemory (distance in meters)
memoryProxy.getData("Device/SubDeviceList/US/Right/Sensor/Value")

#Unsubscribe from sonars and stop them (at Hardware level)
sonarProxy.unsubscribe("TestApplication")
