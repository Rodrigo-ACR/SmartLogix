<template>
    <div>
        <NavbarAdmin />
        <div class="container page">
            <div class="page-header">
                <h1 class="page-title" style="margin:0">Envíos</h1>
                <button class="btn-primary" @click="abrirModal">+ Nuevo Envío</button>
            </div>

            <div v-if="errorCarga" class="error-banner-admin">
                <span>🔴</span>
                <div>
                    <strong>{{ errorCarga }}</strong>
                    <p>El sistema se recuperará automáticamente cuando el servicio vuelva a estar disponible.</p>
                </div>
                <button @click="$router.go(0)" class="btn-retry-admin">🔄 Reintentar</button>
            </div>

            <!-- TABLA 1: Envíos activos -->
            <div class="seccion-titulo">
                <span class="dot-activo"></span>
                Envíos en curso
                <span class="badge-count">{{ enviosActivos.length }}</span>
            </div>
            <div class="tabla-card card">
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Pedido</th>
                            <th>Dirección</th>
                            <th>Transportista</th>
                            <th>Fecha estimada</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <template v-if="loading">
                            <tr v-for="i in 5" :key="'sk' + i" class="skeleton-row-tr">
                                <td v-for="j in 7" :key="j">
                                    <div class="sk-cell"></div>
                                </td>
                            </tr>
                        </template>
                        <tr v-if="!loading && enviosActivos.length === 0">
                            <td colspan="7" class="text-center text-muted">No hay envíos en curso</td>
                        </tr>
                        <tr v-for="e in activosPaginados" :key="e.id">
                            <td class="text-muted">#{{ e.id }}</td>
                            <td>#{{ e.pedidoId }}</td>
                            <td>{{ e.direccion }}</td>
                            <td>{{ e.transportista || '-' }}</td>
                            <td>{{ formatFecha(e.fechaEstimada) }}</td>
                            <td><span :class="badgeEnvio(e.estado)" class="badge">{{ e.estado }}</span></td>
                            <td>
                                <select class="estado-select" :value="e.estado"
                                    @change="cambiarEstado(e.id, $event.target.value)">
                                    <option value="PENDIENTE">PENDIENTE</option>
                                    <option value="ASIGNADO">ASIGNADO</option>
                                    <option value="EN_TRANSITO">EN_TRANSITO</option>
                                    <option value="ENTREGADO">ENTREGADO</option>
                                    <option value="INCIDENCIA">INCIDENCIA</option>
                                </select>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div v-if="totalPaginasActivos > 1" class="paginacion">
                <button class="pag-btn" :disabled="paginaActivos === 1" @click="paginaActivos--">
                    <Icons name="arrow-left" :size="18" color="currentColor" />
                </button>
                <button v-for="n in totalPaginasActivos" :key="n" class="pag-num"
                    :class="{ active: paginaActivos === n }" @click="paginaActivos = n">{{ n }}</button>
                <button class="pag-btn" :disabled="paginaActivos === totalPaginasActivos" @click="paginaActivos++">
                    <Icons name="arrow-right" :size="18" color="currentColor" />
                </button>
            </div>

            <!-- TABLA 2: Envíos entregados -->
            <div class="seccion-titulo" style="margin-top:2rem">
                <span class="dot-entregado">👑</span>
                Envíos entregados
                <span class="badge-count badge-count-gold">{{ enviosEntregados.length }}</span>
            </div>
            <div class="tabla-card card">
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Pedido</th>
                            <th>Dirección</th>
                            <th>Transportista</th>
                            <th>Fecha entrega</th>
                            <th>Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="!loading && enviosEntregados.length === 0">
                            <td colspan="6" class="text-center text-muted">Aún no hay envíos entregados</td>
                        </tr>
                        <tr v-for="e in entregadosPaginados" :key="e.id" class="fila-entregada">
                            <td class="text-muted">#{{ e.id }}</td>
                            <td>#{{ e.pedidoId }}</td>
                            <td>{{ e.direccion }}</td>
                            <td>{{ e.transportista || '-' }}</td>
                            <td>{{ formatFecha(e.fechaEstimada) }}</td>
                            <td><span class="badge badge-success">✓ ENTREGADO</span></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <div v-if="totalPaginasEntregados > 1" class="paginacion" style="margin-top:1rem">
            <button class="pag-btn" :disabled="paginaEntregados === 1" @click="paginaEntregados--">
                <Icons name="arrow-left" :size="18" color="currentColor" />
            </button>
            <button v-for="n in totalPaginasEntregados" :key="n" class="pag-num"
                :class="{ active: paginaEntregados === n }" @click="paginaEntregados = n">{{ n }}</button>
            <button class="pag-btn" :disabled="paginaEntregados === totalPaginasEntregados" @click="paginaEntregados++">
                <Icons name="arrow-right" :size="18" color="currentColor" />
            </button>
        </div>

        <!-- MODAL CREAR ENVÍO -->
        <div v-if="mostrarModal" class="modal-overlay" @click.self="cerrarModal">
            <div class="modal-box">
                <h2 class="modal-title">Nuevo Envío</h2>

                <div class="form-group">
                    <label>Pedido (EN_PREPARACION)</label>
                    <select v-model="nuevoEnvio.pedidoId" class="form-control" @change="onPedidoSeleccionado">
                        <option value="" disabled>Selecciona un pedido</option>
                        <option v-for="p in pedidosEnPreparacion" :key="p.id" :value="p.id">
                            #{{ p.id }} — {{ p.cliente }} ({{ p.nombreProducto }})
                        </option>
                    </select>
                    <p v-if="pedidosEnPreparacion.length === 0" class="text-muted hint">
                        No hay pedidos en EN_PREPARACION
                    </p>
                </div>

                <div class="form-group">
                    <label>Dirección de entrega</label>
                    <input v-model="nuevoEnvio.direccion" class="form-control"
                        placeholder="Ej: Av. Principal 123, Santiago" />
                </div>

                <!-- SELECTOR TRANSPORTISTA -->
                <div class="form-group">
                    <label>Transportista</label>
                    <select v-model="transportistaSeleccionado" class="form-control" @change="onTransportistaChange">
                        <option value="">Selecciona un transportista</option>
                        <option value="Correos de Chile">Correos de Chile</option>
                        <option value="Chilexpress">Chilexpress</option>
                        <option value="Starken">Starken</option>
                        <option value="Transportista Local">Transportista Local</option>
                        <option value="otro">Otro (escribir)</option>
                    </select>
                </div>

                <div class="form-group" v-if="transportistaSeleccionado === 'otro'">
                    <label>Nombre del transportista</label>
                    <input v-model="nuevoEnvio.transportista" class="form-control"
                        placeholder="Ej: DHL, FedEx, Blue Express..." />
                </div>

                <div class="form-group">
                    <label>Fecha estimada de entrega</label>
                    <input v-model="nuevoEnvio.fechaEstimada" type="date" class="form-control" />
                </div>

                <p v-if="errorModal" class="text-danger">{{ errorModal }}</p>

                <div class="modal-actions">
                    <button class="btn-secondary" @click="cerrarModal">Cancelar</button>
                    <button class="btn-primary" @click="crearEnvio" :disabled="creando">
                        {{ creando ? 'Creando...' : 'Crear Envío' }}
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import NavbarAdmin from "../../components/NavbarAdmin.vue";
import Icons from "../../components/Icons.vue";
import { getEnvios, cambiarEstadoEnvio, crearEnvio, getPedidos, getUsuarios } from "../../services/api";
import "@/assets/styles/enviosview.css";

