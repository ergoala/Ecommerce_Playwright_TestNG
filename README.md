# ECommerce Playwright TestNG

Framework de automatización E2E para el sitio **Liverpool.com.mx** construido con **Playwright (Java)**, **TestNG** y **ExtentReports**.

## Stack tecnológico

| Herramienta | Propósito |
|---|---|
| Playwright 1.49.0 | Automatización de navegador (Chromium, Firefox, WebKit) |
| TestNG 7.11.0 | Test runner y manejo del ciclo de vida de pruebas |
| ExtentReports 4.1.5 | Reportes HTML con capturas de pantalla |
| Apache POI 5.4.0 | Lectura de datos desde Excel (.xlsx) |
| Jackson 2.13.4.2 | Parseo de JSON (localizadores y datos de prueba) |
| Log4j 2.17.2 | Logs de consola y archivo |
| JavaFaker 1.0.2 | Generación de datos fake |

## Estructura del proyecto

```
src/test/java/com/liverpool/
├── commons/                        # Clases base e infraestructura
│   ├── BaseClass.java              # Ciclo de vida de Playwright + ThreadLocal
│   ├── BasePage.java               # Métodos de interacción web (click, fill, etc.)
│   ├── ExcelReader.java            # Lector de Excel con Apache POI
│   ├── ReadProperties.java         # Cargador de configuration.properties
│   ├── Utility.java                # Utilidades (validaciones, datos fake, números)
│   ├── ExtentListeners.java        # Listener TestNG para ExtentReports
│   ├── ExtentLogger.java           # Logging estructurado a ExtentReports
│   ├── CreateExcelData.java        # Generador del Excel de datos de prueba
│   └── InstallBrowsers.java        # Instalador de binarios de Playwright
├── ui/
│   ├── pages/                      # Page Objects
│   │   ├── HomePage.java
│   │   ├── SearchResultsPage.java
│   │   └── HeaderFooterPage.java
│   └── tests/                      # Casos de prueba
│       ├── TC_01_Search_Product.java
│       ├── TC_02_Navigation_Menu.java
│       └── TC_03_Login.java        # Placeholder (incompleto)
```

```
src/test/resources/
├── config/configuration.properties    # Configuración general
├── locatorsDefinition/HomePage.json   # Localizadores XPath/CSS
├── TestData/LiverpoolTestData.json    # Datos de prueba (respaldo)
├── Excel/LiverpoolTestData.xlsx       # Datos de prueba (fuente principal)
└── log4j2.xml                         # Configuración de Log4j
```

## Patrones de diseño

- **Page Object Model (POM):** Interacciones encapsuladas en clases por página.
- **Data-Driven Testing:** Los datos fluyen desde Excel hacia los mapas estáticos de `Utility`.
- **Externalized Locators:** Todos los selectores están en `HomePage.json` con XPath y CSS.
- **ThreadLocal:** `BaseClass` usa `ThreadLocal` para soportar ejecución paralela.
- **Property-Based Config:** Tiempos de espera, URLs y rutas en `.properties`.

## Prerrequisitos

- Java 8+
- Maven 3.6+

## Instalación

```bash
# Clonar el repositorio
git clone <repo-url>
cd Ecommerce_Playwright_TestNG

# Generar datos de prueba y descargar navegadores (una vez)
mvn test -Dgroups=setup -P setup

# Ejecutar todas las pruebas
mvn test
```

## Ejecución

```bash
# Suite completa
mvn test

# Por grupo
mvn test -Dgroups=smoke
mvn test -Dgroups=search

# Navegador específico
mvn test -Dbrowser=firefox
mvn test -Dbrowser=webkit

# Prueba específica
mvn test -Dtest=TC_01_Search_Product
```

## Reportes

Los reportes HTML se generan en `TestReport/` con timestamp, incluyendo capturas de pantalla en fallos.

## Configuración

Editar `src/test/resources/config/configuration.properties`:

| Propiedad | Descripción |
|---|---|
| `baseUrl` | URL del entorno (https://www.liverpool.com.mx) |
| `ENVIRONMENT` | QA o PROD |
| `DATASHEET_PATH` | Ruta al Excel de datos |
| `browser` | Navegador por defecto (chromium) |
| `headless` | Modo headless (true/false) |
| `timeout` | Timeout global en ms |
| `navigationTimeout` | Timeout de navegación en ms |
