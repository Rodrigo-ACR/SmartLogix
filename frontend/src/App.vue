<template>
  <div>

    <!-- NAVBAR -->
    <nav class="navbar">
      <h2>SmartLogix 🚀</h2>
      <div v-if="token">
        <span>{{ rol }}</span>
        <button @click="logout">Salir</button>
      </div>
    </nav>

    <!-- LOGIN -->
    <div v-if="!token" class="login">
      <h2>Iniciar sesión</h2>

      <input v-model="user.username" placeholder="Usuario" />
      <input v-model="user.password" type="password" placeholder="Contraseña" />

      <button @click="login">Entrar</button>

      <p v-if="error" class="error">{{ error }}</p>
    </div>

    <!-- DASHBOARD -->
    <div v-else class="dashboard">

      <!-- CARDS -->
      <div class="cards">
        <div class="card" @click="cargar('productos')">
          <h3>📦 Productos</h3>
          <p>{{ stats.productos }}</p>
        </div>

        <div class="card" v-if="rol === 'ADMIN'" @click="cargar('pedidos')">
          <h3>🧾 Pedidos</h3>
          <p>{{ stats.pedidos }}</p>
        </div>

        <div class="card" @click="cargar('envios')">
          <h3>🚚 Envíos</h3>
          <p>{{ stats.envios }}</p>
        </div>
      </div>

      <!-- CLOUDINARY -->
      <div class="upload">
        <h3>Subir Imagen ☁️</h3>

        <input type="file" @change="seleccionarImagen" />
        <button @click="subirImagen">Subir Imagen</button>

        <img v-if="imagenUrl" :src="imagenUrl" width="200" />
      </div>

      <!-- TABLA -->
      <div class="tabla">
        <table v-if="datos.length > 0">
          <thead>
            <tr>
              <th v-for="(v, k) in datos[0]" :key="k">{{ k }}</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="item in datos" :key="item.id">

              <td v-for="(v, k) in item" :key="k">
                <img v-if="k === 'imagenUrl'" :src="v" width="60" />
                <span v-else>{{ v }}</span>
              </td>

              <!-- BOTONES -->
              <td v-if="rol === 'ADMIN'">
                <button @click="editarProducto(item)">✏️</button>
                <button @click="eliminarProducto(item.id)">🗑️</button>
              </td>

            </tr>
          </tbody>
        </table>

        <p v-else class="empty">Selecciona una opción</p>
      </div>

      <p v-if="errorApi" class="error">{{ errorApi.mensaje }}</p>

    </div>

  </div>
</template>

