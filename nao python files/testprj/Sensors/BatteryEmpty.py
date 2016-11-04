from naoqi import ALProxy

batteryProxy = ALProxy("ALBatteryProxy","192.168.178.1",9559)

status = batteryProxy.getBatteryCharge()
