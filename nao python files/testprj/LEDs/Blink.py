from naoqi import ALProxy
import time

led = ALProxy("AlLedsProxy","127.0.0.1",9559);

led.fadeRGB("FaceLeds", 0xffffff);
time.sleep(0.5);
led.fadeRGB("FaceLeds", 0x000000);
time.sleep(0.5);
led.fadeRGB("FaceLeds", 0xffffff);
