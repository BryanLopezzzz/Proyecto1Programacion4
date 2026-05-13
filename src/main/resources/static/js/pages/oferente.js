/**
 * pages/oferente.js — Vistas del rol OFERENTE.
 */

Pages.oferenteDashboard = async () => {
    if (!Auth.requireRole('ROLE_OFERENTE')) return;
    UI.loading();
    const perfil = await Api.oferente.perfil();
    UI.render(`
    <div class="container">
      <h1>Bienvenido</h1>
      <p>Hola, <strong>${perfil.nombre} ${perfil.primerApellido}</strong>. Administrá tus habilidades y tu CV.</p>
      <div class="card-grid">
        <a class="card-btn c1" onclick="Router.go('/oferente/habilidades')" style="cursor:pointer">Mis habilidades</a>
        <a class="card-btn c2" onclick="Router.go('/oferente/cv')" style="cursor:pointer">Mi CV</a>
        <a class="card-btn c3" onclick="Router.go('/oferente/puestos')" style="cursor:pointer">Buscar puestos</a>
      </div>
    </div>
  `);
};

Pages.oferenteHabilidades = async () => {
    if (!Auth.requireRole('ROLE_OFERENTE')) return;
    UI.loading();
    try {
        const [habilidades, raices] = await Promise.all([
            Api.oferente.habilidades(),
            Api.getCaracteristicas()
        ]);

        const habRows = habilidades.map(h => `
      <tr>
        <td>${h.caracteristicaNombre}</td>
        <td><span class="badge badge-activo">${UI.nivelTexto(h.nivel)}</span></td>
        <td>
          <button class="btn btn-outline btn-sm"
                  onclick="_eliminarHabilidad(${h.caracteristicaId})">✕ Eliminar</button>
        </td>
      </tr>`).join('');

        // Aplanar árbol para el select
        const opciones = _aplanarCarac(raices);

        UI.render(`
      <div class="container panels" style="display:flex;gap:28px;flex-wrap:wrap;padding:40px 10%">
        <div class="card panel-left" style="flex:1;min-width:320px">
          <h2>Mis Habilidades</h2>
          ${habilidades.length ? `
            <table>
              <thead><tr><th>Característica</th><th>Nivel</th><th>Eliminar</th></tr></thead>
              <tbody>${habRows}</tbody>
            </table>` : '<p class="empty">No has registrado habilidades aún.</p>'}
        </div>
        <div class="card" style="min-width:260px">
          <h3>Agregar Habilidad</h3>
          <label>Característica</label>
          <select id="oh-carac">
            <option value="">-- Seleccione --</option>
            ${opciones}
          </select>
          <label>Nivel</label>
          <select id="oh-nivel">
            <option value="1">Básico</option>
            <option value="2" selected>Intermedio</option>
            <option value="3">Avanzado</option>
          </select>
          <button id="oh-btn" class="btn btn-primary" style="margin-top:16px;width:100%">
            Agregar habilidad
          </button>
        </div>
      </div>
    `);

        document.getElementById('oh-btn').addEventListener('click', async () => {
            const caracId = parseInt(document.getElementById('oh-carac').value);
            const nivel   = parseInt(document.getElementById('oh-nivel').value);
            if (!caracId) { UI.toast('Seleccione una característica', false); return; }
            try {
                await Api.oferente.agregarHabilidad({ caracteristicaId: caracId, nivel });
                UI.toast('Habilidad guardada');
                Pages.oferenteHabilidades();
            } catch (e) { UI.toast(e.message, false); }
        });

    } catch (e) {
        UI.render(`<div class="container"><p class="alert-error">${e.message}</p></div>`);
    }
};

window._eliminarHabilidad = async (id) => {
    if (!confirm('¿Eliminar esta habilidad?')) return;
    try {
        await Api.oferente.eliminarHabilidad(id);
        UI.toast('Habilidad eliminada');
        Pages.oferenteHabilidades();
    } catch (e) { UI.toast(e.message, false); }
};

function _aplanarCarac(raices) {
    let html = '';
    raices.forEach(r => {
        if (r.hijos && r.hijos.length) {
            html += `<optgroup label="${r.nombre}">`;
            r.hijos.forEach(h => {
                html += `<option value="${h.id}">${h.nombre}</option>`;
                (h.hijos || []).forEach(n => {
                    html += `<option value="${n.id}">↳ ${n.nombre}</option>`;
                });
            });
            html += `</optgroup>`;
        } else {
            html += `<option value="${r.id}">${r.nombre}</option>`;
        }
    });
    return html;
}

