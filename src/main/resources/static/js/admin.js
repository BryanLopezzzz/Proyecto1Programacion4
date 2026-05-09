/**
 * pages/admin.js — Vistas del rol ADMIN.
 */

Pages.adminDashboard = () => {
    if (!Auth.requireRole('ROLE_ADMIN')) return;
    UI.render(`
    <div class="container">
      <h1>Panel de Administración</h1>
      <p>Bienvenido al panel de control. Gestioná empresas, oferentes, características y reportes.</p>
      <div class="card-grid">
        <a class="card-btn c1" onclick="Router.go('/admin/empresas')" style="cursor:pointer">Empresas pendientes</a>
        <a class="card-btn c2" onclick="Router.go('/admin/oferentes')" style="cursor:pointer">Oferentes pendientes</a>
        <a class="card-btn c3" onclick="Router.go('/admin/caracteristicas')" style="cursor:pointer">Características</a>
        <a class="card-btn c1" onclick="Router.go('/admin/reportes')" style="cursor:pointer;background:#4c0519">Reportes</a>
      </div>
    </div>
  `);
};

Pages.adminEmpresas = async () => {
    if (!Auth.requireRole('ROLE_ADMIN')) return;
    UI.loading();
    const pendientes = await Api.admin.empresasPendientes();

    UI.render(`
    <div class="container">
      <div class="top-bar"><h2>Empresas Pendientes de Aprobación</h2></div>
      ${pendientes.length ? `
        <table>
          <thead><tr><th>Nombre</th><th>Correo</th><th>Teléfono</th><th>Localización</th><th>Descripción</th><th>Acciones</th></tr></thead>
          <tbody>
            ${pendientes.map(e => `
              <tr>
                <td>${e.nombre}</td>
                <td>${e.correo}</td>
                <td>${e.telefono}</td>
                <td>${e.localizacion}</td>
                <td>${e.descripcion.substring(0,60)}…</td>
                <td>
                  <button class="btn btn-primary btn-sm" onclick="_aprobarEmpresa(${e.usuarioId})">✔ Aprobar</button>
                  <button class="btn btn-secondary btn-sm" onclick="_rechazarEmpresa(${e.usuarioId})">✘ Rechazar</button>
                </td>
              </tr>`).join('')}
          </tbody>
        </table>` : '<p class="empty">No hay empresas pendientes.</p>'}
      <br/><button class="btn btn-outline" onclick="Router.go('/admin/dashboard')">← Dashboard</button>
    </div>
  `);
};

window._aprobarEmpresa = async (id) => {
    await Api.admin.aprobarEmpresa(id);
    UI.toast('Empresa aprobada');
    Pages.adminEmpresas();
};
window._rechazarEmpresa = async (id) => {
    await Api.admin.rechazarEmpresa(id);
    UI.toast('Empresa rechazada');
    Pages.adminEmpresas();
};

Pages.adminOferentes = async () => {
    if (!Auth.requireRole('ROLE_ADMIN')) return;
    UI.loading();
    const pendientes = await Api.admin.oferentesPendientes();

    UI.render(`
    <div class="container">
      <div class="top-bar"><h2>Oferentes Pendientes de Aprobación</h2></div>
      ${pendientes.length ? `
        <table>
          <thead><tr><th>Nombre</th><th>Identificación</th><th>Correo</th><th>Teléfono</th><th>Nacionalidad</th><th>Acciones</th></tr></thead>
          <tbody>
            ${pendientes.map(o => `
              <tr>
                <td>${o.nombre} ${o.primerApellido}</td>
                <td>${o.identificacion}</td>
                <td>${o.correo}</td>
                <td>${o.telefono}</td>
                <td>${o.nacionalidad}</td>
                <td>
                  <button class="btn btn-primary btn-sm" onclick="_aprobarOferente(${o.usuarioId})">✔ Aprobar</button>
                  <button class="btn btn-secondary btn-sm" onclick="_rechazarOferente(${o.usuarioId})">✘ Rechazar</button>
                </td>
              </tr>`).join('')}
          </tbody>
        </table>` : '<p class="empty">No hay oferentes pendientes.</p>'}
      <br/><button class="btn btn-outline" onclick="Router.go('/admin/dashboard')">← Dashboard</button>
    </div>
  `);
};

