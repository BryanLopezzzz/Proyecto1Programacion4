import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { adminReportePuestos, adminReporteCoincid } from '../../api/api'

const MESES = ['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre']
const nivelTexto = n => n === 1 ? 'Basico' : n === 2 ? 'Intermedio' : 'Avanzado'
const anioActual = new Date().getFullYear()

export default function AdminReportes() {
  const nav = useNavigate()
  const [rpMes, setRpMes] = useState('1')
  const [rpAnio, setRpAnio] = useState(String(anioActual))
  const [rpData, setRpData] = useState(null)
  const [rcMes, setRcMes] = useState('1')
  const [rcAnio, setRcAnio] = useState(String(anioActual))
  const [rcData, setRcData] = useState(null)

  const verPuestos = async () => {
    const data = await adminReportePuestos(rpMes, rpAnio)
    setRpData(data || [])
  }

  const verCoincid = async () => {
    const data = await adminReporteCoincid(rcMes, rcAnio)
    setRcData(data || [])
  }

  return (
      <div className="container">
        <h2>Reportes</h2>
        <p className="no-print">Generá reportes en pantalla. Usa <strong>Ctrl+P</strong> para imprimir o guardar PDF.</p>

        <div className="card">
          <h3>Reporte de Puestos Publicados por Mes</h3>
          <div className="no-print">
            <div className="row2">
              <div>
                <label>Mes *</label>
                <select value={rpMes} onChange={e => setRpMes(e.target.value)}>
                  {MESES.map((m, i) => <option key={i} value={i+1}>{m}</option>)}
                </select>
              </div>
              <div>
                <label>Anio *</label>
                <input type="number" value={rpAnio} onChange={e => setRpAnio(e.target.value)} min="2020" max="2030" />
              </div>
            </div>
            <button className="btn btn-primary" style={{ marginTop: 16 }} onClick={verPuestos}>Ver reporte</button>
          </div>
          {rpData && (
              rpData.length === 0
                  ? <p className="empty">Sin puestos en ese periodo.</p>
                  : <table style={{ marginTop: 16 }}>
                    <thead><tr><th>#</th><th>Empresa</th><th>Descripcion</th><th>Salario</th><th>Tipo</th><th>Caracteristicas</th><th>Fecha</th></tr></thead>
                    <tbody>
                    {rpData.map((p, i) => (
                        <tr key={p.id}>
                          <td>{i+1}</td><td>{p.empresa}</td><td>{p.descripcion}</td>
                          <td>{p.moneda} {Number(p.salario).toLocaleString('es-CR')}</td>
                          <td><span className={`badge ${p.tipo === 'PUBLICO' ? 'badge-publico' : 'badge-privado'}`}>{p.tipo}</span></td>
                          <td>{(p.caracteristicas || []).map(c => `${c.nombre} (${nivelTexto(c.nivel)})`).join(', ') || 'Sin caracteristicas'}</td>
                          <td>{p.fecha.substring(0, 10)}</td>
                        </tr>
                    ))}
                    </tbody>
                  </table>
          )}
        </div>

        <div className="card">
          <h3>Reporte de Coincidencias Candidatos / Puestos</h3>
          <div className="no-print">
            <div className="row2">
              <div>
                <label>Mes *</label>
                <select value={rcMes} onChange={e => setRcMes(e.target.value)}>
                  {MESES.map((m, i) => <option key={i} value={i+1}>{m}</option>)}
                </select>
              </div>
              <div>
                <label>Anio *</label>
                <input type="number" value={rcAnio} onChange={e => setRcAnio(e.target.value)} min="2020" max="2030" />
              </div>
            </div>
            <button className="btn btn-primary" style={{ marginTop: 16 }} onClick={verCoincid}>Ver reporte</button>
          </div>
          {rcData && (
              rcData.length === 0
                  ? <p className="empty">Sin puestos en ese periodo.</p>
                  : rcData.map((item, i) => (
                      <div key={i} style={{ marginBottom: 28, borderRadius: 'var(--radius-md)', border: '1px solid var(--border)', overflow: 'hidden' }}>
                        <div style={{ background: 'var(--accent)', color: 'white', padding: '10px 16px', display: 'flex', justifyContent: 'space-between' }}>
                          <span>{item.empresa}: {item.puesto.substring(0, 60)}</span>
                          <span style={{ fontSize: 11, background: 'rgba(255,255,255,.2)', padding: '2px 10px', borderRadius: 10 }}>{item.tipo}</span>
                        </div>
                        {item.candidatos.length
                            ? <table style={{ border: 'none', borderRadius: 0, boxShadow: 'none', width: '100%', tableLayout: 'fixed' }}>
                              <thead>
                              <tr>
                                <th style={{ width: '18%' }}>Oferente</th>
                                <th style={{ width: '16%' }}>Identificacion</th>
                                <th style={{ width: '23%' }}>Correo</th>
                                <th style={{ width: '12%' }}>Cumplidos</th>
                                <th style={{ width: '8%' }}>Total</th>
                                <th style={{ width: '22%' }}>% Coincidencia</th>
                              </tr>
                              </thead>
                              <tbody>
                              {item.candidatos.map((c, j) => (
                                  <tr key={j}>
                                    <td>{c.nombre}</td>
                                    <td>{c.identificacion}</td>
                                    <td style={{ wordBreak: 'break-all' }}>{c.correo}</td>
                                    <td>{c.cumplidos}</td>
                                    <td>{c.total}</td>
                                    <td>
                                      <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                                        <div style={{ background: 'var(--border)', borderRadius: 999, height: 8, flex: 1, overflow: 'hidden' }}>
                                          <div style={{ background: 'linear-gradient(90deg, var(--accent) 0%, #7A9A8A 100%)', height: 8, borderRadius: 999, width: `${c.porcentaje}%` }} />
                                        </div>
                                        <span style={{ fontSize: 13, fontWeight: 600, color: 'var(--accent)', whiteSpace: 'nowrap' }}>{c.porcentaje.toFixed(1)}%</span>
                                      </div>
                                    </td>
                                  </tr>
                              ))}
                              </tbody>
                            </table>
                            : <div style={{ background: 'var(--bg-subtle)', padding: 16, textAlign: 'center', fontStyle: 'italic', color: 'var(--text-tertiary)' }}>Sin candidatos con coincidencias.</div>
                        }
                      </div>
                  ))
          )}
        </div>

        <button className="btn btn-outline no-print" onClick={() => nav('/admin/dashboard')}>Dashboard</button>
      </div>
  )
}