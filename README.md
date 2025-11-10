# Inmobiliaria MR 2025

Proyecto desarrollado para la gestión del Propietario, incluyendo administración de propiedades, con integración a una API en .NET.

---

## Configuración Inicial

Antes de ejecutar el proyecto, asegurarse de **cambiar la IP del host** en el ApiClient para que apunte al host donde se está ejecutando la API.  
> Esto es necesario ya que el proyecto utiliza una API propia desarrollada en .NET y no la propuesta en clase.

---

## Permisos Faltantes

Para el correcto funcionamiento, deben añadirse los siguientes permisos en el archivo `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.CALL_PHONE" />
```
---

## Seguridad

Por motivos de seguridad, **la API Key no se incluyó en este repositorio público**. Agregarla como:

```xml
<meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="PONER_API_KEY" />
```
