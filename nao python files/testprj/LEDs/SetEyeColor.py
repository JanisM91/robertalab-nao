from naoqi import ALProxy

led = ALProxy("AlLedsProxy","127.0.0.1",9559);

led.fadeRGB("FaceLeds", "White");
