
#include <main.h>

void setup()
{
  //Configura el ble
  bleSetUp();
  Serial.begin(115200);
  //GPS
  serial_conection.begin(9600, SERIAL_8N1, 16, 17);
  //BME208
  if (!bme.begin(0x76))
  {
    Serial.println("Could not find a valid BME280 sensor, check wiring!");
    while (1)
      ;
  }
}

void loop()
{

  updateGPS();
  updateBME();
  updateUV();
  delay(1500);
}

/**
 * Actualiza si es posible la posición gps
 * */
void updateGPS()
{
  if (serial_conection.available())
  {
    gps.encode(serial_conection.read());
  }
  if (gps.location.isUpdated())
  {
    //Leyendo latitude
    updateData(LATITUDE_UUID, gps.location.lat(), 10);
    //Leyendo longitud
    updateData(LONGITUDE_UUID, gps.location.lng(), 10);
    //Leyendo altiud manera de leer la altura
    updateData(ALTITUDE_UUID, gps.altitude.meters(), 3);
  }
}

/**
 * Actualiza los valores de la temperatura, presión y humedo obtenidos por el bme/bmp280
 * */
void updateBME()
{

  // BME208: Temperatura - Presion - Humedad

  //Leyendo temperatura en  ºC
  updateData(TEMPERATURE_UUID, bme.readTemperature(), 3);

  //Leyendo presion en hPa
  updateData(PRESSURE_UUID, bme.readPressure() / 100.0F, 3);
  //Manera alternativa para leer altitud en m, pero muy imprecisa ya que la pression a nivel del mar
  //cambia dependiendo del dia y lugar, mejor usar gps
  //updateData(ALT_POS, bme.readAltitude(SEALEVELPRESSURE_HPA));
  //Read Hummidity in %
  updateData(HUMIDITY_UUID, bme.readHumidity(), 3);
}

/**
 * Actualiza el valor del indice uv
 * */
void updateUV()
{
  float rvVoltage = analogRead(32);
  rvVoltage = rvVoltage / 4095 * 3.3;
  int switchStatement = rvVoltage * 1000;
  //Switch para determinar el indice UV
  switch (switchStatement)
  {
  case 0 ... 49:
    updateData(UV_UUID, 0, 1);
    break;
  case 50 ... 226:
    updateData(UV_UUID, 1, 1);
    break;
  case 227 ... 317:
    updateData(UV_UUID, 2, 1);
    break;
  case 318 ... 407:
    updateData(UV_UUID, 3, 1);
    break;
  case 408 ... 502:
    updateData(UV_UUID, 4, 1);
    break;
  case 503 ... 605:
    updateData(UV_UUID, 5, 1);
    break;
  case 606 ... 695:
    updateData(UV_UUID, 6, 1);
    break;
  case 696 ... 794:
    updateData(UV_UUID, 7, 1);
    break;
  case 795 ... 880:
    updateData(UV_UUID, 8, 1);
    break;
  case 881 ... 975:
    updateData(UV_UUID, 9, 1);
    break;
  case 976 ... 1078:
    updateData(UV_UUID, 10, 1);
    break;
  default:
    if (switchStatement >= 1079)
    {
      updateData(UV_UUID, 11, 1);
    }
    else
    {
      updateData(UV_UUID, 0, 1);
    }
    break;
  }
}