<script>
export default {
  data() {
    return {
      user: { username: "", password: "" },
      token: localStorage.getItem("token"),
      rol: localStorage.getItem("rol"),
      datos: [],
      error: null,
      errorApi: null,
      archivo: null,
      imagenUrl: null,
      stats: {
        productos: 0,
        pedidos: 0,
        envios: 0
      }
    }
  },

  mounted() {
    if (this.token) {
      this.cargarStats()
    }
  },

  methods: {

    async login() {
      this.error = null

      try {
        const res = await fetch("http://localhost:8085/auth/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(this.user)
        })

        const data = await res.json()

        if (!res.ok) {
          this.error = data.mensaje
          return
        }

        localStorage.setItem("token", data.token)
        localStorage.setItem("rol", data.rol)

        this.token = data.token
        this.rol = data.rol

        this.cargarStats()

      } catch {
        this.error = "Error de conexión"
      }
    },

    logout() {
      localStorage.clear()
      location.reload()
    },

    async cargar(tipo) {
      this.errorApi = null
      this.datos = []

      let url = "http://localhost:8085/api/productos"

      if (tipo === "pedidos") url += "/pedidos"
      if (tipo === "envios") url += "/envios"

      try {
        const res = await fetch(url, {
          headers: {
            Authorization: "Bearer " + this.token
          }
        })

        const data = await res.json()

        if (!res.ok) {
          this.errorApi = data
        } else {
          this.datos = data
        }

      } catch {
        this.errorApi = { mensaje: "Error con el servidor" }
      }
    },

    async cargarStats() {
      try {
        const headers = {
          Authorization: "Bearer " + this.token
        }

        const prod = await fetch("http://localhost:8085/api/productos", { headers })
        const pedidos = await fetch("http://localhost:8085/api/productos/pedidos", { headers })
        const envios = await fetch("http://localhost:8085/api/productos/envios", { headers })

        this.stats.productos = (await prod.json()).length || 0
        this.stats.pedidos = pedidos.ok ? (await pedidos.json()).length : 0
        this.stats.envios = (await envios.json()).length || 0

      } catch {
        console.log("Error stats")
      }
    },

    seleccionarImagen(e) {
      this.archivo = e.target.files[0]
    },

    async eliminarProducto(id) {

      if (!confirm("¿Eliminar producto?")) return

      try {
        const res = await fetch(`http://localhost:8085/api/productos/${id}`, {
          method: "DELETE",
          headers: {
            "Authorization": "Bearer " + this.token
          }
        })

        if (!res.ok) {
          const error = await res.text()
          alert("Error: " + error)
          return
        }

        alert("Producto eliminado 🔥")
        await this.cargar("productos")

      } catch (e) {
        alert("Error conectando con el BFF")
      }
    },

    async editarProducto(p) {

      const nombre = prompt("Nuevo nombre", p.nombre)
      const precio = prompt("Nuevo precio", p.precio)

      if (!nombre || !precio) return

      const actualizado = {
        id: p.id,
        nombre: nombre,
        precio: parseFloat(precio), // 🔥 importante
        imagenUrl: p.imagenUrl
      }

      try {
        const res = await fetch(`http://localhost:8085/api/productos/${p.id}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + this.token
          },
          body: JSON.stringify(actualizado)
        })

        if (!res.ok) {
          const error = await res.text()
          alert("Error: " + error)
          return
        }

        alert("Producto actualizado 🔥")
        await this.cargar("productos")

      } catch (e) {
        alert("Error conectando con el BFF")
      }
    },

    async subirImagen() {

      if (!this.archivo) {
        alert("Selecciona una imagen")
        return
      }

      const formData = new FormData()
      formData.append("file", this.archivo)

      try {
        const res = await fetch("http://localhost:8085/api/imagenes/upload", {
          method: "POST",
          body: formData
        })

        const data = await res.json()

        this.imagenUrl = data.url

      } catch {
        alert("Error subiendo imagen")
      }
    }
  }
}
</script>

<style>
body {
  margin: 0;
  font-family: Arial;
  background: #0f172a;
  color: white;
}

/* NAVBAR */
.navbar {
  display: flex;
  justify-content: space-between;
  background: #020617;
  padding: 15px 30px;
}

/* LOGIN */
.login {
  text-align: center;
  margin-top: 100px;
}

.login input {
  display: block;
  margin: 10px auto;
  padding: 10px;
  border-radius: 8px;
}

/* DASHBOARD */
.dashboard {
  padding: 20px;
}

/* CARDS */
.cards {
  display: flex;
  gap: 20px;
}

.card {
  flex: 1;
  background: #1e293b;
  padding: 20px;
  border-radius: 15px;
  cursor: pointer;
}

.card:hover {
  background: #334155;
}

/* UPLOAD */
.upload {
  margin-top: 30px;
  padding: 20px;
  background: #1e293b;
  border-radius: 15px;
}

/* TABLA */
.tabla {
  margin-top: 20px;
}

table {
  width: 100%;
  background: white;
  color: black;
  border-radius: 10px;
}

th {
  background: #3b82f6;
  color: white;
}

td,
th {
  padding: 10px;
}

.empty {
  text-align: center;
  margin-top: 20px;
}

.error {
  color: red;
  margin-top: 10px;
}
</style>