<template>
    <div>
        <NavbarAdmin />
        <div class="container page">

            <div class="page-header">
                <h1>Productos</h1>
                <button class="btn btn-primary" @click="abrirModal()">+ Nuevo producto</button>
            </div>

            <div class="tabla-card card">
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Precio</th>
                            <th>Stock</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="loading">
                            <td colspan="5" class="text-center">Cargando...</td>
                        </tr>
                        <tr v-for="p in productos" :key="p.id">
                            <td class="text-muted">#{{ p.id }}</td>
                            <td>
                                <div class="producto-nombre-cell">
                                    <img v-if="p.imagen1" :src="p.imagen1" class="tabla-img" />
                                    <div v-else class="tabla-img-placeholder">🛍️</div>
                                    <span>{{ p.nombre }}</span>
                                </div>
                            </td>
                            <td class="precio-cell">${{ formatPrecio(p.precio) }}</td>
                            <td>
                                <span
                                    :class="p.stock > 10 ? 'badge-success' : p.stock > 0 ? 'badge-warning' : 'badge-danger'"
                                    class="badge">
                                    {{ p.stock }}
                                </span>
                            </td>
                            <td>
                                <div class="acciones-cell">
                                    <button class="btn btn-outline btn-sm" @click="abrirModal(p)">Editar</button>
                                    <button class="btn btn-danger btn-sm"
                                        @click="confirmarEliminar(p.id)">Eliminar</button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- MODAL -->
            <div v-if="modal" class="modal-overlay" @click.self="modal = false">
                <div class="modal">
                    <div class="modal-header">
                        <h3>{{ editando ? 'Editar producto' : 'Nuevo producto' }}</h3>
                        <button @click="modal = false" class="btn-close">✕</button>
                    </div>
                    <div class="modal-body">
                        <div class="form-grid">
                            <div class="input-group">
                                <label>Nombre</label>
                                <input v-model="form.nombre" type="text" placeholder="Nombre del producto" />
                            </div>
                            <div class="input-group">
                                <label>Precio</label>
                                <input v-model="form.precio" type="number" placeholder="9990" />
                            </div>
                            <div class="input-group">
                                <label>Stock</label>
                                <input v-model="form.stock" type="number" placeholder="50" />
                            </div>
                            <div class="input-group span-2">
                                <label>Descripción</label>
                                <input v-model="form.descripcion" type="text" placeholder="Descripción del producto" />
                            </div>

                            <!-- IMÁGENES -->
                            <div class="input-group span-2">
                                <label>Imágenes (máximo 3)</label>
                                <div class="imagenes-grid">
                                    <div v-for="n in 3" :key="n" class="imagen-slot" @click="triggerUpload(n)">
                                        <img v-if="form['imagen' + n]" :src="form['imagen' + n]" />
                                        <div v-else class="imagen-placeholder">
                                            <span v-if="subiendo === n" class="subiendo-icon">⏳</span>
                                            <span v-else>
                                                <span class="upload-icon">📷</span>
                                                <span class="upload-text">Foto {{ n }}</span>
                                            </span>
                                        </div>
                                        <button v-if="form['imagen' + n]" class="btn-remove-img"
                                            @click.stop="form['imagen' + n] = ''">✕</button>
                                    </div>
                                </div>
                                <input ref="fileInput" type="file" accept="image/*" style="display:none"
                                    @change="subirImagen" />
                            </div>
                        </div>
                        <p v-if="error" class="error-msg">{{ error }}</p>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-outline" @click="modal = false">Cancelar</button>
                        <button class="btn btn-primary" @click="guardar" :disabled="guardando || subiendo !== null">
                            {{ guardando ? 'Guardando...' : subiendo ? 'Subiendo imagen...' : 'Guardar' }}
                        </button>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>

<script>
import NavbarAdmin from "../../components/NavbarAdmin.vue";
import { getProductos, crearProducto, editarProducto, eliminarProducto } from "../../services/api";

const CLOUD_NAME = "diq24kgrd";
const UPLOAD_PRESET = "Productos";

