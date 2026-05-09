/**
 * pages/empresa.js — Vistas del rol EMPRESA.
 */

Pages.empresaDashboard = async () => {
    if (!Auth.requireRole('ROLE_EMPRESA')) return;
    UI.loading();
    const perfil = await Api.empresa.perfil();
    UI.render(`
    <div class="container">
      <h1>Empresa - Dashboard</h1>
      <p>Bienvenido, <strong>${perfil.nombre}</strong>. Administrá tus puestos y buscá candidatos.</p>
      <div class="card-grid">
        <a class="card-btn c1" onclick="Router.go('/empresa/puestos')" style="cursor:pointer">Mis puestos</a>
        <a class="card-btn c2" onclick="Router.go('/empresa/puestos/nuevo')" style="cursor:pointer">Publicar puesto</a>
      </div>
      <div class="card" style="margin-top:28px">
        <h3>Datos de la empresa</h3>
        <div class="info-row"><span class="lbl">Nombre:</span><span class="val">${perfil.nombre}</span></div>
        <div class="info-row"><span class="lbl">Correo:</span><span class="val">${perfil.correo}</span></div>
        <div class="info-row"><span class="lbl">Teléfono:</span><span class="val">${perfil.telefono}</span></div>
        <div class="info-row"><span class="lbl">Localización:</span><span class="val">${perfil.localizacion}</span></div>
        <div class="info-row"><span class="lbl">Descripción:</span><span class="val">${perfil.descripcion}</span></div>
        <div class="info-row"><span class="lbl">Estado:</span>
          <span class="badge badge-${perfil.estado.toLowerCase()}">${perfil.estado}</span>
        </div>
      </div>
    </div>
  `);
};

Pages.empresaPuestos = async () => {
    if (!Auth.requireRole('ROLE_EMPRESA')) return;
    UI.loading();
    const puestos = await Api.empresa.puestos();
    const filas = puestos.map(p => `
    <tr>
      <td>${p.id}</td>
      <td>${p.descripcion.substring(0,55)}${p.descripcion.length>55?'…':''}</td>
      <td>${Number(p.salario).toLocaleString('es-CR')}</td>
      <td>${p.moneda.codigo}</td>
      <td>${p.tipo}</td>
      <td><span class="badge ${p.activo?'badge-activo':'badge-inactivo'}">${p.activo?'Sí':'No'}</span></td>
      <td>
        ${p.activo ? `<button class="btn btn-secondary btn-sm" onclick="_desactivarPuesto(${p.id})">Desactivar</button>` : ''}
        <button class="btn btn-primary btn-sm" onclick="_verCandidatos(${p.id})">Buscar candidatos</button>
      </td>
    </tr>`).join('');

    UI.render(`
    <div class="container">
      <div class="top-bar">
        <h2>Mis Puestos</h2>
        <a class="btn btn-primary" onclick="Router.go('/empresa/puestos/nuevo')" style="cursor:pointer">+ Publicar nuevo puesto</a>
      </div>
      ${puestos.length ? `
        <table>
          <thead><tr><th>ID</th><th>Descripción</th><th>Salario</th><th>Moneda</th><th>Tipo</th><th>Activo</th><th>Acciones</th></tr></thead>
          <tbody>${filas}</tbody>
        </table>` : '<p class="empty">No has publicado puestos aún.</p>'}
    </div>
  `);
};

window._desactivarPuesto = async (id) => {
    if (!confirm('¿Desactivar este puesto?')) return;
    try {
        await Api.empresa.desactivar(id);
        UI.toast('Puesto desactivado');
        Pages.empresaPuestos();
    } catch (e) { UI.toast(e.message, false); }
};

window._verCandidatos = async (puestoId) => {
    UI.loading();
    try {
        const candidatos = await Api.empresa.candidatos(puestoId);

        const filas = candidatos.map(c => {
            const detalleHtml = c.detalle.map(d => `
                <span style="display:inline-block;margin:2px 4px;padding:2px 8px;
                      border-radius:10px;font-size:11px;
                      background:${d.cumple ? '#dcfce7' : '#fff1f2'};
                      color:${d.cumple ? '#166534' : '#9f1239'};
                      border:1px solid ${d.cumple ? '#86efac' : '#fecdd3'}">
                    ${d.cumple ? '✓' : '✗'} ${d.caracteristica}
                    (tiene: ${d.nivelOferente > 0 ? d.nivelOferente : 'ninguno'},
                     pide: ${d.nivelRequerido})
                </span>`).join('');

            return `<tr>
                <td>${c.oferente.nombre} ${c.oferente.primerApellido}</td>
                <td>${c.requisitosCumplidos} / ${c.requisitosTotal}</td>
                <td>
                    <div style="margin-bottom:4px;font-size:12px;color:#666">
                        Binario: ${UI.barraCoincidencia(c.porcentajeCoincidencia)}
                    </div>
                    <div style="font-size:12px;color:#666">
                        Ponderado: ${UI.barraCoincidencia(c.porcentajePonderado)}
                    </div>
                </td>
                <td style="font-size:12px">${detalleHtml}</td>
                <td>
                    <button class="btn btn-primary btn-sm"
                            onclick="Router.go('/empresa/candidatos/${c.oferente.id}')">
                        Ver detalle
                    </button>
                </td>
            </tr>`;
        }).join('');

        UI.render(`
            <div class="container">
                <h2>Candidatos para el puesto</h2>
                ${candidatos.length ? `
                    <table>
                        <thead>
                            <tr>
                                <th>Oferente</th>
                                <th>Requisitos</th>
                                <th>% Coincidencia</th>
                                <th>Detalle por característica</th>
                                <th>Acción</th>
                            </tr>
                        </thead>
                        <tbody>${filas}</tbody>
                    </table>` : '<p class="empty">No se encontraron candidatos.</p>'}
                <br/>
                <button class="btn btn-outline"
                        onclick="Router.go('/empresa/puestos')">← Volver</button>
            </div>
        `);
    } catch (e) { UI.toast(e.message, false); }
};