export default {
    components: { NavbarAdmin, Icons },
    data() {
        return {
            envios: [],
            loading: true,
            mostrarModal: false,
            pedidosEnPreparacion: [],
            usuarios: [],
            creando: false,
            errorCarga: "",
            paginaActivos: 1,
            paginaEntregados: 1,
            porPagina: 6,
            errorModal: "",
            transportistaSeleccionado: "",
            nuevoEnvio: {
                pedidoId: "",
                direccion: "",
                transportista: "",
                fechaEstimada: ""
            }
        };
    },
    computed: {
        enviosActivos() {
            return this.envios.filter(e => e.estado !== "ENTREGADO");
        },
        enviosEntregados() {
            return this.envios.filter(e => e.estado === "ENTREGADO");
        },
        activosPaginados() {
            const i = (this.paginaActivos - 1) * this.porPagina;
            return this.enviosActivos.slice(i, i + this.porPagina);
        },
        totalPaginasActivos() {
            return Math.ceil(this.enviosActivos.length / this.porPagina);
        },
        entregadosPaginados() {
            const i = (this.paginaEntregados - 1) * this.porPagina;
            return this.enviosEntregados.slice(i, i + this.porPagina);
        },
        totalPaginasEntregados() {
            return Math.ceil(this.enviosEntregados.length / this.porPagina);
        }
    },
    async mounted() {
        try { this.envios = await getEnvios(); } catch { this.errorCarga = "⚠️ No se pudieron cargar los envíos. Servicio temporalmente no disponible."; }
        this.loading = false;
    },
    methods: {
        async abrirModal() {
            this.errorModal = "";
            this.transportistaSeleccionado = "";
            this.nuevoEnvio = { pedidoId: "", direccion: "", transportista: "", fechaEstimada: "" };
            try {
                const [todos, usuarios] = await Promise.all([getPedidos(), getUsuarios()]);
                this.pedidosEnPreparacion = todos.filter(p => p.estado === "EN_PREPARACION");
                this.usuarios = usuarios;
            } catch {
                this.pedidosEnPreparacion = [];
                this.usuarios = [];
            }
            this.mostrarModal = true;
        },
        cerrarModal() {
            this.mostrarModal = false;
            this.errorModal = "";
        },
        onPedidoSeleccionado() {
            const pedido = this.pedidosEnPreparacion.find(p => p.id == this.nuevoEnvio.pedidoId);
            if (!pedido) return;
            const usuario = this.usuarios.find(u =>
                u.correo === pedido.cliente || u.nombre === pedido.cliente
            );
            if (usuario && usuario.direccion) {
                this.nuevoEnvio.direccion = usuario.direccion;
            }
        },
        onTransportistaChange() {
            if (this.transportistaSeleccionado !== "otro") {
                this.nuevoEnvio.transportista = this.transportistaSeleccionado;
            } else {
                this.nuevoEnvio.transportista = "";
            }
        },
        async crearEnvio() {
            if (!this.nuevoEnvio.pedidoId) { this.errorModal = "Debes seleccionar un pedido"; return; }
            if (!this.nuevoEnvio.direccion.trim()) { this.errorModal = "La dirección es obligatoria"; return; }
            if (!this.nuevoEnvio.transportista.trim()) { this.errorModal = "El transportista es obligatorio"; return; }
            this.creando = true;
            this.errorModal = "";
            try {
                const envioCreado = await crearEnvio(this.nuevoEnvio);
                this.envios.unshift(envioCreado);
                this.cerrarModal();
            } catch {
                this.errorModal = "Error al crear el envío. Intenta de nuevo.";
            }
            this.creando = false;
        },
        async cambiarEstado(id, estado) {
            try {
                await cambiarEstadoEnvio(id, estado);
                const e = this.envios.find(e => e.id === id);
                if (e) e.estado = estado;
            } catch { window.$toast.mostrar("Error al cambiar estado del envío", "error"); }
        },
        badgeEnvio(e) {
            const m = {
                PENDIENTE: "badge-warning", ASIGNADO: "badge-accent",
                EN_TRANSITO: "badge-warning", ENTREGADO: "badge-success", INCIDENCIA: "badge-danger"
            };
            return m[e] || "badge-accent";
        },
        formatFecha(f) {
            if (!f) return "-";
            return new Date(f).toLocaleDateString("es-CL");
        }
    }
}
</script>

