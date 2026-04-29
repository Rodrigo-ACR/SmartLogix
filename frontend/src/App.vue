<template>
  <div class="container">
    <h1>SmartLogix 🚀</h1>

    <!-- BOTONES -->
    <div class="botones">
      <button @click="cargar('productos')">📦 Productos</button>
      <button @click="cargar('pedidos')">🧾 Pedidos</button>
      <button @click="cargar('envios')">🚚 Envíos</button>
    </div>

    <!-- FORMULARIO -->
    <div class="formulario">
      <h2>Crear Pedido</h2>

      <input v-model="nuevo.cliente" placeholder="Cliente" />
      <input v-model="nuevo.producto" placeholder="Producto" />
      <input v-model.number="nuevo.cantidad" type="number" placeholder="Cantidad" />
      <input v-model="nuevo.fecha" type="date" />

      <button @click="crearPedido">Guardar Pedido</button>
    </div>

    <!-- ERROR -->
    <div v-if="error" class="error">
      {{ error.mensaje }}
    </div>

    <!-- TABLA -->
    <table v-if="datos.length > 0">
      <thead>
        <tr>
          <th v-for="(value, key) in datos[0]" :key="key">
            {{ key }}
          </th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="item in datos" :key="item.id">
          <td v-for="(value, key) in item" :key="key">
            {{ value }}
          </td>
        </tr>
      </tbody>
    </table>

    <p v-else>No hay datos...</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      datos: [],
      error: null,
      nuevo: {
        cliente: "",
        producto: "",
        cantidad: 0,
        fecha: ""
      }
    }
  },
  methods: {

    async cargar(tipo) {
      this.error = null
      this.datos = []

      let url = "http://localhost:8085/api/productos"

      if (tipo === "pedidos") url += "/pedidos"
      if (tipo === "envios") url += "/envios"

      try {
        const res = await fetch(url)
        const data = await res.json()

        if (!res.ok) {
          this.error = data
        } else {
          this.datos = data
        }

      } catch (e) {
        this.error = { mensaje: "No se pudo conectar al BFF" }
      }
    },

    async crearPedido() {
      this.error = null

      try {
        const res = await fetch("http://localhost:8085/api/productos/pedidos", {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(this.nuevo)
        })

        const data = await res.json()

        if (!res.ok) {
          this.error = data
        } else {
          alert("Pedido creado correctamente 🚀")
          this.cargar("pedidos")
        }

      } catch (e) {
        this.error = { mensaje: "Error al crear pedido" }
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

.container {
  padding: 20px;
  text-align: center;
}

.botones button {
  margin: 10px;
  padding: 12px 20px;
  background: #3b82f6;
  border: none;
  border-radius: 10px;
  color: white;
  cursor: pointer;
}

.formulario {
  margin-top: 20px;
}

.formulario input {
  margin: 5px;
  padding: 10px;
  border-radius: 8px;
  border: none;
}

.formulario button {
  margin-top: 10px;
  padding: 10px 20px;
  background: #22c55e;
  border: none;
  border-radius: 10px;
  color: white;
  cursor: pointer;
}

table {
  width: 100%;
  margin-top: 20px;
  background: white;
  color: black;
}

th, td {
  padding: 10px;
}

.error {
  color: red;
  margin-top: 10px;
}
</style>