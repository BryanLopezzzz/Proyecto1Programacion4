import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { empresaCandidatos } from '../../api/api'
import BarraCoincidencia from '../../components/BarraCoincidencia'

const estadoColor = { CUMPLE: '#dcfce7', PARCIAL: '#fef3c7', INSUFICIENTE: '#fee2e2', AUSENTE: '#ede9fe' }

export default function Candidatos() {
  const { id } = useParams()
  const [candidatos, setCandidatos] = useState([])
  const [loading, setLoading] = useState(true)
  const nav = useNavigate()

  useEffect(() => {
    empresaCandidatos(id).then(data => { setCandidatos(data || []); setLoading(false) }).catch(() => setLoading(false))
  }, [id])

  if (loading) return <div className="loading">Cargando…</div>

  const medallas = ['🥇', '🥈', '🥉']

  return (
    <div className="container">
      <h2>Candidatos para el puesto</h2>
      <p style={{ fontSize: 13, color: '#9f1239' }}>Ordenados por score ponderado.</p>
      {candidatos.length === 0
        ? <p className="empty">No se encontraron candidatos.</p>
        : <table>
            <thead><tr><th>Oferente</th><th>Cumplidos</th><th>% Coincidencia</th><th>Detalle</th><th>Acción</th></tr></thead>
            <tbody>
              {candidatos.map((c, idx) => (
                <tr key={c.oferente.id}>
                  <td><strong>{idx < 3 ? medallas[idx] : `#${idx + 1}`}</strong> {c.oferente.nombre} {c.oferente.primerApellido}</td>
                  <td>{c.requisitosCumplidos} / {c.requisitosTotal}</td>
                  <td>
                    <BarraCoincidencia pct={c.porcentajeCoincidencia} />
                    <div style={{ fontSize: 11, color: '#6b7280', marginTop: 2 }}>
                      Cumplidos exactos: {c.porcentajeCumplidos.toFixed(1)}% · Excedente: +{c.nivelExcedenteTotal}
                    </div>
                  </td>
                  <td style={{ fontSize: 12 }}>
                    {c.coincidencias.map(d => (
                      <span key={d.caracteristica} style={{ display: 'inline-block', margin: '2px 4px', padding: '2px 8px', borderRadius: 10, fontSize: 11, background: estadoColor[d.estado], border: '1px solid #d1d5db' }}>
                        {d.estado === 'CUMPLE' ? '✓' : d.estado === 'PARCIAL' ? '~' : '✗'} {d.caracteristica} (tiene:{d.nivelOferente ?? '—'} pide:{d.nivelRequerido})
                      </span>
                    ))}
                  </td>
                  <td>
                    <button className="btn btn-primary btn-sm" onClick={() => nav(`/empresa/candidatos/${c.oferente.id}`)}>Ver perfil</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
      }
      <br />
      <button className="btn btn-outline" onClick={() => nav('/empresa/puestos')}>← Volver</button>
    </div>
  )
}
