from naoqi import ALProxy

memoryProxy = ALProxy("AlMemory","192.168.178.1",9559)

position = "Vorne"

if position == "Vorne":
    memoryProxy.getData("FrontTactilTouched")
elif position == "Mitte":
    memoryProxy.getData("MiddleTactilTouched")
elif position == "Hinten":
    memoryProxy.getData("RearTactilTouched")
