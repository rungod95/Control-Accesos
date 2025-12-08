import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { VitePWA } from 'vite-plugin-pwa';

export default defineConfig({
  plugins: [
    vue(),
    VitePWA({
      registerType: 'autoUpdate',
      includeAssets: ['vite.svg'],
      manifest: {
        name: 'Control Accesos Mina',
        short_name: 'Accesos',
        description: 'Portal PWA con roles Trabajador, Operador, Administrador y Visitante.',
        start_url: '/',
        display: 'standalone',
        background_color: '#020617',
        theme_color: '#0ea5e9',
        icons: [
          {
            src: 'vite.svg',
            sizes: '192x192',
            type: 'image/svg+xml',
          },
        ],
      },
    }),
  ],
});
