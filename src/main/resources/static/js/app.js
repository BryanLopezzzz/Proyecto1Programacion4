/**
 * app.js — Registro de rutas e inicio de la SPA.
 */

// ---- Rutas públicas ----
Router.add('/',                   Pages.inicio);
Router.add('/login',              Pages.login);
Router.add('/buscar',             Pages.buscar);
Router.add('/registro/empresa',   Pages.registroEmpresa);
Router.add('/registro/oferente',  Pages.registroOferente);

// ---- Rutas empresa ----
Router.add('/empresa/dashboard',      Pages.empresaDashboard);
Router.add('/empresa/puestos',        Pages.empresaPuestos);
Router.add('/empresa/puestos/nuevo',  Pages.empresaNuevoPuesto);
Router.add('/empresa/candidatos/:id', Pages.empresaCandidatoDetalle);

// ---- Rutas oferente ----
Router.add('/oferente/dashboard',   Pages.oferenteDashboard);
Router.add('/oferente/habilidades', Pages.oferenteHabilidades);
Router.add('/oferente/cv',          Pages.oferenteCv);
Router.add('/oferente/puestos',     Pages.oferentePuestos);

// ---- Rutas admin ----
Router.add('/admin/dashboard',       Pages.adminDashboard);
Router.add('/admin/empresas',        Pages.adminEmpresas);
Router.add('/admin/oferentes',       Pages.adminOferentes);
Router.add('/admin/caracteristicas', Pages.adminCaracteristicas);
Router.add('/admin/reportes',        Pages.adminReportes);

// Iniciar
Router.init();