window._aprobarOferente = async (id) => {
    await Api.admin.aprobarOferente(id);
    UI.toast('Oferente aprobado');
    Pages.adminOferentes();
};
window._rechazarOferente = async (id) => {
    await Api.admin.rechazarOferente(id);
    UI.toast('Oferente rechazado');
    Pages.adminOferentes();
};

Pages.adminCaracteristicas = async () => {
    if (!Auth.requireRole('ROLE_ADMIN')) return;
    UI.loading();
    try {
        const [arbol, todos] = await Promise.all([Api.admin.caracArbol(), Api.admin.caracTodos()]);

        const renderArbol = (nodos, depth = 0) => nodos.map(n => `
      <div style="margin-left:${depth*20}px;margin-bottom:6px">
        <span style="color:${depth===0?'#4c0519':'#831843'};font-weight:${depth===0?800:500};font-size:${14-depth}px">
          ${depth > 0 ? '↳ ' : ''}${n.nombre}
        </span>
        ${n.hijos && n.hijos.length ? renderArbol(n.hijos, depth + 1) : ''}
      </div>`).join('');

        UI.render(`
      <div class="container panels" style="display:flex;gap:28px;flex-wrap:wrap;padding:40px 10%">
        <div class="panel-left" style="flex:1;min-width:320px">
          <h2>Árbol de Características</h2>
          <div class="arbol" style="max-height:none">${renderArbol(arbol)}</div>
        </div>
        <div style="min-width:260px">
          <div class="card">
            <h3>Nueva Característica</h3>
            <label>Nombre *</label>
            <input id="nc-nombre" type="text" placeholder="Ej: Java"/>
            <label>Nodo padre (opcional)</label>
            <select id="nc-padre">
              <option value="">-- Raíz (sin padre) --</option>
              ${todos.map(n => `<option value="${n.id}">${n.tienePadre ? '↳ ' : ''}${n.nombre}</option>`).join('')}
            </select>
            <button id="nc-btn" class="btn btn-primary" style="margin-top:16px;width:100%">+ Agregar</button>
          </div>
          <button class="btn btn-outline" onclick="Router.go('/admin/dashboard')" style="width:100%;margin-top:8px">← Dashboard</button>
        </div>
      </div>
    `);

        document.getElementById('nc-btn').addEventListener('click', async () => {
            const nombre  = document.getElementById('nc-nombre').value.trim();
            const padreId = document.getElementById('nc-padre').value || null;
            if (!nombre) { UI.toast('El nombre es requerido', false); return; }
            try {
                await Api.admin.crearCarac({ nombre, padreId });
                UI.toast('Característica creada');
                Pages.adminCaracteristicas();
            } catch (e) { UI.toast(e.message, false); }
        });

    } catch (e) {
        UI.render(`<div class="container"><p class="alert-error">${e.message}</p></div>`);
    }
};

