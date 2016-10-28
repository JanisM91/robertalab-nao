'''
Created on 06.10.2016

@author: jmohr
'''

from naoqi import ALProxy

memoryProxy = ALProxy("AlMemory","192.168.178.1",9559)

memoryProxy.getData("Device/SubDeviceList/InertialSensor/AccelerometerX/Sensor/Value")
memoryProxy.getData("Device/SubDeviceList/InertialSensor/AccelerometerY/Sensor/Value")
memoryProxy.getData("Device/SubDeviceList/InertialSensor/AccelerometerZ/Sensor/Value")