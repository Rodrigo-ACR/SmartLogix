### 🌐 Asignación de Puertos - Ecosistema SmartLogix

| Componente | Puerto | Descripción |
| :--- | :--- | :--- |
| **BFF (Comunicador)** | `8085` | Puerta de entrada única para el Frontend |
| **MS Inventario** | `8091` | Gestión de stock y productos |
| **MS Pedidos** | `8092` | Procesamiento de órdenes de compra |
| **MS Envíos** | `8093` | Seguimiento y logística de despacho |

---


## 🛠️ Stack Tecnológico
* **Backend:** Java 17+ con Spring Boot 3.x
* **Base de Datos:** PostgreSQL (Database per Service)
* **Frontend:** Vue.js (NPM)
* **Control de Versiones:** Git / GitHub

## 🌿 Estrategia de Ramas (GitFlow)
Para este proyecto se implementó una estrategia de ramificación organizada:
* `main`: Código productivo y estable.
* `develop`: Rama de integración para nuevas funcionalidades.
* `feature/`: Ramas específicas para cada microservicio y componente (BFF, Inventario, Pedidos, Envíos).
