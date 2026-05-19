/**
 * pages/publico.js — Páginas accesibles sin autenticación.
 */

const Pages = {};

// ---- INICIO ----
Pages.inicio = async () => {
    UI.loading();
    try {
        const puestos = await Api.getPuestosPublicos();

        const cards = puestos.map(p => `
            <div class="puesto-card">
                <div class="puesto-card-empresa">${p.empresa.nombre}</div>
                <div class="puesto-card-desc">${p.descripcion.substring(0, 50)}…</div>
                <span class="puesto-card-salario">
                    ${p.moneda.codigo} ${Number(p.salario).toLocaleString('es-CR')}
                </span>
                <a class="btn btn-outline puesto-card-btn" onclick="Router.go('/buscar')" style="cursor:pointer">
                    Ver detalle
                </a>
                <div class="puesto-card-tooltip">
                    <strong>${p.descripcion}</strong>
                    <ul class="puesto-card-tooltip-list">
                        ${(p.caracteristicas || []).map(c =>
            `<li>${c.nombre} (nivel ${c.nivel})</li>`
        ).join('')}
                    </ul>
                </div>
            </div>
        `).join('');

        UI.render(`
            <div class="container">
                <h1>Bolsa de Empleo</h1>
                <p class="subtitulo-seccion">Últimos 5 puestos públicos</p>
                <div class="puesto-grid">${cards}</div>
                ${puestos.length === 0 ? '<p class="empty">No hay puestos publicados aún.</p>' : ''}
            </div>
        `);

        // Tooltips al hover
        document.querySelectorAll('.puesto-card').forEach(card => {
            const tip = card.querySelector('.puesto-card-tooltip');
            card.addEventListener('mouseenter', () => tip.style.display = 'block');
            card.addEventListener('mouseleave', () => tip.style.display = 'none');
        });

    } catch (e) {
        UI.render(`<div class="container"><p class="alert-error">${e.message}</p></div>`);
    }
};

// ---- LOGIN ----
Pages.login = () => {
    if (Auth.isLogged()) { _redirectByRole(); return; }

    UI.render(`
        <div class="auth-centrado">
            <div class="auth-box narrow">
                <div class="logo">🏢 BolsaEmpleo</div>
                <div class="logo-sub">Inicia sesión en tu cuenta</div>
                <div id="login-error" style="display:none" class="alert-error"></div>
                <div>
                    <label>Usuario</label>
                    <input id="l-user" type="text" placeholder="Correo o Identificación" autofocus/>
                    <label>Contraseña</label>
                    <input id="l-pass" type="password" placeholder="••••••••"/>
                    <button id="l-btn" class="btn btn-primary" style="width:100%;margin-top:24px">Entrar</button>
                </div>
                <div class="auth-links" style="margin-top:20px">
                    <a onclick="Router.go('/registro/empresa')" style="cursor:pointer">Registrar Empresa</a>
                    <span class="divider">|</span>
                    <a onclick="Router.go('/registro/oferente')" style="cursor:pointer">Registrar Oferente</a>
                    <br><br>
                    <a onclick="Router.go('/')" style="cursor:pointer">← Volver al inicio</a>
                </div>
            </div>
        </div>
    `);

    document.getElementById('l-btn').addEventListener('click', async () => {
        const user   = document.getElementById('l-user').value.trim();
        const pass   = document.getElementById('l-pass').value;
        const errDiv = document.getElementById('login-error');
        errDiv.style.display = 'none';
        try {
            const data = await Api.login(user, pass);
            Auth.save(data.token, data.role, data.correo);
            _redirectByRole();
        } catch (e) {
            errDiv.textContent = e.message;
            errDiv.style.display = 'block';
        }
    });

    ['l-user', 'l-pass'].forEach(id =>
        document.getElementById(id).addEventListener('keydown', e => {
            if (e.key === 'Enter') document.getElementById('l-btn').click();
        })
    );
};

function _redirectByRole() {
    const role = Auth.getRole();
    if (role === 'ROLE_EMPRESA')       Router.go('/empresa/dashboard');
    else if (role === 'ROLE_OFERENTE') Router.go('/oferente/dashboard');
    else if (role === 'ROLE_ADMIN')    Router.go('/admin/dashboard');
    else Router.go('/');
}

