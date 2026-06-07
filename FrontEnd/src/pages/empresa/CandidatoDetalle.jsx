import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { empresaCandidato } from '../../api/api'

const nivelTexto = n => n === 1 ? 'Básico' : n === 2 ? 'Intermedio' : 'Avanzado'

export default function CandidatoDetalle() {
  const { id } = useParams()
  const [o, setO] = useState(null)
  const nav = useNavigate()

  useEffect(() => { empresaCandidato(id).then(setO).catch(() => {}) }, [id])

  if (!o) return <div className="loading">Cargando…</div>

  return (
    <div className="container-narrow" style={{ padding: '40px 10%', maxWidth: 720 }}>
      <h2>Detalle de Oferente</h2>
      <div className="card">
        <h3>{o.nombre} {o.primerApellido}</h3>
        {[['Identificación', o.identificacion], ['Email', o.correo], ['Teléfono', o.telefono], ['Residencia', o.residencia], ['Nacionalidad', o.nacionalidad]].map(([l, v]) => (
          <div key={l} className="info-row"><span className="lbl">{l}:</span><span className="val">{v}</span></div>
        ))}
      </div>
      <div className="card">
        <h3>Habilidades</h3>
        {o.habilidades.length === 0
          ? <p className="empty">Sin habilidades.</p>
          : <table>
              <thead><tr><th>Característica</th><th>Nivel</th></tr></thead>
              <tbody>
                {o.habilidades.map(h => (
                  <tr key={h.caracteristica}><td>{h.caracteristica}</td><td><span className="badge badge-activo">{nivelTexto(h.nivel)}</span></td></tr>
                ))}
              </tbody>
            </table>
        }
      </div>
        {o.curriculumPdf && <a className="btn btn-primary" href={`http://localhost:8080/uploads/${o.curriculumPdf}`} target="_blank" rel="noreferrer">Ver Currículo PDF</a>}
      <br /><br />
      <button className="btn btn-outline" onClick={() => nav(-1)}>← Volver</button>
    </div>
  )
}
