const API_BASE = '/api'

const getToken = () => localStorage.getItem('be_token')

const fetchAPI = async (url, options = {}) => {
  const token = getToken()
  const res = await fetch(API_BASE + url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: 'Bearer ' + token } : {}),
      ...(options.headers || {}),
    },
  })
  if (res.status === 401) {
    localStorage.clear()
    window.location.href = '/login'
    return null
  }
  const data = await res.json().catch(() => ({}))
  if (!res.ok) throw new Error(data.error || 'Error del servidor')
  return data
}

// AUTH
export const login = (username, clave) =>
  fetchAPI('/auth/login', { method: 'POST', body: JSON.stringify({ username, clave }) })

// PÚBLICOS
export const getPuestosPublicos = () => fetchAPI('/puestos/publicos')
export const buscarPuestosPublicos = (params) =>
  fetchAPI('/puestos/buscar?' + new URLSearchParams(params))
export const getCaracteristicas = () => fetchAPI('/caracteristicas')
export const getMonedas = () => fetchAPI('/monedas')

// REGISTRO
export const registrarEmpresa = (data) =>
  fetchAPI('/registro/empresa', { method: 'POST', body: JSON.stringify(data) })
export const registrarOferente = (data) =>
  fetchAPI('/registro/oferente', { method: 'POST', body: JSON.stringify(data) })

// EMPRESA
export const empresaPerfil = () => fetchAPI('/empresa/perfil')
export const empresaPuestos = () => fetchAPI('/empresa/puestos')
export const empresaPublicarPuesto = (d) =>
  fetchAPI('/empresa/puestos', { method: 'POST', body: JSON.stringify(d) })
export const empresaDesactivar = (id) =>
  fetchAPI(`/empresa/puestos/${id}/desactivar`, { method: 'POST' })
export const empresaCandidatos = (id) => fetchAPI(`/empresa/puestos/${id}/candidatos`)
export const empresaCandidato = (id) => fetchAPI(`/empresa/candidatos/${id}`)

// OFERENTE
export const oferentePerfil = () => fetchAPI('/oferente/perfil')
export const oferenteHabilidades = () => fetchAPI('/oferente/habilidades')
export const oferenteAgregarHabilidad = (d) =>
  fetchAPI('/oferente/habilidades', { method: 'POST', body: JSON.stringify(d) })
export const oferenteEliminarHabilidad = (id) =>
  fetchAPI(`/oferente/habilidades/${id}`, { method: 'DELETE' })
export const oferenteBuscarPuestos = (params) =>
  fetchAPI('/oferente/puestos/buscar?' + new URLSearchParams(params))
export const oferenteSubirCv = (file) => {
  const fd = new FormData()
  fd.append('archivo', file)
  return fetch(API_BASE + '/oferente/cv', {
    method: 'POST',
    headers: { Authorization: 'Bearer ' + getToken() },
    body: fd,
  }).then((r) => r.json())
}

// ADMIN
export const adminEmpresasPendientes = () => fetchAPI('/admin/empresas/pendientes')
export const adminAprobarEmpresa = (id) =>
  fetchAPI(`/admin/empresas/${id}/aprobar`, { method: 'POST' })
export const adminRechazarEmpresa = (id) =>
  fetchAPI(`/admin/empresas/${id}/rechazar`, { method: 'POST' })
export const adminOferentesPendientes = () => fetchAPI('/admin/oferentes/pendientes')
export const adminAprobarOferente = (id) =>
  fetchAPI(`/admin/oferentes/${id}/aprobar`, { method: 'POST' })
export const adminRechazarOferente = (id) =>
  fetchAPI(`/admin/oferentes/${id}/rechazar`, { method: 'POST' })
export const adminCaracArbol = () => fetchAPI('/admin/caracteristicas')
export const adminCaracTodos = () => fetchAPI('/admin/caracteristicas/todos')
export const adminCrearCarac = (d) =>
  fetchAPI('/admin/caracteristicas', { method: 'POST', body: JSON.stringify(d) })
export const adminReportePuestos = (mes, anio) =>
  fetchAPI(`/admin/reportes/puestos?mes=${mes}&anio=${anio}`)
export const adminReporteCoincid = (mes, anio) =>
  fetchAPI(`/admin/reportes/coincidencias?mes=${mes}&anio=${anio}`)
export const getPuestoDetalle = (id) => fetchAPI(`/puestos/${id}`)
