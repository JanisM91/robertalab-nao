from naoqi import ALProxy

tts = ALProxy("AlTextToSpeech", "192.168.178.1",9559)

volume = tts.getVolume()

