# FCMNode

Backend mínimo en Spring Boot para enviar notificaciones push con **Firebase Cloud Messaging (API v1)**. Recibe el mensaje por HTTP, obtiene un token de acceso con la llave de la cuenta de servicio y se lo reenvía a FCM.

La llave se queda en el servidor: la app nunca la ve.

## Requisitos

- **JDK 17 o superior.** Compruébalo con `java -version`.
- No hace falta instalar Maven: el proyecto trae `mvnw`.

## 1. La llave

En la consola de Firebase, entra a *Configuración del proyecto → Cuentas de servicio* y pulsa **Generar nueva clave privada**. Guarda el archivo descargado en la raíz de este proyecto con el nombre **`fcm-key.json`**.

`fcm-key.json` está en `.gitignore`: no lo subas a ningún repositorio.

## 2. Correr el servidor

```shell
./mvnw spring-boot:run
```

En Windows:

```shell
mvnw.cmd spring-boot:run
```

El servidor queda en `http://localhost:8080/fcm/messages`.

## 3. Enviar un mensaje

Guarda el mensaje en `mensaje.json`:

```json
{
  "message": {
    "topic": "general",
    "notification": {
      "title": "Hola",
      "body": "Mi primera notificación"
    }
  }
}
```

Y envíalo:

```shell
curl -X POST http://localhost:8080/fcm/messages \
  -H "Content-Type: application/json" \
  -d @mensaje.json
```

En PowerShell, escribe `curl.exe` en una sola línea y pon `"@mensaje.json"` entre comillas.

## Respuestas

- **200**: FCM aceptó el mensaje y responde con su nombre, `{"name": "projects/…/messages/…"}`.
- **400**: el cuerpo no tiene `message`.
- **500**: no está `fcm-key.json` en la raíz del proyecto.
- **502**: Google rechazó la llave, por ejemplo porque se borró en la consola.
- **Cualquier otro código**: el error tal como lo devolvió FCM.

## Desde la app

Desde el emulador de Android, tu computador es `10.0.2.2`, así que la URL es `http://10.0.2.2:8080/fcm/messages`. Desde un teléfono real, usa la IP de tu computador en la misma red Wi-Fi.

## Formato anterior

El servidor sigue aceptando el cuerpo con la llave adentro, `{"data": {"message": {…}}, "key": {…}}`. Sirve para un servidor compartido, pero no lo uses en un backend real: la llave nunca debería viajar en una petición.

## Configuración

`src/main/resources/application.properties`:

- `server.servlet.context-path=/fcm`
- `fcm.key-file=fcm-key.json`: la ruta de la llave. Se puede cambiar al arrancar con `./mvnw spring-boot:run -Dspring-boot.run.arguments=--fcm.key-file=/otra/ruta.json`.
