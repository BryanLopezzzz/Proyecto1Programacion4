/**
 * api.js — Capa de comunicación con el backend REST.
 * Todas las funciones retornan Promises.
 */

const API_BASE = '/api';

const Api = {
    //en este js cumplimos con el PASO 3 mencionado en la
    // la diapositiva de JWT en "¿Cómo funciona JWT?"

    //Uso del Token en Peticiones: El cliente envía el token
    //en el encabezado Authorization para acceder a recursos protegidos.

    //justo aquí el cliente guarda el token y lo manda en cada request
    _headers(includeAuth = true) {
        const h = { 'Content-Type': 'application/json' };
        if (includeAuth) {
            const token = Auth.getToken();
            if (token) h['Authorization'] = 'Bearer ' + token;
        }
        return h;
    },

    async _fetch(url, options = {}) {
        const res = await fetch(API_BASE + url, {
            ...options,
            headers: { ...this._headers(), ...(options.headers || {}) }
        });
        if (res.status === 401) { Auth.logout(); return null; }
        const data = await res.json().catch(() => ({}));
        if (!res.ok) throw new Error(data.error || 'Error del servidor');
        return data;
    },

    // ---- AUTH ----
    login: (username, clave) =>
        Api._fetch('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, clave }),
            headers: Api._headers(false)
        }),

    // ---- PÚBLICOS ----
    getPuestosPublicos:  () => Api._fetch('/puestos/publicos'),
    buscarPuestos:       (params) => Api._fetch('/puestos/buscar?' + new URLSearchParams(params)),
    getCaracteristicas:  () => Api._fetch('/caracteristicas'),
    getMonedas:          () => Api._fetch('/monedas'),

    // ---- REGISTRO ----
    registrarEmpresa:  (data) => Api._fetch('/registro/empresa',  { method: 'POST', body: JSON.stringify(data) }),
    registrarOferente: (data) => Api._fetch('/registro/oferente', { method: 'POST', body: JSON.stringify(data) }),

    // ---- EMPRESA ----
    empresa: {
        perfil:         () => Api._fetch('/empresa/perfil'),
        puestos:        () => Api._fetch('/empresa/puestos'),
        publicarPuesto: (d) => Api._fetch('/empresa/puestos', { method: 'POST', body: JSON.stringify(d) }),
        desactivar:     (id) => Api._fetch(`/empresa/puestos/${id}/desactivar`, { method: 'POST' }),
        candidatos:     (id) => Api._fetch(`/empresa/puestos/${id}/candidatos`),
        candidato:      (id) => Api._fetch(`/empresa/candidatos/${id}`)
    },

    // ---- OFERENTE ----
    oferente: {
        perfil:          () => Api._fetch('/oferente/perfil'),
        habilidades:     () => Api._fetch('/oferente/habilidades'),
        agregarHabilidad:(d)  => Api._fetch('/oferente/habilidades', { method: 'POST', body: JSON.stringify(d) }),
        eliminarHabilidad:(id) => Api._fetch(`/oferente/habilidades/${id}`, { method: 'DELETE' }),
        buscarPuestos:   (params) => Api._fetch('/oferente/puestos/buscar?' + new URLSearchParams(params)),
        subirCv: (file) => {
            const fd = new FormData(); fd.append('archivo', file);
            return fetch(API_BASE + '/oferente/cv', {
                method: 'POST',
                headers: { 'Authorization': 'Bearer ' + Auth.getToken() },
                body: fd
            }).then(r => r.json());
        }
    },

    // ---- ADMIN ----
    admin: {
        empresasPendientes:  () => Api._fetch('/admin/empresas/pendientes'),
        aprobarEmpresa:  (id) => Api._fetch(`/admin/empresas/${id}/aprobar`,  { method: 'POST' }),
        rechazarEmpresa: (id) => Api._fetch(`/admin/empresas/${id}/rechazar`, { method: 'POST' }),
        oferentesPendientes: () => Api._fetch('/admin/oferentes/pendientes'),
        aprobarOferente:  (id) => Api._fetch(`/admin/oferentes/${id}/aprobar`,  { method: 'POST' }),
        rechazarOferente: (id) => Api._fetch(`/admin/oferentes/${id}/rechazar`, { method: 'POST' }),
        caracArbol:       () => Api._fetch('/admin/caracteristicas'),
        caracTodos:       () => Api._fetch('/admin/caracteristicas/todos'),
        crearCarac:       (d)  => Api._fetch('/admin/caracteristicas', { method: 'POST', body: JSON.stringify(d) }),
        reportePuestos:   (mes, anio) => Api._fetch(`/admin/reportes/puestos?mes=${mes}&anio=${anio}`),
        reporteCoincid:   (mes, anio) => Api._fetch(`/admin/reportes/coincidencias?mes=${mes}&anio=${anio}`)
    }
};