// ---- BÚSQUEDA PÚBLICA ----
Pages.buscar = async () => {
    UI.loading();
    try {
        const [raices, monedas] = await Promise.all([Api.getCaracteristicas(), Api.getMonedas()]);

        UI.render(`
            <div class="container-wide">
                <h2>Buscar puestos por características</h2>
                <div class="arbol">${UI.renderArbolCheckboxes(raices)}</div>

                <div style="margin-bottom:16px">
                    <label>Filtrar por moneda (opcional)</label>
                    <select id="b-moneda">
                        <option value="">-- Todas las monedas --</option>
                        ${monedas.map(m => `<option value="${m.id}">${m.nombre} (${m.codigo})</option>`).join('')}
                    </select>
                </div>

                <div class="modo">
                    <strong>Modo:</strong>
                    <label><input type="radio" name="modo" value="false" checked/> Cualquiera (OR)</label>
                    <label><input type="radio" name="modo" value="true"/> Todos (AND)</label>
                </div>

                <div class="btns">
                    <button id="b-buscar" class="btn btn-primary">Buscar</button>
                    <button id="b-limpiar" class="btn btn-outline">Limpiar</button>
                </div>

                <h3>Resultados</h3>
                <div id="b-resultados"></div>
            </div>
        `);

        document.getElementById('b-buscar').addEventListener('click', async () => {
            const checked = [...document.querySelectorAll('.arbol input[type=checkbox]:checked')]
                .map(cb => cb.getAttribute('data-id'));
            const modo     = document.querySelector('input[name="modo"]:checked').value;
            const monedaId = document.getElementById('b-moneda').value;
            const params   = {};
            if (checked.length) params['caracteristicas'] = checked;
            params['modoTodos'] = modo;
            if (monedaId) params['monedaId'] = monedaId;

            const res = await Api.buscarPuestos(params);
            _renderResultados(res, document.getElementById('b-resultados'));
        });

        document.getElementById('b-limpiar').addEventListener('click', () => {
            document.querySelectorAll('.arbol input[type=checkbox]').forEach(cb => cb.checked = false);
            document.getElementById('b-moneda').value = '';
            document.getElementById('b-resultados').innerHTML = '';
        });

    } catch (e) {
        UI.render(`<div class="container"><p class="alert-error">${e.message}</p></div>`);
    }
};