Pages.empresaCandidatoDetalle = async (id) => {
    if (!Auth.requireRole('ROLE_EMPRESA')) return;
    UI.loading();
    try {
        const o = await Api.empresa.candidato(id);
        const habsRows = o.habilidades.map(h =>
            `<tr><td>${h.caracteristica}</td><td><span class="badge badge-activo">${UI.nivelTexto(h.nivel)}</span></td></tr>`
        ).join('');

        UI.render(`
      <div class="container-narrow" style="padding:40px 10%;max-width:720px">
        <h2>Detalle de Oferente</h2>
        <div class="card">
          <h3>${o.nombre} ${o.primerApellido}</h3>
          <div class="info-row"><span class="lbl">Identificación:</span><span class="val">${o.identificacion}</span></div>
          <div class="info-row"><span class="lbl">Email:</span><span class="val">${o.correo}</span></div>
          <div class="info-row"><span class="lbl">Teléfono:</span><span class="val">${o.telefono}</span></div>
          <div class="info-row"><span class="lbl">Residencia:</span><span class="val">${o.residencia}</span></div>
          <div class="info-row"><span class="lbl">Nacionalidad:</span><span class="val">${o.nacionalidad}</span></div>
        </div>
        <div class="card">
          <h3>Habilidades</h3>
          ${o.habilidades.length ? `
            <table><thead><tr><th>Característica</th><th>Nivel</th></tr></thead>
            <tbody>${habsRows}</tbody></table>` : '<p class="empty">Sin habilidades.</p>'}
        </div>
        ${o.curriculumPdf ? `
          <a class="btn btn-primary" href="/uploads/${o.curriculumPdf}" target="_blank">Ver Currículo PDF</a>` : ''}
        <br/><br/>
        <button class="btn btn-outline" onclick="history.back()">← Volver</button>
      </div>
    `);
    } catch (e) { UI.toast(e.message, false); }
};

Pages.empresaNuevoPuesto = async () => {
    if (!Auth.requireRole('ROLE_EMPRESA')) return;
    UI.loading();
    try {
        const [raices, monedas] = await Promise.all([Api.getCaracteristicas(), Api.getMonedas()]);

        UI.render(`
      <div class="container-narrow" style="padding:40px 10%;max-width:720px">
        <h2>Publicar Nuevo Puesto</h2>
        <div id="np-error" class="alert-error" style="display:none"></div>

        <label>Descripción del puesto *</label>
        <textarea id="np-desc" placeholder="Descripción detallada…"></textarea>

        <div class="row2">
          <div>
            <label>Salario *</label>
            <input id="np-salario" type="number" step="0.01" min="1" placeholder="800000"/>
          </div>
          <div>
            <label>Moneda *</label>
            <select id="np-moneda">
              <option value="">-- Seleccione --</option>
              ${monedas.map(m => `<option value="${m.id}">${m.nombre} (${m.codigo})</option>`).join('')}
            </select>
          </div>
        </div>

        <label>Tipo de publicación *</label>
        <select id="np-tipo">
          <option value="">-- Seleccione --</option>
          <option value="PUBLICO">Público (visible para todos)</option>
          <option value="PRIVADO">Privado (solo oferentes registrados)</option>
        </select>

        <label>Características requeridas</label>
        <div class="arbol">${UI.renderArbolCheckboxes(raices, [], true)}</div>

        <button id="np-btn" class="btn btn-primary">Publicar Puesto</button>
      </div>
    `);

        document.getElementById('np-btn').addEventListener('click', async () => {
            const err = document.getElementById('np-error');
            err.style.display = 'none';

            const descripcion = document.getElementById('np-desc').value.trim();
            const salario     = document.getElementById('np-salario').value;
            const monedaId    = document.getElementById('np-moneda').value;
            const tipo        = document.getElementById('np-tipo').value;

            if (!descripcion || !salario || !monedaId || !tipo) {
                err.textContent = 'Todos los campos son requeridos'; err.style.display = 'block'; return;
            }

            // Recoger características seleccionadas con su nivel
            const caracteristicas = [];
            document.querySelectorAll('.arbol input[type=checkbox]:checked').forEach(cb => {
                const id    = parseInt(cb.getAttribute('data-id'));
                const nivelSel = document.querySelector(`select[name="nivel_${id}"]`);
                const nivel = nivelSel ? parseInt(nivelSel.value) : 2;
                caracteristicas.push({ id, nivel });
            });

            try {
                await Api.empresa.publicarPuesto({
                    descripcion, salario: parseFloat(salario),
                    monedaId: parseInt(monedaId), tipo, caracteristicas
                });
                UI.toast('Puesto publicado correctamente');
                Router.go('/empresa/puestos');
            } catch (e) {
                err.textContent = e.message; err.style.display = 'block';
            }
        });

    } catch (e) {
        UI.render(`<div class="container"><p class="alert-error">${e.message}</p></div>`);
    }
};