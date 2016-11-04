from naoqi import ALProxy

led = ALProxy("AlLedsProxy","127.0.0.1",9559);

led.setIntensity("EarLeds", 0.3);
