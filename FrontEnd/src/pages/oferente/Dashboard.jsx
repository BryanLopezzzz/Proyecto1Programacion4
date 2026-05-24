import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { oferentePerfil } from '../../api/api'

export default function OferenteDashboard() {
  const [perfil, setPerfil] = useState(null)
  const nav = useNavigate()

  useEffect(() => { oferentePerfil().then(setPerfil).catch(() => {}) }, [])
  if (!perfil) return <div className="loading">Cargando…</div>

  return (
    <div className="container">
      <h1>Bienvenido</h1>
      <p>Hola, <strong>{perfil.nombre} {perfil.primerApellido}</strong>. Administrá tus habilidades y tu CV.</p>
      <div className="card-grid">
        <a className="card-btn c1" onClick={() => nav('/oferente/habilidades')} style={{ cursor: 'pointer' }}>Mis habilidades</a>
        <a className="card-btn c2" onClick={() => nav('/oferente/cv')} style={{ cursor: 'pointer' }}>Mi CV</a>
        <a className="card-btn c3" onClick={() => nav('/oferente/puestos')} style={{ cursor: 'pointer' }}>Buscar puestos</a>
      </div>
    </div>
  )
}