Pages.oferenteCv = async () => {
    if (!Auth.requireRole('ROLE_OFERENTE')) return;
    UI.loading();
    const perfil = await Api.oferente.perfil();

    UI.render(`
    <div class="container-narrow" style="padding:40px 10%;max-width:720px">
      <h2>Mi Currículum (CV)</h2>
      <div class="card">
        <div id="cv-error" class="alert-error" style="display:none"></div>
        ${perfil.curriculumPdf ? `
          <div class="cv-actual">
            📄 CV actual: <strong>${perfil.curriculumPdf}</strong><br><br>
            <a class="btn btn-outline" href="/uploads/${perfil.curriculumPdf}" target="_blank">Ver CV actual</a>
          </div>` : ''}
        <label>Subir nuevo CV (solo PDF, máx. 5MB)</label>
        <input id="cv-file" type="file" accept=".pdf"/>
        <button id="cv-btn" class="btn btn-primary" style="margin-top:12px">Subir CV</button>
      </div>
    </div>
  `);

    document.getElementById('cv-btn').addEventListener('click', async () => {
        const err  = document.getElementById('cv-error');
        const file = document.getElementById('cv-file').files[0];
        err.style.display = 'none';
        if (!file) { err.textContent = 'Seleccione un archivo'; err.style.display = 'block'; return; }
        try {
            const res = await Api.oferente.subirCv(file);
            if (res.error) throw new Error(res.error);
            UI.toast('CV subido correctamente');
            Pages.oferenteCv();
        } catch (e) { err.textContent = e.message; err.style.display = 'block'; }
    });
};

Pages.oferentePuestos = async () => {
    if (!Auth.requireRole('ROLE_OFERENTE')) return;
    UI.loading();
    try {
        const raices = await Api.getCaracteristicas();

        UI.render(`
      <div class="container-wide" style="padding:40px 10%;max-width:960px">
        <h2>Buscar Puestos Disponibles</h2>
        <div class="arbol">${UI.renderArbolCheckboxes(raices)}</div>
        <div class="modo">
          <strong>Modo:</strong>
          <label><input type="radio" name="modo" value="false" checked/> Cualquiera (OR)</label>
          <label><input type="radio" name="modo" value="true"/> Todos (AND)</label>
        </div>
        <div class="btns">
          <button id="op-buscar" class="btn btn-primary">Buscar</button>
          <button id="op-limpiar" class="btn btn-outline">Limpiar</button>
        </div>
        <h3>Resultados</h3>
        <div id="op-res"></div>
      </div>
    `);

        document.getElementById('op-buscar').addEventListener('click', async () => {
            const ids  = [...document.querySelectorAll('.arbol input:checked')].map(cb => cb.getAttribute('data-id'));
            const modo = document.querySelector('input[name="modo"]:checked').value;
            const params = { modoTodos: modo };
            if (ids.length) params['caracteristicas'] = ids;

            const res = await Api.oferente.buscarPuestos(params);
            const container = document.getElementById('op-res');

            if (!res.length) { container.innerHTML = '<p class="empty">No se encontraron puestos.</p>'; return; }

            const estadoTexto = { CUMPLE:'✓', PARCIAL:'~', INSUFICIENTE:'!', AUSENTE:'✗' };
            const estadoColor = { CUMPLE:'#dcfce7', PARCIAL:'#fef3c7', INSUFICIENTE:'#fee2e2', AUSENTE:'#ede9fe' };

            container.innerHTML = res.map(p => `
        <div class="card" style="margin-bottom:16px">
            <div style="display:flex;justify-content:space-between;align-items:flex-start">
                <div>
                    <strong style="color:#4c0519">${p.empresa.nombre}</strong>
                    <span style="font-size:12px;color:#9f1239;margin-left:8px">${p.tipo}</span>
                    <div style="font-size:14px;margin-top:4px">${p.descripcion.substring(0,80)}…</div>
                    <div style="font-size:13px;color:#4c0519;font-weight:bold;margin-top:6px">
                        ${p.moneda.codigo} ${Number(p.salario).toLocaleString('es-CR')}
                    </div>
                </div>
                <div style="text-align:center;min-width:90px">
                    <div style="font-size:26px;font-weight:900;color:${p.porcentajeCoincidencia>=70?'#15803d':p.porcentajeCoincidencia>=40?'#b45309':'#dc2626'}">
                        ${p.porcentajeCoincidencia.toFixed(1)}%
                    </div>
                    <div style="font-size:11px;color:#9f1239">match</div>
                    <div style="font-size:11px;color:#6b7280">${p.requisitosCumplidos}/${p.requisitosTotal} cumplidos</div>
                </div>
            </div>
            <div style="margin-top:10px">
                ${UI.barraCoincidencia(p.porcentajeCoincidencia)}
            </div>
            ${p.coincidencias && p.coincidencias.length ? `
            <div style="margin-top:10px;display:flex;flex-wrap:wrap;gap:4px">
                ${p.coincidencias.map(d => `
                    <span style="font-size:11px;padding:2px 8px;border-radius:10px;
                          background:${estadoColor[d.estado]};border:1px solid #d1d5db">
                        ${estadoTexto[d.estado]} ${d.caracteristica}
                        ${d.nivelOferente > 0 ? `(${d.nivelOferente}→${d.nivelRequerido})` : '(sin habilidad)'}
                    </span>`).join('')}
            </div>` : ''}
        </div>`).join('');
        });
        document.getElementById('op-limpiar').addEventListener('click', () => {
            document.querySelectorAll('.arbol input').forEach(cb => cb.checked = false);
            document.getElementById('op-res').innerHTML = '';
        });

    } catch (e) {
        UI.render(`<div class="container"><p class="alert-error">${e.message}</p></div>`);
    }
};