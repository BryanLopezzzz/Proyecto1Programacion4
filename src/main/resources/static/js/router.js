/**
 * router.js — SPA Router basado en History API.
 */

const Router = {

    routes: [],

    add(path, handler) {
        // Soporta rutas con parámetros simples: /ruta/:id
        const regex = new RegExp('^' + path.replace(/:\w+/g, '([^/]+)') + '$');
        this.routes.push({ regex, handler, path });
    },

    go(path) {
        history.pushState(null, '', path);
        this.resolve(path);
    },

    resolve(path) {
        UI.renderNav();
        for (const route of this.routes) {
            const match = path.match(route.regex);
            if (match) {
                const params = match.slice(1);
                route.handler(...params);
                return;
            }
        }
        // 404
        UI.render('<div class="container"><h2>Página no encontrada</h2><a class="btn btn-outline" onclick="Router.go(\'/\')">← Inicio</a></div>');
    },

    init() {
        window.addEventListener('popstate', () => this.resolve(location.pathname));
        this.resolve(location.pathname);
    }
};