<style scoped>
.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
}

.seccion-titulo {
    display: flex;
    align-items: center;
    gap: 0.6rem;
    font-size: 1rem;
    font-weight: 700;
    color: #94a3b8;
    margin-bottom: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

.dot-activo {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background: #00ff88;
    box-shadow: 0 0 6px #00ff88;
    animation: pulse 1.5s infinite;
}

.dot-entregado {
    font-size: 1rem;
}

.badge-count {
    background: #1e293b;
    color: #94a3b8;
    border-radius: 20px;
    padding: 2px 10px;
    font-size: 0.8rem;
    font-weight: 700;
}

.badge-count-gold {
    background: #ffd70022;
    color: #ffd700;
    border: 1px solid #ffd70044;
}

.fila-entregada td {
    opacity: 0.6;
}

.modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal-box {
    background: #1e293b;
    border-radius: 16px;
    padding: 2rem;
    width: 100%;
    max-width: 480px;
    border: 1px solid #334155;
    max-height: 90vh;
    overflow-y: auto;
}

.modal-title {
    font-size: 1.4rem;
    font-weight: 700;
    color: #f1f5f9;
    margin-bottom: 1.5rem;
}

.form-group {
    margin-bottom: 1rem;
}

.form-group label {
    display: block;
    font-size: 0.85rem;
    color: #94a3b8;
    margin-bottom: 0.4rem;
}

.form-control {
    width: 100%;
    padding: 0.6rem 0.8rem;
    background: #0f172a;
    border: 1px solid #334155;
    border-radius: 8px;
    color: #f1f5f9;
    font-size: 0.95rem;
    box-sizing: border-box;
}

.form-control:focus {
    outline: none;
    border-color: #7c3aed;
}

.modal-actions {
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
    margin-top: 1.5rem;
}

.btn-primary {
    background: #7c3aed;
    color: white;
    border: none;
    padding: 0.6rem 1.2rem;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
}

.btn-primary:hover {
    background: #6d28d9;
}

.btn-primary:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.btn-secondary {
    background: transparent;
    color: #94a3b8;
    border: 1px solid #334155;
    padding: 0.6rem 1.2rem;
    border-radius: 8px;
    cursor: pointer;
}

.btn-secondary:hover {
    background: #0f172a;
}

.text-danger {
    color: #ef4444;
    font-size: 0.85rem;
    margin-top: 0.5rem;
}

.hint {
    font-size: 0.8rem;
    margin-top: 0.4rem;
}

@keyframes pulse {

    0%,
    100% {
        opacity: 1;
    }

    50% {
        opacity: 0.4;
    }
}
</style>