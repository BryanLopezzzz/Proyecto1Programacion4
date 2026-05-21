import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { empresaPerfil } from '../../api/api'

export default function EmpresaDashboard() {
  const [perfil, setPerfil] = useState(null)
  const nav = useNavigate()

  useEffect(() => { empresaPerfil().then(setPerfil).catch(() => {}) }, [])

  if (!perfil) return <div className="loading">Cargando…</div>

  return (
    <div className="container">
      <h1>Empresa - Dashboard</h1>
      <p>Bienvenido, <strong>{perfil.nombre}</strong>. Administrá tus puestos y buscá candidatos.</p>
      <div className="card-grid">
        <a className="card-btn c1" onClick={() => nav('/empresa/puestos')} style={{ cursor: 'pointer' }}>Mis puestos</a>
        <a className="card-btn c2" onClick={() => nav('/empresa/puestos/nuevo')} style={{ cursor: 'pointer' }}>Publicar puesto</a>
      </div>
      <div className="card" style={{ marginTop: 28 }}>
        <h3>Datos de la empresa</h3>
        {[['Nombre', perfil.nombre], ['Correo', perfil.correo], ['Teléfono', perfil.telefono], ['Localización', perfil.localizacion], ['Descripción', perfil.descripcion]].map(([l, v]) => (
          <div key={l} className="info-row"><span className="lbl">{l}:</span><span className="val">{v}</span></div>
        ))}
        <div className="info-row"><span className="lbl">Estado:</span>
          <span className={`badge badge-${perfil.estado.toLowerCase()}`}>{perfil.estado}</span>
        </div>
      </div>
    </div>
  )
}