Pages.adminReportes = () => {
    if (!Auth.requireRole('ROLE_ADMIN')) return;
    const anioActual = new Date().getFullYear();
    const mesesOpts = ['Enero','Febrero','Marzo','Abril','Mayo','Junio',
        'Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre']
        .map((m, i) => `<option value="${i+1}">${m}</option>`).join('');

    UI.render(`
    <div class="container">
      <h2>Reportes</h2>
      <p>Generá reportes en pantalla. Usá <strong>Ctrl+P</strong> para imprimir o guardar PDF.</p>

      <div class="card">
        <h3>Reporte de Puestos Publicados por Mes</h3>
        <div class="row2">
          <div><label>Mes *</label><select id="rp-mes">${mesesOpts}</select></div>
          <div><label>Año *</label><input id="rp-anio" type="number" value="${anioActual}" min="2020" max="2030"/></div>
        </div>
        <button id="rp-btn" class="btn btn-primary" style="margin-top:16px">Ver reporte →</button>
        <div id="rp-res" style="margin-top:20px"></div>
      </div>

      <div class="card">
        <h3>Reporte de Coincidencias Candidatos / Puestos</h3>
        <div class="row2">
          <div><label>Mes *</label><select id="rc-mes">${mesesOpts}</select></div>
          <div><label>Año *</label><input id="rc-anio" type="number" value="${anioActual}" min="2020" max="2030"/></div>
        </div>
        <button id="rc-btn" class="btn btn-primary" style="margin-top:16px">Ver reporte →</button>
        <div id="rc-res" style="margin-top:20px"></div>
      </div>

      <button class="btn btn-outline" onclick="Router.go('/admin/dashboard')">← Dashboard</button>
    </div>
  `);

    // Reporte puestos
    document.getElementById('rp-btn').addEventListener('click', async () => {
        const mes = document.getElementById('rp-mes').value;
        const anio = document.getElementById('rp-anio').value;
        const puestos = await Api.admin.reportePuestos(mes, anio);
        const container = document.getElementById('rp-res');

        if (!puestos.length) { container.innerHTML = '<p class="empty">Sin puestos en ese período.</p>'; return; }

        container.innerHTML = `
      <table>
        <thead><tr><th>#</th><th>Empresa</th><th>Descripción</th><th>Salario</th><th>Tipo</th><th>Características</th><th>Fecha</th></tr></thead>
        <tbody>
          ${puestos.map((p, i) => `
            <tr>
              <td>${i+1}</td>
              <td>${p.empresa}</td>
              <td>${p.descripcion}</td>
              <td>${p.moneda} ${Number(p.salario).toLocaleString('es-CR')}</td>
              <td><span class="badge ${p.tipo==='PUBLICO'?'badge-publico':'badge-privado'}">${p.tipo}</span></td>
              <td>${(p.caracteristicas||[]).map(c=>`${c.nombre} (${UI.nivelTexto(c.nivel)})`).join(', ')||'<em>Sin características</em>'}</td>
              <td>${p.fecha.substring(0,10)}</td>
            </tr>`).join('')}
        </tbody>
      </table>`;
    });

    // Reporte coincidencias
    document.getElementById('rc-btn').addEventListener('click', async () => {
        const mes = document.getElementById('rc-mes').value;
        const anio = document.getElementById('rc-anio').value;
        const data = await Api.admin.reporteCoincid(mes, anio);
        const container = document.getElementById('rc-res');

        if (!data.length) { container.innerHTML = '<p class="empty">Sin puestos en ese período.</p>'; return; }

        container.innerHTML = data.map(item => `
      <div style="margin-bottom:28px">
        <div class="puesto-titulo" style="background:#4c0519;color:white;padding:10px 16px;border-radius:8px 8px 0 0;display:flex;justify-content:space-between">
          <span>${item.empresa}: ${item.puesto.substring(0,60)}</span>
          <span style="font-size:11px;background:rgba(255,255,255,.2);padding:2px 10px;border-radius:10px">${item.tipo}</span>
        </div>
        ${item.candidatos.length ? `
          <table>
            <thead><tr><th>Oferente</th><th>Identificación</th><th>Correo</th><th>Cumplidos</th><th>Total</th><th>% Coincidencia</th></tr></thead>
            <tbody>
              ${item.candidatos.map(c => `
                <tr>
                  <td>${c.nombre}</td>
                  <td>${c.identificacion}</td>
                  <td>${c.correo}</td>
                  <td>${c.cumplidos}</td>
                  <td>${c.total}</td>
                  <td>${UI.barraCoincidencia(c.porcentaje)}</td>
                </tr>`).join('')}
            </tbody>
          </table>` : '<div style="background:#fff1f2;border:1px solid #fecdd3;padding:16px;text-align:center;font-style:italic;color:#9f1239">Sin candidatos con coincidencias.</div>'}
      </div>`).join('');
    });
};