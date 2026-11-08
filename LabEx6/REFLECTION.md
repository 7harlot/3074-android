# COMP3074 Sensors Lab: Reflection Answers

## Sensor availability on my test device
- Test device: ____________________
- Ambient temperature sensor: ____________________ (available / not available)
- Gyroscope: ____________________ (available / not available)

## 1. Why must getDefaultSensor() be treated as potentially returning null?
Android does not require devices to include every sensor. If the requested sensor is missing, getDefaultSensor() returns null, and using that reference without a check would crash the app with a NullPointerException. That is why the sensor fields are declared as `Sensor?` and the listeners are only registered through `?.let`.

## 2. Why should sensor listeners be unregistered when the activity is paused?
A registered sensor keeps delivering events, and keeps the hardware active, even when the app is not on screen. That wastes battery and CPU for data nobody is looking at. Unregistering in onPause() stops this, and onResume() registers the listeners again when the user returns.

## 3. What is the difference between gyroscope angular velocity and device orientation?
The gyroscope measures how fast the device is rotating around each axis at this moment, not which way it is pointing. A phone held tilted but perfectly still reads close to zero. Orientation is the actual angle of the device, which comes from sources such as the rotation vector sensor, or the accelerometer combined with the magnetometer.

## 4. What units are used for the gyroscope values in this lab?
Radians per second (rad/s). One radian per second is about 57 degrees per second.

## 5. Why is battery temperature not a valid replacement for TYPE_AMBIENT_TEMPERATURE?
Battery temperature measures the battery itself, which is heated by charging, the processor, and the user's hand. It is usually well above the surrounding air temperature, so reporting it as ambient temperature would give incorrect data.

## 6. How does mutableStateOf allow a SensorEvent callback to update the Compose UI?
mutableStateOf creates observable state. Compose records which composables read each state value. When onSensorChanged() assigns a new value, Compose recomposes only the composables that read it, so the screen updates automatically. A plain variable would change without Compose knowing, and the UI would never refresh.
