import { useEffect, useState } from 'react'
import { getCaracteristicas, oferenteHabilidades, oferenteAgregarHabilidad, oferenteEliminarHabilidad } from '../../api/api'

const nivelTexto = n => n === 1 ? 'Básico' : n === 2 ? 'Intermedio' : 'Avanzado'

function aplanarCarac(raices) {
  const opts = []
  raices.forEach(r => {
    if (r.hijos && r.hijos.length) {
      opts.push({ group: r.nombre, hijos: r.hijos.flatMap(h => [
        { id: h.id, label: h.nombre },
        ...(h.hijos || []).map(n => ({ id: n.id, label: `↳ ${n.nombre}` }))
      ])})
    } else {
      opts.push({ id: r.id, label: r.nombre })
    }
  })
  return opts
}

export default function Habilidades() {
  const [habilidades, setHabilidades] = useState([])
  const [raices, setRaices] = useState([])
  const [caracId, setCaracId] = useState('')
  const [nivel, setNivel] = useState('2')

  const cargar = () => Promise.all([oferenteHabilidades(), getCaracteristicas()])
    .then(([h, r]) => { setHabilidades(h); setRaices(r) })
    .catch(() => {})

  useEffect(() => { cargar() }, [])

  const agregar = async () => {
    if (!caracId) return
    await oferenteAgregarHabilidad({ caracteristicaId: parseInt(caracId), nivel: parseInt(nivel) })
    cargar()
  }

  const eliminar = async (id) => {
    if (!confirm('¿Eliminar esta habilidad?')) return
    await oferenteEliminarHabilidad(id)
    cargar()
  }

  const opciones = aplanarCarac(raices)

  return (
    <div className="container panels" style={{ display: 'flex', gap: 28, flexWrap: 'wrap', padding: '40px 10%' }}>
      <div className="card panel-left" style={{ flex: 1, minWidth: 320 }}>
        <h2>Mis Habilidades</h2>
        {habilidades.length === 0
          ? <p className="empty">No has registrado habilidades aún.</p>
          : <table>
              <thead><tr><th>Característica</th><th>Nivel</th><th>Eliminar</th></tr></thead>
              <tbody>
                {habilidades.map(h => (
                  <tr key={h.caracteristicaId}>
                    <td>{h.caracteristicaNombre}</td>
                    <td><span className="badge badge-activo">{nivelTexto(h.nivel)}</span></td>
                    <td><button className="btn btn-outline btn-sm" onClick={() => eliminar(h.caracteristicaId)}>✕ Eliminar</button></td>
                  </tr>
                ))}
              </tbody>
            </table>
        }
      </div>
      <div className="card" style={{ minWidth: 260 }}>
        <h3>Agregar Habilidad</h3>
        <label>Característica</label>
        <select value={caracId} onChange={e => setCaracId(e.target.value)}>
          <option value="">-- Seleccione --</option>
          {opciones.map((o, i) =>
            o.group
              ? <optgroup key={i} label={o.group}>
                  {o.hijos.map(h => <option key={h.id} value={h.id}>{h.label}</option>)}
                </optgroup>
              : <option key={o.id} value={o.id}>{o.label}</option>
          )}
        </select>
        <label>Nivel</label>
        <select value={nivel} onChange={e => setNivel(e.target.value)}>
          <option value="1">Básico</option>
          <option value="2">Intermedio</option>
          <option value="3">Avanzado</option>
        </select>
        <button className="btn btn-primary" style={{ marginTop: 16, width: '100%' }} onClick={agregar}>Agregar habilidad</button>
      </div>
    </div>
  )
}