export default {
    components: { NavbarAdmin },
    data() {
        return {
            productos: [],
            loading: true,
            modal: false,
            editando: null,
            guardando: false,
            error: "",
            subiendo: null,
            slotActual: null,
            form: {
                nombre: "",
                descripcion: "",
                precio: "",
                stock: "",
                imagen1: "",
                imagen2: "",
                imagen3: ""
            }
        };
    },
    async mounted() { await this.cargar(); },
    methods: {
        async cargar() {
            this.loading = true;
            try { this.productos = await getProductos(); } catch { }
            this.loading = false;
        },

        abrirModal(p = null) {
            this.editando = p;
            this.error = "";
            this.form = p
                ? {
                    nombre: p.nombre,
                    descripcion: p.descripcion || "",
                    precio: p.precio,
                    stock: p.stock,
                    imagen1: p.imagen1 || "",
                    imagen2: p.imagen2 || "",
                    imagen3: p.imagen3 || ""
                }
                : { nombre: "", descripcion: "", precio: "", stock: "", imagen1: "", imagen2: "", imagen3: "" };
            this.modal = true;
        },

        async guardar() {
            this.guardando = true;
            this.error = "";
            try {
                if (this.editando) {
                    await editarProducto(this.editando.id, this.form);
                } else {
                    await crearProducto(this.form);
                }
                this.modal = false;
                await this.cargar();
            } catch {
                this.error = "Error al guardar";
            }
            this.guardando = false;
        },

        async confirmarEliminar(id) {
            if (confirm("¿Eliminar este producto?")) {
                await eliminarProducto(id);
                await this.cargar();
            }
        },

        triggerUpload(n) {
            this.slotActual = n;
            this.$refs.fileInput.value = "";
            this.$refs.fileInput.click();
        },

        async subirImagen(event) {
            const file = event.target.files[0];
            if (!file) return;

            this.subiendo = this.slotActual;
            this.error = "";

            const formData = new FormData();
            formData.append("file", file);
            formData.append("upload_preset", UPLOAD_PRESET);
            formData.append("folder", "SmartLogix/Productos");

            try {
                const res = await fetch(
                    `https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`,
                    { method: "POST", body: formData }
                );
                const data = await res.json();
                if (data.secure_url) {
                    this.form["imagen" + this.slotActual] = data.secure_url;
                } else {
                    this.error = "Error al subir imagen a Cloudinary";
                }
            } catch {
                this.error = "Error de conexión con Cloudinary";
            }

            this.subiendo = null;
            event.target.value = "";
        },

        formatPrecio(n) { return Number(n).toLocaleString("es-CL"); }
    }
}
</script>

<style scoped>
.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
}

.page-header h1 {
    font-size: 2rem;
}

.tabla-card {
    overflow: hidden;
}

.tabla {
    width: 100%;
    border-collapse: collapse;
}

.tabla th {
    text-align: left;
    padding: 14px 20px;
    color: var(--text-muted);
    font-size: 0.8rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    border-bottom: 1px solid var(--border);
}

.tabla td {
    padding: 14px 20px;
    border-bottom: 1px solid var(--border);
    font-size: 0.95rem;
}

.tabla tr:last-child td {
    border-bottom: none;
}

.tabla tr:hover td {
    background: var(--bg-hover);
}

.text-muted {
    color: var(--text-muted);
}

.text-center {
    text-align: center;
}

.producto-nombre-cell {
    display: flex;
    align-items: center;
    gap: 12px;
}

.tabla-img {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    object-fit: cover;
}

.tabla-img-placeholder {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    background: var(--bg-hover);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.2rem;
}

.precio-cell {
    font-weight: 600;
    color: var(--accent);
}

.acciones-cell {
    display: flex;
    gap: 8px;
}

.modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.7);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 200;
    padding: 24px;
}

.modal {
    background: var(--bg-card);
    border: 1px solid var(--border);
    border-radius: var(--radius-lg);
    width: 100%;
    max-width: 560px;
    max-height: 90vh;
    overflow-y: auto;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    border-bottom: 1px solid var(--border);
    position: sticky;
    top: 0;
    background: var(--bg-card);
    z-index: 1;
}

.btn-close {
    background: none;
    color: var(--text-secondary);
    font-size: 1.2rem;
    padding: 4px 8px;
}

.modal-body {
    padding: 24px;
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 20px 24px;
    border-top: 1px solid var(--border);
    position: sticky;
    bottom: 0;
    background: var(--bg-card);
}

.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
}

.span-2 {
    grid-column: span 2;
}

.error-msg {
    color: var(--danger);
    font-size: 0.85rem;
    margin-top: 12px;
}

/* IMÁGENES */
.imagenes-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
    margin-top: 8px;
}

.imagen-slot {
    aspect-ratio: 1;
    border-radius: var(--radius);
    border: 2px dashed var(--border);
    overflow: hidden;
    cursor: pointer;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--bg-secondary);
    transition: var(--transition);
}

.imagen-slot:hover {
    border-color: var(--accent);
    background: var(--bg-hover);
}

.imagen-slot img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.imagen-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
    color: var(--text-muted);
}

.upload-icon {
    font-size: 1.5rem;
    display: block;
}

.upload-text {
    font-size: 0.75rem;
}

.subiendo-icon {
    font-size: 1.5rem;
}

.btn-remove-img {
    position: absolute;
    top: 6px;
    right: 6px;
    background: rgba(0, 0, 0, 0.7);
    color: white;
    border-radius: 50%;
    width: 22px;
    height: 22px;
    font-size: 0.7rem;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: var(--transition);
}

.btn-remove-img:hover {
    background: var(--danger);
}
</style>