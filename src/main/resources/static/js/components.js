/**
 * components.js — Helpers de UI reutilizables.
 */

const UI = {

    render(html) {
        document.getElementById('app').innerHTML = html;
    },

    toast(msg, ok = true) {
        const t = document.createElement('div');
        t.className = ok ? 'alert-success' : 'alert-error';
        t.style.cssText = 'position:fixed;top:20px;right:20px;z-index:9999;min-width:260px;padding:14px 18px';
        t.textContent = msg;
        document.body.appendChild(t);
        setTimeout(() => t.remove(), 3500);
    },

    loading() {
        this.render('<div class="loading">Cargando…</div>');
    },

    // Árbol de checkboxes para características
    renderArbolCheckboxes(raices, seleccionadas = [], mostrarNivel = false) {
        const renderNodo = (nodo, depth = 0) => {
            const pad = depth * 16;
            const checked = seleccionadas.includes(nodo.id) ? 'checked' : '';
            const nivelSel = mostrarNivel ? `
        <select name="nivel_${nodo.id}" class="nivel-sel">
          <option value="1">Básico</option>
          <option value="2" selected>Intermedio</option>
          <option value="3">Avanzado</option>
        </select>` : '';
            let html = `<li style="margin-left:${pad}px;margin:6px 0 6px ${pad}px">
        <label>
          <input type="checkbox" name="sel_${nodo.id}" value="${nodo.id}" ${checked}
                 data-id="${nodo.id}"/>
          <span>${nodo.nombre}</span>
        </label>
        ${nivelSel}`;
            if (nodo.hijos && nodo.hijos.length) {
                html += `<ul style="list-style:none;padding:0">`;
                nodo.hijos.forEach(h => html += renderNodo(h, depth + 1));
                html += `</ul>`;
            }
            return html + '</li>';
        };

        return `<ul style="list-style:none;padding:0">
      ${raices.map(r => renderNodo(r)).join('')}
    </ul>`;
    },

    // Barra de progreso de coincidencia
    barraCoincidencia(pct) {
        return `<div class="bar-wrap">
      <div class="bar-outer"><div class="bar-inner" style="width:${pct}%"></div></div>
      <span class="bar-label">${pct.toFixed(1)}%</span>
    </div>`;
    },

    // Badge de nivel numérico → texto
    nivelTexto(n) {
        return n === 1 ? 'Básico' : n === 2 ? 'Intermedio' : 'Avanzado';
    },

    // Nav dinámico según rol
    renderNav() {
        const links = document.getElementById('nav-links');
        const userDiv = document.getElementById('nav-user');
        const role = Auth.getRole();

        if (!Auth.isLogged()) {
            links.innerHTML = `
        <a onclick="Router.go('/')">Inicio</a>
        <a onclick="Router.go('/buscar')">Buscar puestos</a>
        <a onclick="Router.go('/registro/empresa')">Registro Empresa</a>
        <a onclick="Router.go('/registro/oferente')">Registro Oferente</a>`;
            userDiv.innerHTML = `<a onclick="Router.go('/login')" style="color:#fecdd3;text-decoration:none;font-size:13px">Login</a>`;
            return;
        }

        const user = Auth.getUser();
        let navHtml = '';

        if (role === 'ROLE_EMPRESA') {
            navHtml = `
        <a onclick="Router.go('/empresa/dashboard')">Dashboard</a>
        <a onclick="Router.go('/empresa/puestos')">Mis puestos</a>
        <a onclick="Router.go('/empresa/puestos/nuevo')">Publicar puesto</a>`;
        } else if (role === 'ROLE_OFERENTE') {
            navHtml = `
        <a onclick="Router.go('/oferente/dashboard')">Dashboard</a>
        <a onclick="Router.go('/oferente/habilidades')">Mis habilidades</a>
        <a onclick="Router.go('/oferente/cv')">Mi CV</a>
        <a onclick="Router.go('/oferente/puestos')">Buscar puestos</a>`;
        } else if (role === 'ROLE_ADMIN') {
            navHtml = `
        <a onclick="Router.go('/admin/dashboard')">Dashboard</a>
        <a onclick="Router.go('/admin/empresas')">Empresas</a>
        <a onclick="Router.go('/admin/oferentes')">Oferentes</a>
        <a onclick="Router.go('/admin/caracteristicas')">Características</a>
        <a onclick="Router.go('/admin/reportes')">Reportes</a>`;
        }

        links.innerHTML = navHtml;
        userDiv.innerHTML = `
      <span style="color:#fecdd3;font-size:13px">${user}</span>
      <a onclick="Auth.logout()" style="color:#fecdd3;text-decoration:none;font-size:13px;margin-left:14px;cursor:pointer">Salir</a>`;
    }
};