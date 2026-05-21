import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getPuestosPublicos } from '../../api/api'

export default function Inicio() {
  const [puestos, setPuestos] = useState([])
  const [hoverId, setHoverId] = useState(null)
  const nav = useNavigate()

  useEffect(() => { getPuestosPublicos().then(setPuestos).catch(() => {}) }, [])

  return (
    <div className="container">
      <h1>Bolsa de Empleo</h1>
      <p className="subtitulo-seccion">Últimos 5 puestos públicos</p>
      <div className="puesto-grid">
        {puestos.map(p => (
          <div key={p.id} className="puesto-card"
            onMouseEnter={() => setHoverId(p.id)}
            onMouseLeave={() => setHoverId(null)}>
            <div className="puesto-card-empresa">{p.empresa.nombre}</div>
            <div className="puesto-card-desc">{p.descripcion.substring(0, 50)}…</div>
            <span className="puesto-card-salario">{p.moneda.codigo} {Number(p.salario).toLocaleString('es-CR')}</span>
            <a className="btn btn-outline puesto-card-btn" onClick={() => nav('/buscar')} style={{ cursor: 'pointer' }}>Ver detalle</a>
            {hoverId === p.id && (
              <div className="puesto-card-tooltip" style={{ display: 'block' }}>
                <strong>{p.descripcion}</strong>
                <ul className="puesto-card-tooltip-list">
                  {(p.caracteristicas || []).map(c => <li key={c.id}>{c.nombre} (nivel {c.nivel})</li>)}
                </ul>
              </div>
            )}
          </div>
        ))}
      </div>
      {puestos.length === 0 && <p className="empty">No hay puestos publicados aún.</p>}
    </div>
  )
}
