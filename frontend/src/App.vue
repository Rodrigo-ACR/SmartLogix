<template>
  <router-view />
  <ToastNotification ref="toast" />
  <ConfirmModal ref="confirm" />
</template>

<script>
import ToastNotification from "./components/ToastNotification.vue";
import ConfirmModal from "./components/ConfirmModal.vue";

export default {
  components: { ToastNotification, ConfirmModal },
  mounted() {
    // Exponer globalmente para que cualquier vista los use
    window.$toast   = this.$refs.toast;
    window.$confirm = this.$refs.confirm;
  }
}
</script>