function _renderResultados(puestos, container) {
    if (!puestos.length) {
        container.innerHTML = '<p class="empty">No se encontraron resultados.</p>';
        return;
    }
    container.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>Empresa</th>
                    <th>Descripción</th>
                    <th>Salario</th>
                    <th>Moneda</th>
                    <th>Características</th>
                </tr>
            </thead>
            <tbody>
                ${puestos.map(p => `
                    <tr>
                        <td>${p.empresa.nombre}</td>
                        <td>${p.descripcion}</td>
                        <td>${Number(p.salario).toLocaleString('es-CR')}</td>
                        <td>${p.moneda.codigo}</td>
                        <td>${(p.caracteristicas || []).map(c =>
        `${c.nombre} (${UI.nivelTexto(c.nivel)})`).join(', ')}
                        </td>
                    </tr>`).join('')}
            </tbody>
        </table>
    `;
}

// ---- REGISTRO EMPRESA ----
Pages.registroEmpresa = () => {
    UI.render(`
        <div class="auth-centrado" style="padding: 40px 20px">
            <div class="auth-box">
                <h2 style="text-align:center;margin-bottom:22px">Registro de Empresa</h2>
                <div id="re-error" class="alert-error" style="display:none"></div>
                <div id="re-ok"    class="alert-success" style="display:none"></div>

                <label>Nombre *</label>
                <input id="re-nombre" type="text" placeholder="TechCorp S.A."/>
                <label>Correo *</label>
                <input id="re-correo" type="email" placeholder="contacto@empresa.com"/>
                <label>Contraseña * (mín. 6)</label>
                <input id="re-clave"  type="password" placeholder="••••••"/>
                <label>Confirmar contraseña *</label>
                <input id="re-clave2" type="password" placeholder="••••••"/>
                <label>Localización *</label>
                <input id="re-loc"  type="text" placeholder="San José, Costa Rica"/>
                <label>Teléfono * (formato +506 XXXX XXXX)</label>
                <input id="re-tel"  type="text" placeholder="+506 8888 8888"/>
                <label>Descripción *</label>
                <textarea id="re-desc" placeholder="Descripción de la empresa…"></textarea>

                <button id="re-btn" class="btn btn-primary" style="width:100%;margin-top:22px">
                    Registrar Empresa
                </button>
                <div class="auth-links" style="margin-top:12px">
                    <a onclick="Router.go('/login')" style="cursor:pointer">
                        Ya tengo cuenta → Iniciar sesión
                    </a>
                </div>
            </div>
        </div>
    `);

    document.getElementById('re-btn').addEventListener('click', async () => {
        const err = document.getElementById('re-error');
        const ok  = document.getElementById('re-ok');
        err.style.display = 'none'; ok.style.display = 'none';

        const clave  = document.getElementById('re-clave').value;
        const clave2 = document.getElementById('re-clave2').value;
        if (clave.length < 6) { err.textContent = 'Contraseña mínimo 6 caracteres'; err.style.display = 'block'; return; }
        if (clave !== clave2) { err.textContent = 'Las claves no coinciden';         err.style.display = 'block'; return; }

        try {
            const data = {
                nombre:       document.getElementById('re-nombre').value.trim(),
                correo:       document.getElementById('re-correo').value.trim(),
                clave,
                localizacion: document.getElementById('re-loc').value.trim(),
                telefono:     document.getElementById('re-tel').value.trim(),
                descripcion:  document.getElementById('re-desc').value.trim()
            };
            const res = await Api.registrarEmpresa(data);
            ok.textContent = res.mensaje + ' Redirigiendo al login…';
            ok.style.display = 'block';
            setTimeout(() => Router.go('/login'), 2000);
        } catch (e) {
            err.textContent = e.message;
            err.style.display = 'block';
        }
    });
};

// ---- REGISTRO OFERENTE ----
Pages.registroOferente = () => {
    UI.render(`
        <div class="auth-centrado" style="padding: 40px 20px">
            <div class="auth-box">
                <h2 style="text-align:center;margin-bottom:22px">Registro de Oferente</h2>
                <div id="ro-error" class="alert-error" style="display:none"></div>
                <div id="ro-ok"    class="alert-success" style="display:none"></div>

                <label>Identificación *</label>
                <input id="ro-id"     type="text" placeholder="1-2345-6789"/>
                <label>Nombre *</label>
                <input id="ro-nombre" type="text" placeholder="Juan"/>
                <label>Primer apellido *</label>
                <input id="ro-ap"     type="text" placeholder="Pérez"/>
                <label>Nacionalidad *</label>
                <input id="ro-nac"    type="text" placeholder="Costarricense"/>
                <label>Residencia *</label>
                <input id="ro-res"    type="text" placeholder="San José, Costa Rica"/>
                <label>Teléfono * (formato +506 XXXX XXXX)</label>
                <input id="ro-tel"    type="text" placeholder="+506 8888 8888"/>
                <label>Correo *</label>
                <input id="ro-correo" type="email" placeholder="correo@ejemplo.com"/>
                <label>Contraseña * (mín. 6)</label>
                <input id="ro-clave"  type="password" placeholder="••••••"/>
                <label>Confirmar contraseña *</label>
                <input id="ro-clave2" type="password" placeholder="••••••"/>

                <button id="ro-btn" class="btn btn-primary" style="width:100%;margin-top:22px">
                    Registrar Oferente
                </button>
                <div class="auth-links" style="margin-top:12px">
                    <a onclick="Router.go('/login')" style="cursor:pointer">
                        Ya tengo cuenta → Iniciar sesión
                    </a>
                </div>
            </div>
        </div>
    `);

    document.getElementById('ro-btn').addEventListener('click', async () => {
        const err = document.getElementById('ro-error');
        const ok  = document.getElementById('ro-ok');
        err.style.display = 'none'; ok.style.display = 'none';

        const clave  = document.getElementById('ro-clave').value;
        const clave2 = document.getElementById('ro-clave2').value;
        if (clave.length < 6) { err.textContent = 'Contraseña mínimo 6 caracteres'; err.style.display = 'block'; return; }
        if (clave !== clave2) { err.textContent = 'Las claves no coinciden';         err.style.display = 'block'; return; }

        try {
            const data = {
                identificacion: document.getElementById('ro-id').value.trim(),
                nombre:         document.getElementById('ro-nombre').value.trim(),
                primerApellido: document.getElementById('ro-ap').value.trim(),
                nacionalidad:   document.getElementById('ro-nac').value.trim(),
                residencia:     document.getElementById('ro-res').value.trim(),
                telefono:       document.getElementById('ro-tel').value.trim(),
                correo:         document.getElementById('ro-correo').value.trim(),
                clave
            };
            const res = await Api.registrarOferente(data);
            ok.textContent = res.mensaje + ' Redirigiendo al login…';
            ok.style.display = 'block';
            setTimeout(() => Router.go('/login'), 2000);
        } catch (e) {
            err.textContent = e.message;
            err.style.display = 'block';
        }
    });
};